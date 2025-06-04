package com.example.zenithtasks.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

@Singleton
class ThemePreference @Inject constructor(@ApplicationContext private val context: Context) {

    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")

    // Flow to observe the dark theme setting
    val darkThemeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false // Default to false (light theme)
        }

    // Function to toggle and save the dark theme setting
    suspend fun toggleDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }
}