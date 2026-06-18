package com.sun.kudos_demo.feature.awards.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.awards.AwardContent
import com.sun.kudos_demo.feature.awards.AwardData
import com.sun.kudos_demo.feature.awards.AwardValue
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * award section (6885:10765): trophy image + info rows.
 *
 * Trophy (6885:10766): 160×160dp. The composited PNG (gold ring + glow + pedestal + name)
 * is drawn normally on its transparent canvas; the gold glow (#FAE287) is baked into the
 * asset and the transparent areas let the key-visual show through behind it.
 *
 * Award info block: title row (badge icon + label) + description.
 * Dividers (Rectangle 8/9): 335×1dp, color #2E3940.
 * Stat rows: title row (icon + label) + number row (amount + unit).
 * Values supports 1 or 2 rows (Signature 2025 - Creator has 2).
 */
@Composable
fun AwardTrophyCard(
    award: AwardContent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Trophy image — 160×160dp (6885:10766). The composited PNG already bakes in the
        // gold ring, glow and pedestal on a transparent canvas, so it is drawn normally;
        // the transparent areas let the key-visual show through behind it.
        Image(
            painter = painterResource(award.trophy),
            contentDescription = stringResource(award.dropdownLabelRes),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(160.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Award info block (6885:10767): title + description, gap 12dp
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title row (6885:10768): badge icon 24dp + award label 14sp bold gold
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_award_badge),
                    contentDescription = null,
                    tint = KudosGold,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(award.dropdownLabelRes),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = KudosGold
                )
            }

            // Description paragraph (6885:10771): 14sp light 300 white
            Text(
                text = stringResource(award.descriptionRes),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                color = KudosWhite
            )
        }

        Spacer(Modifier.height(16.dp))

        // Divider (Rectangle 8 — 6885:10772): 335×1dp #2E3940
        HorizontalDivider(color = KudosDivider, thickness = 1.dp)

        Spacer(Modifier.height(16.dp))

        // Quantity row (6885:10773): quantity label + "01 Cá nhân / Individual"
        AwardStatRow(
            iconRes = R.drawable.ic_award_diamond,
            label = stringResource(R.string.award_quantity_label),
            valueRows = listOf(AwardValue(award.quantity, award.quantityUnitRes))
        )

        Spacer(Modifier.height(16.dp))

        // Divider (Rectangle 9 — 6885:10780)
        HorizontalDivider(color = KudosDivider, thickness = 1.dp)

        Spacer(Modifier.height(16.dp))

        // Prize value row(s) (6885:10781): prize value label + 1–2 value rows
        AwardStatRow(
            iconRes = R.drawable.ic_award_flag,
            label = stringResource(R.string.award_value_label),
            valueRows = award.values
        )
    }
}

/**
 * Stat row: icon 24dp + label 14sp bold gold, then N value rows below.
 * Each value row: amount 18sp bold 700 white + note 14sp light 300 white, gap 8dp.
 * Design: 6885:10773 (qty) and 6885:10781 (prize), h60 with gap 12dp.
 */
@Composable
private fun AwardStatRow(
    iconRes: Int,
    label: String,
    valueRows: List<AwardValue>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title row: icon + label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = KudosGold,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = KudosGold
            )
        }

        // Value rows — 1 or 2 entries
        valueRows.forEach { value ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Amount: 18sp bold 700, letterSpacing 0.5sp, white
                Text(
                    text = value.amount,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = KudosWhite
                )
                // Note: 14sp light 300, letterSpacing 0.25sp, white
                Text(
                    text = stringResource(value.noteRes),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                    color = KudosWhite
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun AwardTrophyCardPreview() {
    KudosAppTheme {
        AwardTrophyCard(
            award = AwardData.awards.first(),
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun AwardTrophyCardSignaturePreview() {
    KudosAppTheme {
        AwardTrophyCard(
            award = AwardData.awards.first { it.id == "signature_creator" },
            modifier = Modifier.padding(20.dp)
        )
    }
}
