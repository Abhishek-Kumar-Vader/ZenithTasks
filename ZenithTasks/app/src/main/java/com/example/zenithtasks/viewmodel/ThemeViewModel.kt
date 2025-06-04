package com.example.zenithtasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenithtasks.data.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreference: ThemePreference
) : ViewModel() {

    val darkThemeEnabled: StateFlow<Boolean> = themePreference.darkThemeEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false // Initial value while flow is collecting
        )

    fun toggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            themePreference.toggleDarkTheme(enabled)
        }
    }
}