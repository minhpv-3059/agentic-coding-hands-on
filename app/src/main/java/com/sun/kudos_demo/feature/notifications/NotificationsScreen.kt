package com.sun.kudos_demo.feature.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.notifications.components.MarkAllReadButton
import com.sun.kudos_demo.feature.notifications.components.NotificationItem
import com.sun.kudos_demo.feature.notifications.components.NotificationsTopBar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground

/**
 * Presentational Notifications screen — pixel-perfect to design [iOS] Notifications (_b68CBWKl5).
 *
 * Layout (top → bottom):
 *   1. bg_home_keyvisual full-bleed key-visual (Box + Image fillMaxSize, ContentScale.Crop)
 *   2. NotificationsTopBar — detail style: back left + title centred, no trailing icons
 *   3. MarkAllReadButton — left-aligned 20dp, height 40dp
 *   4. Notification list container — 20dp horizontal margins, rounded 8dp,
 *      background rgba(0,7,12,0.60) = KudosContainer2 @ 60% alpha, scrollable
 *
 * KHÔNG có bottom nav (detail flow). Mọi state đi vào qua params, event ra qua lambda.
 *
 * @param title           tiêu đề top bar (localized: "Thông báo" VN / "Notifications" EN)
 * @param notifications   danh sách 7 item, theo thứ tự từ design
 * @param onBack          back arrow tap
 * @param onItemClick     tap lên bất kỳ notification item
 * @param onMarkAllRead   tap "Đánh dấu đọc tất cả"
 * @param onStandardsLink tap inline link "Tiêu chuẩn cộng đồng ↗" (chỉ CONTENT_HIDDEN)
 */
@Composable
fun NotificationsScreen(
    title: String,
    notifications: List<AppNotification>,
    onBack: () -> Unit,
    onItemClick: (AppNotification) -> Unit,
    onMarkAllRead: () -> Unit,
    onStandardsLink: (AppNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    // Scaffold: key-visual background toàn màn hình, giống MyProfileScreen
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        // --- Key-visual background (full-bleed, crop) ---
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- Foreground content ---
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top bar — hấp thụ status bar inset qua statusBarsPadding() bên trong
            NotificationsTopBar(
                title = title,
                onBack = onBack
            )

            // 2. "Đánh dấu đọc tất cả" — left 20dp, height 40dp
            //    Design node 6885:9392: startX=20, height=40dp
            MarkAllReadButton(
                onClick = onMarkAllRead,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp)
            )

            // 3. Notification list — cuộn dọc nếu dài hơn viewport
            //    Container 335dp (20dp margins mỗi bên), radius 8dp,
            //    background rgba(0,7,12,0.60) = KudosContainer2 @ alpha 0.60
            // weight(1f) cấp viewport có giới hạn cho verticalScroll (nếu thiếu, child được đo
            // với ràng buộc cao vô hạn → không bao giờ cuộn, clip trên màn nhỏ). Ref: KudosSearchScreen.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF00070C).copy(alpha = 0.60f))
                    .verticalScroll(rememberScrollState())
            ) {
                notifications.forEachIndexed { index, notification ->
                    NotificationItem(
                        notification = notification,
                        isLast = index == notifications.lastIndex,
                        onClick = { onItemClick(notification) },
                        // Tap link "Tiêu chuẩn cộng đồng" cũng phải mark-read item (event của
                        // Text.clickable nuốt mất Row.onClick) → truyền notification ra route.
                        onStandardsLink = { onStandardsLink(notification) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF00101A,
    device = "id:pixel_5",
    name = "Notifications Screen"
)
@Composable
private fun NotificationsScreenPreview() {
    // Reuse the shared seed (DRY) — same 7 items the route injects at runtime.
    KudosAppTheme {
        NotificationsScreen(
            title = "Thông báo",
            notifications = NotificationsMockData.notifications,
            onBack = {},
            onItemClick = {},
            onMarkAllRead = {},
            onStandardsLink = {}
        )
    }
}
