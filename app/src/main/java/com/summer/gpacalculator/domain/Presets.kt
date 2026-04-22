package com.summer.gpacalculator.domain

object Presets {
    const val DEFAULT_PRESET_ID = "stockshsidgrade10"

    val iosOriginalPresetIds = listOf(
        "stockshsidgrade6",
        "stockshsidgrade7",
        "stockshsidgrade8",
        "stockshsidgrade9",
        "stockshsidgrade10",
        "stockshsidgrade11-al-2m2-1m3",
        "stockshsidgrade11-al-1m2-1m3-1m45",
        "stockshsidgrade11-al-1m2-1m3-1m4-1m5",
        "stockshsidgrade11-ap-2m2-1m3",
        "stockshsidgrade11-ap-1m2-1m3-1m45",
        "stockshsidgrade11-ap-1m2-1m3-1m4-1m5",
        "stockshsidgrade11-ib",
    )

    val enabledPresetIds = listOf(
        "stockshsidgrade6",
        "stockshsidgrade7",
        "stockshsidgrade8",
        "stockshsidgrade9",
        "stockshsidgrade10",
        "stockshsidgrade11-ib",
        "stockshsidgrade11-ib-ee",
        "stockshsidgrade12-ib",
    )

    val grade11CourseSelectionPresetIds = listOf(
        "stockshsidgrade11-al-2m1-1m2",
        "stockshsidgrade11-al-1m1-1m2-1m3-1m4",
        "stockshsidgrade11-al-1m1-1m2-1m3",
        "stockshsidgrade11-al-1m1-1m2-1m4",
        "stockshsidgrade11-ap-2m1-1m2",
        "stockshsidgrade11-ap-1m1-1m2-1m3-1m4",
        "stockshsidgrade11-ap-1m1-1m2-1m3",
        "stockshsidgrade11-ap-1m1-1m2-1m4",
    )

    val grade12PresetIds = listOf(
        "stockshsidgrade12-al-2m2-1m3",
        "stockshsidgrade12-al-1m2-1m3-1m45",
        "stockshsidgrade12-al-1m2-1m3-1m4-1m5",
        "stockshsidgrade12-ap-2m2-1m3",
        "stockshsidgrade12-ap-1m2-1m3-1m45",
        "stockshsidgrade12-ap-1m2-1m3-1m4-1m5",
        "stockshsidgrade12-ib",
    )

    val legacyPresetIdAliases = mapOf(
        "stockshsidgrade11-2m2-1m3" to "stockshsidgrade11-al-2m1-1m2",
        "stockshsidgrade11-1m2-1m3-1m45" to "stockshsidgrade11-al-1m1-1m2-1m3",
        "stockshsidgrade11-1m2-1m3-1m4-1m5" to "stockshsidgrade11-al-1m1-1m2-1m3-1m4",
        "stockshsidgrade11-al-2m2-1m3" to "stockshsidgrade11-al-2m1-1m2",
        "stockshsidgrade11-al-1m2-1m3-1m45" to "stockshsidgrade11-al-1m1-1m2-1m3",
        "stockshsidgrade11-al-1m2-1m3-1m4-1m5" to "stockshsidgrade11-al-1m1-1m2-1m3-1m4",
        "stockshsidgrade11-ap-2m2-1m3" to "stockshsidgrade11-ap-2m1-1m2",
        "stockshsidgrade11-ap-1m2-1m3-1m45" to "stockshsidgrade11-ap-1m1-1m2-1m3",
        "stockshsidgrade11-ap-1m2-1m3-1m4-1m5" to "stockshsidgrade11-ap-1m1-1m2-1m3-1m4",
        "stockshsidgrade11-ib-noee" to "stockshsidgrade11-ib",
        "stockshsidgrade11-ib-withee" to "stockshsidgrade11-ib-ee",
        "stockshsidgrade12-2m2-1m3" to "stockshsidgrade12-al-2m2-1m3",
        "stockshsidgrade12-1m2-1m3-1m45" to "stockshsidgrade12-al-1m2-1m3-1m45",
        "stockshsidgrade12-1m2-1m3-1m4-1m5" to "stockshsidgrade12-al-1m2-1m3-1m4-1m5",
    )

