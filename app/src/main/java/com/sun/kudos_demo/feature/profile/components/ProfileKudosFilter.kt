package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.profile.ProfileKudosTab
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal
import com.sun.kudos_demo.ui.theme.KudosWhite

private val PillShape = RoundedCornerShape(4.dp)
private val MenuShape = RoundedCornerShape(8.dp)

/**
 * Pill-button filter for the Profile kudos list: "Đã nhận (N)" / "Đã gửi (N)".
 *
 * Design: mms_dropdown (6885:10388), overlay mms_A_Dropdown-List (6891:17101)
 *   - Pill: inline-flex, height 40dp, padding 8dp H+V, gap 8dp, border 1dp #998C5F,
 *           bg rgba(255,234,158,0.10), radius 4dp
 *   - Label text: Montserrat Regular 14sp/20sp white, letter-spacing 0.25dp
 *   - Chevron icon: 24dp
 *   - Overlay: bg #00070C, border 1dp #998C5F, radius 8dp, padding 6dp
 *   - Selected item bg: rgba(255,234,158,0.10) (highlighted) — Montserrat Medium 14sp gold glow
 *   - Unselected item: Montserrat Medium 14sp white
 *
 * Stateless — caller owns [selectedTab]; [receivedCount] and [sentCount] drive the labels.
 */
@Composable
fun ProfileKudosFilter(
    selectedTab: ProfileKudosTab,
    receivedCount: Int,
    sentCount: Int,
    onFilterChange: (ProfileKudosTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val labelText = when (selectedTab) {
        ProfileKudosTab.RECEIVED -> stringResource(R.string.profile_tab_received, receivedCount)
        ProfileKudosTab.SENT     -> stringResource(R.string.profile_tab_sent, sentCount)
    }

    Box(modifier = modifier) {
        // Pill trigger button
        Row(
            modifier = Modifier
                .height(40.dp)
                .clip(PillShape)
                .background(KudosSecondaryButtonNormal)
                .border(1.dp, KudosBorder, PillShape)
                .clickable { expanded = !expanded }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = labelText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.25.sp
                ),
                color = KudosWhite
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                              else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = KudosWhite,
                modifier = Modifier.size(24.dp)
            )
        }

        // Anchored dropdown overlay
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(118.dp)
                .background(KudosContainer2, MenuShape)
                .border(1.dp, KudosBorder, MenuShape)
        ) {
            FilterMenuItem(
                label = stringResource(R.string.profile_tab_received, receivedCount),
                isSelected = selectedTab == ProfileKudosTab.RECEIVED,
                onClick = {
                    onFilterChange(ProfileKudosTab.RECEIVED)
                    expanded = false
                }
            )
            FilterMenuItem(
                label = stringResource(R.string.profile_tab_sent, sentCount),
                isSelected = selectedTab == ProfileKudosTab.SENT,
                onClick = {
                    onFilterChange(ProfileKudosTab.SENT)
                    expanded = false
                }
            )
        }
    }
}

/**
 * Single item inside the dropdown overlay.
 * Selected: bg rgba(255,234,158,0.10) + gold glow text.
 * Unselected: transparent bg + white text.
 */
@Composable
private fun FilterMenuItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val itemBg = if (isSelected) KudosSecondaryButtonNormal
                 else androidx.compose.ui.graphics.Color.Transparent
    val textColor = if (isSelected) KudosGold else KudosWhite

    DropdownMenuItem(
        text = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                ),
                color = textColor
            )
        },
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(itemBg),
        colors = MenuDefaults.itemColors(
            textColor = textColor,
            leadingIconColor = textColor
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun ProfileKudosFilterCollapsedPreview() {
    KudosAppTheme {
        ProfileKudosFilter(
            selectedTab = ProfileKudosTab.SENT,
            receivedCount = 5,
            sentCount = 5,
            onFilterChange = {},
            modifier = androidx.compose.ui.Modifier.padding(16.dp)
        )
    }
}
