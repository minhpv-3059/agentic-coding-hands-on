package com.sun.kudos_demo.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.NotificationsRepository
import com.sun.kudos_demo.feature.auth.AppLanguage
import com.sun.kudos_demo.feature.auth.LanguageManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Remaining time to the event, split into the units the Home countdown shows. */
data class CountdownState(
    val days: Int = 20,
    val hours: Int = 20,
    val minutes: Int = 20
)

data class HomeUiState(
    val language: AppLanguage = AppLanguage.VN,
    val unreadNotifications: Int = 0,      // mirrors NotificationsRepository.unreadCount
    val countdown: CountdownState = CountdownState()
)

/**
 * State holder for the Home screen. Mock-data only (no real API for this phase).
 *
 * Countdown: the design's event date (26/12/2025) has already passed, so per the
 * Phase 04 clarification we count down to a demo target = launch time + 20d 20h 20m.
 * This makes the timer start at the design's 20/20/20 and tick down live.
 */
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(unreadNotifications = NotificationsRepository.unreadCount.value)
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val targetTimeMs: Long =
        System.currentTimeMillis() + ((20L * 24 + 20) * 60 + 20) * 60 * 1000

    init {
        startCountdown()
        observeUnreadNotifications()
        observeLanguage()
    }

    /** Reflect the app-wide language in the header switcher. */
    private fun observeLanguage() {
        viewModelScope.launch {
            LanguageManager.language.collect { lang -> _uiState.update { it.copy(language = lang) } }
        }
    }

    /** Keep the header bell badge in sync as notifications are marked read elsewhere. */
    private fun observeUnreadNotifications() {
        viewModelScope.launch {
            NotificationsRepository.unreadCount.collect { count ->
                _uiState.update { it.copy(unreadNotifications = count) }
            }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (true) {
                val remaining = (targetTimeMs - System.currentTimeMillis()).coerceAtLeast(0L)
                _uiState.update { it.copy(countdown = remaining.toCountdown()) }
                if (remaining == 0L) break
                delay(1_000L)
            }
        }
    }

    /** Header switcher drives the app-wide language (persisted + re-renders i18n screens). */
    fun toggleLanguage() {
        val current = LanguageManager.language.value
        LanguageManager.set(if (current == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)
    }
}

/** Split a millisecond duration into days / hours / minutes (seconds floored). */
internal fun Long.toCountdown(): CountdownState {
    val totalMinutes = this / 60_000L
    val days = (totalMinutes / (24 * 60)).toInt()
    val hours = ((totalMinutes / 60) % 24).toInt()
    val minutes = (totalMinutes % 60).toInt()
    return CountdownState(days, hours, minutes)
}
