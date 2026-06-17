package com.sun.kudos_demo.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

/**
 * Inline markdown produced by the Send Kudos rich-text toolbar
 * (see [com.sun.kudos_demo.feature.send.RichTextFormatter]):
 *   `**bold**`  `*italic*`  `~~strike~~`  `[label](url)`
 * Block prefixes ("1. ", "> ") are left as literal text — they read fine inline.
 * `.+?` never spans a newline, so matches stay within a single line.
 */
private val INLINE_MARKDOWN =
    Regex("""\*\*(.+?)\*\*|~~(.+?)~~|\[([^\]]+)\]\(([^)]*)\)|\*([^*\n]+?)\*""")

/** Parse the small markdown subset into a styled [AnnotatedString]; unknown text is kept verbatim. */
fun parseKudoMarkdown(raw: String, linkColor: Color): AnnotatedString = buildAnnotatedString {
    var last = 0
    for (m in INLINE_MARKDOWN.findAll(raw)) {
        if (m.range.first > last) append(raw.substring(last, m.range.first))
        when {
            m.groups[1] != null ->
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(m.groupValues[1]) }
            m.groups[2] != null ->
                withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append(m.groupValues[2]) }
            m.groups[3] != null ->
                withStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)) {
                    append(m.groupValues[3])
                }
            m.groups[5] != null ->
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(m.groupValues[5]) }
        }
        last = m.range.last + 1
    }
    if (last < raw.length) append(raw.substring(last))
}

/**
 * Drop-in replacement for [Text] that renders a kudo message with inline markdown formatting.
 * Plain (marker-free) text renders identically to a normal Text.
 */
@Composable
fun MarkdownText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    linkColor: Color = color
) {
    Text(
        text = parseKudoMarkdown(text, linkColor),
        style = style,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}
