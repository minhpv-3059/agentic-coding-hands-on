package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosAccentRed

/**
 * Red validation error banner shown above the action buttons when required fields
 * are missing. Matches design node 6885:10124 — 12sp / Normal weight / AccentRed color.
 *
 * Visibility is controlled by the caller — only render this composable when showError is true.
 */
@Composable
fun SendKudosErrorBanner(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.send_error_missing_fields),
        style = MaterialTheme.typography.bodySmall,
        color = KudosAccentRed,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun SendKudosErrorBannerPreview() {
    KudosAppTheme {
        SendKudosErrorBanner(modifier = Modifier.padding(16.dp))
    }
}
