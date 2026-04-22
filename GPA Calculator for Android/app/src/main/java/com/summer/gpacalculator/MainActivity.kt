package com.summer.gpacalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.summer.gpacalculator.data.PresetRepository
import com.summer.gpacalculator.data.SelectionStateSanitizer
import com.summer.gpacalculator.data.SelectionStateStore
import com.summer.gpacalculator.data.SelectionStateTransforms
import com.summer.gpacalculator.ui.GpaApp
import com.summer.gpacalculator.ui.MainViewModel
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_CLEAR_STATE = "clear_state"
    }

    private val presetRepository by lazy(LazyThreadSafetyMode.NONE) { PresetRepository() }
    private val selectionStateStore by lazy(LazyThreadSafetyMode.NONE) {
        SelectionStateStore(
            context = applicationContext,
            defaultPresetId = presetRepository.defaultPreset.id,
        )
    }
    private val viewModel by viewModels<MainViewModel> {
        MainViewModel.Factory(
            presetRepository = presetRepository,
            selectionStateStore = selectionStateStore,
            transforms = SelectionStateTransforms(),
            sanitizer = SelectionStateSanitizer(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.getBooleanExtra(EXTRA_CLEAR_STATE, false)) {
            runBlocking {
                selectionStateStore.clear()
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            GpaApp(viewModel = viewModel)
        }
    }
}

