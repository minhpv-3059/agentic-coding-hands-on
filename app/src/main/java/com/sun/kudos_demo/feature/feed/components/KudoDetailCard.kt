package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.starLevel
import com.sun.kudos_demo.ui.components.KudoAvatar
import com.sun.kudos_demo.ui.theme.KudosAccentRed
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosPrimaryButtonHover

// Design token: card background #FFF8E1, border KudosBorder (#998C5F), radius 8 dp
private val CardBackground = KudosPrimaryButtonHover   // #FFF8E1
private val CardBorderShape = RoundedCornerShape(8.dp)
private val HashtagColor = KudosAccentRed       // #D4271D node 6885:10175
private val TitleColor = KudosDarkText          // node 6885:10156: rgba(0,16,26,1) on gold bg
private val HeartActiveColor = KudosAccentRed

/**
 * The main card on the View Kudo detail screen.
 * Handles both normal (kudo.sender != null) and anonymous (kudo.isAnonymous == true) variants.
 *
 * Design: mms_B.3_KUDO – Highlight (id 6885:10148):
 *   background #FFF8E1, border KudosBorder 1 dp, radius 8 dp, padding 8/12 dp.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KudoDetailCard(
    kudo: Kudo,
    isLiked: Boolean,
    onToggleLike: () -> Unit,
    onSenderClick: (KudoUser) -> Unit,
    onRecipientClick: (KudoUser) -> Unit,
    onHashtagClick: (String) -> Unit,
    onImageClick: (Int) -> Unit,
    onCopyLink: () -> Unit,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackground, CardBorderShape)
            .border(1.dp, KudosBorder, CardBorderShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row: sender — arrow — recipient
        SenderRecipientRow(
            kudo = kudo,
            onSenderClick = onSenderClick,
            onRecipientClick = onRecipientClick
        )

        HorizontalDivider(thickness = 0.5.dp, color = KudosBorder.copy(alpha = 0.4f))

        // Time range
        Text(
            text = kudo.timeRange,
            style = MaterialTheme.typography.labelSmall,
            color = KudosGray
        )

        // Title — gold (uppercase, bold, centered)
        Text(
            text = kudo.title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = TitleColor,
                textAlign = TextAlign.Center
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Full message body
        Text(
            text = kudo.message,
            style = MaterialTheme.typography.bodySmall,
            color = KudosDarkText,
            textAlign = TextAlign.Justify
        )

        // Image gallery (visible only when imageCount > 0)
        if (kudo.imageCount > 0) {
            KudoImageGallery(
                imageCount = kudo.imageCount,
                onImageClick = onImageClick
            )
        }

        // Hashtags — each chip tappable to filter by that tag (TC_FUN_016)
        if (kudo.hashtags.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                kudo.hashtags.forEach { tag ->
                    Text(
                        text = "#$tag",
                        style = MaterialTheme.typography.labelSmall,
                        color = HashtagColor,
                        modifier = Modifier.clickable { onHashtagClick(tag) }
                    )
                }
            }
        }

        // Action row: hearts | Copy Link | Xem chi tiết
        ActionRow(
            heartCount = kudo.heartCount,
            isLiked = isLiked,
            onToggleLike = onToggleLike,
            onCopyLink = onCopyLink,
            onViewDetail = onViewDetail
        )
    }
}

@Composable
private fun SenderRecipientRow(
    kudo: Kudo,
    onSenderClick: (KudoUser) -> Unit,
    onRecipientClick: (KudoUser) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sender slot
        UserInfoSlot(
            name = kudo.senderDisplayName,
            code = if (kudo.isAnonymous) null else kudo.sender?.code,
            badge = if (kudo.isAnonymous) null else kudo.sender?.badge,
            subLabel = if (kudo.isAnonymous) "Người gửi ẩn danh" else null,
            isAnonymous = kudo.isAnonymous,
            onClick = if (kudo.isAnonymous) null else kudo.sender?.let { s -> { onSenderClick(s) } },
            modifier = Modifier.weight(1f)
        )

        // Paper plane icon (center)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Gửi tới",
            tint = KudosGold,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(18.dp)
        )

        // Recipient slot (with star level indicator)
        UserInfoSlot(
            name = kudo.recipient.name,
            code = kudo.recipient.code,
            badge = kudo.recipient.badge,
            subLabel = null,
            isAnonymous = false,
            starLevel = starLevel(kudo.recipientKudosCount),
            onClick = { onRecipientClick(kudo.recipient) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun UserInfoSlot(
    name: String,
    code: String?,
    badge: String?,
    subLabel: String?,
    isAnonymous: Boolean,
    starLevel: Int = 0,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val rowModifier = if (onClick != null) modifier.clickable { onClick() } else modifier
    Column(
        modifier = rowModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        KudoAvatar(name = if (isAnonymous) null else name, size = 36.dp, anonymous = isAnonymous)
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = KudosDarkText,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        if (subLabel != null) {
            Text(
                text = subLabel,
                style = MaterialTheme.typography.labelSmall,
                color = KudosGray,
                textAlign = TextAlign.Center
            )
        }
        // code + star badge row
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (code != null) {
                Text(
                    text = code,
                    style = MaterialTheme.typography.labelSmall,
                    color = KudosGray
                )
            }
            if (badge != null) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = KudosGold
                )
            }
            if (starLevel > 0) {
                Text(
                    text = "★".repeat(starLevel),
                    style = MaterialTheme.typography.labelSmall,
                    color = KudosGold
                )
            }
        }
    }
}

@Composable
private fun ActionRow(
    heartCount: Int,
    isLiked: Boolean,
    onToggleLike: () -> Unit,
    onCopyLink: () -> Unit,
    onViewDetail: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hearts
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.clickable { onToggleLike() }
        ) {
            Text(
                text = "$heartCount",
                style = MaterialTheme.typography.labelSmall,
                color = KudosDarkText
            )
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isLiked) "Bỏ thích" else "Thích",
                tint = if (isLiked) HeartActiveColor else KudosDarkText,
                modifier = Modifier.size(14.dp)
            )
        }

        // Copy Link button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clickable { onCopyLink() }
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = null,
                tint = KudosDarkText,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "Copy Link",
                style = MaterialTheme.typography.labelSmall,
                color = KudosDarkText
            )
        }

        // Xem chi tiết →
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clickable { onViewDetail() }
                .padding(4.dp)
        ) {
            Text(
                text = "Xem chi tiết",
                style = MaterialTheme.typography.labelSmall,
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
