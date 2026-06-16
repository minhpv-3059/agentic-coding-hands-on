package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Section header — "Sun* Annual Awards 2025" subtitle + divider + "KUDOS" headline.
 *
 * Design: mms_4_header (6885:10387), componentId 6885:8015
 *   - Container: 335×53dp, gap 4dp (column)
 *   - Subtitle: Montserrat Regular 12sp/16sp white (left-aligned)
 *   - Divider: 1dp #2E3940
 *   - Headline: Montserrat Medium 22sp/28sp gold (left-aligned)
 */
@Composable
fun ProfileSectionHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // "Sun* Annual Awards 2025" — 12sp Regular white
        Text(
            text = "Sun* Annual Awards 2025",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            color = KudosWhite
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Thin divider — 1dp #2E3940
        HorizontalDivider(thickness = 1.dp, color = KudosDivider)

        Spacer(modifier = Modifier.height(4.dp))

        // "KUDOS" — Montserrat Medium 22sp/28sp gold
        Text(
            text = "KUDOS",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp
            ),
            color = KudosGold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun ProfileSectionHeaderPreview() {
    KudosAppTheme {
        ProfileSectionHeader(modifier = Modifier.padding(16.dp))
    }
}
