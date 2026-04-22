package com.summer.gpacalculator.data

import com.google.common.truth.Truth.assertThat
import com.summer.gpacalculator.domain.Presets
import com.summer.gpacalculator.domain.ScoreDisplay
import com.summer.gpacalculator.domain.SelectionState
import com.summer.gpacalculator.domain.SubjectSelection
import org.junit.Test

class SelectionStateTransformsTest {
    private val repository = PresetRepository()
    private val fullRepository = PresetRepository(presets = Presets.buildAllPresets())
    private val transforms = SelectionStateTransforms()

    @Test
    fun changingPresetRebuildsSelectionLength() {
        val grade6 = repository.findById("stockshsidgrade6")!!
        val changed = transforms.changePreset(SelectionState(scoreDisplay = ScoreDisplay.LETTER), grade6)

        assertThat(changed.presetId).isEqualTo(grade6.id)
        assertThat(changed.subjectSelections).hasSize(5)
        assertThat(changed.subjectSelections.distinct()).containsExactly(SubjectSelection())
    }

    @Test
    fun resetKeepsPresetButClearsSelections() {
        val preset = repository.defaultPreset
        val state = SelectionState(
            presetId = preset.id,
            subjectSelections = List(7) { SubjectSelection(levelIndex = 1, scoreIndex = 2) },
        )

        val reset = transforms.resetSelections(state, preset)

        assertThat(reset.presetId).isEqualTo(preset.id)
        assertThat(reset.subjectSelections).containsExactlyElementsIn(List(7) { SubjectSelection() }).inOrder()
    }

    @Test
    fun updatingScoreDisplayOnlyChangesDisplayMode() {
        val state = SelectionState(scoreDisplay = ScoreDisplay.PERCENTAGE)

        val updated = transforms.updateScoreDisplay(state, ScoreDisplay.LETTER)

        assertThat(updated.scoreDisplay).isEqualTo(ScoreDisplay.LETTER)
        assertThat(updated.subjectSelections).isEmpty()
    }

    @Test
    fun grade12UsesStaticPresetSubjectCount() {
        val preset = fullRepository.findById("stockshsidgrade12-al-2m2-1m3")!!

        val state = transforms.changePreset(SelectionState(), preset)

        assertThat(state.subjectSelections).hasSize(8)
    }

    @Test
    fun grade11FullModulePresetHasExpandedVisibleLength() {
        val preset = fullRepository.findById("stockshsidgrade11-ap-1m1-1m2-1m3-1m4")!!

        val state = transforms.changePreset(SelectionState(), preset)

        assertThat(state.subjectSelections).hasSize(7)
        assertThat(preset.maxSubjectGroups).isNull()
    }
}
