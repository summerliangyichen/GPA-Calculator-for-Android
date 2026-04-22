package com.summer.gpacalculator.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.summer.gpacalculator.domain.SelectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

private val Context.selectionStateDataStore by preferencesDataStore(name = "selection_state")

class SelectionStateStore(
    private val context: Context,
    private val defaultPresetId: String,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val stateKey = stringPreferencesKey("state_json")

    val state: Flow<SelectionState> = context.selectionStateDataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences ->
            val stored = preferences[stateKey]
            if (stored.isNullOrBlank()) {
                SelectionState(presetId = defaultPresetId)
            } else {
                runCatching { json.decodeFromString(SelectionState.serializer(), stored) }
                    .getOrElse { SelectionState(presetId = defaultPresetId) }
            }
        }

    suspend fun save(state: SelectionState) {
        context.selectionStateDataStore.edit { preferences ->
            preferences[stateKey] = json.encodeToString(SelectionState.serializer(), state)
        }
    }

    suspend fun clear() {
        context.selectionStateDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
