package com.sun.kudos_demo.feature.notifications.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.OutlinedFlag
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.sun.kudos_demo.feature.notifications.NotificationType
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosNotiAmber
import com.sun.kudos_demo.ui.theme.KudosNotiBlue
import com.sun.kudos_demo.ui.theme.KudosNotiCyan
import com.sun.kudos_demo.ui.theme.KudosNotiGreen
import com.sun.kudos_demo.ui.theme.KudosNotiMagenta
import com.sun.kudos_demo.ui.theme.KudosNotiPink

/**
 * Ánh xạ NotificationType → (icon vector, tint color).
 *
 * Dùng material-icons-extended (thêm vào build.gradle.kts) để đủ icons.
 * Tint lấy từ frame image visual [iOS] Notifications (_b68CBWKl5).
 * Dễ swap sang real SVG Figma asset khi export sẵn.
 *
 * Figma component IDs để reference khi export:
 *   KUDOS_RECEIVED  → 6885:8273  (phong bì xanh dương)
 *   HEART_RECEIVED  → 6885:8281  (tim hồng)
 *   SECRET_BOX      → 6885:8275  (hộp quà vàng)
 *   LEVEL_UP        → 6885:8277  (ngôi sao cyan)
 *   CONTENT_HIDDEN  → 6885:8279  (tam giác cảnh báo amber)
 *   BADGE_COLLECTED → 6885:8311  (badge xanh lá)
 *   REVIEW_REQUEST  → 6885:8313  (flag tím hồng)
 */
data class NotificationIconSpec(
    val icon: ImageVector,
    val tint: Color
)

internal fun notificationIconSpec(type: NotificationType): NotificationIconSpec = when (type) {
    NotificationType.KUDOS_RECEIVED -> NotificationIconSpec(
        icon = Icons.Outlined.Email,
        tint = KudosNotiBlue        // #4A9EEA — blue envelope
    )
    NotificationType.HEART_RECEIVED -> NotificationIconSpec(
        icon = Icons.Outlined.FavoriteBorder,
        tint = KudosNotiPink        // #E85D75 — pink heart
    )
    NotificationType.SECRET_BOX -> NotificationIconSpec(
        icon = Icons.Outlined.CardGiftcard,
        tint = KudosGold            // #FFEA9E — gold gift box
    )
    NotificationType.LEVEL_UP -> NotificationIconSpec(
        icon = Icons.Outlined.StarOutline,
        tint = KudosNotiCyan        // #4DD9E5 — cyan star
    )
    NotificationType.CONTENT_HIDDEN -> NotificationIconSpec(
        icon = Icons.Outlined.Warning,
        tint = KudosNotiAmber       // #FFB800 — amber warning triangle
    )
    NotificationType.BADGE_COLLECTED -> NotificationIconSpec(
        icon = Icons.Outlined.VerifiedUser,
        tint = KudosNotiGreen       // #4CAF50 — green badge/shield
    )
    NotificationType.REVIEW_REQUEST -> NotificationIconSpec(
        icon = Icons.Outlined.OutlinedFlag,
        tint = KudosNotiMagenta     // #E879A0 — magenta/pink flag
    )
}
