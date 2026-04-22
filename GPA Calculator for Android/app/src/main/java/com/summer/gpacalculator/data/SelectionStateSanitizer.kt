package com.summer.gpacalculator.data

import com.summer.gpacalculator.domain.Preset
import com.summer.gpacalculator.domain.SelectionState
import com.summer.gpacalculator.domain.SubjectSelection

class SelectionStateSanitizer {
    fun sanitize(
        state: SelectionState,
        preset: Preset,
    ): SelectionState {
        val descriptors = preset.expandedSubjectDescriptors()
        val sanitizedSelections = descriptors.mapIndexed { index, descriptor ->
            val selection = state.subjectSelections.getOrNull(index) ?: SubjectSelection()
            val maxLevelIndex = descriptor.subject.levels.lastIndex.coerceAtLeast(0)
            val scoreMap = descriptor.subject.customScoreToBaseGPAMap ?: preset.defaultScoreToBaseGPAMap
            val maxScoreIndex = scoreMap.lastIndex.coerceAtLeast(0)
            SubjectSelection(
                levelIndex = selection.levelIndex.coerceIn(0, maxLevelIndex),
                scoreIndex = selection.scoreIndex.coerceIn(0, maxScoreIndex),
            )
        }
        return state.copy(
            presetId = preset.id,
            subjectSelections = sanitizedSelections,
        )
    }
}
