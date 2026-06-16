package com.sun.kudos_demo.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosGold

/**
 * Floating action button matching design node mms_6_float button (6885:9058).
 * Layout: pill-shaped row — [pencil icon | divider | kudos S icon]
 * Position: bottom-end, overlaid via Box in HomeScreen.
 * Size: 89×48 dp total (Button frame), gold glow shadow per design.
 *
 * Design shadow: box-shadow: 0 4px 4px 0 rgba(0,0,0,0.25), 0 0 6px 0 #FAE287
 */
@Composable
fun HomeFab(
    onSendKudos: () -> Unit = {},
    onOpenKudosFeed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val fabShape = RoundedCornerShape(50)

    // Outer glow shadow — approximate design's FAE287 (KudosGold) ambient glow
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = fabShape,
                ambientColor = KudosGold,
                spotColor = KudosGold
            )
            .clip(fabShape)
            .background(KudosGold)
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Left: pencil/compose icon → send kudos
            // Design: MM_MEDIA_Pen, Frame 483 (41×32 dp inside 89×48 Button frame)
            Box(
                modifier = Modifier
                    .width(41.dp)
                    .height(48.dp)
                    .clickable(onClick = onSendKudos)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = "Send Kudos",
                    tint = KudosBackground,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Divider — subtle separation between the two tap zones
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(Color(0x33000000)) // semi-transparent dark divider
            )

            // Right: S/Kudos icon → open kudos feed
            // Design: MM_MEDIA_IC_Kudos Logo, Button (89×48 dp)
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clickable(onClick = onOpenKudosFeed)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Real Sun* Kudos logo (ic_kudos_logo) — multicolor, no tint
                Image(
                    painter = painterResource(R.drawable.ic_kudos_logo),
                    contentDescription = "Kudos Feed",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HomeFabPreview() {
    KudosAppTheme {
        Box(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            HomeFab()
        }
    }
}
