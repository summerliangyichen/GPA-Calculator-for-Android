package com.summer.gpacalculator.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.summer.gpacalculator.domain.ScoreDisplay
import com.summer.gpacalculator.ui.theme.GpaCalculatorTheme

@Composable
fun GpaApp(viewModel: MainViewModel) {
    GpaCalculatorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val navController = rememberNavController()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavHost(
                navController = navController,
                startDestination = "main",
            ) {
                composable("main") {
                    MainScreen(
                        uiState = uiState,
                        onCustomize = { navController.navigate("customize") },
                        onReset = viewModel::onResetSelections,
                        onLevelSelected = viewModel::onLevelSelected,
                        onScoreSelected = viewModel::onScoreSelected,
                    )
                }
                composable("customize") {
                    CustomizeScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onPresetSelected = viewModel::onPresetSelected,
                        onScoreDisplayChanged = viewModel::onScoreDisplayChanged,
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    uiState: GpaUiState,
    onCustomize: () -> Unit,
    onReset: () -> Unit,
    onLevelSelected: (Int, Int) -> Unit,
    onScoreSelected: (Int, Int) -> Unit,
) {
    when (uiState) {
        GpaUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Loading...")
            }
        }

        is GpaUiState.Ready -> {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                ) {
                    HeaderCard(
                        title = "GPA Calculator",
                        gpaText = uiState.gpaText,
                        presetName = uiState.preset.name,
                        presetSubtitle = uiState.preset.subtitle,
                        subjectCount = uiState.subjectCount,
                        onCustomize = onCustomize,
                        onReset = onReset,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    uiState.displayEntries.forEach { entry ->
                        when (entry) {
                            is SubjectEntryUiModel.Regular -> {
                                SubjectCard(
                                    subject = entry.subject,
                                    onLevelSelected = onLevelSelected,
                                    onScoreSelected = onScoreSelected,
                                )
                            }

                            is SubjectEntryUiModel.MaxGroup -> {
                                MaxGroupCard(
                                    group = entry,
                                    onLevelSelected = onLevelSelected,
                                    onScoreSelected = onScoreSelected,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCard(
    title: String,
    gpaText: String,
    presetName: String,
    presetSubtitle: String?,
    subjectCount: Int,
    onCustomize: () -> Unit,
    onReset: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Your GPA: $gpaText",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ActionButton(
                    text = "Customize",
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Customize" },
                    onClick = onCustomize,
                    testTag = UiTags.CUSTOMIZE_BUTTON,
                )
                ActionButton(
                    text = "Reset",
                    modifier = Modifier.weight(1f),
                    onClick = onReset,
                    testTag = UiTags.RESET_BUTTON,
                )
            }
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = presetName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!presetSubtitle.isNullOrBlank()) {
                        Text(
                            text = presetSubtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "$subjectCount subjects",
                        modifier = Modifier.semantics { contentDescription = "subject-count" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.semantics { contentDescription = testTag },
        )
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectRowUiModel,
    onLevelSelected: (Int, Int) -> Unit,
    onScoreSelected: (Int, Int) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
            )
            SegmentedChipsRow(
                options = subject.levelOptions,
                selectedIndex = subject.selectedLevelIndex,
                onSelected = { onLevelSelected(subject.visibleIndex, it) },
            )
            SegmentedChipsRow(
                options = subject.scoreOptions,
                selectedIndex = subject.selectedScoreIndex,
                onSelected = { onScoreSelected(subject.visibleIndex, it) },
            )
        }
    }
}

@Composable
private fun MaxGroupCard(
    group: SubjectEntryUiModel.MaxGroup,
    onLevelSelected: (Int, Int) -> Unit,
    onScoreSelected: (Int, Int) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                            ),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                group.subjects.forEachIndexed { index, subject ->
                    val isSelected = index == group.selectedIndex
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.surfaceVariant
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                        ),
                        border = if (isSelected) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                )
                                if (isSelected) {
                                    Text(
                                        text = "Counted in GPA",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                            SegmentedChipsRow(
                                options = subject.levelOptions,
                                selectedIndex = subject.selectedLevelIndex,
                                onSelected = { onLevelSelected(subject.visibleIndex, it) },
                            )
                            SegmentedChipsRow(
                                options = subject.scoreOptions,
                                selectedIndex = subject.selectedScoreIndex,
                                onSelected = { onScoreSelected(subject.visibleIndex, it) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedChipsRow(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEachIndexed { index, option ->
            FilterChip(
                selected = index == selectedIndex,
                onClick = { onSelected(index) },
                label = { Text(text = option) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CustomizeScreen(
    uiState: GpaUiState,
    onBack: () -> Unit,
    onPresetSelected: (String) -> Unit,
    onScoreDisplayChanged: (ScoreDisplay) -> Unit,
) {
    when (uiState) {
        GpaUiState.Loading -> Unit

        is GpaUiState.Ready -> {
            Scaffold(
                modifier = Modifier.semantics { contentDescription = UiTags.CUSTOMIZE_SCREEN },
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Customize") },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back",
                                )
                            }
                        },
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    SectionBlock(title = "Format") {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilterChip(
                                selected = uiState.scoreDisplay == ScoreDisplay.PERCENTAGE,
                                onClick = { onScoreDisplayChanged(ScoreDisplay.PERCENTAGE) },
                                label = { Text("Percentage") },
                            )
                            FilterChip(
                                selected = uiState.scoreDisplay == ScoreDisplay.LETTER,
                                onClick = { onScoreDisplayChanged(ScoreDisplay.LETTER) },
                                label = { Text("Letter") },
                            )
                        }
                    }
                    SectionBlock(title = "Presets") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            maxItemsInEachRow = 2,
                        ) {
                            uiState.presetCards.forEach { preset ->
                                val selectedBrush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.primary,
                                    ),
                                )
                                val cardModifier = Modifier
                                    .fillMaxWidth(0.48f)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        brush = if (preset.isSelected) selectedBrush else Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.surfaceVariant,
                                                MaterialTheme.colorScheme.surfaceVariant,
                                            ),
                                        ),
                                    )
                                    .clickable { onPresetSelected(preset.id) }
                                    .padding(16.dp)
                                Column(
                                    modifier = cardModifier.semantics {
                                        contentDescription = UiTags.presetCard(preset.id)
                                    },
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = preset.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = preset.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "based on the original GPA Calculator project",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "reconstruction by Summer LiangYiChen",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "special thanks to Codex",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionBlock(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}
