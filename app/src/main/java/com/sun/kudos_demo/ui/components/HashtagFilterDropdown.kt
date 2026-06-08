package com.sun.kudos_demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal
import com.sun.kudos_demo.ui.theme.KudosWhite

private val DropdownPillShape = RoundedCornerShape(50)
private val MenuShape = RoundedCornerShape(8.dp)

/**
 * Pill-shaped filter button that shows a dropdown menu anchored directly below it.
 * Stateless — caller owns `expanded` and selection state.
 *
 * @param items     List of hashtag strings (without '#' prefix) to display.
 * @param selected  Currently selected hashtag, or null when no filter is active.
 * @param expanded  Whether the dropdown menu is currently visible.
 * @param onExpandedChange  Called when the pill is tapped; caller toggles [expanded].
 * @param onSelect  Called with the chosen hashtag when an item is tapped.
 * @param modifier  Optional layout modifier for the pill button.
 */
@Composable
fun HashtagFilterDropdown(
    items: List<String>,
    selected: String?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = selected != null
    val pillBorderColor = if (isActive) KudosGold else KudosBorder.copy(alpha = 0.6f)
    val pillBgColor = if (isActive) KudosSecondaryButtonNormal else KudosSecondaryButtonNormal
    val labelColor = if (isActive) KudosGold else KudosGray

    Box(modifier = modifier) {
        // Pill button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, pillBorderColor, DropdownPillShape)
                .background(pillBgColor, DropdownPillShape)
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (selected != null) "#$selected" else "Hashtag",
                style = MaterialTheme.typography.labelMedium,
                color = labelColor
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = labelColor,
                modifier = Modifier.width(16.dp)
            )
        }

        // Dropdown menu anchored below the pill
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .widthIn(min = 160.dp)
                .background(KudosContainer, MenuShape)
        ) {
            items.forEach { tag ->
                val isItemSelected = tag == selected
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isItemSelected) KudosGold else KudosWhite
                        )
                    },
                    onClick = {
                        onSelect(tag)
                        onExpandedChange(false)
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = KudosWhite,
                        leadingIconColor = KudosWhite
                    )
                )
            }
        }
    }
}

// --- Previews ---

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HashtagFilterDropdownCollapsedPreview() {
    KudosAppTheme {
        HashtagFilterDropdown(
            items = KudosMockData.hashtags,
            selected = null,
            expanded = false,
            onExpandedChange = {},
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HashtagFilterDropdownSelectedPreview() {
    KudosAppTheme {
        HashtagFilterDropdown(
            items = KudosMockData.hashtags,
            selected = "Dedicated",
            expanded = false,
            onExpandedChange = {},
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HashtagFilterDropdownExpandedPreview() {
    var expanded by remember { mutableStateOf(true) }
    KudosAppTheme {
        HashtagFilterDropdown(
            items = KudosMockData.hashtags,
            selected = null,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
