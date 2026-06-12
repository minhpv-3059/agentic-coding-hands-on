package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosWhite

private val ToolbarShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
private val ButtonShape = RoundedCornerShape(2.dp)

// Format toolbar buttons represented as text labels (avoids material-icons-extended dependency).
private data class FormatBtn(val format: String, val label: String)

private val formatButtons = listOf(
    FormatBtn("bold",   "B"),
    FormatBtn("italic", "I"),
    FormatBtn("strike", "S"),
    FormatBtn("list",   "1."),
    FormatBtn("link",   "🔗"),
    FormatBtn("quote",  "❝"),
)

/**
 * Formatting toolbar rendered above the message text field.
 *
 * A10: activeFormats removed — buttons are stateless; no highlight styling.
 * Each tap calls [onToggleFormat] which applies the format in-field via RichTextFormatter.
 *
 * @param onToggleFormat Called with the format key when user taps a button.
 */
@Composable
fun RichTextToolbar(
    onToggleFormat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(KudosWhite, ToolbarShape)
            .border(1.dp, KudosBorder, ToolbarShape)
            .padding(horizontal = 6.dp)
    ) {
        formatButtons.forEach { btn ->
            val textStyle = when (btn.format) {
                "bold"   -> MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = KudosDarkText
                )
                "italic" -> MaterialTheme.typography.labelSmall.copy(
                    fontStyle = FontStyle.Italic,
                    color = KudosDarkText
                )
                "strike" -> MaterialTheme.typography.labelSmall.copy(
                    textDecoration = TextDecoration.LineThrough,
                    color = KudosDarkText
                )
                else     -> MaterialTheme.typography.labelSmall.copy(
                    color = KudosDarkText,
                    fontSize = 11.sp
                )
            }
            Text(
                text = btn.label,
                style = textStyle,
                modifier = Modifier
                    .size(20.dp)
                    .clip(ButtonShape)
                    .background(KudosWhite, ButtonShape)
                    .clickable { onToggleFormat(btn.format) }
                    .padding(2.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun RichTextToolbarPreview() {
    KudosAppTheme {
        RichTextToolbar(
            onToggleFormat = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
