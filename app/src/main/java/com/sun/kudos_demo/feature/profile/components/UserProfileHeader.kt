package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.ui.components.KudoAvatar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Hero member block for the other-user profile screen.
 *
 * Design node 6885:10401 — size 237×144 dp, centered, absolute-positioned over the keyvisual BG.
 * Layout (column, gap=24dp): avatar (72dp circle, 4dp white border) → name (18sp bold gold) →
 * team·badge row (14sp white regular, separator dot 2×2dp, badge pill 60×12dp gold border).
 *
 * The background keyvisual is rendered by the parent screen; this composable is centred over it.
 *
 * @param name       full display name, e.g. "Huỳnh Dương Xuân Nhật"
 * @param teamCode   team code shown before the separator, e.g. "CEVC3"
 * @param badge      badge label shown in a pill after the separator, e.g. "Rising Hero"
 */
@Composable
fun UserProfileHeader(
    name: String,
    teamCode: String,
    badge: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(237.dp)
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Avatar — 72dp circle, 4dp white border, with the rank pill overlaid on the bottom edge
        Box(contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .border(4.dp, KudosWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                KudoAvatar(
                    name = name,
                    size = 64.dp  // inner circle (72 − 2×4 = 64)
                )
            }
            RankBadge(
                badge = badge,
                height = 16.dp,
                modifier = Modifier.offset(y = 6.dp)
            )
        }

        Spacer(Modifier.height(4.dp))

        // Full name — 18sp bold gold (node 6885:10404)
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                color = KudosGold
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Team · badge row (node 6885:10405)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Team code — 14sp regular white (node 6885:10406)
            Text(
                text = teamCode,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    letterSpacing = 0.25.sp,
                    color = KudosWhite
                )
            )

            // Separator dot — 2×2dp, 40% opacity (node 6885:10409)
            Box(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .size(2.dp)
                    .background(KudosGray.copy(alpha = 0.4f), CircleShape)
            )

            // Achievement rank pill — real Figma export (img_rank_*) via shared RankBadge
            RankBadge(badge = badge, height = 14.dp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun UserProfileHeaderPreview() {
    KudosAppTheme {
        UserProfileHeader(
            name = "Huỳnh Dương Xuân Nhật",
            teamCode = "CEVC3",
            badge = "Rising Hero"
        )
    }
}
