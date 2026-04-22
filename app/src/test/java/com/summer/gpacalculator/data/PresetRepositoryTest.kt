package com.summer.gpacalculator.data

import com.google.common.truth.Truth.assertThat
import com.summer.gpacalculator.domain.Presets
import org.junit.Test

class PresetRepositoryTest {
    private val repository = PresetRepository()
    private val fullRepository = PresetRepository(presets = Presets.buildAllPresets())

    @Test
    fun defaultPresetMatchesIosGrade10() {
        assertThat(repository.defaultPreset.id).isEqualTo("stockshsidgrade10")
        assertThat(repository.defaultPreset.name).isEqualTo("Grade 10")
    }

    @Test
    fun defaultRepositoryExposesGrade6Through10AndIbPresetsOnly() {
        assertThat(repository.presets.map { it.id })
            .containsExactlyElementsIn(Presets.enabledPresetIds)
            .inOrder()
        assertThat(repository.findById("stockshsidgrade11-ib")).isNotNull()
        assertThat(repository.findById("stockshsidgrade11-ib-ee")).isNotNull()
        assertThat(repository.findById("stockshsidgrade12-ib")).isNotNull()
        assertThat(repository.findById("stockshsidgrade11-ap-2m1-1m2")).isNull()
        assertThat(repository.findById("stockshsidgrade12-ap-2m2-1m3")).isNull()
    }

    @Test
    fun grade11IbHasNoEeAndWithEeVariants() {
        val noEe = repository.findById("stockshsidgrade11-ib")!!
        val withEe = repository.findById("stockshsidgrade11-ib-ee")!!

        assertThat(noEe.subtitle).isEqualTo("IB (No EE)")
        assertThat(noEe.subjects.map { it.name }).doesNotContain("EE")
        assertThat(noEe.subjects.map { it.name }).containsAtLeast("Physics", "Chemistry", "Biology")
        assertThat(withEe.subtitle).isEqualTo("IB (With EE)")
        assertThat(withEe.subjects.map { it.name }).contains("EE")
        assertThat(withEe.subjects.map { it.name }).containsAtLeast("Physics", "Chemistry", "Biology")
        assertThat(withEe.subjects).hasSize(noEe.subjects.size + 1)
    }

    @Test
    fun defaultRepositoryKeepsOnlyGrade12IbForGrade12() {
        val grade12Ids = repository.presets.map { it.id }.filter { it.startsWith("stockshsidgrade12-") }

        assertThat(grade12Ids).containsExactly("stockshsidgrade12-ib")
    }

    @Test
    fun fullPresetListStillResolvesLegacyIds() {
        Presets.iosOriginalPresetIds.forEach { id ->
            assertThat(fullRepository.findById(id)).isNotNull()
        }
    }

    @Test
    fun grade11CourseSelectionPresetsAreAllPresentInFullPresetList() {
        val ids = fullRepository.presets.map { it.id }
        assertThat(ids).containsAtLeastElementsIn(Presets.grade11CourseSelectionPresetIds)
    }

    @Test
    fun grade12PresetsAreAllPresentInFullPresetList() {
        val ids = fullRepository.presets.map { it.id }
        assertThat(ids).containsAtLeastElementsIn(Presets.grade12PresetIds)
    }

    @Test
    fun grade12IncludesNonIbAndIbVariants() {
        val ids = fullRepository.presets.map { it.id }
        assertThat(ids).contains("stockshsidgrade12-al-2m2-1m3")
        assertThat(ids).contains("stockshsidgrade12-al-1m2-1m3-1m45")
        assertThat(ids).contains("stockshsidgrade12-al-1m2-1m3-1m4-1m5")
        assertThat(ids).contains("stockshsidgrade12-ap-2m2-1m3")
        assertThat(ids).contains("stockshsidgrade12-ap-1m2-1m3-1m45")
        assertThat(ids).contains("stockshsidgrade12-ap-1m2-1m3-1m4-1m5")
        assertThat(ids).contains("stockshsidgrade12-ib")
        assertThat(ids).doesNotContain("stockshsidgrade12-ib-ee")
    }