    fun buildEnabledPresets(): List<Preset> {
        val enabledIds = enabledPresetIds.toSet()
        return buildAllPresets().filter { it.id in enabledIds }
    }

    fun buildAllPresets(): List<Preset> {
        val presets = mutableListOf<Preset>()
        val defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap()

        var grade6 = Preset(
            id = "stockshsidgrade6",
            name = "Grade 6",
            subjects = emptyList(),
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
        )
        grade6 = grade6.copy(subjects = listOf(
            fastSubject("eng", "English", 6.5).editLevels { removeAt(4) },
            fastSubject("", "Math", 6.5).editLevels { removeAt(3) },
            fastSubject("chi", "Chinese", 5.0).editLevels {
                removeAt(4)
                this[2] = this[2].copy(name = "S")
                indices.forEach { index -> this[index] = this[index].copy(offset = this[index].offset - 0.1) }
            },
            fastSubject("", "Science", 2.5).editLevels {
                removeAt(2)
                removeAt(2)
            },
            fastSubject("", "History", 2.5).editLevels {
                removeAt(2)
                removeAt(2)
            },
        ))
        presets += grade6

        var grade7 = Preset(
            id = "stockshsidgrade7",
            name = "Grade 7",
            subjects = emptyList(),
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
        )
        grade7 = grade7.copy(subjects = listOf(
            fastSubject("eng", "English", 6.0).editLevels { removeAt(4) },
            fastSubject("", "Math", 6.0).editLevels { removeAt(3) },
            fastSubject("", "History", 5.0).editLevels { removeAt(3) },
            fastSubject("chi", "Chinese", 5.0).editLevels {
                removeAt(4)
                this[2] = this[2].copy(name = "S/5-6")
                indices.forEach { index -> this[index] = this[index].copy(offset = this[index].offset - 0.1) }
            },
            fastSubject("", "Science", 3.0).editLevels {
                removeAt(3)
                removeAt(1)
            },
        ))
        presets += grade7

        var grade8 = Preset(
            id = "stockshsidgrade8",
            name = "Grade 8",
            subjects = emptyList(),
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
        )
        grade8 = grade8.copy(subjects = listOf(
            fastSubject("eng", "English", 6.0).editLevels { removeAt(4) },
            fastSubject("", "Math", 6.0).editLevels { removeAt(3) },
            fastSubject("", "Geography", 5.0).editLevels { removeAt(3) },
            fastSubject("chi", "Chinese", 5.0).editLevels {
                removeAt(4)
                this[2] = this[2].copy(name = "S/5-7")
                indices.forEach { index -> this[index] = this[index].copy(offset = this[index].offset - 0.1) }
            },
            fastSubject("", "Biology", 3.0).editLevels {
                removeAt(3)
                removeAt(1)
            },
            fastSubject("", "Physics", 2.5).editLevels {
                removeAt(3)
                removeAt(1)
            },
        ))
        presets += grade8

        var grade9 = Preset(
            id = "stockshsidgrade9",
            name = "Grade 9",
            subjects = emptyList(),
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
        )
        grade9 = grade9.copy(subjects = listOf(
            fastSubject("eng", "English", 6.5).editLevels { removeAt(4) },
            fastSubject("", "Math", 6.0).editLevels { removeAt(3) },
            fastSubject("", "History", 4.0).editLevels { removeAt(3) },
            fastSubject("", "Chemistry", 3.0).editLevels { removeAt(3) },
            fastSubject("chi", "Chinese", 3.0).editLevels {
                removeAt(4)
                this[2] = this[2].copy(name = "S/5-7")
            },
            fastSubject("", "Elective", 3.0).editLevels {
                removeAt(1)
                removeAt(2)
            },
            fastSubject("", "Physics", 3.0).editLevels { removeAt(3) },
        ))
        presets += grade9

        val grade10 = Preset(
            id = "stockshsidgrade10",
            name = "Grade 10",
            subtitle = "2x Module 2, 1x Module 3",
            subjects = listOf(
                fastSubject("", "Math", 5.5).editLevels { removeAt(3) },
                fastSubject("eng", "English", 5.5),
                grade10HistorySubject(),
                scienceWithoutAp("Module 2"),
                scienceWithoutAp("Module 2"),
                highSchoolElectiveSubject("Module 3"),
                fastSubject("chi", "Chinese", 3.0),
            ),
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
        )
        presets += grade10

        val g11NonIbChinese = fastSubject("chi", "Chinese", 3.0).editLevels {
            this[2] = this[2].copy(name = "S/5-7")
        }
        val aLevelTrackSubjects = buildNonIbTrackSubjects(trackLevelName = "A-L", includeApEnglish = false)
        val apTrackSubjects = buildNonIbTrackSubjects(trackLevelName = "AP", includeApEnglish = true)

        presets += buildGrade11NonIbPresets(
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            trackId = "al",
            trackLabel = "A-Level Track",
            trackSubjects = aLevelTrackSubjects,
            chinese = g11NonIbChinese,
        )
        presets += buildGrade11NonIbPresets(
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            trackId = "ap",
            trackLabel = "AP Track",
            trackSubjects = apTrackSubjects,
            chinese = g11NonIbChinese,
        )

        val ibScoreToBaseGPAMap = ibScoreToBaseGPAMap()
        val tokScoreToBaseGPAMap = tokScoreToBaseGPAMap()

        presets += Preset(
            id = "stockshsidgrade11-ib",
            name = "Grade 11",
            subtitle = "IB (No EE)",
            subjects = listOf(
                ibSubject("Math"),
                ibSubject("English"),
                ibSubject("Chinese"),
                ibSubject("Physics"),
                ibSubject("Chemistry"),
                ibSubject("Biology"),
                ibCoreSubject("ToK", 0.5, tokScoreToBaseGPAMap),
            ),
            defaultScoreToBaseGPAMap = ibScoreToBaseGPAMap,
        )
        presets += Preset(
            id = "stockshsidgrade11-ib-ee",
            name = "Grade 11",
            subtitle = "IB (With EE)",
            subjects = listOf(
                ibSubject("Math"),
                ibSubject("English"),
                ibSubject("Chinese"),
                ibSubject("Physics"),
                ibSubject("Chemistry"),
                ibSubject("Biology"),
                ibCoreSubject("ToK", 0.5, tokScoreToBaseGPAMap),
                ibCoreSubject("EE", 0.5, tokScoreToBaseGPAMap),
            ),
            defaultScoreToBaseGPAMap = ibScoreToBaseGPAMap,
        )

        presets += buildGrade12Presets(
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            ibScoreToBaseGPAMap = ibScoreToBaseGPAMap,
            tokScoreToBaseGPAMap = tokScoreToBaseGPAMap,
            aLevelTrackSubjects = aLevelTrackSubjects,
            apTrackSubjects = apTrackSubjects,
        )

        return presets
    }

