package com.sun.kudos_demo.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal

/**
 * Hero CTA button row: ABOUT AWARD (primary) + ABOUT KUDOS (secondary).
 * Design: actions frame (6885:9025), gap 16 dp, each button 160×40 dp, 4 dp radius.
 */
@Composable
fun HeroActionButtonRow(
    onAboutAward: () -> Unit,
    onAboutKudos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeroActionButton(
            text = "ABOUT AWARD",
            isPrimary = true,
            onClick = onAboutAward,
            modifier = Modifier.weight(1f)
        )
        HeroActionButton(
            text = "ABOUT KUDOS",
            isPrimary = false,
            onClick = onAboutKudos,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Single hero CTA button.
 *
 * Primary (isPrimary = true):
 *   - Gold fill (#FFEA9E), dark text, 4 dp radius, 160×40 dp
 *   - Design: mms_2.2_Button (6885:9026), backgroundColor rgba(255,234,158,1)
 *
 * Secondary (isPrimary = false):
 *   - 10% gold tint bg, gold border (#998C5F), gold text, 4 dp radius
 *   - Design: mms_2.3_Button (6885:9027), border 1px #998C5F, bg rgba(255,234,158,0.10)
 *
 * Both buttons show an ↗ arrow icon after the label (per design).
 */
@Composable
fun HeroActionButton(
    text: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)
    Surface(
        onClick = onClick,
        shape = shape,
        color = if (isPrimary) KudosGold else KudosSecondaryButtonNormal,
        border = if (isPrimary) null else BorderStroke(1.dp, KudosBorder),
        modifier = modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isPrimary) KudosBackground else KudosGold
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = if (isPrimary) KudosBackground else KudosGold,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HeroActionButtonRowPreview() {
    KudosAppTheme {
        HeroActionButtonRow(onAboutAward = {}, onAboutKudos = {})
    }
}
