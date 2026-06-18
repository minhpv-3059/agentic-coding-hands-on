package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Section header for the other-user profile kudos list.
 *
 * Design node 6885:10418 — 335×53dp, column, gap=4dp:
 *   Row 1: "Sun* Annual Awards 2025" — 12sp regular white, 16dp line-height
 *   Row 2: 1dp divider, color #2E3940 (KudosDivider)
 *   Row 3: "KUDOS" — 22sp medium gold, 28dp line-height
 *
 * Named UserProfileSectionHeader (not ProfileSectionHeader) to avoid
 * file-ownership clash with the own-profile agent.
 */
@Composable
fun UserProfileSectionHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // "Sun* Annual Awards 2025" — 12sp regular white (node I6885:10418;75:1884)
        Text(
            text = stringResource(R.string.profile_section_awards_subtitle),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Normal,
                color = KudosWhite
            )
        )

        // Thin divider — 1dp, #2E3940 (node I6885:10418;75:1885)
        HorizontalDivider(
            thickness = 1.dp,
            color = KudosDivider,
            modifier = Modifier.fillMaxWidth()
        )

        // "KUDOS" — 22sp medium gold (node I6885:10418;75:1887)
        Text(
            text = stringResource(R.string.profile_section_kudos_headline),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Medium,
                color = KudosGold
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun UserProfileSectionHeaderPreview() {
    KudosAppTheme {
        UserProfileSectionHeader(modifier = Modifier.padding(vertical = 8.dp))
    }
}
