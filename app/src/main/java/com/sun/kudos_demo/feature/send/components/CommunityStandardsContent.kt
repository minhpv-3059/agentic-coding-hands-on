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
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

// Violation criteria — sourced verbatim from spec node 6885:10852 (design node text)
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

// Design typography constants (from node queries)
// Titles (6885:10849, 6885:10855): fontSize=18px, fontWeight=700, lineHeight=24px
// Bold intro (6885:10851): fontSize=14px, fontWeight=700, lineHeight=20px, letterSpacing=0.25px
// Body text (6885:10852, 6885:10857): fontSize=14px, fontWeight=400, lineHeight=20px, letterSpacing=0.25px
// Contact (6885:10859): fontSize=14px, fontWeight=700, lineHeight=20px, letterSpacing=0.25px

/**
 * Section B — Tiêu chuẩn cộng đồng.
 * Spec node 6885:10848. Static content, sourced verbatim from design.
 *
 * Issue 4 fix: typography aligned to design node values:
 *   - Section title: 18sp/Bold, KudosGold
 *   - Intro paragraph: 14sp/Bold, KudosGold (node 6885:10851)
 *   - Criteria intro: 14sp/Regular, KudosWhite (node 6885:10852 first line)
 *   - Numbered criteria: 14sp, gold number + white text, gap=16dp between items
 */
@Composable
internal fun CommunityStandardsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Title — node 6885:10849: 18sp/Bold, gold
        Text(
            text = "Tiêu chuẩn cộng đồng",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            color = KudosGold
        )
        Spacer(Modifier.height(16.dp))
        // Intro bold paragraph — node 6885:10851: 14sp/Bold, gold, letterSpacing=0.25sp
        Text(
            text = "Tiêu chuẩn Cộng đồng (Community Standards) được xây dựng nhằm đảm bảo " +
                "một môi trường văn minh, an toàn và tích cực cho tất cả thành viên tham gia " +
                "phong trào ghi nhận, cảm ơn Sun* Kudos.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = KudosGold
        )
        Spacer(Modifier.height(16.dp))
        // Criteria intro — node 6885:10852: 14sp/Regular, white
        Text(
            text = "Các nội dung phát hiện có một trong những tiêu chí vi phạm bên dưới sẽ " +
                "được gắn nhãn Spam và được hệ thống chủ động ẩn.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = KudosWhite
        )
        Spacer(Modifier.height(16.dp))
        // Violation criteria list — gap 16dp between items per design (node 6885:10850: gap=16dp)
        VIOLATION_CRITERIA.forEachIndexed { index, criterion ->
            ViolationItem(number = index + 1, text = criterion)
            if (index < VIOLATION_CRITERIA.lastIndex) Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ViolationItem(number: Int, text: String) {
    // Node 6885:10852 body: 14sp/Regular, white; gold number prefix Bold
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = KudosGold, fontWeight = FontWeight.Bold)) {
                append("$number. ")
            }
            withStyle(SpanStyle(color = KudosWhite, fontWeight = FontWeight.Normal)) {
                append(text)
            }
        },
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
}

/**
 * Section C — Tiêu chuẩn bảo mật.
 * Spec node 6885:10854. Static content, sourced verbatim from design.
 *
 * Issue 4 fix: typography aligned to design node values — same scale as Section B.
 * Security sub-items use a darker container (KudosDivider) with 8dp radius, padding 12/10.
 */
@Composable
internal fun SecurityStandardsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Title — node 6885:10855: 18sp/Bold, gold
        Text(
            text = "Tiêu chuẩn bảo mật",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            color = KudosGold
        )
        Spacer(Modifier.height(16.dp))
        // Intro — node 6885:10857 first sentence: 14sp/Regular, white
        Text(
            text = "Sunner cam kết bảo vệ thông tin. Mọi thành viên có trách nhiệm bảo mật " +
                "nội dung chia sẻ trên hệ thống.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = KudosWhite
        )
        Spacer(Modifier.height(16.dp))
        // Infor frame (node 6885:10856): gap=4dp between sub-items
        SecuritySubItem(
            label = "Bảo mật Thông tin",
            body = "Toàn bộ thông tin Sunner chia sẻ sẽ được bảo mật trên hệ thống."
        )
        Spacer(Modifier.height(4.dp))
        SecuritySubItem(
            label = "Phạm vi Chia sẻ",
            body = "Toàn bộ thông tin nhân sự và dự án trong hệ thống được bảo mật. " +
                "Sunner vui lòng chỉ chia sẻ trong nội bộ Sun*."
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = KudosDivider, thickness = 1.dp)
        Spacer(Modifier.height(16.dp))
        // Liên hệ Hỗ trợ block — node 6885:10859: 14sp/Bold, gold label + white body
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = KudosGold, fontWeight = FontWeight.Bold)) {
                    append("Liên hệ Hỗ trợ: ")
                }
                withStyle(SpanStyle(color = KudosWhite, fontWeight = FontWeight.Normal)) {
                    append(
                        "Mọi thắc mắc, Sunner vui lòng liên hệ đại diện BTC SAA: " +
                            "Slack duong.thi.thuy.an để được hỗ trợ."
                    )
                }
            },
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
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
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
            color = KudosGold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = body,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = KudosWhite
        )
    }
}
