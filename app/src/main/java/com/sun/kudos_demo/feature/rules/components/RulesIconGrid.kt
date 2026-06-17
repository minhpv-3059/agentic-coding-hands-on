package com.sun.kudos_demo.feature.rules.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosWhite

// Icon data — ordered as shown in design (Group 1: REVIVAL, TOUCH OF LIGHT, STAY GOLD;
// Group 2: FLOW TO HORIZON, BEYOND THE BOUNDARY, ROOT FURTHER).
// Design node 6885:10933: list row, gap 4 dp, 2 groups side-by-side.
// Each icon cell: 48×52 dp (or 50/64 dp for wider labels), icon circle 32×32 dp,
// label 10sp Normal white, centered, lineHeight 16sp.
private data class CollectionIcon(
    val drawableRes: Int,
    @androidx.annotation.StringRes val labelRes: Int
)

private val collectionIcons = listOf(
    CollectionIcon(R.drawable.img_badge_revival, R.string.rules_icon_revival),
    CollectionIcon(R.drawable.img_badge_touch_of_light, R.string.rules_icon_touch_of_light),
    CollectionIcon(R.drawable.img_badge_stay_gold, R.string.rules_icon_stay_gold),
    CollectionIcon(R.drawable.img_badge_flow_to_horizon, R.string.rules_icon_flow_to_horizon),
    CollectionIcon(R.drawable.img_badge_beyond_boundary, R.string.rules_icon_beyond_boundary),
    CollectionIcon(R.drawable.img_badge_root_futher, R.string.rules_icon_root_futher)
)

// Single icon cell — circular image 32×32 dp with 1 dp white border + label below.
// Design: border "1px solid #FFF", border-radius 100px, gap 4 dp icon→label.
@Composable
private fun CollectionIconCell(icon: CollectionIcon, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val label = androidx.compose.ui.res.stringResource(icon.labelRes)
        Image(
            painter = painterResource(icon.drawableRes),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .border(1.dp, KudosWhite, CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                // Design: 10sp, Normal weight, lineHeight 16sp
                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
            ),
            color = KudosWhite,
            textAlign = TextAlign.Center
        )
    }
}

// 6-icon row — design node 6885:10933: two sub-groups (3+3) with 4 dp outer gap,
// icons within each group also 4 dp apart.
// Rendered as a single Row with equal weight distribution so icons spread naturally.
@Composable
fun RulesIconGrid(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        collectionIcons.forEach { icon ->
            CollectionIconCell(
                icon = icon,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun RulesIconGridPreview() {
    KudosAppTheme {
        RulesIconGrid()
    }
}
