package com.sun.kudos_demo.feature.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme

// 404 Not Found — design [iOS] Not Found (sn2mdavs1a, node 6885:9448).
// Differs from AccessDeniedScreen only by the title; both share the same robot
// "404" illustration (extracted from MoMorph S3 → R.drawable.img_error_robot) and message.

/**
 * 404 Not Found screen — thin wrapper around [ErrorScreen].
 *
 * @param onBack  Called when the back (‹) icon is tapped.
 * @param onHome  Called when the "Go back to Home" button is tapped.
 */
@Composable
fun NotFoundScreen(
    onBack: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorScreen(
        title = stringResource(R.string.error_404_title),
        message = stringResource(R.string.error_message),
        illustration = {
            Image(
                painter = painterResource(R.drawable.img_error_robot),
                contentDescription = stringResource(R.string.error_illustration_desc),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        },
        ctaText = stringResource(R.string.error_go_home),
        onBack = onBack,
        onHome = onHome,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun NotFoundScreenPreview() {
    KudosAppTheme {
        NotFoundScreen(onBack = {}, onHome = {})
    }
}
