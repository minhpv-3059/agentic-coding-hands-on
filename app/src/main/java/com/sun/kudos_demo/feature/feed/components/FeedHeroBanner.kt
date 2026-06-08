package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosGold

/**
 * Hero banner at the top of the Kudos Feed.
 * Design: mms_A_KV Kudos (6885:9066) — dark background + "KUDOS" gold title + tagline.
 * A real background image can be swapped via [backgroundContent] slot once exported from Figma.
 */
@Composable
fun FeedHeroBanner(modifier: Modifier = Modifier) {
    val heroBg = Brush.verticalGradient(
        colorStops = arrayOf(
            0f to KudosBackground,
            0.5f to Color(0xFF0A1E2A),
            1f to KudosBackground
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(heroBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "KUDOS",
                style = MaterialTheme.typography.displayLarge,   // 52sp ExtraBold per code-standards
                color = KudosGold,
                letterSpacing = 6.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Hệ thống ghi nhận và cảm ơn",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun FeedHeroBannerPreview() {
    KudosAppTheme {
        FeedHeroBanner(modifier = Modifier.padding(0.dp))
    }
}
