package com.sun.kudos_demo.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.NotificationsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** UI state for the Notifications screen (mock-data driven). */
data class NotificationsUiState(
    val notifications: List<AppNotification> = emptyList()
)

/**
 * State holder for the Notifications screen. Reads the shared [NotificationsRepository]
 * so "mark read" / "mark all read" stay in sync with the Home / Feed bell badge.
 *
 * Screen title is resolved in the composable layer via stringResource(R.string.noti_title)
 * so the runtime VN↔EN switch takes effect without any VM involvement.
 */
class NotificationsViewModel : ViewModel() {

    val uiState: StateFlow<NotificationsUiState> =
        NotificationsRepository.notifications
            .map { list -> NotificationsUiState(notifications = list) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                NotificationsUiState()
            )

    /** Tapped item → mark read (unread dot disappears, badge decrements). */
    fun markRead(id: String) = NotificationsRepository.markRead(id)

    /** "Mark all read" → clear every unread item. */
    fun markAllRead() = NotificationsRepository.markAllRead()
}
