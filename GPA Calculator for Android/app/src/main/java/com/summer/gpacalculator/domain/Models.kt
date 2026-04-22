package com.summer.gpacalculator.domain

import kotlinx.serialization.Serializable

data class ScoreToBaseGPAMap(
    val percentageName: String,
    val letterName: String,
    val baseGPA: Double,
)

data class Level(
    val name: String,
    val weight: Double,
    val offset: Double,
)

data class Subject(
    val name: String,
    val levels: List<Level>,
    val customScoreToBaseGPAMap: List<ScoreToBaseGPAMap>? = null,
)

data class maxSubjectGroup(
    val insertAt: Int,
    val subjects: List<Subject>,
)

enum class ComponentType {
    REGULAR,
    MAX_GROUP,
}

data class Components(
    val index: Int,
    val type: ComponentType,
)

data class SubjectDescriptor(
    val key: String,
    val subject: Subject,
)

data class CourseLimitRule(
    val targetModuleId: String,
    val value: Int,
    val triggerThreshold: Int = 1,
)

data class CourseExcludeRule(
    val targetModuleId: String,
    val targetMatch: String,
)

data class CourseOption(
    val id: String,
    val name: String,
    val subject: Subject,
    val limitRules: List<CourseLimitRule> = emptyList(),
    val excludeRules: List<CourseExcludeRule> = emptyList(),
)

data class CourseModule(
    val id: String,
    val title: String,
    val selectionLimit: Int,
    val allowEmpty: Boolean = false,
    val defaultSelectedOptionIds: List<String> = emptyList(),
    val description: String? = null,
    val limitRules: List<CourseLimitRule> = emptyList(),
    val options: List<CourseOption>,
)

@Serializable
enum class ScoreDisplay {
    PERCENTAGE,
    LETTER,
}

@Serializable
data class SubjectSelection(
    val levelIndex: Int = 0,
    val scoreIndex: Int = 0,
)

@Serializable
data class SelectionState(
    val presetId: String = "",
    val scoreDisplay: ScoreDisplay = ScoreDisplay.PERCENTAGE,
    val subjectSelections: List<SubjectSelection> = emptyList(),
    val moduleSelections: Map<String, List<String>> = emptyMap(),
)

