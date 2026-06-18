package com.sun.kudos_demo.feature.rules.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

// Hero tier rows (design node 6885:10905). Strings are localized via string resources so the
// section participates in the app-wide language switch; the accent colour distinguishes the
// "rising" tiers (gold) from the entry tier (silver/white), matching the design pills.
enum class HeroLevel(
    @StringRes val pillLabelRes: Int,
    @StringRes val subtitleRes: Int,
    @StringRes val descriptionRes: Int,
    val accent: Color
) {
    NEW_HERO(R.string.rules_hero_new, R.string.rules_hero_new_count, R.string.rules_hero_desc_early, KudosWhite),
    RISING_HERO(R.string.rules_hero_rising, R.string.rules_hero_rising_count, R.string.rules_hero_desc_early, KudosGold),
    SUPER_HERO(R.string.rules_hero_super, R.string.rules_hero_super_count, R.string.rules_hero_desc_late, KudosGold),
    LEGEND_HERO(R.string.rules_hero_legend, R.string.rules_hero_legend_count, R.string.rules_hero_desc_late, KudosGold)
}

// Hero badge pill — design 91.789 × 16 dp, fully-rounded (radius 40), 0.5 dp gold border,
// subtle dark translucent fill. Label is two-tone: tier word in [accent], "Hero" in white.
@Composable
fun HeroPill(level: HeroLevel, modifier: Modifier = Modifier) {
    val pillShape = RoundedCornerShape(40.dp)
    val label = stringResource(level.pillLabelRes)
    val accentWord = label.substringBefore(' ')
    val rest = label.substringAfter(' ', "")
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(16.dp)
            .background(KudosContainer2.copy(alpha = 0.6f), pillShape)
            .border(0.5.dp, KudosGold, pillShape)
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = level.accent, fontWeight = FontWeight.Bold)) {
                    append(accentWord)
                }
                if (rest.isNotEmpty()) {
                    withStyle(SpanStyle(color = KudosWhite)) { append(" $rest") }
                }
            },
            fontSize = 9.6.sp,
            lineHeight = 13.7.sp,
            letterSpacing = 0.07.sp
        )
    }
}

// Single hero level row: pill + bold subtitle + description (design item gap 6 dp).
@Composable
fun HeroLevelRow(level: HeroLevel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HeroPill(level = level)
        // Subtitle — 14sp Bold white
        Text(
            text = stringResource(level.subtitleRes),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.25.sp
            ),
            color = KudosWhite
        )
        // Description — 14sp Regular white
        Text(
            text = stringResource(level.descriptionRes),
            style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.25.sp),
            color = KudosWhite
        )
    }
}

// Full hero section — 4 rows, 16 dp between rows (design node 6885:10905).
@Composable
fun HeroSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeroLevel.entries.forEach { level ->
            HeroLevelRow(level = level)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HeroSectionPreview() {
    KudosAppTheme {
        HeroSection(modifier = Modifier.padding(20.dp))
    }
}
