package com.summer.gpacalculator.data

import com.google.common.truth.Truth.assertThat
import com.summer.gpacalculator.domain.Presets
import com.summer.gpacalculator.domain.SelectionState
import com.summer.gpacalculator.domain.SubjectSelection
import org.junit.Test

class SelectionStateSanitizerTest {
    private val repository = PresetRepository()
    private val fullRepository = PresetRepository(presets = Presets.buildAllPresets())
    private val sanitizer = SelectionStateSanitizer()

    @Test
    fun outOfBoundsIndicesAreClampedDuringRestore() {
        val preset = repository.defaultPreset
        val state = SelectionState(
            presetId = preset.id,
            subjectSelections = listOf(
                SubjectSelection(levelIndex = 99, scoreIndex = 99),
            ),
        )

        val sanitized = sanitizer.sanitize(state, preset)

        assertThat(sanitized.subjectSelections).hasSize(7)
        assertThat(sanitized.subjectSelections.first().levelIndex).isEqualTo(2)
        assertThat(sanitized.subjectSelections.first().scoreIndex).isEqualTo(7)
    }

    @Test
    fun grade12StaticPresetRestoresExpectedLength() {
        val preset = fullRepository.findById("stockshsidgrade12-al-2m2-1m3")!!
        val state = SelectionState(
            presetId = preset.id,
            subjectSelections = listOf(SubjectSelection()),
        )

        val sanitized = sanitizer.sanitize(state, preset)

        assertThat(sanitized.subjectSelections).hasSize(8)
    }

    @Test
    fun subjectSelectionsAlwaysAlignWithStaticGrade12Preset() {
        val preset = fullRepository.findById("stockshsidgrade12-ib")!!
        val state = SelectionState(
            presetId = preset.id,
            subjectSelections = listOf(
                SubjectSelection(),
                SubjectSelection(),
            ),
        )

        val sanitized = sanitizer.sanitize(state, preset)

        assertThat(sanitized.subjectSelections).hasSize(7)
    }
}
