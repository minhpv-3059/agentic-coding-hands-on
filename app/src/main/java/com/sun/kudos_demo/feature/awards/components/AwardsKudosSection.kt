package com.sun.kudos_demo.feature.awards.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.home.components.SectionHeader
import com.sun.kudos_demo.ui.components.KudosSecondaryButton
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * "Sun* Kudos" section at the bottom of the Awards tab — mirrors HomeKudosSection exactly.
 *
 * Design nodes on all 6 Awards screens (mms_5_kudos / 6885:9039):
 * - SectionHeader: eyebrow "Phong trào ghi nhận" / title "Sun* Kudos"
 * - img_kudos_banner (335×145), clipped to 8dp radius
 * - "ĐIỂM MỚI CỦA SAA 2025" label (KudosGold) + body paragraph (white light)
 * - "Chi tiết ↗" secondary button, width ~120dp
 *
 * This is intentionally identical to HomeKudosSection; it is a separate composable so that
 * the Awards feature owns its own component tree without importing from the Home feature.
 */
@Composable
fun AwardsKudosSection(
    onKudosDetail: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SectionHeader(
            eyebrow = stringResource(R.string.award_kudos_eyebrow),
            title = stringResource(R.string.award_kudos_title)
        )

        // Kudos banner — shared asset img_kudos_banner (335×145), width-filling
        Image(
            painter = painterResource(R.drawable.img_kudos_banner),
            contentDescription = stringResource(R.string.award_kudos_title),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        )

        // Note block — exact same text as HomeKudosSection (Figma node 6885:9054)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.award_kudos_new_label),
                style = MaterialTheme.typography.labelLarge,
                color = KudosGold
            )
            Text(
                text = stringResource(R.string.award_kudos_body),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                color = KudosWhite
            )
        }

        // "Chi tiết ↗" button
        KudosSecondaryButton(
            text = stringResource(R.string.award_kudos_detail_button),
            onClick = onKudosDetail,
            showExternalIcon = true,
            modifier = Modifier.width(120.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun AwardsKudosSectionPreview() {
    KudosAppTheme {
        AwardsKudosSection(modifier = Modifier.padding(20.dp))
    }
}
