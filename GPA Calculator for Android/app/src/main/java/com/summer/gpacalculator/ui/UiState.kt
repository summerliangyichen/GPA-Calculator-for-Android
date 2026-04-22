package com.summer.gpacalculator.ui

import com.summer.gpacalculator.domain.Preset
import com.summer.gpacalculator.domain.ScoreDisplay
import com.summer.gpacalculator.domain.SelectionState

data class SubjectRowUiModel(
    val visibleIndex: Int,
    val name: String,
    val levelOptions: List<String>,
    val scoreOptions: List<String>,
    val selectedLevelIndex: Int,
    val selectedScoreIndex: Int,
    val isCountedInGpa: Boolean,
)

sealed interface SubjectEntryUiModel {
    data class Regular(
        val subject: SubjectRowUiModel,
    ) : SubjectEntryUiModel

    data class MaxGroup(
        val groupIndex: Int,
        val title: String,
        val subjects: List<SubjectRowUiModel>,
        val selectedIndex: Int,
    ) : SubjectEntryUiModel
}

data class PresetCardUiModel(
    val id: String,
    val name: String,
    val subtitle: String,
    val isSelected: Boolean,
)

sealed interface GpaUiState {
    data object Loading : GpaUiState

    data class Ready(
        val preset: Preset,
        val selectionState: SelectionState,
        val scoreDisplay: ScoreDisplay,
        val gpaText: String,
        val subjectCount: Int,
        val displayEntries: List<SubjectEntryUiModel>,
        val presetCards: List<PresetCardUiModel>,
    ) : GpaUiState
}
