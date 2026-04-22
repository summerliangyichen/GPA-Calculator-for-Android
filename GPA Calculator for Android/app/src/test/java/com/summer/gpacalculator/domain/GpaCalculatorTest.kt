package com.summer.gpacalculator.domain

import com.google.common.truth.Truth.assertThat
import com.summer.gpacalculator.data.PresetRepository
import org.junit.Test

class GpaCalculatorTest {
    @Test
    fun regularSubjectCalculationUsesOriginalFormula() {
        val preset = Preset(
            id = "regular",
            name = "Regular",
            subjects = listOf(
                Subject(
                    name = "Math",
                    levels = listOf(Level(name = "H", weight = 2.0, offset = 0.5)),
                ),
            ),
            defaultScoreToBaseGPAMap = listOf(
                ScoreToBaseGPAMap("88", "A-", 4.2),
            ),
        )

        val result = GpaCalculator.calculate(
            preset = preset,
            subjectSelections = listOf(SubjectSelection(levelIndex = 0, scoreIndex = 0)),
        )

        assertThat(result.gpa).isWithin(0.00001).of(3.7)
    }

    @Test
    fun baseScoreNeverDropsBelowZero() {
        val preset = Preset(
            id = "offset",
            name = "Offset",
            subjects = listOf(
                Subject(
                    name = "English",
                    levels = listOf(Level(name = "S", weight = 1.0, offset = 0.5)),
                ),
            ),
            defaultScoreToBaseGPAMap = listOf(
                ScoreToBaseGPAMap("0", "F", 0.0),
            ),
        )

        val result = GpaCalculator.calculate(
            preset = preset,
            subjectSelections = listOf(SubjectSelection()),
        )

        assertThat(result.gpa).isEqualTo(0.0)
    }

    @Test
    fun maxSubjectGroupKeepsOnlyHighestWeightedSubject() {
        val preset = Preset(
            id = "maxGroup",
            name = "Max Group",
            subjects = emptyList(),
            defaultScoreToBaseGPAMap = listOf(
                ScoreToBaseGPAMap("78", "B", 3.6),
                ScoreToBaseGPAMap("88", "A-", 4.2),
            ),
            maxSubjectGroups = listOf(
                maxSubjectGroup(
                    insertAt = 0,
                    subjects = listOf(
                        Subject("History", listOf(Level("H", 1.0, 0.0))),
                        Subject("Economics", listOf(Level("H", 1.0, 0.0))),
                    ),
                ),
            ),
        )

        val result = GpaCalculator.calculate(
            preset = preset,
            subjectSelections = listOf(
                SubjectSelection(scoreIndex = 0),
                SubjectSelection(scoreIndex = 1),
            ),
        )

        assertThat(result.gpa).isWithin(0.00001).of(4.2)
        assertThat(result.selectedSubjectIndexByMaxGroup[0]).isEqualTo(1)
    }

    @Test
    fun apPriorityRestrictsComparisonToApSubjectsOnly() {
        val preset = Preset(
            id = "apPriority",
            name = "AP Priority",
            subjects = emptyList(),
            defaultScoreToBaseGPAMap = listOf(
                ScoreToBaseGPAMap("78", "B", 3.6),
                ScoreToBaseGPAMap("93", "A/A+", 4.5),
            ),
            maxSubjectGroups = listOf(
                maxSubjectGroup(
                    insertAt = 0,
                    subjects = listOf(
                        Subject("History", listOf(Level("AP", 1.0, 0.0))),
                        Subject("Economics", listOf(Level("H", 1.0, 0.0))),
                    ),
                ),
            ),
        )

        val result = GpaCalculator.calculate(
            preset = preset,
            subjectSelections = listOf(
                SubjectSelection(levelIndex = 0, scoreIndex = 0),
                SubjectSelection(levelIndex = 0, scoreIndex = 1),
            ),
        )

        assertThat(result.gpa).isWithin(0.00001).of(3.6)
        assertThat(result.selectedSubjectIndexByMaxGroup[0]).isEqualTo(0)
    }

    @Test
    fun tokUsesItsOwnCustomScoreMap() {
        val repository = PresetRepository(presets = Presets.buildAllPresets())
        val preset = repository.findById("stockshsidgrade11-ib")!!
        val selections = List(preset.expandedSubjectDescriptors().size) { SubjectSelection() }.toMutableList()
        selections[6] = SubjectSelection(scoreIndex = 2)

        val result = GpaCalculator.calculate(
            preset = preset,
            subjectSelections = selections,
        )

        assertThat(result.gpa).isWithin(0.00001).of(2.0 / 6.5)
    }

    @Test
    fun grade12ModuleFourFivePresetCountsOnlyBestModule() {
        val repository = PresetRepository(presets = Presets.buildAllPresets())
        val preset = repository.findById("stockshsidgrade12-ap-1m2-1m3-1m4-1m5")!!
        val selections = List(preset.expandedSubjectDescriptors().size) { SubjectSelection(scoreIndex = 7) }.toMutableList()
        selections[4] = SubjectSelection(levelIndex = 0, scoreIndex = 7)
        selections[5] = SubjectSelection(levelIndex = 3, scoreIndex = 7)

        val result = GpaCalculator.calculate(
            preset = preset,
            subjectSelections = selections,
        )

        assertThat(result.selectedSubjectIndexByMaxGroup[0]).isEqualTo(1)
        assertThat(result.totalWeight).isWithin(0.00001).of(37.0)
    }
}