    private fun buildGrade12Presets(
        defaultScoreToBaseGPAMap: List<ScoreToBaseGPAMap>,
        ibScoreToBaseGPAMap: List<ScoreToBaseGPAMap>,
        tokScoreToBaseGPAMap: List<ScoreToBaseGPAMap>,
        aLevelTrackSubjects: NonIbTrackSubjects,
        apTrackSubjects: NonIbTrackSubjects,
    ): List<Preset> {
        val grade12AlevelExtraSubjects = listOf(
            grade10HistorySubject().withoutLevel("AP"),
            highSchoolElectiveSubject("Economics").withoutLevel("AP"),
            highSchoolElectiveSubject("VA").withoutLevel("AP"),
        )
        val grade12ApExtraSubjects = listOf(
            grade10HistorySubject(),
            highSchoolElectiveSubject("Economics"),
            highSchoolElectiveSubject("VA"),
        )
        val grade12DefaultIbSubjects = listOf(
            ibSubject("Physics"),
        )

        val grade12NonIb = buildGrade12NonIbPresets(
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            trackId = "al",
            trackLabel = "A-Level Track",
            trackSubjects = aLevelTrackSubjects,
            extraSubjects = grade12AlevelExtraSubjects,
        ) + buildGrade12NonIbPresets(
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            trackId = "ap",
            trackLabel = "AP Track",
            trackSubjects = apTrackSubjects,
            extraSubjects = grade12ApExtraSubjects,
        )

        val grade12IbBaseSubjects = listOf(
            ibSubject("Math"),
            ibSubject("English"),
            ibSubject("Chinese"),
            ibSubject("Chemistry"),
            ibSubject("Biology"),
            ibCoreSubject("ToK", 0.5, tokScoreToBaseGPAMap),
        )

        val grade12Ib = Preset(
            id = "stockshsidgrade12-ib",
            name = "Grade 12",
            subtitle = "IB",
            subjects = grade12IbBaseSubjects + grade12DefaultIbSubjects,
            defaultScoreToBaseGPAMap = ibScoreToBaseGPAMap,
        )

        return grade12NonIb + grade12Ib
    }

