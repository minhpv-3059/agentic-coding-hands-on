package com.sun.kudos_demo.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.components.KudosSecondaryButton
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Kudos section: header ("Phong trào ghi nhận" / "Sun* Kudos")
 * + banner image (img_kudos_banner, 335×145 dp)
 * + note ("ĐIỂM MỚI CỦA SAA 2025" heading + body — exact Figma node 6885:9054)
 * + "Chi tiết" secondary button.
 * Design: mms_5_kudos (6885:9039).
 */
@Composable
fun HomeKudosSection(
    onKudosDetail: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SectionHeader(
            eyebrow = "Phong trào ghi nhận",
            title = "Sun* Kudos"
        )

        // Kudos banner — real Figma asset (335×145), scales to width keeping aspect
        Image(
            painter = painterResource(R.drawable.img_kudos_banner),
            contentDescription = "Sun* Kudos",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        )

        // Note — exact text from node 6885:9054; first line acts as a gold sub-heading
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "ĐIỂM MỚI CỦA SAA 2025",
                style = MaterialTheme.typography.labelLarge,
                color = KudosGold
            )
            Text(
                text = "Hoạt động ghi nhận và cảm ơn đồng nghiệp - lần đầu tiên được diễn ra " +
                    "dành cho tất cả Sunner. Hoạt động sẽ được triển khai vào tháng 11/2025, " +
                    "khuyến khích người Sun* chia sẻ những lời ghi nhận, cảm ơn đồng nghiệp " +
                    "trên hệ thống do BTC công bố. Đây sẽ là chất liệu để Hội đồng Heads tham " +
                    "khảo trong quá trình lựa chọn người đạt giải.",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                color = KudosWhite
            )
        }

        // "Chi tiết ↗" secondary button — mms_5.3_Button
        KudosSecondaryButton(
            text = "Chi tiết",
            onClick = onKudosDetail,
            showExternalIcon = true,
            modifier = Modifier.width(120.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HomeKudosSectionPreview() {
    KudosAppTheme {
        HomeKudosSection()
    }
}