    @Test
    fun grade10UsesGenericModulesInsteadOfCourseNames() {
        val preset = repository.findById("stockshsidgrade10")!!
        val subjectNames = preset.subjects.map { it.name }

        assertThat(subjectNames).containsExactly(
            "Math",
            "English",
            "History",
            "Module 2",
            "Module 2",
            "Module 3",
            "Chinese",
        ).inOrder()
        assertThat(subjectNames).containsNoneOf(
            "Physics",
            "Chemistry",
            "Biology",
            "Economics",
            "Visual Arts",
            "Computer Science",
            "Human Geography",
            "Western Theatre",
            "Music Appreciation",
        )
    }

    @Test
    fun grade10ModuleStandardsMatchOriginalPresetWeights() {
        val preset = repository.findById("stockshsidgrade10")!!
        val module2 = preset.subjects.first { it.name == "Module 2" }
        val module3 = preset.subjects.first { it.name == "Module 3" }

        assertThat(module2.levels.map { it.name }).containsExactly("S", "S+", "H").inOrder()
        assertThat(module2.levels.map { it.weight }).containsExactly(3.0, 3.0, 3.0).inOrder()
        assertThat(module2.levels.map { it.offset }).containsExactly(0.5, 0.35, 0.2).inOrder()
        assertThat(module3.levels.map { it.name }).containsExactly("S", "H", "AP").inOrder()
        assertThat(module3.levels.map { it.weight }).containsExactly(3.0, 3.0, 4.0).inOrder()
        assertThat(module3.levels.map { it.offset }).containsExactly(0.5, 0.2, 0.0).inOrder()
    }

    @Test
    fun grade11NonIbPresetsFollowCourseSelectionModuleLayouts() {
        assertThat(fullRepository.findById("stockshsidgrade11-al-2m1-1m2")!!.subjects.map { it.name })
            .containsExactly("Math", "English", "Module 1", "Module 1", "Module 2", "Chinese")
            .inOrder()
        assertThat(fullRepository.findById("stockshsidgrade11-ap-1m1-1m2-1m3-1m4")!!.subjects.map { it.name })
            .containsExactly("Math", "English", "Module 1", "Module 2", "Module 3", "Module 4", "Chinese")
            .inOrder()
        assertThat(fullRepository.findById("stockshsidgrade11-ap-1m1-1m2-1m3")!!.subjects.map { it.name })
            .containsExactly("Math", "English", "Module 1", "Module 2", "Module 3", "Chinese")
            .inOrder()
        assertThat(fullRepository.findById("stockshsidgrade11-ap-1m1-1m2-1m4")!!.subjects.map { it.name })
            .containsExactly("Math", "English", "Module 1", "Module 2", "Module 4", "Chinese")
            .inOrder()
    }

    @Test
    fun grade11CourseSelectionPresetsDoNotExposeCourseNamesInUiSubjects() {
        val blockedCourseNames = listOf(
            "Physics",
            "Chemistry",
            "Biology",
            "Economics",
            "Computer Science",
            "Human Geography",
            "A Cappella",
            "Drawing",
            "Art",
            "Music Theory",
        )
        val grade11NonIbPresets = fullRepository.presets.filter {
            it.id.startsWith("stockshsidgrade11-al-") || it.id.startsWith("stockshsidgrade11-ap-")
        }

        assertThat(grade11NonIbPresets).isNotEmpty()
        grade11NonIbPresets.forEach { preset ->
            assertThat(preset.subjects.map { it.name }).containsNoneIn(blockedCourseNames)
        }
    }