data class Preset(
    val id: String,
    val name: String,
    val subtitle: String? = null,
    val subjects: List<Subject>,
    val defaultScoreToBaseGPAMap: List<ScoreToBaseGPAMap>,
    val maxSubjectGroups: List<maxSubjectGroup>? = null,
    val courseModules: List<CourseModule> = emptyList(),
) {
    fun getComponents(): List<Components> {
        val components = MutableList(subjects.size) { index ->
            Components(index = index, type = ComponentType.REGULAR)
        }
        maxSubjectGroups?.forEachIndexed { index, group ->
            components.add(group.insertAt, Components(index = index, type = ComponentType.MAX_GROUP))
        }
        return components
    }

    fun defaultModuleSelections(): Map<String, List<String>> {
        return courseModules.associate { module ->
            module.id to module.defaultSelectedOptionIds
                .filter { optionId -> module.options.any { it.id == optionId } }
                .take(module.selectionLimit)
        }.let(::sanitizeModuleSelections)
    }

    fun sanitizeModuleSelections(moduleSelections: Map<String, List<String>>): Map<String, List<String>> {
        if (courseModules.isEmpty()) {
            return emptyMap()
        }

        val sanitized = courseModules.associate { module ->
            val knownOptionIds = module.options.map { it.id }.toSet()
            val selected = moduleSelections[module.id]
                ?.filter { it in knownOptionIds }
                ?.distinct()
                ?: module.defaultSelectedOptionIds.filter { it in knownOptionIds }
            module.id to selected.take(module.selectionLimit)
        }.toMutableMap()

        var changed: Boolean
        var guard = 0
        do {
            changed = false
            val effectiveLimits = effectiveModuleLimits(sanitized)

            courseModules.forEach { module ->
                val current = sanitized[module.id].orEmpty()
                    .filter { optionId -> module.options.any { it.id == optionId } }
                    .take(effectiveLimits[module.id] ?: module.selectionLimit)
                    .let { selected ->
                        if (selected.isEmpty() && !module.allowEmpty && (effectiveLimits[module.id] ?: 0) > 0) {
                            module.defaultSelectedOptionIds
                                .filter { optionId -> module.options.any { it.id == optionId } }
                                .take(effectiveLimits[module.id] ?: module.selectionLimit)
                        } else {
                            selected
                        }
                    }
                if (sanitized[module.id] != current) {
                    sanitized[module.id] = current
                    changed = true
                }
            }

            selectedCourseOptions(sanitized).forEach { (_, option) ->
                option.excludeRules.forEach { rule ->
                    val targetModule = courseModules.firstOrNull { it.id == rule.targetModuleId } ?: return@forEach
                    val current = sanitized[targetModule.id].orEmpty()
                    val filtered = current.filterNot { optionId ->
                        targetModule.options.firstOrNull { it.id == optionId }
                            ?.name
                            ?.contains(rule.targetMatch, ignoreCase = true) == true
                    }
                    if (filtered != current) {
                        sanitized[targetModule.id] = filtered
                        changed = true
                    }
                }
            }

            guard += 1
        } while (changed && guard < 10)

        return sanitized.toMap()
    }

    fun effectivePreset(moduleSelections: Map<String, List<String>>): Preset {
        if (courseModules.isEmpty()) {
            return this
        }
        val selectedSubjects = selectedCourseDescriptors(moduleSelections).map { it.subject }
        return copy(
            subjects = subjects + selectedSubjects,
            maxSubjectGroups = null,
            courseModules = emptyList(),
        )
    }

    fun expandedSubjectDescriptors(
        moduleSelections: Map<String, List<String>> = defaultModuleSelections(),
    ): List<SubjectDescriptor> {
        val descriptors = mutableListOf<SubjectDescriptor>()
        getComponents().forEach { component ->
            when (component.type) {
                ComponentType.REGULAR -> descriptors += SubjectDescriptor(
                    key = "subject:${component.index}",
                    subject = subjects[component.index],
                )

                ComponentType.MAX_GROUP -> {
                    val group = requireNotNull(maxSubjectGroups)[component.index]
                    group.subjects.forEachIndexed { index, subject ->
                        descriptors += SubjectDescriptor(
                            key = "max:${component.index}:$index",
                            subject = subject,
                        )
                    }
                }
            }
        }
        selectedCourseDescriptors(moduleSelections).forEach { descriptor ->
            descriptors += descriptor
        }
        return descriptors
    }

    fun effectiveModuleLimits(moduleSelections: Map<String, List<String>>): Map<String, Int> {
        if (courseModules.isEmpty()) {
            return emptyMap()
        }
        val limits = courseModules.associate { it.id to it.selectionLimit }.toMutableMap()

        courseModules.forEach { module ->
            val selectedCount = moduleSelections[module.id].orEmpty().size
            module.limitRules
                .filter { selectedCount >= it.triggerThreshold }
                .forEach { rule ->
                    limits[rule.targetModuleId] = minOf(limits[rule.targetModuleId] ?: rule.value, rule.value)
                }
        }

        selectedCourseOptions(moduleSelections).forEach { (_, option) ->
            option.limitRules.forEach { rule ->
                limits[rule.targetModuleId] = minOf(limits[rule.targetModuleId] ?: rule.value, rule.value)
            }
        }

        return limits
    }

    fun disabledCourseOptionIds(moduleSelections: Map<String, List<String>>): Map<String, Set<String>> {
        if (courseModules.isEmpty()) {
            return emptyMap()
        }
        val disabled = courseModules.associate { it.id to mutableSetOf<String>() }.toMutableMap()
        val effectiveLimits = effectiveModuleLimits(moduleSelections)

        courseModules.forEach { module ->
            if ((effectiveLimits[module.id] ?: module.selectionLimit) <= 0) {
                disabled.getValue(module.id).addAll(module.options.map { it.id })
            }
        }

        selectedCourseOptions(moduleSelections).forEach { (sourceModule, option) ->
            option.excludeRules.forEach { rule ->
                val targetModule = courseModules.firstOrNull { it.id == rule.targetModuleId } ?: return@forEach
                targetModule.options
                    .filter { it.name.contains(rule.targetMatch, ignoreCase = true) }
                    .forEach { targetOption ->
                        if (sourceModule.id != targetModule.id || targetOption.id != option.id) {
                            disabled.getValue(targetModule.id).add(targetOption.id)
                        }
                    }
            }
        }

        return disabled.mapValues { it.value.toSet() }
    }

    private fun selectedCourseDescriptors(moduleSelections: Map<String, List<String>>): List<SubjectDescriptor> {
        val sanitizedSelections = sanitizeModuleSelections(moduleSelections)
        return courseModules.flatMap { module ->
            sanitizedSelections[module.id].orEmpty().mapNotNull { optionId ->
                val option = module.options.firstOrNull { it.id == optionId } ?: return@mapNotNull null
                SubjectDescriptor(
                    key = "course:${module.id}:${option.id}",
                    subject = option.subject,
                )
            }
        }
    }

    private fun selectedCourseOptions(
        moduleSelections: Map<String, List<String>>,
    ): List<Pair<CourseModule, CourseOption>> {
        return courseModules.flatMap { module ->
            moduleSelections[module.id].orEmpty().mapNotNull { optionId ->
                val option = module.options.firstOrNull { it.id == optionId } ?: return@mapNotNull null
                module to option
            }
        }
    }
}
