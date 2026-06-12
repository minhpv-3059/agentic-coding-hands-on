package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

// Violation criteria — sourced verbatim from spec node 6885:10848 description
private val VIOLATION_CRITERIA = listOf(
    "Sử dụng từ ngữ thô tục, chửi bậy, hay có nội dung xúc phạm, bôi nhọ.",
    "Đề cập đến các vấn đề chính trị, tôn giáo, phân biệt giới tính.",
    "Chứa số liệu cụ thể (doanh thu, hợp đồng, KPI, khách hàng, mã dự án, số tài khoản…).",
    "Đề cập tên đối tác, khách hàng, tổ chức bên ngoài.",
    "Chứa thông tin cá nhân (email, số điện thoại, địa chỉ, thông tin gia đình).",
    "Gửi lặp lại 3+ tin nhắn có nội dung tương tự nhau trong thời gian ngắn.",
    "Nội dung Kudos quá ngắn (dưới 30 kí tự), không có ngữ cảnh (\"Cảm ơn nhiều\", \"Thanks nhé\", \"Good job!\").",
    "Gửi cho quá nhiều người/nhóm người trong thời gian ngắn (<3s/lời nhắn).",
    "Ngôn từ spam (chỉ chứa ký tự như \".\", \",\", \"...\", hay ký tự không có nội dung).",
    "Mức độ \"tim\" tăng đột biến bất thường (theo hành vi người dùng trung bình)."
)

/**
 * Section B — Tiêu chuẩn cộng đồng.
 * Spec node 6885:10848. Static content, sourced verbatim from design.
 */
@Composable
internal fun CommunityStandardsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Tiêu chuẩn cộng đồng",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = KudosGold
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Tiêu chuẩn Cộng đồng (Community Standards) được xây dựng nhằm đảm bảo " +
                "một môi trường văn minh, an toàn và tích cực cho tất cả thành viên tham gia " +
                "phong trào ghi nhận, cảm ơn Sun* Kudos.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = KudosGold
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Các nội dung phát hiện có một trong những tiêu chí vi phạm bên dưới sẽ " +
                "được gắn nhãn Spam và được hệ thống chủ động ẩn.",
            style = MaterialTheme.typography.bodyMedium,
            color = KudosWhite
        )
        Spacer(Modifier.height(12.dp))
        VIOLATION_CRITERIA.forEachIndexed { index, criterion ->
            ViolationItem(number = index + 1, text = criterion)
            if (index < VIOLATION_CRITERIA.lastIndex) Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ViolationItem(number: Int, text: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = KudosGold, fontWeight = FontWeight.SemiBold)) {
                append("$number. ")
            }
            withStyle(SpanStyle(color = KudosWhite)) {
                append(text)
            }
        },
        style = MaterialTheme.typography.bodyMedium
    )
}

/**
 * Section C — Tiêu chuẩn bảo mật.
 * Spec node 6885:10854. Static content, sourced verbatim from design.
 */
@Composable
internal fun SecurityStandardsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Tiêu chuẩn bảo mật",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = KudosGold
        )
        Spacer(Modifier.height(16.dp))
        // Main description (node 6885:10857) — first block
        Text(
            text = "Sunner cam kết bảo vệ thông tin. Mọi thành viên có trách nhiệm bảo mật " +
                "nội dung chia sẻ trên hệ thống.",
            style = MaterialTheme.typography.bodyMedium,
            color = KudosWhite
        )
        Spacer(Modifier.height(8.dp))
        SecuritySubItem(
            label = "Bảo mật Thông tin",
            body = "Toàn bộ thông tin Sunner chia sẻ sẽ được bảo mật trên hệ thống."
        )
        Spacer(Modifier.height(8.dp))
        SecuritySubItem(
            label = "Phạm vi Chia sẻ",
            body = "Toàn bộ thông tin nhân sự và dự án trong hệ thống được bảo mật. " +
                "Sunner vui lòng chỉ chia sẻ trong nội bộ Sun*."
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = KudosDivider, thickness = 1.dp)
        Spacer(Modifier.height(16.dp))
        // Liên hệ Hỗ trợ block (node 6885:10859)
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = KudosGold, fontWeight = FontWeight.Bold)) {
                    append("Liên hệ Hỗ trợ: ")
                }
                withStyle(SpanStyle(color = KudosWhite)) {
                    append(
                        "Mọi thắc mắc, Sunner vui lòng liên hệ đại diện BTC SAA: " +
                            "Slack duong.thi.thuy.an để được hỗ trợ."
                    )
                }
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SecuritySubItem(label: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = KudosDivider,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = KudosGold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = KudosWhite
        )
    }
}
