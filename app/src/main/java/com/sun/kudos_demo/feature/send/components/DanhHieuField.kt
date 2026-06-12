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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.send.SendKudosMockData
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosError
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

private val FieldShape = RoundedCornerShape(4.dp)
private val MenuShape = RoundedCornerShape(8.dp)

// A8: mockDanhHieuOptions re-export val removed — callers use SendKudosMockData.danhHieuOptions directly.

/**
 * "Danh hiệu" field — label with red asterisk (design node 6885:9913), dropdown selector,
 * helper text with a "Tiêu chuẩn cộng đồng" inline link.
 *
 * B5: Red asterisk IS present in design (node 6885:9913, rgba(207,19,34,1)).
 * B3: Placeholder copy = "Dành tặng một danh hiệu cho..." (design node 6885:9914).
 * B4: Helper copy = "...hiển thị làm tiêu đề Kudos..." (design node 6885:9915).
 * A7: Selected trailing icon uses Icons.Filled.Check (distinct from unselected chevron).
 * B8: Dropdown background uses KudosWhite instead of Color.White.
 */
@Composable
fun DanhHieuField(
    selectedDanhHieu: String?,
    options: List<String>,
    expanded: Boolean,
    onToggle: (Boolean) -> Unit,
    onSelect: (String) -> Unit,
    onCommunityStandardsClick: () -> Unit,
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

            // B8: KudosWhite instead of Color.White
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onToggle(false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(min = 200.dp)
                    .background(KudosWhite, MenuShape)
            ) {
                options.forEach { option ->
                    val isSelected = option == selectedDanhHieu
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodySmall,
                                color = KudosDarkText
                            )
                        },
                        // A7: selected item shows Check icon (distinct from the unselected chevron)
                        trailingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = KudosDarkText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
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

        // B4: Helper text verbatim from design node 6885:9915
        val helperText = buildAnnotatedString {
            withStyle(SpanStyle(color = KudosGray, fontSize = MaterialTheme.typography.labelSmall.fontSize)) {
                append("Ví dụ: Người truyền động lực cho tôi. Danh hiệu sẽ hiển thị làm tiêu đề Kudos của bạn. ")
            }
            withStyle(
                SpanStyle(
                    color = KudosGold,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Tiêu chuẩn cộng đồng")
            }
        }
        Text(
            text = helperText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCommunityStandardsClick() }
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
            onCommunityStandardsClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
