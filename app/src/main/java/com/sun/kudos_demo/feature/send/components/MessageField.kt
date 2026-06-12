package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.send.RichTextFormatter
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosError
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

private val TextFieldBottomShape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)

/**
 * Multi-line message field with a functional rich-text toolbar above it.
 *
 * Tapping a toolbar button applies markdown-style formatting to the current selection
 * (or caret) via [RichTextFormatter] — the field owns a [TextFieldValue] so the selection
 * range is known. The resulting plain text is propagated up through [onMessageChange].
 *
 * A10: activeFormats removed — toolbar buttons are stateless (no highlight state).
 */
@Composable
fun MessageField(
    message: String,
    hasError: Boolean,
    onMessageChange: (String) -> Unit,
    onToggleFormat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (hasError) KudosError else KudosBorder
    var fieldValue by remember { mutableStateOf(TextFieldValue(message)) }

    // Keep in sync when the message is changed externally (e.g. reset after submit).
    LaunchedEffect(message) {
        if (message != fieldValue.text) {
            fieldValue = TextFieldValue(message, TextRange(message.length))
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        RichTextToolbar(
            onToggleFormat = { format ->
                onToggleFormat(format)
                val updated = RichTextFormatter.apply(fieldValue, format)
                fieldValue = updated
                onMessageChange(updated.text)
            }
        )

        TextField(
            value = fieldValue,
            onValueChange = { newValue ->
                fieldValue = newValue
                if (newValue.text != message) onMessageChange(newValue.text)
            },
            placeholder = {
                Text(
                    text = "Hãy gửi gắm lời cảm ơn và lời nhắn đến đồng đội tại đây nhé",
                    style = MaterialTheme.typography.bodySmall,
                    color = KudosGray
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = KudosWhite,
                unfocusedContainerColor = KudosWhite,
                disabledContainerColor = KudosWhite,
                focusedTextColor = KudosDarkText,
                unfocusedTextColor = KudosDarkText,
                cursorColor = KudosDarkText,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.bodySmall,
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 89.dp)
                .border(1.dp, borderColor, TextFieldBottomShape)
                .background(KudosWhite, TextFieldBottomShape)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Bạn có thể \"@ + tên\" để nhắc tới đồng nghiệp khác",
            style = MaterialTheme.typography.labelSmall,
            color = KudosGray
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun MessageFieldEmptyPreview() {
    KudosAppTheme {
        MessageField(
            message = "",
            hasError = false,
            onMessageChange = {},
            onToggleFormat = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun MessageFieldFilledPreview() {
    KudosAppTheme {
        MessageField(
            message = "Tôi rất **quý** bạn",
            hasError = false,
            onMessageChange = {},
            onToggleFormat = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun MessageFieldErrorPreview() {
    KudosAppTheme {
        MessageField(
            message = "",
            hasError = true,
            onMessageChange = {},
            onToggleFormat = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
