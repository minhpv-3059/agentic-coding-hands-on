package com.sun.kudos_demo.feature.notifications

import com.sun.kudos_demo.R

/**
 * Seed notifications for the Notifications screen — exactly the 7 items rendered in the
 * design ([iOS] Notifications, screen _b68CBWKl5), one per [NotificationType], in order.
 *
 * Mock-data only (no push integration this phase). Template placeholders from the design
 * (<tên Sunner>, <X>, <tên level>, <tên quà>…) are filled with sensible mock values.
 * Per clarifications.md (Session 2026-06-16) only the first item is unread — a single red
 * dot, matching the design exactly. The unread count drives the Home/Feed top-bar bell badge.
 *
 * [AppNotification.timeRes] holds a @StringRes so the relative-time label switches
 * automatically with the VN↔EN runtime locale.
 */
object NotificationsMockData {

    val notifications: List<AppNotification> = listOf(
        AppNotification(
            id = "n1",
            type = NotificationType.KUDOS_RECEIVED,
            message = "Sunner Huỳnh Dương Xuân Nhật vừa gửi đến bạn lời ghi nhận đầy yêu thương!",
            timeRes = R.string.noti_time_15_min,
            isRead = false,
            targetId = "k7"
        ),
        AppNotification(
            id = "n2",
            type = NotificationType.HEART_RECEIVED,
            message = "Wow! Lời nhắn gửi của bạn cho Sunner Huỳnh Dương Xuân Nhật vừa nhận thêm lượt tim!",
            timeRes = R.string.noti_time_1_hour,
            isRead = true,
            targetId = "k6"
        ),
        AppNotification(
            id = "n3",
            type = NotificationType.SECRET_BOX,
            message = "Chúc mừng! Bạn vừa nhận được lượt mở Secret Box mới! Click vào đây để mở ngay nhé!",
            timeRes = R.string.noti_time_1_day,
            isRead = true
        ),
        AppNotification(
            id = "n4",
            type = NotificationType.LEVEL_UP,
            message = "Bạn nhận được 5 lời nhắn gửi từ đồng nghiệp và thăng hạng Legend Hero!\n" +
                "Tiếp tục lan tỏa năng lượng tích cực đến đồng nghiệp nhé!",
            timeRes = R.string.noti_time_1_day,
            isRead = true
        ),
        AppNotification(
            id = "n5",
            type = NotificationType.CONTENT_HIDDEN,
            message = "Tiếc quá! Bạn có một lời nhắn bị tạm ẩn vì \"vướng\" một số tiêu chuẩn! " +
                "Hãy xem các tiêu chuẩn và gửi lại cho đồng đội nhé!",
            timeRes = R.string.noti_time_1_month,
            isRead = true,
            targetId = "k6"
        ),
        AppNotification(
            id = "n6",
            type = NotificationType.BADGE_COLLECTED,
            message = "Chúc mừng bạn đã thu thập đủ 6 huy hiệu của SAA. Bạn đã nhận được phần quà " +
                "từ BTC chính là Voucher Tiki 500.000đ. BTC sẽ liên hệ để gửi quà đến bạn vào cuối sự kiện.",
            timeRes = R.string.noti_time_1_month,
            isRead = true
        ),
        AppNotification(
            id = "n7",
            type = NotificationType.REVIEW_REQUEST,
            message = "Có 3 lời nhắn cần bạn xem xét! Một lời nhắn vừa bị hệ thống gắn cờ nghi ngờ " +
                "vi phạm tiêu chuẩn. Vui lòng kiểm tra và xác nhận trạng thái: Hợp lệ / Tạm ẩn / Reject.",
            timeRes = R.string.noti_time_1_month,
            isRead = true
        )
    )
}
