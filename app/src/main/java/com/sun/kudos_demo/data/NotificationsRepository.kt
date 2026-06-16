package com.sun.kudos_demo.data

import com.sun.kudos_demo.feature.notifications.AppNotification
import com.sun.kudos_demo.feature.notifications.NotificationsMockData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Single in-memory source of truth for notifications (mock-data only, no push API).
 *
 * App-process singleton shared across the Notifications screen and the Home / Feed top-bar
 * bell badge: marking items read here ([markRead] / [markAllRead]) decrements [unreadCount],
 * which both Home and Feed observe so the bell badge stays in sync (TC_NOTIF_FUN_001/002).
 *
 * Seeded from [NotificationsMockData]. State is not persisted across process death
 * (clarifications.md Session 2026-06-16 — in-memory, no DataStore).
 */
object NotificationsRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _notifications = MutableStateFlow(NotificationsMockData.notifications)

    /** Live notification list — emits whenever read-state changes. */
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    /** Number of unread notifications — drives the top-bar bell badge on Home / Feed. */
    val unreadCount: StateFlow<Int> = _notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = NotificationsMockData.notifications.count { !it.isRead }
        )

    /** Mark a single notification read (tapped item) — its unread dot disappears. */
    fun markRead(id: String) = _notifications.update { list ->
        list.map { if (it.id == id && !it.isRead) it.copy(isRead = true) else it }
    }

    /** Mark every notification read ("Đánh dấu đọc tất cả") — clears all unread dots + badge. */
    fun markAllRead() = _notifications.update { list ->
        list.map { if (it.isRead) it else it.copy(isRead = true) }
    }
}
