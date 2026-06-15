package com.sun.kudos_demo.feature.send

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.send.components.CommunityStandardsSection
import com.sun.kudos_demo.feature.send.components.SecurityStandardsSection
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Community Standards screen — stateless / presentational.
 * Design: [iOS] Sun*Kudos_Tiêu chuẩn cộng đồng (screen xms7csmDhD).
 *
 * Issue 4 fix: keyvisual BG (node 6885:10808 = mm_media_MM_MEDIA_Keyvisual BG) fills the
 * full screen — same drawable as other screens (bg_home_keyvisual). Previously was a plain
 * KudosBackground color fill. Content is overlaid via Box/LazyColumn.
 *
 * Layout (scrollable):
 *   - Keyvisual BG full-bleed (node 6885:10808)
 *   - Top bar: back arrow + "Tiêu chuẩn cộng đồng" title (transparent over BG)
 *   - Logo Banner: ROOT FURTHER branding block (spec A, node 6885:10829) — placeholder
 *   - Section B: Community Standards + 10 violation criteria (node 6885:10848)
 *   - Divider (node 6885:10853: rgba(46,57,64,1) = KudosDivider, 1dp)
 *   - Section C: Security Standards (node 6885:10854)
 *
 * Content container (node 6885:10832): padding 20dp horizontal, 16dp bottom; gap 12dp.
 */
@Composable
fun CommunityStandardsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Issue 4: keyvisual background — node 6885:10808 (mm_media_MM_MEDIA_Keyvisual BG).
        // Same asset used on SendKudosScreen and HomeScreen.
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            CommunityStandardsTopBar(onBack = onBack)
            HorizontalDivider(color = KudosDivider, thickness = 1.dp)

            // Content container — node 6885:10832: padding 0/20/16/20, gap 12dp
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                item { RootFurtherBanner() }
                item { Spacer(Modifier.height(12.dp)) }
                item { CommunityStandardsSection() }
                item { Spacer(Modifier.height(12.dp)) }
                // Divider — node 6885:10853: rgba(46,57,64,1) = KudosDivider, 1dp
                item { HorizontalDivider(color = KudosDivider, thickness = 1.dp) }
                item { Spacer(Modifier.height(12.dp)) }
                item { SecurityStandardsSection() }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

// --- Top Bar ---

@Composable
private fun CommunityStandardsTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = KudosWhite
                )
            }
        }
        Text(
            text = "Tiêu chuẩn cộng đồng",
            style = MaterialTheme.typography.titleLarge,
            color = KudosWhite
        )
    }
}

// --- Logo Banner (Spec A, node 6885:10829) ---

@Composable
private fun RootFurtherBanner() {
    // NOTE(asset): ROOT FURTHER banner (node 6885:10830) uses CSS background-position
    // offset (-26px -24.511px / 132.371%) — the cropped asset is img_root_further.png.
    // Rendered on a transparent overlay matching the KV area (no separate dark container
    // since the keyvisual BG is already visible behind it).
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.img_root_further),
            contentDescription = "ROOT FURTHER — SAA 2025",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(0.5f)
        )
    }
}

// --- Preview ---

@Preview(showBackground = true, backgroundColor = 0xFF00101A, device = "id:pixel_5")
@Composable
private fun CommunityStandardsScreenPreview() {
    KudosAppTheme {
        CommunityStandardsScreen(onBack = {})
    }
}