    private data class NonIbTrackSubjects(
        val math: Subject,
        val english: Subject,
        val module2: Subject,
        val module3: Subject,
        val module45: Subject,
        val module4: Subject,
        val module5: Subject,
    )

    private fun buildNonIbTrackSubjects(
        trackLevelName: String,
        includeApEnglish: Boolean,
    ): NonIbTrackSubjects {
        val math = fastSubject("", "Math", 6.0).editLevels {
            removeAt(1)
            this[lastIndex] = this[lastIndex].copy(name = trackLevelName)
        }
        val english = fastSubject("eng", "English", 6.0).let { subject ->
            if (includeApEnglish) subject else subject.withoutLevel("AP")
        }
        val module2 = fastSubject("", "Module 2", 6.0).editLevels {
            this[lastIndex] = this[lastIndex].copy(name = trackLevelName)
        }
        val module3 = fastSubject("", "Module 3", 4.5).editLevels {
            add(3, Level(name = "A-L", weight = 6.0, offset = 0.0))
            if (trackLevelName == "A-L") {
                removeAll { it.name == "AP" }
            } else {
                removeAll { it.name == "A-L" }
            }
        }
        val module45 = fastSubject("", "Module 4/5", 3.0).editLevels {
            removeLast()
            add(Level(name = "A-L", weight = 6.0, offset = 0.0))
            add(Level(name = "AP", weight = 4.5, offset = 0.0))
            if (trackLevelName == "A-L") {
                removeAll { it.name == "AP" }
            } else {
                removeAll { it.name == "A-L" }
            }
        }
        return NonIbTrackSubjects(
            math = math,
            english = english,
            module2 = module2,
            module3 = module3,
            module45 = module45,
            module4 = module45.copy(name = "Module 4"),
            module5 = module45.copy(name = "Module 5"),
        )
    }

    private fun buildGrade11NonIbPresets(
        defaultScoreToBaseGPAMap: List<ScoreToBaseGPAMap>,
        trackId: String,
        trackLabel: String,
        trackSubjects: NonIbTrackSubjects,
        chinese: Subject,
    ): List<Preset> {
        val module1 = trackSubjects.module2.copy(name = "Module 1")
        val module2 = trackSubjects.module3.copy(name = "Module 2")
        val module3 = trackSubjects.module45.copy(name = "Module 3")
        val module4 = trackSubjects.module45.copy(name = "Module 4")

        return listOf(
            Preset(
                id = "stockshsidgrade11-$trackId-2m1-1m2",
                name = "Grade 11",
                subtitle = "$trackLabel - 2x Module 1, 1x Module 2",
                subjects = listOf(
                    trackSubjects.math,
                    trackSubjects.english,
                    module1,
                    module1,
                    module2,
                    chinese,
                ),
                defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            ),
            Preset(
                id = "stockshsidgrade11-$trackId-1m1-1m2-1m3-1m4",
                name = "Grade 11",
                subtitle = "$trackLabel - 1x Module 1, Module 2, Module 3, Module 4",
                subjects = listOf(
                    trackSubjects.math,
                    trackSubjects.english,
                    module1,
                    module2,
                    module3,
                    module4,
                    chinese,
                ),
                defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            ),
            Preset(
                id = "stockshsidgrade11-$trackId-1m1-1m2-1m3",
                name = "Grade 11",
                subtitle = "$trackLabel - 1x Module 1, Module 2, Module 3",
                subjects = listOf(
                    trackSubjects.math,
                    trackSubjects.english,
                    module1,
                    module2,
                    module3,
                    chinese,
                ),
                defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            ),
            Preset(
                id = "stockshsidgrade11-$trackId-1m1-1m2-1m4",
                name = "Grade 11",
                subtitle = "$trackLabel - 1x Module 1, Module 2, Module 4",
                subjects = listOf(
                    trackSubjects.math,
                    trackSubjects.english,
                    module1,
                    module2,
                    module4,
                    chinese,
                ),
                defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            ),
        )
    }

