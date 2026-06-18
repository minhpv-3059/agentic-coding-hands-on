package com.sun.kudos_demo.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.auth.AppLanguage
import com.sun.kudos_demo.ui.theme.KudosBgUpdate
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Reusable language selector (design screen "[iOS] Language dropdown", uUvW6Qm1ve).
 * Split into a [LanguageTrigger] (flag + code + chevron) and a [LanguageDropdownPanel]
 * (the 2-option overlay) so a host can position the panel however its layout needs —
 * shared by the Login header and the top app bar.
 */

/** Flag drawable for each language — real assets, not emoji. */
@DrawableRes
fun languageFlagRes(language: AppLanguage): Int = when (language) {
    AppLanguage.VN -> R.drawable.ic_vn_flag
    AppLanguage.EN -> R.drawable.ic_uk_flag
}

@Composable
private fun LanguageFlag(language: AppLanguage, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(languageFlagRes(language)),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .width(24.dp)
            .height(16.dp)
            .clip(RoundedCornerShape(2.dp))
    )
}

/** The collapsed selector shown in a header: [flag] [code] ⌄. */
@Composable
fun LanguageTrigger(
    selected: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = KudosWhite,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LanguageFlag(selected, Modifier.size(width = 24.dp, height = 16.dp))
        Text(selected.code, style = MaterialTheme.typography.labelMedium, color = tint)
        Icon(
            Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * The open dropdown panel — VN first, then EN, per design (TC_LANGDD_FUN_010). The currently
 * selected option carries a highlighted fill + gold hairline border to distinguish it.
 */
@Composable
fun LanguageDropdownPanel(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = KudosContainer2,
        border = BorderStroke(1.dp, KudosBorder),
        modifier = modifier.width(122.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            AppLanguage.entries.forEach { language ->
                LanguageOption(
                    language = language,
                    isSelected = language == selected,
                    onClick = { onSelect(language) }
                )
            }
        }
    }
}

@Composable
private fun LanguageOption(language: AppLanguage, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(if (isSelected) KudosBgUpdate else Color.Transparent)
            .then(if (isSelected) Modifier.border(0.5.dp, KudosGold, shape) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LanguageFlag(language, Modifier.size(width = 24.dp, height = 16.dp))
        Text(language.code, style = MaterialTheme.typography.labelMedium, color = KudosWhite)
    }
}
