package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Hero banner at the top of the Kudos Feed.
 * Design: mms_A_KV Kudos (6885:9066) over the SAA key-visual (mm_media_bg / MM_MEDIA_Keyvisual BG).
 *
 * Layout (top→bottom): tagline "Hệ thống ghi nhận và cảm ơn" then the Sun* Kudos logo
 * (red mark `ic_kudos_logo` + gold "KUDOS"). The shared SAA key-visual (`bg_home_keyvisual`,
 * also used on Home/Login) is the background, cropped to its top where the artwork sits, and
 * faded into the dark page background at the bottom.
 */
@Composable
fun FeedHeroBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(KudosBackground)
    ) {
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        )
        // Fade the artwork into the page background so the section below blends seamlessly.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.55f to Color.Transparent,
                        1f to KudosBackground
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 72.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.Start   // design is left-aligned; artwork sits on the right
        ) {
            Text(
                text = stringResource(R.string.feed_hero_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = KudosGold
            )
            Spacer(Modifier.height(8.dp))
            // Sun* Kudos brand wordmark (red mark + cream KUDOS) — traced to ic_kudos_wordmark (221×39)
            Image(
                painter = painterResource(R.drawable.ic_kudos_wordmark),
                contentDescription = stringResource(R.string.feed_hero_logo_desc),
                modifier = Modifier
                    .height(40.dp)
                    .width(227.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun FeedHeroBannerPreview() {
    KudosAppTheme {
        FeedHeroBanner()
    }
}
