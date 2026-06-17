package com.sun.kudos_demo.feature.home.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * mms_3_note — theme description paragraph.
 *
 * Exact text from Figma node 6885:9029 (mms_3_note → txt):
 * 14sp, fontWeight 300 (Light), white, letterSpacing 0.25px, lineHeight 20px.
 * Width 333px in design (horizontal padding handled by caller).
 */
@Composable
fun HomeNoteSection(modifier: Modifier = Modifier) {
    Text(
        text = "Không đơn thuần là một cái tên, \"Root Further\" chính là tinh thần mà mỗi " +
            "người Sun* đang hướng tới: luôn nhìn nhận sâu sắc trong mọi bối cảnh và không " +
            "ngừng sáng tạo, mở rộng bản thân để vượt qua những giới hạn mà chính mình đã " +
            "từng đặt ra. Mượn hình ảnh ẩn dụ của lý thuyết phối màu, chỉ từ ba màu cơ bản: " +
            "đỏ, vàng và lam, sức sáng tạo vô tận của mỗi cá nhân có thể tạo ra số lượng màu " +
            "sắc gần như vô hạn, với mỗi gam màu đều đại diện cho sự bứt phá và sáng tạo " +
            "không giới hạn.",
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
        color = KudosWhite,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HomeNoteSectionPreview() {
    KudosAppTheme {
        HomeNoteSection()
    }
}
