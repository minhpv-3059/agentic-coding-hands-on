package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.KudoStats
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

private val BlockShape = RoundedCornerShape(8.dp)
private val SecretBoxButtonShape = RoundedCornerShape(4.dp)

/**
 * Statistics block — design node mms_D.1_Thống kê tổng quát (6885:9223).
 * 5 stat rows (the "Số tim" row shows a fire + x2 badge when fireBonusActive),
 * then the "Mở Secret Box 🎁" solid-gold button inside the same bordered panel.
 */
@Composable
fun StatsBlock(
    stats: KudoStats,
    onOpenSecretBox: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(BlockShape)
            .background(KudosContainer2)
            .border(1.dp, KudosBorder, BlockShape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatRow(label = "Số Kudos bạn nhận được:", value = stats.received.toString())
        StatRow(label = "Số Kudos bạn đã gửi:", value = stats.sent.toString())
        StatRow(
            label = "Số tim bạn nhận được:",
            value = stats.heartsReceived.toString(),
            showFireBadge = stats.fireBonusActive
        )

        HorizontalDivider(color = KudosDivider, thickness = 1.dp)

        StatRow(label = "Số Secret Box bạn đã mở:", value = stats.secretBoxOpened.toString())
        StatRow(label = "Số Secret Box chưa mở:", value = stats.secretBoxUnopened.toString())

        Button(
            onClick = onOpenSecretBox,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = SecretBoxButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = KudosGold,
                contentColor = KudosDarkText
            )
        ) {
            Text(
                text = "Mở Secret Box 🎁",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.25.sp
                )
            )
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    showFireBadge: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                color = KudosWhite
            )
            if (showFireBadge) {
                // Fire icon with the "x2" multiplier overlaid (design mms_D.1.4).
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(26.dp)) {
                    Image(
                        painter = painterResource(R.drawable.ic_fire),
                        contentDescription = "x2 tim",
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "x2",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = KudosWhite
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.25.sp
            ),
            color = KudosGold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun StatsBlockPreview() {
    KudosAppTheme {
        StatsBlock(
            stats = KudosMockData.stats,
            onOpenSecretBox = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
