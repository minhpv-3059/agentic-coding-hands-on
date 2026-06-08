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

private val DeptPillShape = RoundedCornerShape(50)
private val DeptMenuShape = RoundedCornerShape(8.dp)

/**
 * Pill-shaped filter button that shows a department dropdown anchored directly below it.
 * Stateless — caller owns `expanded` and selection state.
 *
 * @param items     List of department name strings to display (e.g. "CEVC2", "CEVC3").
 * @param selected  Currently selected department, or null when no filter is active.
 * @param expanded  Whether the dropdown menu is currently visible.
 * @param onExpandedChange  Called when the pill is tapped; caller toggles [expanded].
 * @param onSelect  Called with the chosen department name when an item is tapped.
 * @param modifier  Optional layout modifier for the pill button.
 */
@Composable
fun DepartmentFilterDropdown(
    items: List<String>,
    selected: String?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = selected != null
    val pillBorderColor = if (isActive) KudosGold else KudosBorder.copy(alpha = 0.6f)
    val labelColor = if (isActive) KudosGold else KudosGray

    Box(modifier = modifier) {
        // Pill button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, pillBorderColor, DeptPillShape)
                .background(KudosSecondaryButtonNormal, DeptPillShape)
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = selected ?: "Phòng ban",
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

        // Dropdown menu anchored below the pill; scrollable for long lists
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .widthIn(min = 160.dp)
                .background(KudosContainer, DeptMenuShape)
        ) {
            items.forEach { dept ->
                val isItemSelected = dept == selected
                DropdownMenuItem(
                    text = {
                        Text(
                            text = dept,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isItemSelected) KudosGold else KudosWhite
                        )
                    },
                    onClick = {
                        onSelect(dept)
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
private fun DepartmentFilterDropdownCollapsedPreview() {
    KudosAppTheme {
        DepartmentFilterDropdown(
            items = KudosMockData.departments,
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
private fun DepartmentFilterDropdownSelectedPreview() {
    KudosAppTheme {
        DepartmentFilterDropdown(
            items = KudosMockData.departments,
            selected = "CEVC2",
            expanded = false,
            onExpandedChange = {},
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun DepartmentFilterDropdownExpandedPreview() {
    var expanded by remember { mutableStateOf(true) }
    KudosAppTheme {
        DepartmentFilterDropdown(
            items = KudosMockData.departments,
            selected = null,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
