package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.send.SendKudosMockData
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosDropdownHighlight
import com.sun.kudos_demo.ui.theme.KudosError
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

private val FieldShape = RoundedCornerShape(4.dp)
private val MenuShape = RoundedCornerShape(8.dp)

// A8: mockDanhHieuOptions re-export val removed — callers use SendKudosMockData.danhHieuOptions directly.

/**
 * "Danh hiệu" field — label with red asterisk (design node 6885:9913), dropdown selector,
 * helper text (only "Ví dụ: …" copy — no link; link moved to toolbar row per design node 6885:9931).
 *
 * B5: Red asterisk IS present in design (node 6885:9913, rgba(207,19,34,1)).
 * B3: Placeholder copy = "Dành tặng một danh hiệu cho..." (design node 6885:9914).
 * B4: Helper copy = "Ví dụ: … Danh hiệu sẽ hiển thị làm tiêu đề Kudos của bạn." (node 6885:9915).
 *     "Tiêu chuẩn cộng đồng" link is now on the toolbar row (node 6885:9931), not here.
 * A7: Selected trailing icon uses Icons.Filled.Check (distinct from unselected chevron).
 * DARK dropdown: background #00070C (KudosContainer2) + border #998C5F per design node 6891:17450.
 *   Selected item highlight = rgba(255,234,158,0.20) = KudosDropdownHighlight.
 */
@Composable
fun DanhHieuField(
    selectedDanhHieu: String?,
    options: List<String>,
    expanded: Boolean,
    onToggle: (Boolean) -> Unit,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // B2: label LEFT, input RIGHT — matches design row layout (node 6885:9910, flexDirection:row)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Label: "Danh hiệu *" — B5: red asterisk confirmed in design (node 6885:9913)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Danh hiệu",
                    style = MaterialTheme.typography.bodySmall,
                    color = KudosDarkText
                )
                Text(
                    text = "*",
                    style = MaterialTheme.typography.bodySmall,
                    color = KudosError
                )
            }

            Spacer(Modifier.width(8.dp))

            // Dropdown anchor — takes remaining width
            Box(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(1.dp, KudosBorder, FieldShape)
                    .background(KudosWhite, FieldShape)
                    .clickable { onToggle(!expanded) }
                    .padding(horizontal = 10.dp)
            ) {
                // B3: placeholder = "Dành tặng một danh hiệu cho..." (verbatim from design node)
                Text(
                    text = selectedDanhHieu ?: "Dành tặng một danh hiệu cho...",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selectedDanhHieu != null) KudosDarkText else KudosGray,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = KudosDarkText,
                    modifier = Modifier.size(16.dp)
                )
            }

            // DARK dropdown per design node 6891:17450: background=#00070C, border=#998C5F
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onToggle(false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(min = 200.dp)
                    .background(KudosContainer2, MenuShape)
                    .border(1.dp, KudosBorder, MenuShape)
            ) {
                options.forEach { option ->
                    val isSelected = option == selectedDanhHieu
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodySmall,
                                // White text on dark dropdown background
                                color = KudosWhite
                            )
                        },
                        // A7: selected item shows Check icon with gold tint
                        trailingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = KudosGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        // Selected item gets rgba(255,234,158,0.20) highlight
                        modifier = Modifier.background(
                            if (isSelected) KudosDropdownHighlight else KudosContainer2
                        ),
                        onClick = {
                            onSelect(option)
                            onToggle(false)
                        }
                    )
                }
            }
            }
        }

        Spacer(Modifier.height(4.dp))

        // B4: Helper text — node 6885:9915: only "Ví dụ: …" copy, no link here.
        // "Tiêu chuẩn cộng đồng" link moved to toolbar row (node 6885:9931).
        Text(
            text = "Ví dụ: Người truyền động lực cho tôi.\nDanh hiệu sẽ hiển thị làm tiêu đề Kudos của bạn.",
            style = MaterialTheme.typography.labelSmall,
            color = KudosGray,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun DanhHieuFieldPreview() {
    KudosAppTheme {
        DanhHieuField(
            selectedDanhHieu = null,
            options = SendKudosMockData.danhHieuOptions,
            expanded = false,
            onToggle = {},
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
