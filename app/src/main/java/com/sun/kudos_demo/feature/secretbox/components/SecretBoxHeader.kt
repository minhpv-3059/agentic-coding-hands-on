package com.sun.kudos_demo.feature.secretbox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.secretbox.SecretBoxPhase
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Phần header đổi theo phase của màn Secret Box.
 *
 * CLOSED / OPENING:
 *  - Title: "KHÁM PHÁ SECRET BOX CỦA BẠN" — Montserrat 700 18/24 #FFEA9E (node 6885:9437)
 *  - Divider mảnh — #2E3940 (node 6885:9438)
 *  - Subtitle: "Click vào box để mở" hoặc "Bạn đã mở hết Secret Box" khi allOpened
 *    — Montserrat 500 14/20 #FFFFFF (node 6885:9440)
 *
 * REWARD:
 *  - Heading: "Chúc mừng bạn đã nhận được phần quà từ BTC SAA 2025"
 *    — Montserrat 700 18/24 #FFEA9E, 2 dòng (node 6885:9659)
 *  - Divider — #2E3940 (node 6885:9660); không có subtitle
 */
@Composable
fun SecretBoxHeader(
    phase: SecretBoxPhase,
    allOpened: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (phase) {
            SecretBoxPhase.CLOSED, SecretBoxPhase.OPENING -> ClosedHeader(allOpened)
            SecretBoxPhase.REWARD -> RewardHeader()
        }
    }
}

@Composable
private fun ClosedHeader(allOpened: Boolean) {
    // Title — Montserrat Bold 18sp/24sp gold (node 6885:9437)
    Text(
        text = stringResource(R.string.sb_header_title),
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        color = KudosGold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )

    Spacer(Modifier.height(8.dp))

    // Divider mảnh — #2E3940 (node 6885:9438, height=0 → render 1dp trên Android)
    HorizontalDivider()

    Spacer(Modifier.height(8.dp))

    // Subtitle — Montserrat Medium 14sp/20sp white (node 6885:9440)
    val subtitle = if (allOpened) stringResource(R.string.sb_subtitle_all_opened)
                   else stringResource(R.string.sb_subtitle_tap)
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),
        color = KudosWhite,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )
}

@Composable
private fun RewardHeader() {
    // Heading chúc mừng — Montserrat Bold 18sp/24sp gold (node 6885:9659, 2 dòng, height=48dp)
    Text(
        text = stringResource(R.string.sb_reward_heading),
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        color = KudosGold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )

    Spacer(Modifier.height(8.dp))

    // Divider — #2E3940 (node 6885:9660)
    HorizontalDivider()
    // Không có subtitle trong REWARD header
}

/** Đường kẻ ngang dùng chung trong header — màu #2E3940, 1dp */
@Composable
private fun HorizontalDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF2E3940))
    )
}

/** Counter row — "Secret box chưa mở" + số hai chữ số vàng (node 6885:9445) */
@Composable
fun SecretBoxCounter(
    unopenedCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        // Label — Montserrat 400 12sp/16sp white (node 6885:9446)
        Text(
            text = stringResource(R.string.sb_counter_label),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp
            ),
            color = KudosWhite
        )

        Spacer(Modifier.width(5.dp))

        // Số — Montserrat Bold 18sp/24sp gold (node 6885:9447)
        Text(
            text = unopenedCount.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp
            ),
            color = KudosGold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, name = "Header — CLOSED")
@Composable
private fun PreviewClosedHeader() {
    KudosAppTheme { SecretBoxHeader(SecretBoxPhase.CLOSED, allOpened = false) }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, name = "Header — REWARD")
@Composable
private fun PreviewRewardHeader() {
    KudosAppTheme { SecretBoxHeader(SecretBoxPhase.REWARD, allOpened = false) }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, name = "Counter")
@Composable
private fun PreviewCounter() {
    KudosAppTheme { SecretBoxCounter(unopenedCount = 5) }
}
