package com.sun.kudos_demo.feature.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.notifications.AppNotification
import com.sun.kudos_demo.feature.notifications.NotificationType
import com.sun.kudos_demo.ui.theme.KudosAccentRed
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Một dòng notification trong danh sách — design node 6885:9394 (mms_B.1_Noti).
 *
 * Layout:
 *   [Icon 24dp] ←16dp gap→ [Column: message + time] [Unread dot 8dp, nếu !isRead]
 *
 * Padding: 8dp tất cả phía. Border-bottom 1dp KudosDivider giữa các item;
 * item cuối KHÔNG có border (isLast=true).
 *
 * Unread (B.1.3): 8×8dp ellipse màu KudosAccentRed (#D4271D), căn phải trên.
 * Message text: isRead=false → fontWeight Bold (700) / white; isRead=true → Normal (400) / white.
 * Time text: bodySmall, màu KudosGray (#999999).
 *
 * CONTENT_HIDDEN đặc biệt: thêm inline link "Tiêu chuẩn cộng đồng ↗" bên dưới message.
 */
@Composable
fun NotificationItem(
    notification: AppNotification,
    isLast: Boolean,
    onClick: () -> Unit,
    onStandardsLink: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconRes = notificationIconRes(notification.type)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // --- Icon 24×24dp — Figma SVG export; màu bake sẵn nên tint = Unspecified ---
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(16.dp))

            // --- Content column: message + optional inline link + time ---
            Column(modifier = Modifier.weight(1f)) {
                // Message — unread: Bold 700, read: Normal 400; cả hai màu white
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        letterSpacing = 0.25.sp
                    ),
                    color = KudosWhite
                )

                // Inline link chỉ cho CONTENT_HIDDEN — design node I6885:9398;128:3467
                if (notification.type == NotificationType.CONTENT_HIDDEN) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.noti_standards_link),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.sp
                        ),
                        color = KudosWhite,
                        modifier = Modifier.clickable(onClick = onStandardsLink)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Time — bodySmall 12sp, màu KudosGray (#999999)
                Text(
                    text = stringResource(notification.timeRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = KudosGray
                )
            }

            // --- Unread dot (B.1.3) — 8×8dp đỏ, chỉ khi !isRead ---
            if (!notification.isRead) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(KudosAccentRed)
                )
            }
        }

        // Border-bottom 1dp KudosDivider — bỏ qua item cuối
        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(KudosDivider)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00070C)
@Composable
private fun NotificationItemUnreadPreview() {
    KudosAppTheme {
        NotificationItem(
            notification = AppNotification(
                id = "n1",
                type = NotificationType.KUDOS_RECEIVED,
                message = "Sunner Huỳnh Dương Xuân Nhật vừa gửi đến bạn lời ghi nhận đầy yêu thương!",
                timeRes = R.string.noti_time_15_min,
                isRead = false
            ),
            isLast = false,
            onClick = {},
            onStandardsLink = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00070C)
@Composable
private fun NotificationItemContentHiddenPreview() {
    KudosAppTheme {
        NotificationItem(
            notification = AppNotification(
                id = "n5",
                type = NotificationType.CONTENT_HIDDEN,
                message = "Tiếc quá! Bạn có một lời nhắn bị tạm ẩn vì \"vướng\" một số tiêu chuẩn! Hãy xem các tiêu chuẩn và gửi lại cho đồng đội nhé!",
                timeRes = R.string.noti_time_1_month,
                isRead = true
            ),
            isLast = true,
            onClick = {},
            onStandardsLink = {}
        )
    }
}
