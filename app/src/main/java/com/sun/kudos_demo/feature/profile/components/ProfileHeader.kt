package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.ui.components.KudoAvatar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Hero profile section — circular avatar with white ring, full name (gold), team code + badge pill.
 *
 * Design: mms_1.1_member (6885:10339)
 *   - Avatar: 72dp diameter, 1.911dp white ring border
 *   - Gap between avatar and name block: 24dp
 *   - Name: Montserrat Bold 18sp/24sp gold (#FFEA9E), center-aligned
 *   - Detail row: team code Montserrat Regular 14sp/20sp white, 2dp gray dot, badge pill
 *   - Badge pill: gold border 0.3dp, radius 30dp, text Bold 8sp white, h-padding 5dp
 */
@Composable
fun ProfileHeader(
    name: String,
    teamCode: String,
    badge: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with the rank pill overlaid on the bottom edge (design: rank label on avatar)
        Box(contentAlignment = Alignment.BottomCenter) {
            // border applied BEFORE clip so it renders inside the circle bounds
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(75.dp)
                    .border(1.5.dp, KudosWhite, CircleShape)
                    .padding(1.5.dp)
                    .clip(CircleShape)
            ) {
                KudoAvatar(
                    name = name,
                    size = 72.dp
                )
            }
            RankBadge(
                badge = badge,
                height = 16.dp,
                modifier = Modifier.offset(y = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Full name — Montserrat Bold 18sp/24sp gold
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp
            ),
            color = KudosGold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Detail row: team code · dot · badge pill
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Team code — Regular 14sp/20sp white
            Text(
                text = teamCode,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.25.sp
                ),
                color = KudosWhite
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Dot separator — 2dp circle, gray 40% opacity (design: Ellipse 70)
            Box(
                modifier = Modifier
                    .size(2.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF999999).copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Achievement rank pill — real Figma export (img_rank_*) via shared RankBadge
            RankBadge(badge = badge, height = 14.dp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun ProfileHeaderPreview() {
    KudosAppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ProfileHeader(
                name = "Huỳnh Dương Xuân Nhật",
                teamCode = "CEVC3",
                badge = "Legend Hero"
            )
        }
    }
}
