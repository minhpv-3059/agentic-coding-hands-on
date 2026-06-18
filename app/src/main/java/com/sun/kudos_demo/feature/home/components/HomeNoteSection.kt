package com.sun.kudos_demo.feature.home.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * mms_3_note — theme description paragraph.
 *
 * Exact text from Figma node 6885:9029 (mms_3_note → txt):
 * 14sp, fontWeight 300 (Light), white, letterSpacing 0.25px, lineHeight 20px.
 * Width 333px in design (horizontal padding handled by caller).
 */
@Composable
fun HomeNoteSection(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.home_theme_note),
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
        color = KudosWhite,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HomeNoteSectionPreview() {
    KudosAppTheme {
        HomeNoteSection()
    }
}
