package com.sun.kudos_demo.feature.send

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Functional rich-text formatting for the message field — applies markdown-style markers
 * to the current selection (or at the caret) of a [TextFieldValue].
 *
 * Inline formats wrap the selection (`**bold**`, `*italic*`, `~~strike~~`, `[text](url)`);
 * block formats prefix the current line (`1. `, `> `). The resulting plain text is what
 * gets stored on the sent Kudo, so the formatting persists into the feed.
 */
object RichTextFormatter {

    fun apply(value: TextFieldValue, format: String): TextFieldValue = when (format) {
        "bold" -> wrap(value, "**", "**")
        "italic" -> wrap(value, "*", "*")
        "strike" -> wrap(value, "~~", "~~")
        "link" -> link(value)
        "list" -> prefixLine(value, "1. ")
        "quote" -> prefixLine(value, "> ")
        else -> value
    }

    private fun wrap(value: TextFieldValue, pre: String, post: String): TextFieldValue {
        val text = value.text
        val start = value.selection.min
        val end = value.selection.max
        val selected = text.substring(start, end)
        val newText = text.substring(0, start) + pre + selected + post + text.substring(end)
        // Caret sits between markers when nothing was selected, else after the wrapped text.
        val caret = if (selected.isEmpty()) start + pre.length else end + pre.length + post.length
        return value.copy(text = newText, selection = TextRange(caret))
    }

    private fun link(value: TextFieldValue): TextFieldValue {
        val text = value.text
        val start = value.selection.min
        val end = value.selection.max
        val label = text.substring(start, end).ifEmpty { "text" }
        val inserted = "[$label](https://)"
        val newText = text.substring(0, start) + inserted + text.substring(end)
        // Place caret inside the empty URL parens for quick editing.
        val caret = start + inserted.length - 1
        return value.copy(text = newText, selection = TextRange(caret))
    }

    private fun prefixLine(value: TextFieldValue, prefix: String): TextFieldValue {
        val text = value.text
        val caret = value.selection.min
        val lineStart = text.lastIndexOf('\n', (caret - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
        // A3: idempotent — do not stack the prefix if the line already starts with it
        if (text.substring(lineStart).startsWith(prefix)) return value
        val newText = text.substring(0, lineStart) + prefix + text.substring(lineStart)
        return value.copy(text = newText, selection = TextRange(caret + prefix.length))
    }
}
