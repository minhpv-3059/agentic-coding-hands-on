package com.sun.kudos_demo.feature.notifications.components

import androidx.annotation.DrawableRes
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.notifications.NotificationType

/**
 * Ánh xạ [NotificationType] → drawable icon thật, export trực tiếp từ Figma (SVG → vector drawable).
 * Màu fill đã bake sẵn trong từng vector → render với `tint = Color.Unspecified` để giữ đúng màu design.
 *
 * Figma colors (authoritative):
 *   KUDOS_RECEIVED  → ic_receive_noti  #3F95FF (phong bì xanh dương)
 *   HEART_RECEIVED  → ic_heart_plus    #EE5850 (tim + đỏ san hô)
 *   SECRET_BOX      → ic_gift          #FFEA9E (hộp quà vàng)
 *   LEVEL_UP        → ic_star          #00FBFF (ngôi sao cyan)
 *   CONTENT_HIDDEN  → ic_warning       #FFD900 (tam giác cảnh báo vàng)
 *   BADGE_COLLECTED → ic_notes         #2AD32D (clipboard-check xanh lá)
 *   REVIEW_REQUEST  → ic_flag          #B51097 (cờ tím + x)
 */
@DrawableRes
internal fun notificationIconRes(type: NotificationType): Int = when (type) {
    NotificationType.KUDOS_RECEIVED -> R.drawable.ic_receive_noti
    NotificationType.HEART_RECEIVED -> R.drawable.ic_heart_plus
    NotificationType.SECRET_BOX -> R.drawable.ic_gift
    NotificationType.LEVEL_UP -> R.drawable.ic_star
    NotificationType.CONTENT_HIDDEN -> R.drawable.ic_warning
    NotificationType.BADGE_COLLECTED -> R.drawable.ic_notes
    NotificationType.REVIEW_REQUEST -> R.drawable.ic_flag
}
