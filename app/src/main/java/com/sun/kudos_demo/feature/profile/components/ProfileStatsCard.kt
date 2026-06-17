package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.profile.ProfileStats
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

private val CardShape = RoundedCornerShape(8.dp)
private val ButtonShape = RoundedCornerShape(4.dp)

/**
 * Stats summary card with gold border and "Mở Secret Box" CTA button.
 *
 * Design: mms_D.1_Thống kê tổng quat (6885:10358)
 *   - Card: bg #00070C, border 0.794dp #998C5F, padding 12dp, radius 8dp
 *   - Stat rows: label Montserrat Light 14sp/20sp white (left), value Bold 14sp/20sp gold (right), gap 12dp
 *   - Divider after 3rd row: 1dp #2E3940
 *   - Button: full-width 40dp, bg #FFEA9E, radius 4dp, label Medium 14sp KudosDarkText
 */
@Composable
fun ProfileStatsCard(
    stats: ProfileStats,
    onOpenSecretBox: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(KudosContainer2)
            .border(0.8.dp, KudosBorder, CardShape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatRow(label = stringResource(R.string.profile_stat_kudos_received), value = stats.kudosReceived.toString())
        StatRow(label = stringResource(R.string.profile_stat_kudos_sent),     value = stats.kudosSent.toString())
        StatRow(label = stringResource(R.string.profile_stat_hearts_received), value = stats.heartsReceived.toString())

        // Divider: mms_D.1.5_phân cách nội dung (6885:10375), 1dp, #2E3940
        HorizontalDivider(thickness = 1.dp, color = KudosDivider)

        StatRow(label = stringResource(R.string.profile_stat_secret_box_opened),   value = stats.secretBoxOpened.toString())
        StatRow(label = stringResource(R.string.profile_stat_secret_box_unopened), value = stats.secretBoxUnopened.toString())

        // Button: mms_Button (6885:10386), 40dp height, bg gold, radius 4dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(ButtonShape)
                .background(KudosGold)
                .clickable(onClick = onOpenSecretBox),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.profile_open_secret_box),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.sp
                ),
                color = KudosDarkText,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Single stat row: label (Light) on the left, bold gold value on the right.
 * Design: each row 20dp tall, label left-aligned, value right-aligned.
 */
@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Light,
                letterSpacing = 0.25.sp
            ),
            color = KudosWhite,
            modifier = Modifier.weight(1f, fill = false)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.25.sp
            ),
            color = KudosGold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun ProfileStatsCardPreview() {
    KudosAppTheme {
        ProfileStatsCard(
            stats = ProfileStats(
                kudosReceived = 5,
                kudosSent = 25,
                heartsReceived = 25,
                secretBoxOpened = 25,
                secretBoxUnopened = 25
            ),
            onOpenSecretBox = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
