package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.GiftRecipient
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoStats
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.home.components.SectionHeader
import com.sun.kudos_demo.ui.components.KudosCard
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold

/**
 * "ALL KUDOS" section of the Feed screen — design node mms_C_All kudos (6885:9220).
 * Contains: SectionHeader → StatsBlock → Open Secret Box button →
 * GiftRecipientsSection → list of KudosCards → "View all Kudos →" link.
 */
@Composable
fun AllKudosSection(
    kudos: List<Kudo>,
    stats: KudoStats,
    giftRecipients: List<GiftRecipient>,
    likedKudoIds: Set<String>,
    currentUserId: String = "",
    modifier: Modifier = Modifier,
    onOpenSecretBox: () -> Unit = {},
    onViewAllKudos: () -> Unit = {},
    onToggleLike: (String) -> Unit = {},
    onCopyLink: (String) -> Unit = {},
    onKudoDetail: (String) -> Unit = {},
    onSenderClick: (KudoUser) -> Unit = {},
    onRecipientClick: (KudoUser) -> Unit = {},
    onHashtagClick: (String) -> Unit = {},
    onGiftRecipientClick: (GiftRecipient) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            eyebrow = "Sun* Annual Awards 2025",
            title = "ALL KUDOS"
        )

        StatsBlock(
            stats = stats,
            onOpenSecretBox = onOpenSecretBox
        )

        GiftRecipientsSection(
            recipients = giftRecipients,
            onRecipientClick = onGiftRecipientClick
        )

        // Kudos list
        if (kudos.isEmpty()) {
            EmptyKudosHint()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                kudos.forEach { kudo ->
                    KudosCard(
                        kudo = kudo,
                        isLiked = kudo.id in likedKudoIds,
                        canLike = kudo.sender?.id != currentUserId,
                        compact = false,
                        onLike = { onToggleLike(kudo.id) },
                        onCopyLink = { onCopyLink(kudo.id) },
                        onDetail = { onKudoDetail(kudo.id) },
                        onSenderClick = onSenderClick,
                        onRecipientClick = onRecipientClick,
                        onHashtagClick = onHashtagClick
                    )
                }
            }
        }

        // "View all Kudos →" link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onViewAllKudos)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "View all Kudos",
                style = MaterialTheme.typography.labelLarge,
                color = KudosGold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = KudosGold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun AllKudosSectionPreview() {
    KudosAppTheme {
        AllKudosSection(
            kudos = KudosMockData.kudos.take(3),
            stats = KudosMockData.stats,
            giftRecipients = KudosMockData.giftRecipients.take(3),
            likedKudoIds = setOf("k1"),
            modifier = Modifier.padding(16.dp)
        )
    }
}
