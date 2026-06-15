package com.sun.kudos_demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.starLevel
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosAccentRed
import com.sun.kudos_demo.ui.theme.KudosCardFaint
import com.sun.kudos_demo.ui.theme.KudosCardMuted
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosPrimaryButtonHover

private val CardShape = RoundedCornerShape(8.dp)
private val CardBackground = KudosPrimaryButtonHover   // #FFF8E1 — from design node 6885:9092
private val HashtagColor = KudosAccentRed              // #D4271D per design (node 6885:10175)

/**
 * Shared Kudos card used by HighlightCarousel (compact=true) and AllKudos list (compact=false).
 *
 * Layout (top→bottom):
 *   - Sender row (avatar + name + code + badge) → arrow → Recipient row (avatar + name + code + star + badge)
 *   - Divider
 *   - timeRange | TITLE | message (truncated) | hashtags
 *   - Divider
 *   - Action row: heartCount + heart icon | Copy Link | Xem chi tiết →
 *
 * Anonymous senders: avatar with anonymous=true, alias as name, "Người gửi ẩn danh" sub-label,
 * no code/badge shown.
 */
@Composable
fun KudosCard(
    kudo: Kudo,
    isLiked: Boolean,
    modifier: Modifier = Modifier,
    canLike: Boolean = true,
    compact: Boolean = false,
    onLike: () -> Unit = {},
    onCopyLink: () -> Unit = {},
    onDetail: () -> Unit = {},
    onSenderClick: (KudoUser) -> Unit = {},
    onRecipientClick: (KudoUser) -> Unit = {},
    onHashtagClick: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(CardBackground)
            .border(1.dp, KudosGold, CardShape)
            .padding(8.dp, 8.dp, 12.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // --- Sender/Recipient row ---
        KudosCardParticipantsRow(
            kudo = kudo,
            onSenderClick = { kudo.sender?.let(onSenderClick) },
            onRecipientClick = { onRecipientClick(kudo.recipient) }
        )

        HorizontalDivider(color = KudosDivider, thickness = 1.dp)

        // --- Content ---
        KudosCardContent(
            kudo = kudo,
            compact = compact,
            onHashtagClick = onHashtagClick
        )

        HorizontalDivider(color = KudosDivider, thickness = 1.dp)

        // --- Action row ---
        KudosCardActions(
            heartCount = kudo.heartCount,
            isLiked = isLiked,
            canLike = canLike,
            onLike = onLike,
            onCopyLink = onCopyLink,
            onDetail = onDetail
        )
    }
}

@Composable
private fun KudosCardParticipantsRow(
    kudo: Kudo,
    onSenderClick: () -> Unit,
    onRecipientClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Sender slot
        KudosParticipantSlot(
            user = kudo.sender,
            isAnonymous = kudo.isAnonymous,
            anonymousAlias = kudo.anonymousAlias,
            modifier = Modifier.weight(1f),
            onClick = onSenderClick
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = null,
            tint = KudosDarkText,
            modifier = Modifier.size(16.dp)
        )

        // Recipient slot
        KudosParticipantSlot(
            user = kudo.recipient,
            isAnonymous = false,
            anonymousAlias = "",
            starLevel = starLevel(kudo.recipientKudosCount),
            modifier = Modifier.weight(1f),
            onClick = onRecipientClick
        )
    }
}

@Composable
private fun KudosParticipantSlot(
    user: KudoUser?,
    isAnonymous: Boolean,
    anonymousAlias: String,
    modifier: Modifier = Modifier,
    starLevel: Int = 0,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        KudoAvatar(
            name = if (isAnonymous) null else user?.name,
            size = 24.dp,
            anonymous = isAnonymous
        )
        Column(modifier = Modifier.weight(1f)) {
            val displayName = if (isAnonymous) anonymousAlias else user?.name.orEmpty()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = KudosDarkText,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (starLevel > 0) {
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "★".repeat(starLevel),
                        style = MaterialTheme.typography.labelSmall,
                        color = KudosGold
                    )
                }
            }
            if (isAnonymous) {
                Text(
                    text = "Người gửi ẩn danh",
                    style = MaterialTheme.typography.labelSmall,
                    color = KudosCardFaint
                )
            } else {
                val code = user?.code.orEmpty()
                val badge = user?.badge
                if (code.isNotEmpty() || badge != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (code.isNotEmpty()) {
                            Text(
                                text = code,
                                style = MaterialTheme.typography.labelSmall,
                                color = KudosCardMuted
                            )
                        }
                        if (badge != null) {
                            KudosBadgeChip(text = badge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KudosBadgeChip(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = KudosDarkText,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(KudosGold)
            .padding(horizontal = 4.dp, vertical = 1.dp)
    )
}

@Composable
private fun KudosCardContent(
    kudo: Kudo,
    compact: Boolean,
    onHashtagClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = kudo.timeRange,
            style = MaterialTheme.typography.labelSmall,
            color = KudosCardFaint
        )
        Text(
            text = kudo.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.23.sp
            ),
            color = KudosDarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = kudo.message,
            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 14.sp),
            color = KudosDarkText,
            textAlign = TextAlign.Justify,
            maxLines = if (compact) 3 else 5,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        if (kudo.hashtags.isNotEmpty()) {
            KudosHashtagRow(hashtags = kudo.hashtags, onHashtagClick = onHashtagClick)
        }
    }
}

@Composable
private fun KudosHashtagRow(hashtags: List<String>, onHashtagClick: (String) -> Unit) {
    val display = if (hashtags.size > 5) hashtags.take(5) else hashtags
    val showEllipsis = hashtags.size > 5
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        display.forEach { tag ->
            Text(
                text = "#$tag",
                style = MaterialTheme.typography.labelSmall,
                color = HashtagColor,
                modifier = Modifier.clickable { onHashtagClick(tag) }
            )
        }
        if (showEllipsis) {
            Text("…", style = MaterialTheme.typography.labelSmall, color = HashtagColor)
        }
    }
}

@Composable
private fun KudosCardActions(
    heartCount: Int,
    isLiked: Boolean,
    canLike: Boolean,
    onLike: () -> Unit,
    onCopyLink: () -> Unit,
    onDetail: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Heart button
        Row(
            modifier = Modifier
                .alpha(if (canLike) 1f else 0.4f)
                .clickable(enabled = canLike, onClick = onLike),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = formatHeartCount(heartCount),
                style = MaterialTheme.typography.labelMedium,
                color = KudosDarkText
            )
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isLiked) "Đã thích" else "Thích",
                tint = if (isLiked) KudosAccentRed else KudosCardFaint,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        // Copy Link
        Text(
            text = "Copy Link",
            style = MaterialTheme.typography.labelMedium,
            color = KudosCardMuted,
            modifier = Modifier.clickable(onClick = onCopyLink)
        )

        // Xem chi tiết
        Row(
            modifier = Modifier.clickable(onClick = onDetail),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Xem chi tiết",
                style = MaterialTheme.typography.labelMedium,
                color = KudosDarkText
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = KudosDarkText,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

private fun formatHeartCount(count: Int): String =
    if (count >= 1000) {
        val thousands = count / 1000
        val remainder = count % 1000
        if (remainder == 0) "$thousands.000" else "$thousands.${remainder.toString().padStart(3, '0')}"
    } else count.toString()

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudosCardCompactPreview() {
    KudosAppTheme {
        KudosCard(
            kudo = KudosMockData.kudos.first(),
            isLiked = false,
            compact = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudosCardExpandedPreview() {
    KudosAppTheme {
        KudosCard(
            kudo = KudosMockData.kudos[3], // anonymous kudo
            isLiked = true,
            compact = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}
