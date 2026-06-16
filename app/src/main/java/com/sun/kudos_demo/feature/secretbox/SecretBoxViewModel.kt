package com.sun.kudos_demo.feature.secretbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.SecretBoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** UI state for the single Secret Box screen (mock-data driven). */
data class SecretBoxUiState(
    val phase: SecretBoxPhase = SecretBoxPhase.CLOSED,
    val unopenedCount: Int = 0,
    val reward: SecretBoxReward? = null
) {
    /** No boxes left to open → tapping the box is a no-op (TC_SB_FUN_003). */
    val allOpened: Boolean get() = unopenedCount == 0
}

/**
 * State holder for the Secret Box screen. The unopened count comes from the shared
 * [SecretBoxRepository] (kept in sync with the Profile stats card); the open/reveal phase is
 * local UI state. Flow (clarifications.md Session 2026-06-16 — Phase 09):
 *   CLOSED --onBoxTap(count>0)--> OPENING --onOpenAnimationEnd--> REWARD(random prize)
 *   REWARD --onContinue--> repo.openOne() (count-1) --> CLOSED
 */
class SecretBoxViewModel : ViewModel() {

    private val phase = MutableStateFlow(SecretBoxPhase.CLOSED)
    private val reward = MutableStateFlow<SecretBoxReward?>(null)

    val uiState: StateFlow<SecretBoxUiState> =
        combine(phase, reward, SecretBoxRepository.counts) { p, r, counts ->
            SecretBoxUiState(phase = p, unopenedCount = counts.unopened, reward = r)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SecretBoxUiState(unopenedCount = SecretBoxRepository.counts.value.unopened)
        )

    /** Tap the box → start the open animation, but only when idle and boxes remain. */
    fun onBoxTap() {
        if (phase.value == SecretBoxPhase.CLOSED && SecretBoxRepository.counts.value.unopened > 0) {
            phase.value = SecretBoxPhase.OPENING
        }
    }

    /** Open animation finished → reveal a random prize (TC_SB_FUN_001). */
    fun onOpenAnimationEnd() {
        if (phase.value == SecretBoxPhase.OPENING) {
            reward.value = SecretBoxMockData.randomReward()
            phase.value = SecretBoxPhase.REWARD
        }
    }

    /** "Tiếp tục" → consume the box (count-1) and return to the closed state (TC_SB_FUN_002). */
    fun onContinue() {
        if (phase.value == SecretBoxPhase.REWARD) {
            SecretBoxRepository.openOne()
            reward.value = null
            phase.value = SecretBoxPhase.CLOSED
        }
    }
}
