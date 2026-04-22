package com.summer.gpacalculator.domain

import java.util.Locale
import kotlin.math.max

data class GpaCalculationResult(
    val gpa: Double,
    val weightedSum: Double,
    val totalWeight: Double,
    val selectedSubjectIndexByMaxGroup: Map<Int, Int>,
)

object GpaCalculator {
    fun calculate(
        preset: Preset,
        subjectSelections: List<SubjectSelection>,
    ): GpaCalculationResult {
        var weightedSum = 0.0
        var totalWeight = 0.0
        var offset = 0
        val selectedSubjectIndexByMaxGroup = mutableMapOf<Int, Int>()

        preset.getComponents().forEach { component ->
            when (component.type) {
                ComponentType.REGULAR -> {
                    val subject = preset.subjects[component.index]
                    val selection = subjectSelections.getOrElse(offset) { SubjectSelection() }
                    val level = subject.levels[safeIndex(selection.levelIndex, subject.levels.size)]
                    val scoreMap = subject.customScoreToBaseGPAMap ?: preset.defaultScoreToBaseGPAMap
                    val base = scoreMap[safeIndex(selection.scoreIndex, scoreMap.size)].baseGPA
                    weightedSum += max(base - level.offset, 0.0) * level.weight
                    totalWeight += level.weight
                    offset += 1
                }

                ComponentType.MAX_GROUP -> {
                    val group = requireNotNull(preset.maxSubjectGroups)[component.index]
                    val hasAp = group.subjects.indices.any { index ->
                        val selection = subjectSelections.getOrElse(offset + index) { SubjectSelection() }
                        val subject = group.subjects[index]
                        val level = subject.levels[safeIndex(selection.levelIndex, subject.levels.size)]
                        level.name == "AP"
                    }

                    var bestWeightedScore = -10.0
                    var selectedIndex = 0
                    var selectedLevelWeight = 0.0

                    group.subjects.forEachIndexed { index, subject ->
                        val selection = subjectSelections.getOrElse(offset + index) { SubjectSelection() }
                        val level = subject.levels[safeIndex(selection.levelIndex, subject.levels.size)]
                        if (hasAp && level.name != "AP") {
                            return@forEachIndexed
                        }
                        val scoreMap = subject.customScoreToBaseGPAMap ?: preset.defaultScoreToBaseGPAMap
                        val base = scoreMap[safeIndex(selection.scoreIndex, scoreMap.size)].baseGPA
                        val weighted = max(base - level.offset, 0.0) * level.weight
                        if (weighted > bestWeightedScore + 0.00001) {
                            bestWeightedScore = weighted
                            selectedIndex = index
                            selectedLevelWeight = level.weight
                        }
                    }

                    weightedSum += max(bestWeightedScore, 0.0)
                    totalWeight += selectedLevelWeight
                    selectedSubjectIndexByMaxGroup[component.index] = selectedIndex
                    offset += group.subjects.size
                }
            }
        }

        val gpa = if (totalWeight == 0.0) 0.0 else weightedSum / totalWeight
        return GpaCalculationResult(
            gpa = gpa,
            weightedSum = weightedSum,
            totalWeight = totalWeight,
            selectedSubjectIndexByMaxGroup = selectedSubjectIndexByMaxGroup,
        )
    }

    fun formatGpa(value: Double): String = String.format(Locale.US, "%.3f", value)

    private fun safeIndex(index: Int, size: Int): Int {
        if (size <= 0) {
            return 0
        }
        return index.coerceIn(0, size - 1)
    }
}

