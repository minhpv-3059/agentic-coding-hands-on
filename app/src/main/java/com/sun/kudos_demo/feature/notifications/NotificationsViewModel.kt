package com.sun.kudos_demo.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.NotificationsRepository
import com.sun.kudos_demo.feature.auth.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** UI state for the Notifications screen (mock-data driven). */
data class NotificationsUiState(
    val title: String = "Thông báo",
    val notifications: List<AppNotification> = emptyList()
)

/**
 * State holder for the Notifications screen. Reads the shared [NotificationsRepository]
 * so "mark read" / "mark all read" stay in sync with the Home / Feed bell badge.
 *
 * Title is localised by the current language (clarifications.md Session 2026-06-16):
 * VN → "Thông báo", EN → "Notifications". Defaults to VN; this detail screen has no
 * language switcher of its own (real localisation lands in Phase 11).
 */
class NotificationsViewModel : ViewModel() {

    private val language = MutableStateFlow(AppLanguage.VN)

    val uiState: StateFlow<NotificationsUiState> =
        combine(NotificationsRepository.notifications, language) { list, lang ->
            NotificationsUiState(title = titleFor(lang), notifications = list)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            NotificationsUiState()
        )

    /** Tapped item → mark read (unread dot disappears, badge decrements). */
    fun markRead(id: String) = NotificationsRepository.markRead(id)

    /** "Đánh dấu đọc tất cả" → clear every unread item. */
    fun markAllRead() = NotificationsRepository.markAllRead()
}

/** Localised screen title — VN → "Thông báo", EN → "Notifications" (clarifications 2026-06-16). */
internal fun titleFor(language: AppLanguage): String = when (language) {
    AppLanguage.VN -> "Thông báo"
    AppLanguage.EN -> "Notifications"
}
