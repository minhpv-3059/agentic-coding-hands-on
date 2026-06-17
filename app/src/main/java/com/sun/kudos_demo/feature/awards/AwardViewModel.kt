package com.sun.kudos_demo.feature.awards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.NotificationsRepository
import com.sun.kudos_demo.feature.auth.AppLanguage
import com.sun.kudos_demo.navigation.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** UI state for the Awards tab. Display-only — award content is static mock data. */
data class AwardsUiState(
    val awards: List<AwardContent> = AwardData.awards,
    val selected: AwardContent = AwardData.awards.first(),
    val dropdownExpanded: Boolean = false,
    val language: AppLanguage = AppLanguage.VN,
    val unreadCount: Int = 0
)

/**
 * State holder for the Awards tab (Phase 10). One screen with a dropdown that switches
 * among the 6 award types — selecting one swaps the Award Information Block content.
 *
 * The optional [NavRoutes.ARG_AWARD] route arg pre-selects an award (e.g. when arriving
 * from a specific card on Home); absent/blank → default MVP (the first award). Eligibility
 * logic is out of scope — this screen only displays award information.
 */
class AwardViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val initialAwardId: String? =
        savedStateHandle.get<String>(NavRoutes.ARG_AWARD)?.takeIf { it.isNotBlank() }

    private val _uiState = MutableStateFlow(
        AwardsUiState(
            selected = AwardData.byId(initialAwardId),
            unreadCount = NotificationsRepository.unreadCount.value
        )
    )
    val uiState: StateFlow<AwardsUiState> = _uiState.asStateFlow()

    init {
        observeUnreadNotifications()
    }

    /** Keep the header bell badge in sync as notifications are marked read elsewhere. */
    private fun observeUnreadNotifications() {
        viewModelScope.launch {
            NotificationsRepository.unreadCount.collect { count ->
                _uiState.update { it.copy(unreadCount = count) }
            }
        }
    }

    /** Pick an award from the dropdown — updates the info block and closes the overlay. */
    fun selectAward(award: AwardContent) {
        _uiState.update { it.copy(selected = award, dropdownExpanded = false) }
    }

    fun setDropdownExpanded(expanded: Boolean) {
        _uiState.update { it.copy(dropdownExpanded = expanded) }
    }

    /** Header language switcher — flips VN/EN (full language UI is Phase 11). */
    fun toggleLanguage() {
        _uiState.update {
            it.copy(language = if (it.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)
        }
    }
}
