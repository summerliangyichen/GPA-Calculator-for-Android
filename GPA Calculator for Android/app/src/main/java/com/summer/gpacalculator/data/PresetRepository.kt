package com.summer.gpacalculator.data

import com.summer.gpacalculator.domain.Preset
import com.summer.gpacalculator.domain.Presets

class PresetRepository(
    val presets: List<Preset> = Presets.buildEnabledPresets(),
) {
    val defaultPreset: Preset = presets.first { it.id == Presets.DEFAULT_PRESET_ID }

    fun findById(id: String): Preset? {
        val resolvedId = Presets.legacyPresetIdAliases[id] ?: id
        return presets.firstOrNull { it.id == resolvedId }
    }
}
