package com.sun.kudos_demo.feature.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.CurrentUser
import com.sun.kudos_demo.data.KudosPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val showLanguageDropdown: Boolean = false
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = KudosPreferences(app)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /** Current app-wide language (drives the dropdown selection + the localized Login text). */
    val language: StateFlow<AppLanguage> = LanguageManager.language

    fun toggleLanguageDropdown() {
        _uiState.update { it.copy(showLanguageDropdown = !it.showLanguageDropdown) }
    }

    fun dismissLanguageDropdown() {
        _uiState.update { it.copy(showLanguageDropdown = false) }
    }

    fun selectLanguage(language: AppLanguage) {
        // Switch globally — the root re-renders all stringResource() text + persists the choice.
        LanguageManager.set(language)
        _uiState.update { it.copy(showLanguageDropdown = false) }
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(1_000L) // mock auth
            prefs.setCurrentUser(CurrentUser.ID) // create + persist the local session at login
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
