package com.summer.gpacalculator.data

import com.summer.gpacalculator.domain.Preset
import com.summer.gpacalculator.domain.ScoreDisplay
import com.summer.gpacalculator.domain.SelectionState
import com.summer.gpacalculator.domain.SubjectSelection

class SelectionStateTransforms {
    fun changePreset(
        currentState: SelectionState,
        preset: Preset,
    ): SelectionState {
        return currentState.copy(
            presetId = preset.id,
            subjectSelections = List(preset.expandedSubjectDescriptors().size) { SubjectSelection() },
        )
    }

    fun resetSelections(
        currentState: SelectionState,
        preset: Preset,
    ): SelectionState {
        return currentState.copy(
            subjectSelections = List(preset.expandedSubjectDescriptors().size) { SubjectSelection() },
        )
    }

    fun updateScoreDisplay(
        currentState: SelectionState,
        scoreDisplay: ScoreDisplay,
    ): SelectionState = currentState.copy(scoreDisplay = scoreDisplay)

    fun updateLevelSelection(
        currentState: SelectionState,
        index: Int,
        levelIndex: Int,
    ): SelectionState {
        return updateSelection(currentState, index) { selection ->
            selection.copy(levelIndex = levelIndex)
        }
    }

    fun updateScoreSelection(
        currentState: SelectionState,
        index: Int,
        scoreIndex: Int,
    ): SelectionState {
        return updateSelection(currentState, index) { selection ->
            selection.copy(scoreIndex = scoreIndex)
        }
    }

    private fun updateSelection(
        currentState: SelectionState,
        index: Int,
        transform: (SubjectSelection) -> SubjectSelection,
    ): SelectionState {
        if (index !in currentState.subjectSelections.indices) {
            return currentState
        }
        val updatedSelections = currentState.subjectSelections.toMutableList()
        updatedSelections[index] = transform(updatedSelections[index])
        return currentState.copy(subjectSelections = updatedSelections)
    }
}
