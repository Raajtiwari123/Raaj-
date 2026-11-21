package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.luminaPrefsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "lumina_preferences"
)

class ThemePreferences(private val context: Context) {
    private val oledDarkKey = booleanPreferencesKey("oled_dark_mode")

    val isOledDarkMode: Flow<Boolean> = context.luminaPrefsDataStore.data.map { prefs ->
        prefs[oledDarkKey] ?: false
    }

    suspend fun setOledDarkMode(enabled: Boolean) {
        context.luminaPrefsDataStore.edit { prefs ->
            prefs[oledDarkKey] = enabled
        }
    }
}
