package com.sun.kudos_demo.feature.awards.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold

/**
 * mms_A_KV Kudos (6885:10739) — the Kudos wordmark hero at the very top of the content,
 * right below the top bar. Column, gap 8dp, left-aligned at the 20dp page margin.
 *
 * 1. Eyebrow TEXT "Hệ thống ghi nhận và cảm ơn" (6885:10741): 14sp / 20, Medium (500),
 *    color #FFEA9E (KudosGold).
 * 2. KUDOS logo group (6885:10743, 221×39, gap 9dp, vertically centered): the Sun* flame
 *    mark (mm_media_logo 49×38 → ic_kudos_logo) + the "KUDOS" wordmark (163×39 →
 *    ic_kudos_wordmark). Heights are constrained to the design values to avoid distortion.
 */
@Composable
fun AwardKvSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.award_kv_eyebrow),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = KudosGold
        )

        // KUDOS logo group: flame mark + wordmark, vertically centered, 9dp gap
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            // Sun* flame mark — 49×38 in design; height-constrained keeps aspect
            Image(
                painter = painterResource(R.drawable.ic_kudos_logo),
                contentDescription = null,
                modifier = Modifier.height(38.dp)
            )
            // "KUDOS" wordmark — 163×39 in design
            Image(
                painter = painterResource(R.drawable.ic_kudos_wordmark),
                contentDescription = "KUDOS",
                modifier = Modifier.height(39.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun AwardKvSectionPreview() {
    KudosAppTheme {
        AwardKvSection(modifier = Modifier.padding(20.dp))
    }
}
