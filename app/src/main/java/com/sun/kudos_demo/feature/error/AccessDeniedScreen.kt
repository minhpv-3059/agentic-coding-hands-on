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

// 403 Access Denied — design [iOS] Access denied (k-7zJk2B7s, node 6885:9490).
// Same layout, illustration and message as the 404 screen per the design spec
// (header 6885:9523 description = "The resource you're looking for…"); only the title differs.

/**
 * 403 Access Denied screen — thin wrapper around [ErrorScreen].
 *
 * @param onBack  Called when the back (‹) icon is tapped.
 * @param onHome  Called when the "Go back to Home" button is tapped.
 */
@Composable
fun AccessDeniedScreen(
    onBack: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorScreen(
        title = stringResource(R.string.error_403_title),
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
private fun AccessDeniedScreenPreview() {
    KudosAppTheme {
        AccessDeniedScreen(onBack = {}, onHome = {})
    }
}
