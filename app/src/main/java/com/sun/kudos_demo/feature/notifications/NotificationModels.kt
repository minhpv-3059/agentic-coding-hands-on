package com.sun.kudos_demo.feature.notifications

import androidx.annotation.StringRes

/**
 * The 7 notification categories in the Sun*Kudos system.
 * Design [iOS] Notifications (screen _b68CBWKl5), spec item B.1.
 *
 * Each type drives its own icon + tint (mapped in the UI layer) and its own
 * navigation target (mapped in the route layer) — see clarifications.md
 * "Session 2026-06-16 (Phase 08 — Notifications)".
 */
enum class NotificationType {
    KUDOS_RECEIVED,   // ✉️  envelope (blue)   → open the received kudo
    HEART_RECEIVED,   // ❤️  heart (pink)      → open the reacted kudo
    SECRET_BOX,       // 🎁  gift (green)      → Open Secret Box (Phase 09 placeholder)
    LEVEL_UP,         // ⭐  star (cyan)       → my Profile
    CONTENT_HIDDEN,   // ⚠️  warning (gold)    → open the hidden kudo (+ inline standards link)
    BADGE_COLLECTED,  // 🛡️  badge (green)     → my Profile
    REVIEW_REQUEST    // 🚩  review (purple)   → Admin review (out of app scope)
}

/**
 * One notification list item. Mock-data only for this phase (no push integration).
 *
 * @param id        stable list key
 * @param type      drives icon, tint and tap navigation
 * @param message   full display text — design templates already filled with mock values
 * @param timeRes   string resource for the relative-time label (e.g. R.string.noti_time_15_min)
 *                  resolved at the composable layer so VN↔EN runtime switch works
 * @param isRead    false → the red unread dot (B.1.3) is shown and the text is emphasised
 * @param targetId  kudo id to open for KUDOS_RECEIVED / HEART_RECEIVED / CONTENT_HIDDEN
 */
data class AppNotification(
    val id: String,
    val type: NotificationType,
    val message: String,
    @StringRes val timeRes: Int,
    val isRead: Boolean,
    val targetId: String? = null
)