    private fun buildGrade12NonIbPresets(
        defaultScoreToBaseGPAMap: List<ScoreToBaseGPAMap>,
        trackId: String,
        trackLabel: String,
        trackSubjects: NonIbTrackSubjects,
        extraSubjects: List<Subject>,
    ): List<Preset> = listOf(
        Preset(
            id = "stockshsidgrade12-$trackId-2m2-1m3",
            name = "Grade 12",
            subtitle = "$trackLabel - 2x Module 2, 1x Module 3",
            subjects = listOf(
                trackSubjects.math,
                trackSubjects.english,
                trackSubjects.module2,
                trackSubjects.module2,
                trackSubjects.module3,
            ) + extraSubjects,
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
        ),
        Preset(
            id = "stockshsidgrade12-$trackId-1m2-1m3-1m45",
            name = "Grade 12",
            subtitle = "$trackLabel - 1x Module 2, Module 3, Module 4/5",
            subjects = listOf(
                trackSubjects.math,
                trackSubjects.english,
                trackSubjects.module2,
                trackSubjects.module3,
                trackSubjects.module45,
            ) + extraSubjects,
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
        ),
        Preset(
            id = "stockshsidgrade12-$trackId-1m2-1m3-1m4-1m5",
            name = "Grade 12",
            subtitle = "$trackLabel - 1x Module 2, Module 3, Module 4, Module 5",
            subjects = listOf(
                trackSubjects.math,
                trackSubjects.english,
                trackSubjects.module2,
                trackSubjects.module3,
            ) + extraSubjects,
            defaultScoreToBaseGPAMap = defaultScoreToBaseGPAMap,
            maxSubjectGroups = listOf(
                maxSubjectGroup(
                    insertAt = 4,
                    subjects = listOf(trackSubjects.module4, trackSubjects.module5),
                ),
            ),
        ),
    )

    private fun defaultScoreToBaseGPAMap(): List<ScoreToBaseGPAMap> = listOf(
        ScoreToBaseGPAMap(percentageName = "0", letterName = "F", baseGPA = 0.0),
        ScoreToBaseGPAMap(percentageName = "60", letterName = "C/C-", baseGPA = 2.6),
        ScoreToBaseGPAMap(percentageName = "68", letterName = "C+", baseGPA = 3.0),
        ScoreToBaseGPAMap(percentageName = "73", letterName = "B-", baseGPA = 3.3),
        ScoreToBaseGPAMap(percentageName = "78", letterName = "B", baseGPA = 3.6),
        ScoreToBaseGPAMap(percentageName = "83", letterName = "B+", baseGPA = 3.9),
        ScoreToBaseGPAMap(percentageName = "88", letterName = "A-", baseGPA = 4.2),
        ScoreToBaseGPAMap(percentageName = "93", letterName = "A/A+", baseGPA = 4.5),
    )

