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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.ui.components.KudoAvatar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosDropdownHighlight
import com.sun.kudos_demo.ui.theme.KudosError
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

private val FieldShape = RoundedCornerShape(4.dp)
// Design node 6891:17450: background=#00070C (KudosContainer2), border=#998C5F, radius=8dp
private val MenuShape = RoundedCornerShape(8.dp)

/**
 * "Người nhận" field — label + required asterisk on the LEFT, search TextField on the RIGHT.
 * Layout matches design node 6885:9905 (flexDirection:row).
 *
 * Fix A: Uses BasicTextField to avoid Material3's built-in min-height/padding that clips
 *   text at 40dp height. Content is vertically centered via Box with wrapContentHeight.
 * Fix B: Dropdown overlay now uses DARK background #00070C (KudosContainer2) with
 *   gold border #998C5F per design node 6891:17450.
 *   Selected item highlight = rgba(255,234,158,0.20) = KudosDropdownHighlight.
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
    val textStyle = MaterialTheme.typography.bodySmall.copy(color = KudosDarkText)

    // Label LEFT, input RIGHT — design node 6885:9905 flexDirection:row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        // Label: "Người nhận *"
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.send_recipient_label),
                style = MaterialTheme.typography.bodySmall,
                color = KudosDarkText
            )
            Text(
                text = stringResource(R.string.send_recipient_required),
                style = MaterialTheme.typography.bodySmall,
                color = KudosError
            )
        }

        Spacer(Modifier.width(8.dp))

        // Search input + dropdown anchor — takes remaining width
        Box(modifier = Modifier.weight(1f)) {
            // Fix: BasicTextField with explicit 40dp height avoids Material3 min-height clipping.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(1.dp, borderColor, FieldShape)
                    .background(KudosWhite, FieldShape)
                    .padding(horizontal = 11.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = selectedRecipient ?: query,
                    onValueChange = { text ->
                        if (selectedRecipient == null || text != selectedRecipient) {
                            onQueryChange(text)
                            if (!expanded) onToggle(true)
                        }
                    },
                    textStyle = textStyle,
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if ((selectedRecipient ?: query).isEmpty()) {
                            Text(
                                text = stringResource(R.string.send_recipient_placeholder),
                                style = MaterialTheme.typography.bodySmall,
                                color = KudosGray
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Dropdown overlay — DARK background per design node 6891:17450
            // background=#00070C, border=#998C5F (gold), radius=8dp
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onToggle(false) },
                modifier = Modifier
                    .widthIn(min = 200.dp)
                    .background(KudosContainer2, MenuShape)
                    .border(1.dp, KudosBorder, MenuShape)
            ) {
                options.forEach { user ->
                    // Selected items get rgba(255,234,158,0.20) highlight (design node 6891:17451)
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                KudoAvatar(name = user.name, size = 32.dp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = KudosWhite
                                    )
                                    Text(
                                        text = user.code,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = KudosGray
                                    )
                                }
                            }
                        },
                        modifier = Modifier.background(
                            if (user.name == selectedRecipient) KudosDropdownHighlight
                            else KudosContainer2
                        ),
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
