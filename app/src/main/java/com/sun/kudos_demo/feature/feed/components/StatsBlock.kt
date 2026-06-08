package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.KudoStats
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

private val BlockShape = RoundedCornerShape(8.dp)

/**
 * Statistics block — design node mms_D.1_Thống kê tổng quát (6885:9223).
 * Shows 5 rows:
 *   1. Số Kudos bạn nhận được
 *   2. Số Kudos bạn đã gửi
 *   3. Số tim bạn nhận được (+ x2 fire badge if fireBonusActive)
 *   --- divider ---
 *   4. Số Secret Box bạn đã mở
 *   5. Số Secret Box chưa mở
 */
@Composable
fun StatsBlock(
    stats: KudoStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(BlockShape)
            .background(KudosContainer)
            .border(1.dp, KudosBorder.copy(alpha = 0.3f), BlockShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatRow(label = "Số Kudos bạn nhận được:", value = stats.received.toString())
        StatRow(label = "Số Kudos bạn đã gửi:", value = stats.sent.toString())
        StatRow(
            label = "Số tim bạn nhận được:",
            value = stats.heartsReceived.toString(),
            trailingBadge = if (stats.fireBonusActive) "x2" else null
        )

        HorizontalDivider(color = KudosDivider, thickness = 1.dp)

        StatRow(label = "Số Secret Box bạn đã mở:", value = stats.secretBoxOpened.toString())
        StatRow(label = "Số Secret Box chưa mở:", value = stats.secretBoxUnopened.toString())
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    trailingBadge: String? = null
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
                style = MaterialTheme.typography.bodySmall,
                color = KudosGray
            )
            if (trailingBadge != null) {
                Text(
                    text = trailingBadge,
                    style = MaterialTheme.typography.labelSmall,
                    color = KudosGold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(KudosGold.copy(alpha = 0.15f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = KudosWhite
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun StatsBlockPreview() {
    KudosAppTheme {
        StatsBlock(
            stats = KudosMockData.stats,
            modifier = Modifier.padding(16.dp)
        )
    }
}
