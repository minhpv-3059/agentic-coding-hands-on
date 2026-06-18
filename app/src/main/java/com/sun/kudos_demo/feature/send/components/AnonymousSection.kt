package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite
// B6: checked color verified from design node 6885:9995 — rgba(153,140,95,1) = KudosBorder

private val NicknameFieldShape = RoundedCornerShape(4.dp)

/**
 * Anonymous send section:
 *  - Checkbox row "Gửi lời cám ơn và ghi nhận ẩn danh"
 *  - When [isAnonymous] is true, reveals a "Nickname ẩn danh *" labeled text field.
 */
@Composable
fun AnonymousSection(
    isAnonymous: Boolean,
    anonymousNickname: String,
    onAnonymousToggle: (Boolean) -> Unit,
    onNicknameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Checkbox row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAnonymousToggle(!isAnonymous) }
        ) {
            Checkbox(
                checked = isAnonymous,
                onCheckedChange = { onAnonymousToggle(it) },
                colors = CheckboxDefaults.colors(
                    // B6: design node 6885:9995 checked fill = rgba(153,140,95,1) = KudosBorder
                    checkedColor = KudosBorder,
                    uncheckedColor = KudosBorder,
                    checkmarkColor = KudosWhite
                ),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.padding(start = 8.dp))
            Text(
                text = stringResource(R.string.send_anonymous_checkbox),
                style = MaterialTheme.typography.bodySmall,
                color = KudosDarkText
            )
        }

        // Nickname field — only visible when anonymous is checked
        if (isAnonymous) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.send_anonymous_nickname_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = KudosDarkText
                )
                Text(
                    text = stringResource(R.string.send_anonymous_nickname_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = KudosGold
                )
            }
            Spacer(Modifier.height(4.dp))
            // Fix: BasicTextField inside a fixed-height Box avoids Material3 TextField
            // min-height (56dp) clipping text at the design's 40dp field height.
            // Pattern matches RecipientField.kt. Node 6885:9914: h=40dp, radius≈4dp.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(1.dp, KudosBorder, NicknameFieldShape)
                    .background(KudosWhite, NicknameFieldShape)
                    .padding(horizontal = 11.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = anonymousNickname,
                    onValueChange = onNicknameChange,
                    textStyle = MaterialTheme.typography.bodySmall.copy(color = KudosDarkText),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (anonymousNickname.isEmpty()) {
                            Text(
                                text = stringResource(R.string.send_anonymous_nickname_placeholder),
                                style = MaterialTheme.typography.bodySmall,
                                color = KudosGray
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun AnonymousSectionUncheckedPreview() {
    KudosAppTheme {
        AnonymousSection(
            isAnonymous = false,
            anonymousNickname = "",
            onAnonymousToggle = {},
            onNicknameChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun AnonymousSectionCheckedPreview() {
    KudosAppTheme {
        AnonymousSection(
            isAnonymous = true,
            anonymousNickname = "Doraemon",
            onAnonymousToggle = {},
            onNicknameChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
