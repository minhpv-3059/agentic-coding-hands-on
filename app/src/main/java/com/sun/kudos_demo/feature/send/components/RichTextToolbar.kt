package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.sun.kudos_demo.ui.theme.KudosLinkRed
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
 * Issue 2 fix: "Tiêu chuẩn cộng đồng" link is on the RIGHT of the toolbar row
 * (design node 6885:9931, inside 6885:9918). Color = rgba(228,96,96,1) = KudosLinkRed.
 * Issue 3 fix: Vertical 1dp dividers (KudosBorder) between each format button,
 * matching the border-sharing pattern on nodes 6885:9919..9929 (each button has a
 * border on all sides; adjacent buttons share a border edge = visual divider).
 *
 * A10: activeFormats removed — buttons are stateless; no highlight styling.
 * Each tap calls [onToggleFormat] which applies the format in-field via RichTextFormatter.
 *
 * @param onToggleFormat Called with the format key when user taps a button.
 * @param onCommunityStandardsClick Called when user taps the "Tiêu chuẩn cộng đồng" link.
 */
@Composable
fun RichTextToolbar(
    onToggleFormat: (String) -> Unit,
    onCommunityStandardsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(KudosWhite, ToolbarShape)
            .border(1.dp, KudosBorder, ToolbarShape)
    ) {
        // Format buttons with vertical dividers between them.
        // Design nodes 6885:9919..9929: each button cell has border on all sides;
        // adjacent cells share a border → 1dp KudosBorder vertical divider appearance.
        formatButtons.forEachIndexed { index, btn ->
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(ButtonShape)
                    .background(KudosWhite, ButtonShape)
                    .clickable { onToggleFormat(btn.format) }
                    .padding(4.dp)
            ) {
                Text(text = btn.label, style = textStyle)
            }
            // Vertical divider between format buttons (1dp, KudosBorder).
            // Also draws between the last format button and the link button.
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(KudosBorder)
            )
        }

        // Right-side "Tiêu chuẩn cộng đồng" link — design node 6885:9931.
        // Fills remaining space (flex: 1 0 0), text centered, color KudosLinkRed (rgba(228,96,96,1)).
        Spacer(Modifier.weight(1f))
        Text(
            text = "Tiêu chuẩn cộng đồng",
            style = MaterialTheme.typography.labelSmall.copy(
                color = KudosLinkRed,
                fontSize = 10.sp,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier
                .clickable { onCommunityStandardsClick() }
                .padding(horizontal = 7.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun RichTextToolbarPreview() {
    KudosAppTheme {
        RichTextToolbar(
            onToggleFormat = {},
            onCommunityStandardsClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
