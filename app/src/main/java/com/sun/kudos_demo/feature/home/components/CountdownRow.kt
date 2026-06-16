package com.sun.kudos_demo.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/** Seven-segment LCD font (DSEG7 Classic, SIL OFL) matching the design's "Digital Numbers". */
private val DigitalFont = FontFamily(Font(R.font.dseg7_classic_regular))

/**
 * Countdown display: three digit-pair boxes showing DAYS / HOURS / MINUTES.
 * Design (countdown 6885:8988): 16 dp gap between units; each unit = two 32×56 dp
 * frosted digit boxes (8 dp gap) + label below.
 * Accepts days/hours/minutes as Int params — caller drives the values from state.
 */
@Composable
fun CountdownRow(
    days: Int = 20,
    hours: Int = 20,
    minutes: Int = 20,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        CountdownUnit(value = days, label = "DAYS")
        CountdownUnit(value = hours, label = "HOURS")
        CountdownUnit(value = minutes, label = "MINUTES")
    }
}

/** A single countdown unit: two digit boxes (8 dp gap) + label below. E.g. [2][0] DAYS */
@Composable
private fun CountdownUnit(
    value: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    val v = value.coerceIn(0, 99)
    val tens = v / 10
    val ones = v % 10

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FlipDigitBox(digit = tens)
            FlipDigitBox(digit = ones)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
            color = KudosWhite
        )
    }
}

/**
 * Single frosted-glass digit box (design node 6885:8992): 32×56 dp, 8 dp radius,
 * 0.5 dp gold border, white→transparent vertical gradient (≈50% opacity).
 * The digit uses the DSEG7 seven-segment font with a faint "8" ghost behind it,
 * reproducing the classic LCD unlit-segment look from the design.
 */
@Composable
private fun FlipDigitBox(
    digit: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .width(32.dp)
            .height(56.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.05f))
                )
            )
            .border(0.5.dp, KudosGold.copy(alpha = 0.5f), shape),
        contentAlignment = Alignment.Center
    ) {
        // Ghost — all segments faintly lit (DSEG renders "8" as the full glyph)
        Text(
            text = "8",
            fontFamily = DigitalFont,
            fontSize = 30.sp,
            color = KudosWhite.copy(alpha = 0.12f)
        )
        // Lit digit on top
        Text(
            text = digit.toString(),
            fontFamily = DigitalFont,
            fontSize = 30.sp,
            color = KudosWhite
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun CountdownRowPreview() {
    KudosAppTheme {
        CountdownRow(days = 20, hours = 20, minutes = 18)
    }
}
