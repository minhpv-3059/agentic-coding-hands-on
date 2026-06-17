package com.sun.kudos_demo.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Hero section: full-bleed keyvisual + ROOT FURTHER + countdown + event info + CTA buttons.
 * Design: mm_media_bg (6885:8979) + mms_2_content (6885:8983).
 * bg_home_keyvisual already in drawable-nodpi — full-bleed via ContentScale.Crop.
 */
@Composable
fun HomeHeroSection(
    days: Int = 20,
    hours: Int = 20,
    minutes: Int = 20,
    onAboutAward: () -> Unit = {},
    onAboutKudos: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(640.dp)
    ) {
        // Full-bleed keyvisual background — 375×812 design, Crop
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()              // clear the status bar (edge-to-edge)
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp)             // clear the overlaid 56dp KudosTopBar
        ) {
            Spacer(Modifier.height(148.dp))

            // ROOT FURTHER logo — 247×109 dp (img_root_further, already in project)
            Image(
                painter = painterResource(R.drawable.img_root_further),
                contentDescription = "ROOT FURTHER",
                modifier = Modifier.width(247.dp).height(109.dp)
            )

            Spacer(Modifier.height(16.dp))

            // "Coming soon" — 14sp, light, white (node 6885:8987)
            Text(
                text = "Coming soon",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                color = KudosWhite
            )

            Spacer(Modifier.height(8.dp))

            // Flip-digit countdown: 20 DAYS / 20 HOURS / 20 MINUTES
            CountdownRow(days = days, hours = hours, minutes = minutes)

            Spacer(Modifier.height(16.dp))

            // Event info rows (node 6885:9016)
            EventInfoRow(label = "Thời gian: ", value = "26/12/2025")
            Spacer(Modifier.height(8.dp))
            EventInfoRow(label = "Địa điểm:", value = "Âu Cơ Art Center")
            Spacer(Modifier.height(8.dp))

            // Livestream note (node 6885:9024)
            Text(
                text = "Tường thuật trực tiếp tại Group Facebook Sun* Family",
                style = MaterialTheme.typography.bodyMedium,
                color = KudosWhite
            )

            Spacer(Modifier.height(24.dp))

            // CTA buttons: ABOUT AWARD + ABOUT KUDOS
            HeroActionButtonRow(
                onAboutAward = onAboutAward,
                onAboutKudos = onAboutKudos
            )
        }
    }
}

/**
 * Single event info row: label (light, white) + value (normal weight, gold).
 * Design: event info frame (6885:9016) — rows for time/location.
 */
@Composable
private fun EventInfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
            color = KudosWhite
        )
        // 18sp, normal weight, gold — headlineSmall size from design node 6885:9019
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Normal),
            color = KudosGold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HomeHeroSectionPreview() {
    KudosAppTheme {
        HomeHeroSection(days = 20, hours = 20, minutes = 20)
    }
}