    @Test
    fun grade11ModuleStandardsAreSplitBetweenAlevelAndApTracks() {
        val aLevelPreset = fullRepository.findById("stockshsidgrade11-al-1m1-1m2-1m3-1m4")!!
        val apPreset = fullRepository.findById("stockshsidgrade11-ap-1m1-1m2-1m3-1m4")!!
        val aLevelMath = aLevelPreset.subjects.first { it.name == "Math" }
        val aLevelEnglish = aLevelPreset.subjects.first { it.name == "English" }
        val aLevelModule1 = aLevelPreset.subjects.first { it.name == "Module 1" }
        val aLevelModule2 = aLevelPreset.subjects.first { it.name == "Module 2" }
        val aLevelModule3 = aLevelPreset.subjects.first { it.name == "Module 3" }
        val aLevelModule4 = aLevelPreset.subjects.first { it.name == "Module 4" }
        val apMath = apPreset.subjects.first { it.name == "Math" }
        val apEnglish = apPreset.subjects.first { it.name == "English" }
        val apModule1 = apPreset.subjects.first { it.name == "Module 1" }
        val apModule2 = apPreset.subjects.first { it.name == "Module 2" }
        val apModule3 = apPreset.subjects.first { it.name == "Module 3" }
        val apModule4 = apPreset.subjects.first { it.name == "Module 4" }

        assertThat(aLevelMath.levels.map { it.name }).containsExactly("S", "H", "A-L").inOrder()
        assertThat(aLevelEnglish.levels.map { it.name }).containsExactly("S", "S+", "H", "H+").inOrder()
        assertThat(aLevelModule1.levels.map { it.name }).containsExactly("S", "S+", "H", "A-L").inOrder()
        assertThat(aLevelModule1.levels.map { it.weight }).containsExactly(6.0, 6.0, 6.0, 6.0).inOrder()
        assertThat(aLevelModule2.levels.map { it.name }).containsExactly("S", "S+", "H", "A-L").inOrder()
        assertThat(aLevelModule2.levels.map { it.weight }).containsExactly(4.5, 4.5, 4.5, 6.0).inOrder()
        assertThat(aLevelModule3.levels.map { it.name }).containsExactly("S", "S+", "H", "A-L").inOrder()
        assertThat(aLevelModule3.levels.map { it.weight }).containsExactly(3.0, 3.0, 3.0, 6.0).inOrder()
        assertThat(aLevelModule4.levels.map { it.name }).containsExactly("S", "S+", "H", "A-L").inOrder()
        assertThat(aLevelModule4.levels.map { it.weight }).containsExactly(3.0, 3.0, 3.0, 6.0).inOrder()

        assertThat(apMath.levels.map { it.name }).containsExactly("S", "H", "AP").inOrder()
        assertThat(apEnglish.levels.map { it.name }).containsExactly("S", "S+", "H", "H+", "AP").inOrder()
        assertThat(apModule1.levels.map { it.name }).containsExactly("S", "S+", "H", "AP").inOrder()
        assertThat(apModule1.levels.map { it.weight }).containsExactly(6.0, 6.0, 6.0, 6.0).inOrder()
        assertThat(apModule2.levels.map { it.name }).containsExactly("S", "S+", "H", "AP").inOrder()
        assertThat(apModule2.levels.map { it.weight }).containsExactly(4.5, 4.5, 4.5, 4.5).inOrder()
        assertThat(apModule3.levels.map { it.name }).containsExactly("S", "S+", "H", "AP").inOrder()
        assertThat(apModule3.levels.map { it.weight }).containsExactly(3.0, 3.0, 3.0, 4.5).inOrder()
        assertThat(apModule4.levels.map { it.name }).containsExactly("S", "S+", "H", "AP").inOrder()
        assertThat(apModule4.levels.map { it.weight }).containsExactly(3.0, 3.0, 3.0, 4.5).inOrder()
    }

    @Test
    fun legacyMixedTrackIdsResolveToAlevelTrack() {
        val preset = fullRepository.findById("stockshsidgrade11-1m2-1m3-1m45")!!
        val math = preset.subjects.first { it.name == "Math" }

        assertThat(preset.id).isEqualTo("stockshsidgrade11-al-1m1-1m2-1m3")
        assertThat(math.levels.map { it.name }).containsExactly("S", "H", "A-L").inOrder()
    }
}
