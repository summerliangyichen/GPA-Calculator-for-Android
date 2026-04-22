package com.summer.gpacalculator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.summer.gpacalculator.data.PresetRepository
import com.summer.gpacalculator.data.SelectionStateSanitizer
import com.summer.gpacalculator.data.SelectionStateStore
import com.summer.gpacalculator.data.SelectionStateTransforms
import com.summer.gpacalculator.domain.ComponentType
import com.summer.gpacalculator.domain.GpaCalculationResult
import com.summer.gpacalculator.domain.GpaCalculator
import com.summer.gpacalculator.domain.Preset
import com.summer.gpacalculator.domain.ScoreDisplay
import com.summer.gpacalculator.domain.SelectionState
import com.summer.gpacalculator.domain.Subject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val presetRepository: PresetRepository,
    private val selectionStateStore: SelectionStateStore,
    private val transforms: SelectionStateTransforms,
    private val sanitizer: SelectionStateSanitizer,
) : ViewModel() {
    private val _uiState = MutableStateFlow<GpaUiState>(GpaUiState.Loading)
    val uiState: StateFlow<GpaUiState> = _uiState.asStateFlow()

    private var latestPreset: Preset = presetRepository.defaultPreset
    private var latestState: SelectionState = SelectionState(presetId = latestPreset.id)

    init {
        viewModelScope.launch {
            selectionStateStore.state.collect { rawState ->
                val preset = presetRepository.findById(rawState.presetId) ?: presetRepository.defaultPreset
                val normalizedState = if (rawState.presetId == preset.id) {
                    rawState
                } else {
                    rawState.copy(presetId = preset.id)
                }
                val sanitizedState = sanitizer.sanitize(normalizedState, preset)
                latestPreset = preset
                latestState = sanitizedState
                _uiState.value = buildUiState(
                    preset = preset,
                    state = sanitizedState,
                )
                if (sanitizedState != normalizedState) {
                    selectionStateStore.save(sanitizedState)
                }
            }
        }
    }

    fun onPresetSelected(presetId: String) {
        val preset = presetRepository.findById(presetId) ?: return
        persist(transforms.changePreset(latestState, preset))
    }

    fun onResetSelections() {
        persist(transforms.resetSelections(latestState, latestPreset))
    }

    fun onScoreDisplayChanged(scoreDisplay: ScoreDisplay) {
        persist(transforms.updateScoreDisplay(latestState, scoreDisplay))
    }

    fun onLevelSelected(index: Int, levelIndex: Int) {
        persist(transforms.updateLevelSelection(latestState, index, levelIndex))
    }

    fun onScoreSelected(index: Int, scoreIndex: Int) {
        persist(transforms.updateScoreSelection(latestState, index, scoreIndex))
    }

    private fun persist(state: SelectionState) {
        viewModelScope.launch {
            selectionStateStore.save(state)
        }
    }

    private fun buildUiState(
        preset: Preset,
        state: SelectionState,
    ): GpaUiState.Ready {
        val calculation = GpaCalculator.calculate(
            preset = preset,
            subjectSelections = state.subjectSelections,
        )
        return GpaUiState.Ready(
            preset = preset,
            selectionState = state,
            scoreDisplay = state.scoreDisplay,
            gpaText = GpaCalculator.formatGpa(calculation.gpa),
            subjectCount = state.subjectSelections.size,
            displayEntries = buildSubjectEntries(
                preset = preset,
                selectionState = state,
                scoreDisplay = state.scoreDisplay,
                calculation = calculation,
            ),
            presetCards = presetRepository.presets.map { option ->
                PresetCardUiModel(
                    id = option.id,
                    name = option.name,
                    subtitle = option.subtitle ?: "${option.expandedSubjectDescriptors().size} subjects",
                    isSelected = option.id == preset.id,
                )
            },
        )
    }

    private fun buildSubjectEntries(
        preset: Preset,
        selectionState: SelectionState,
        scoreDisplay: ScoreDisplay,
        calculation: GpaCalculationResult,
    ): List<SubjectEntryUiModel> {
        val items = mutableListOf<SubjectEntryUiModel>()
        var offset = 0
        preset.getComponents().forEach { component ->
            when (component.type) {
                ComponentType.REGULAR -> {
                    val subject = preset.subjects[component.index]
                    items += SubjectEntryUiModel.Regular(
                        subject = buildSubjectRowUiModel(
                            subject = subject,
                            preset = preset,
                            scoreDisplay = scoreDisplay,
                            selectionState = selectionState,
                            visibleIndex = offset,
                            isCountedInGpa = true,
                        ),
                    )
                    offset += 1
                }

                ComponentType.MAX_GROUP -> {
                    val group = requireNotNull(preset.maxSubjectGroups)[component.index]
                    val selectedIndex = calculation.selectedSubjectIndexByMaxGroup[component.index] ?: 0
                    val rows = group.subjects.mapIndexed { index, subject ->
                        buildSubjectRowUiModel(
                            subject = subject,
                            preset = preset,
                            scoreDisplay = scoreDisplay,
                            selectionState = selectionState,
                            visibleIndex = offset + index,
                            isCountedInGpa = index == selectedIndex,
                        )
                    }
                    items += SubjectEntryUiModel.MaxGroup(
                        groupIndex = component.index,
                        title = "Best of this group",
                        subjects = rows,
                        selectedIndex = selectedIndex,
                    )
                    offset += group.subjects.size
                }
            }
        }
        return items
    }

    private fun buildSubjectRowUiModel(
        subject: Subject,
        preset: Preset,
        scoreDisplay: ScoreDisplay,
        selectionState: SelectionState,
        visibleIndex: Int,
        isCountedInGpa: Boolean,
    ): SubjectRowUiModel {
        val selection = selectionState.subjectSelections.getOrElse(visibleIndex) { com.summer.gpacalculator.domain.SubjectSelection() }
        val scoreMap = subject.customScoreToBaseGPAMap ?: preset.defaultScoreToBaseGPAMap
        return SubjectRowUiModel(
            visibleIndex = visibleIndex,
            name = subject.name,
            levelOptions = subject.levels.map { it.name },
            scoreOptions = scoreMap.map { mapping ->
                if (scoreDisplay == ScoreDisplay.PERCENTAGE) mapping.percentageName else mapping.letterName
            },
            selectedLevelIndex = selection.levelIndex,
            selectedScoreIndex = selection.scoreIndex,
            isCountedInGpa = isCountedInGpa,
        )
    }

    class Factory(
        private val presetRepository: PresetRepository,
        private val selectionStateStore: SelectionStateStore,
        private val transforms: SelectionStateTransforms,
        private val sanitizer: SelectionStateSanitizer,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(
                presetRepository = presetRepository,
                selectionStateStore = selectionStateStore,
                transforms = transforms,
                sanitizer = sanitizer,
            ) as T
        }
    }
}
