package com.sun.kudos_demo.feature.profile.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/** Rank pill aspect ratio from the exported Figma images (122×26 px). */
private const val RANK_PILL_RATIO = 122f / 26f

/** Maps a rank label to its exported pill drawable (img_rank_*), or null if unrecognised. */
@DrawableRes
fun rankBadgeRes(badge: String): Int? = when (badge.trim().lowercase()) {
    "legend hero" -> R.drawable.img_rank_legend_hero
    "rising hero" -> R.drawable.img_rank_rising_hero
    else -> null
}

/**
 * Achievement-rank pill (LEGEND HERO / RISING HERO) from the real Figma export. Shared by the
 * own- and other-profile headers — rendered both beside the team code and overlaid on the avatar.
 * Falls back to a bordered text pill for any unrecognised rank.
 */
@Composable
fun RankBadge(badge: String, modifier: Modifier = Modifier, height: Dp = 14.dp) {
    val res = rankBadgeRes(badge)
    if (res != null) {
        Image(
            painter = painterResource(res),
            contentDescription = badge,
            contentScale = ContentScale.Fit,
            modifier = modifier
                .height(height)
                .aspectRatio(RANK_PILL_RATIO)
        )
    } else {
        Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                lineHeight = 10.sp,
                color = KudosWhite
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
                .border(0.3.dp, KudosGold, RoundedCornerShape(30.dp))
                .padding(horizontal = 5.dp, vertical = 1.dp)
        )
    }
}
