package com.sun.kudos_demo.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Reusable section header (eyebrow + gold divider + title) shared by the Awards and
 * Kudos sections on Home.
 * - Eyebrow: 12sp, regular, white
 * - Gold divider bar (40×2 dp)
 * - Title: 22sp, medium weight, gold
 *
 * Design: mms_4.1_header (6885:9031) and mms_5.1_header (6885:9040).
 */
@Composable
fun SectionHeader(
    eyebrow: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = eyebrow,
            style = MaterialTheme.typography.bodySmall,
            color = KudosWhite
        )
        Spacer(Modifier.height(4.dp))
        // Gold divider — Rectangle 26 in design (mms_4.1_header)
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(2.dp)
                .background(KudosBorder)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium),
            color = KudosGold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun SectionHeaderPreview() {
    KudosAppTheme {
        SectionHeader(
            eyebrow = "Sun* Annual Awards 2025",
            title = "Hệ thống giải thưởng"
        )
    }
}
