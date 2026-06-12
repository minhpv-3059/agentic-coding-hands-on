package com.sun.kudos_demo.feature.send

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.send.components.CommunityStandardsSection
import com.sun.kudos_demo.feature.send.components.SecurityStandardsSection
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Community Standards screen — stateless / presentational.
 * Design: [iOS] Sun*Kudos_Tiêu chuẩn cộng đồng (screen xms7csmDhD).
 *
 * Layout (scrollable):
 *   - Top bar: back arrow + "Tiêu chuẩn cộng đồng" title
 *   - Logo Banner: ROOT FURTHER branding block (spec A, node 6885:10829)
 *   - Section B: Community Standards + 10 violation criteria (node 6885:10848)
 *   - Divider (node 6885:10853)
 *   - Section C: Security Standards (node 6885:10854)
 */
@Composable
fun CommunityStandardsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        CommunityStandardsTopBar(onBack = onBack)
        HorizontalDivider(color = KudosDivider, thickness = 1.dp)

        LazyColumn {
            item { RootFurtherBanner() }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(Modifier.height(16.dp))
                    CommunityStandardsSection()
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = KudosBorder, thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))
                    SecurityStandardsSection()
                    Spacer(Modifier.height(24.dp))
                }
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
            .background(KudosBackground)
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
    // TODO(asset): export ROOT FURTHER banner (node 6885:10830) from Figma.
    // Raw S3 asset has CSS background-position offset (-26px -24.511px / 132.371%)
    // which misaligns the crop — using img_root_further.png wordmark on a dark
    // branded container instead, matching the overall banner appearance.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = KudosContainer,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.img_root_further),
            contentDescription = "ROOT FURTHER — SAA 2025",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(80.dp)
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
