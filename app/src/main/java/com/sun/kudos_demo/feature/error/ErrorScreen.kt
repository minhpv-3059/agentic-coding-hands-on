package com.sun.kudos_demo.feature.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

// Design node refs (403 = 6885:9522 / 404 = 6885:9480 — identical layout):
//   Container: bg #00101A, padding 40dp top/bottom 20dp lr, gap 24dp
//   Title: 18sp Bold, #FFEA9E, lineHeight 24sp, center
//   Separators (Rectangle 16/18): #2E3940 (KudosDivider), 1dp hairline
//   Description: 14sp Medium, #FFFFFF, lineHeight 20sp, center
//   Illustration rect (6885:9487/9529): 320×248dp
//   Button (6885:9489/9531): #FFEA9E fill, h=40dp, border-radius 4dp, align-self stretch

/**
 * Shared parametric error screen scaffold (403 / 404).
 *
 * @param title         Bold gold headline.
 * @param message       White body description text.
 * @param illustration  Composable slot for the error illustration (centered, 320×248dp).
 * @param ctaText       Label for the primary action button.
 * @param onBack        Called when the back (‹) icon is tapped.
 * @param onHome        Called when the primary CTA button is tapped.
 */
@Composable
fun ErrorScreen(
    title: String,
    message: String,
    illustration: @Composable () -> Unit,
    ctaText: String,
    onBack: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top navigation bar — 42dp, back icon at start (design _TopNavigation 6885:9454/9496)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 7.dp)
                    .size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.error_back),
                    tint = KudosWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Main content — padding 40dp top/bottom, 20dp lr, 24dp gap between children
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 40.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header group — title + separator + description (gap 8dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp,
                    color = KudosGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = KudosDivider
                )
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    color = KudosWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Illustration slot — 320×248dp (design node 6885:9487/9529)
            Box(
                modifier = Modifier
                    .width(320.dp)
                    .height(248.dp),
                contentAlignment = Alignment.Center
            ) {
                illustration()
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = KudosDivider
            )

            // Primary CTA — gold fill, 4dp radius, 40dp, full-width (design node 6885:9489/9531)
            ErrorHomeButton(text = ctaText, onClick = onHome, modifier = Modifier.fillMaxWidth())
        }
    }
}

/** Gold "Go back to Home" button — matches design exactly: 4dp radius, 40dp, #FFEA9E fill. */
@Composable
private fun ErrorHomeButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        color = KudosGold,
        modifier = modifier.height(40.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = KudosDarkText
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun ErrorScreenPreview() {
    KudosAppTheme {
        ErrorScreen(
            title = "NOT FOUND",
            message = "The resource you're looking for doesn't exist or has been removed.",
            illustration = { Box(Modifier.size(width = 320.dp, height = 248.dp)) },
            ctaText = "Go back to Home",
            onBack = {},
            onHome = {}
        )
    }
}
