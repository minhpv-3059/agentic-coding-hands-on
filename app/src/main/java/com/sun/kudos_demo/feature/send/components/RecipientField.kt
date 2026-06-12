package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.ui.components.KudoAvatar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosError
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

private val FieldShape = RoundedCornerShape(4.dp)
private val MenuShape = RoundedCornerShape(8.dp)

/**
 * "Người nhận" field — label + required asterisk on the LEFT, search TextField on the RIGHT
 * (B2: horizontal row layout matching design node 6885:9905 flexDirection:row).
 *
 * B7: The field is a TextField bound to [query]; typing filters the dropdown in real time
 * via [onQueryChange] (ViewModel filters recipientOptions). On select, the field shows the
 * selected recipient's name.
 * B8: Dropdown background uses KudosWhite instead of Color.White.
 */
@Composable
fun RecipientField(
    query: String,
    selectedRecipient: String?,
    options: List<KudoUser>,
    expanded: Boolean,
    hasError: Boolean,
    onQueryChange: (String) -> Unit,
    onToggle: (Boolean) -> Unit,
    onSelect: (KudoUser) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (hasError) KudosError else KudosBorder

    // B2: label LEFT, input RIGHT — matches design row layout (node 6885:9905, flexDirection:row)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        // Label: "Người nhận *"
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Người nhận",
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

        // Search input + dropdown anchor — takes remaining width
        Box(modifier = Modifier.weight(1f)) {
            // B7: TextField shows query while typing; shows selected name when selection is set
            TextField(
                value = selectedRecipient ?: query,
                onValueChange = { text ->
                    if (selectedRecipient == null || text != selectedRecipient) {
                        onQueryChange(text)
                        if (!expanded) onToggle(true)
                    }
                },
                placeholder = {
                    Text(
                        text = "Tìm kiếm",
                        style = MaterialTheme.typography.bodySmall,
                        color = KudosGray
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = KudosWhite,
                    unfocusedContainerColor = KudosWhite,
                    focusedTextColor = KudosDarkText,
                    unfocusedTextColor = KudosDarkText,
                    cursorColor = KudosDarkText,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(1.dp, borderColor, FieldShape)
                    .background(KudosWhite, FieldShape)
            )

            // Dropdown overlay — B8: KudosWhite background
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onToggle(false) },
                modifier = Modifier
                    .widthIn(min = 200.dp)
                    .background(KudosWhite, MenuShape)
            ) {
                options.forEach { user ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                KudoAvatar(name = user.name, size = 32.dp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = KudosDarkText
                                    )
                                    Text(
                                        text = user.code,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = KudosGray
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSelect(user)
                            onToggle(false)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun RecipientFieldEmptyPreview() {
    KudosAppTheme {
        RecipientField(
            query = "",
            selectedRecipient = null,
            options = KudosMockData.searchableUsers,
            expanded = false,
            hasError = false,
            onQueryChange = {},
            onToggle = {},
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun RecipientFieldFilledPreview() {
    KudosAppTheme {
        RecipientField(
            query = "",
            selectedRecipient = "Dương Huỳnh Xuân Nhật",
            options = KudosMockData.searchableUsers,
            expanded = false,
            hasError = false,
            onQueryChange = {},
            onToggle = {},
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
