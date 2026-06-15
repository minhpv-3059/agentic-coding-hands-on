package com.sun.kudos_demo.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppLanguage(val code: String) {
    VN("VN"), EN("EN")
}

data class LoginUiState(
    val language: AppLanguage = AppLanguage.VN,
    val isLoading: Boolean = false,
    val showLanguageDropdown: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun toggleLanguageDropdown() {
        _uiState.update { it.copy(showLanguageDropdown = !it.showLanguageDropdown) }
    }

    fun dismissLanguageDropdown() {
        _uiState.update { it.copy(showLanguageDropdown = false) }
    }

    fun selectLanguage(language: AppLanguage) {
        _uiState.update { it.copy(language = language, showLanguageDropdown = false) }
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(1_000L) // mock auth
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
