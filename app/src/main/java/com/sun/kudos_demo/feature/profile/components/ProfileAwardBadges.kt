package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.feature.profile.AwardBadge
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Horizontal row of 6 award badge icons + labels.
 *
 * Design node 6885:10411 — row, gap=4dp, 326×84dp max.
 * Each badge item:
 *   - Column, gap=4dp, alignItems=center
 *   - Circle icon: 32×32dp, 1dp white border, radius=100dp (fully circular)
 *   - Label: 10sp regular white, centered, multi-line allowed (max 2 lines)
 *
 * When [AwardBadge.icon] is null, [AwardBadgePlaceholder] renders a gradient circle so the
 * layout is pixel-accurate. Swap to real image: set icon = R.drawable.img_badge_<name> in
 * the AwardBadge list passed to this composable.
 *
 * **HOW TO SWAP IN REAL IMAGES** (one change per badge):
 * In `UserProfileScreen.kt` (or wherever badges are defined), update each AwardBadge:
 *   AwardBadge(id="revival", label="REVIVAL", icon = R.drawable.img_badge_revival)
 * The [ProfileAwardBadges] composable will automatically use `painterResource(icon)` instead
 * of the placeholder once icon is non-null. No changes needed in this file.
 */
@Composable
fun ProfileAwardBadges(
    badges: List<AwardBadge>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // weight(1f) per item so the 6 badges share the row width evenly and never overflow
        // on a 375dp handset (design row 326dp ≈ 6×51dp); labels wrap within their column.
        badges.forEach { badge ->
            AwardBadgeItem(badge = badge, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun AwardBadgeItem(badge: AwardBadge, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        // Circle icon: 32×32dp, 1dp white border (design: "1px solid #FFF")
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.dp, KudosWhite, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (badge.icon != null) {
                // SWAP TARGET: real image is used here when icon != null
                Image(
                    painter = painterResource(badge.icon),
                    contentDescription = badge.label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                AwardBadgePlaceholder(badgeId = badge.id)
            }
        }

        // Label — 10sp regular white, centered, up to 3 lines (BEYOND THE BOUNDARY)
        Text(
            text = badge.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 16.sp,
                color = KudosWhite
            ),
            textAlign = TextAlign.Center,
            maxLines = 3
        )
    }
}

/**
 * Gradient placeholder for award badge circles — pixel-accurate sized circle matching
 * the 32×32dp design spec. Each badge has a distinct gradient approximating the design colors.
 *
 * Colors are approximated from the MoMorph badge images; exact match requires the real exports.
 * Replace by setting [AwardBadge.icon] — this composable is skipped when icon != null.
 */
@Composable
private fun AwardBadgePlaceholder(badgeId: String) {
    val gradient = when (badgeId) {
        "revival"           -> Brush.radialGradient(listOf(Color(0xFF4FC3F7), Color(0xFF0D47A1)))
        "touch_of_light"    -> Brush.radialGradient(listOf(Color(0xFFFFF9C4), Color(0xFFF9A825)))
        "stay_gold"         -> Brush.radialGradient(listOf(Color(0xFFFFEA9E), Color(0xFFB8860B)))
        "flow_to_horizon"   -> Brush.radialGradient(listOf(Color(0xFFA5D6A7), Color(0xFF1B5E20)))
        "beyond_the_boundary" -> Brush.radialGradient(listOf(Color(0xFFCE93D8), Color(0xFF4A148C)))
        "root_futher"       -> Brush.radialGradient(listOf(Color(0xFFFFCC80), Color(0xFFE65100)))
        else                -> Brush.radialGradient(listOf(Color(0xFF616161), Color(0xFF212121)))
    }
    Box(
        modifier = Modifier
            .size(30.dp)  // inset from the 32dp border box
            .background(gradient, CircleShape)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun ProfileAwardBadgesPreview() {
    KudosAppTheme {
        ProfileAwardBadges(
            badges = listOf(
                AwardBadge("revival", "REVIVAL"),
                AwardBadge("touch_of_light", "TOUCH OF LIGHT"),
                AwardBadge("stay_gold", "STAY GOLD"),
                AwardBadge("flow_to_horizon", "FLOW TO HORIZON"),
                AwardBadge("beyond_the_boundary", "BEYOND THE BOUNDARY"),
                AwardBadge("root_futher", "ROOT FUTHER"),
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}