    private fun ibScoreToBaseGPAMap(): List<ScoreToBaseGPAMap> = listOf(
        ScoreToBaseGPAMap(percentageName = "F", letterName = "F", baseGPA = 0.0),
        ScoreToBaseGPAMap(percentageName = "H4", letterName = "C/C-", baseGPA = 2.6),
        ScoreToBaseGPAMap(percentageName = "L5", letterName = "C+", baseGPA = 3.0),
        ScoreToBaseGPAMap(percentageName = "H5", letterName = "B-", baseGPA = 3.3),
        ScoreToBaseGPAMap(percentageName = "L6", letterName = "B", baseGPA = 3.6),
        ScoreToBaseGPAMap(percentageName = "H6", letterName = "B+", baseGPA = 3.9),
        ScoreToBaseGPAMap(percentageName = "L7", letterName = "A-", baseGPA = 4.2),
        ScoreToBaseGPAMap(percentageName = "H7", letterName = "A/A+", baseGPA = 4.5),
    )

    private fun tokScoreToBaseGPAMap(): List<ScoreToBaseGPAMap> = listOf(
        ScoreToBaseGPAMap(percentageName = "F", letterName = "F", baseGPA = 0.0),
        ScoreToBaseGPAMap(percentageName = "C", letterName = "C", baseGPA = 2.5),
        ScoreToBaseGPAMap(percentageName = "B", letterName = "B", baseGPA = 4.0),
        ScoreToBaseGPAMap(percentageName = "A", letterName = "A", baseGPA = 4.5),
    )

    private fun fastSubject(stype: String, name: String, weigh: Double): Subject {
        return when (stype) {
            "eng" -> Subject(
                name = name,
                levels = listOf(
                    Level(name = "S", weight = weigh, offset = 0.5),
                    Level(name = "S+", weight = weigh, offset = 0.4),
                    Level(name = "H", weight = weigh, offset = 0.2),
                    Level(name = "H+", weight = weigh, offset = 0.1),
                    Level(name = "AP", weight = weigh, offset = 0.0),
                ),
            )

            "chi" -> Subject(
                name = name,
                levels = listOf(
                    Level(name = "1-2", weight = weigh, offset = 0.5),
                    Level(name = "3-4", weight = weigh, offset = 0.4),
                    Level(name = "S/AP/5-7", weight = weigh, offset = 0.3),
                    Level(name = "H", weight = weigh, offset = 0.2),
                    Level(name = "H+", weight = weigh, offset = 0.1),
                ),
            )

            else -> Subject(
                name = name,
                levels = listOf(
                    Level(name = "S", weight = weigh, offset = 0.5),
                    Level(name = "S+", weight = weigh, offset = 0.35),
                    Level(name = "H", weight = weigh, offset = 0.2),
                    Level(name = "AP", weight = weigh, offset = 0.0),
                ),
            )
        }
    }

    private fun Subject.editLevels(transform: MutableList<Level>.() -> Unit): Subject {
        val updated = levels.toMutableList()
        updated.transform()
        return copy(levels = updated.toList())
    }

    private fun Subject.withoutLevel(levelName: String): Subject = copy(
        levels = levels.filterNot { it.name == levelName },
    )

    private fun grade10HistorySubject(): Subject = fastSubject("", "History", 4.0).editLevels {
        this[3] = this[3].copy(weight = 5.0)
    }

    private fun highSchoolElectiveSubject(name: String): Subject = fastSubject("", name, 3.0).editLevels {
        removeAt(1)
        this[2] = this[2].copy(weight = 4.0)
    }

    private fun scienceWithoutAp(name: String): Subject = fastSubject("", name, 3.0).editLevels {
        removeAt(3)
    }

    private fun ibSubject(name: String): Subject = Subject(
        name = name,
        levels = listOf(Level(name = "IB", weight = 1.0, offset = 0.0)),
    )

    private fun ibCoreSubject(
        name: String,
        weight: Double,
        customScoreMap: List<ScoreToBaseGPAMap>,
    ): Subject = Subject(
        name = name,
        levels = listOf(Level(name = "IB", weight = weight, offset = 0.0)),
        customScoreToBaseGPAMap = customScoreMap,
    )
}
