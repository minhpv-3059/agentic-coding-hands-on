package com.sun.kudos_demo.feature.awards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.awards.AwardContent
import com.sun.kudos_demo.feature.awards.AwardData
import com.sun.kudos_demo.feature.home.components.SectionHeader
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosDropdownHighlight
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal
import com.sun.kudos_demo.ui.theme.KudosWhite

private val DropdownFieldShape = RoundedCornerShape(4.dp)

/**
 * mms_B_Highlight (6885:10756): SectionHeader + award selector dropdown.
 *
 * Section header: eyebrow "Sun* Annual Awards 2025", title "Hệ thống giải thưởng SAA 2025".
 * Dropdown field: 248×40dp, border #998C5F, bg rgba(FFEA9E,0.10), 4dp radius.
 * Overlay: dark KudosContainer2 bg, listed items highlight selected in KudosDropdownHighlight.
 */
@Composable
fun AwardHeaderSection(
    awards: List<AwardContent>,
    selected: AwardContent,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (AwardContent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // mms_B.1_header (6885:10758) — reuse SectionHeader (eyebrow 12sp white + gold divider + title 22sp gold)
        SectionHeader(
            eyebrow = "Sun* Annual Awards 2025",
            title = "Hệ thống giải thưởng \nSAA 2025"
        )

        // filter (6885:10759) — 248×40dp dropdown field
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(fraction = 0.74f)   // 248/335 ≈ 74% of content width
                    .border(1.dp, KudosBorder, DropdownFieldShape)
                    .background(KudosSecondaryButtonNormal, DropdownFieldShape)
                    .clickable { onExpandedChange(!expanded) }
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = selected.dropdownLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KudosWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                                  else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = KudosWhite,
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.background(KudosContainer2)
            ) {
                awards.forEach { award ->
                    val isSelected = award.id == selected.id
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = award.dropdownLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) KudosGold else KudosWhite
                            )
                        },
                        onClick = { onSelect(award) },
                        modifier = if (isSelected)
                            Modifier.background(KudosDropdownHighlight)
                        else Modifier
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun AwardHeaderSectionPreview() {
    KudosAppTheme {
        AwardHeaderSection(
            awards = AwardData.awards,
            selected = AwardData.awards.first(),
            expanded = false,
            onExpandedChange = {},
            onSelect = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}
