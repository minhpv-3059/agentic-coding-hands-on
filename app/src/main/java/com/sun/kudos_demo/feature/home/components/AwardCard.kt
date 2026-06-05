package com.sun.kudos_demo.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosContainer
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Award card — 160×298 dp total (design node 6885:9033 / 6885:9034 / 6885:9035).
 * Layout: image placeholder (160×160) + name + 2-line description + "Chi tiết →" link.
 * Gap: 12 dp between sections.
 */
@Composable
fun AwardCard(
    award: AwardItem,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(160.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Award thumbnail — 160×160 dp. Real Figma asset when present, else styled placeholder.
        if (award.image != null) {
            Image(
                painter = painterResource(award.image),
                contentDescription = award.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        } else {
            AwardImagePlaceholder(modifier = Modifier.size(160.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Award name — 14sp, medium weight, gold
            Text(
                text = award.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = KudosGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Description — 14sp, light, white, max 2 lines
            Text(
                text = award.description,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                color = KudosWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // "Chi tiết →" link — 14sp, medium, white + arrow icon
        Row(
            modifier = Modifier.clickable(onClick = onDetailClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Chi tiết",
                style = MaterialTheme.typography.titleMedium,
                color = KudosWhite
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = KudosWhite,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * Placeholder for award card thumbnail (160×160 dp).
 * Dark container + low-alpha gold tint to approximate the design's trophy glow.
 * Design uses blend-mode:screen gold radial glow over a dark bg.
 */
@Composable
fun AwardImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(KudosContainer),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(KudosSecondaryButtonNormal) // 10% gold tint token (placeholder; real trophy → Phase 10)
        )
        Text(
            text = "🏆",
            style = MaterialTheme.typography.headlineLarge,
            color = KudosGold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun AwardCardPreview() {
    KudosAppTheme {
        AwardCard(
            award = mockAwards.first(),
            onDetailClick = {}
        )
    }
}
