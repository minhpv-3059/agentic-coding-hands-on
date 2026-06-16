package com.sun.kudos_demo.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import org.junit.Assert.*
import org.junit.Test

class MarkdownTextTest {

    @Test
    fun plainText_noMarkdown_returnsVerbatim() {
        val raw = "Hello world"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("Hello world", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun plainText_emptyString_returnsEmpty() {
        val raw = ""
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun bold_singleWord_stripMarkersAndApplyBold() {
        val raw = "Cam **on** roi"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("Cam on roi", result.text)
        assertEquals(1, result.spanStyles.size)

        val boldSpan = result.spanStyles[0]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)
        assertEquals(4, boldSpan.start)  // starts at 'o' in "on"
        assertEquals(6, boldSpan.end)    // ends after 'n'
    }

    @Test
    fun bold_multipleBoldSegments_bothAreStyled() {
        val raw = "**hello** and **world**"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("hello and world", result.text)
        assertEquals(2, result.spanStyles.size)

        val first = result.spanStyles[0]
        assertEquals(FontWeight.Bold, first.item.fontWeight)
        assertEquals(0, first.start)
        assertEquals(5, first.end)

        val second = result.spanStyles[1]
        assertEquals(FontWeight.Bold, second.item.fontWeight)
        assertEquals(10, second.start)
        assertEquals(15, second.end)
    }

    @Test
    fun italic_singleWord_stripMarkersAndApplyItalic() {
        val raw = "a *italic* b"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("a italic b", result.text)
        assertEquals(1, result.spanStyles.size)

        val italicSpan = result.spanStyles[0]
        assertEquals(FontStyle.Italic, italicSpan.item.fontStyle)
        assertEquals(2, italicSpan.start)
        assertEquals(8, italicSpan.end)
    }

    @Test
    fun italic_avoidDoubleAsteriskInside_matchesSingleAsterisk() {
        val raw = "Use *asterisk* not **double**"
        val result = parseKudoMarkdown(raw, Color.Red)

        // The regex prioritizes ** first, then *...*
        // "Use " (plain) + "*asterisk*" (italic) + " not " (plain) + "**double**" (bold)
        assertEquals("Use asterisk not double", result.text)
        assertEquals(2, result.spanStyles.size)

        val italicSpan = result.spanStyles[0]
        assertEquals(FontStyle.Italic, italicSpan.item.fontStyle)
        // "Use " = 4 chars, then "asterisk" starts at index 4
        assertEquals(4, italicSpan.start)
        assertEquals(12, italicSpan.end)

        val boldSpan = result.spanStyles[1]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)
        // "Use asterisk not " = 17 chars, then "double" starts at index 17
        assertEquals(17, boldSpan.start)
        assertEquals(23, boldSpan.end)
    }

    @Test
    fun strikethrough_singleWord_stripMarkersAndApplyLineThrough() {
        val raw = "This ~~is~~ works"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("This is works", result.text)
        assertEquals(1, result.spanStyles.size)

        val strikeSpan = result.spanStyles[0]
        assertEquals(TextDecoration.LineThrough, strikeSpan.item.textDecoration)
        assertEquals(5, strikeSpan.start)
        assertEquals(7, strikeSpan.end)
    }

    @Test
    fun strikethrough_multipleSegments_bothAreStyled() {
        val raw = "~~old~~ ~~way~~"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("old way", result.text)
        assertEquals(2, result.spanStyles.size)

        val first = result.spanStyles[0]
        assertEquals(TextDecoration.LineThrough, first.item.textDecoration)
        assertEquals(0, first.start)
        assertEquals(3, first.end)

        val second = result.spanStyles[1]
        assertEquals(TextDecoration.LineThrough, second.item.textDecoration)
        assertEquals(4, second.start)
        assertEquals(7, second.end)
    }

    @Test
    fun link_rendersLabelWithColorAndUnderline() {
        val raw = "Visit [Sun](https://sun.com) now"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("Visit Sun now", result.text)
        assertEquals(1, result.spanStyles.size)

        val linkSpan = result.spanStyles[0]
        assertEquals(Color.Red, linkSpan.item.color)
        assertEquals(TextDecoration.Underline, linkSpan.item.textDecoration)
        assertEquals(6, linkSpan.start)
        assertEquals(9, linkSpan.end)
    }

    @Test
    fun link_usesProvidedLinkColor() {
        val customColor = Color.Green
        val raw = "[click](url)"
        val result = parseKudoMarkdown(raw, customColor)

        assertEquals("click", result.text)
        assertEquals(1, result.spanStyles.size)
        assertEquals(customColor, result.spanStyles[0].item.color)
    }

    @Test
    fun link_emptyUrl_stillRendersLabel() {
        val raw = "[label]()"
        val result = parseKudoMarkdown(raw, Color.Blue)

        assertEquals("label", result.text)
        assertEquals(1, result.spanStyles.size)
        assertEquals(Color.Blue, result.spanStyles[0].item.color)
    }

    @Test
    fun link_multipleLinks_eachIsStyled() {
        val raw = "[one](a) and [two](b)"
        val result = parseKudoMarkdown(raw, Color.Magenta)

        assertEquals("one and two", result.text)
        assertEquals(2, result.spanStyles.size)

        val first = result.spanStyles[0]
        assertEquals(Color.Magenta, first.item.color)
        assertEquals(TextDecoration.Underline, first.item.textDecoration)
        assertEquals(0, first.start)
        assertEquals(3, first.end)

        val second = result.spanStyles[1]
        assertEquals(Color.Magenta, second.item.color)
        assertEquals(TextDecoration.Underline, second.item.textDecoration)
        assertEquals(8, second.start)
        assertEquals(11, second.end)
    }

    @Test
    fun mixed_boldAndItalic_bothAreStyled() {
        val raw = "a **b** c *d*"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("a b c d", result.text)
        assertEquals(2, result.spanStyles.size)

        val boldSpan = result.spanStyles[0]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)
        assertEquals(2, boldSpan.start)
        assertEquals(3, boldSpan.end)

        val italicSpan = result.spanStyles[1]
        assertEquals(FontStyle.Italic, italicSpan.item.fontStyle)
        assertEquals(6, italicSpan.start)
        assertEquals(7, italicSpan.end)
    }

    @Test
    fun mixed_boldAndStrike_bothAreStyled() {
        val raw = "**bold** and ~~strike~~"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("bold and strike", result.text)
        assertEquals(2, result.spanStyles.size)

        val boldSpan = result.spanStyles[0]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)
        assertEquals(0, boldSpan.start)
        assertEquals(4, boldSpan.end)

        val strikeSpan = result.spanStyles[1]
        assertEquals(TextDecoration.LineThrough, strikeSpan.item.textDecoration)
        assertEquals(9, strikeSpan.start)
        assertEquals(15, strikeSpan.end)
    }

    @Test
    fun mixed_boldAndLink_bothAreStyled() {
        val raw = "**bold** [link](url) text"
        val result = parseKudoMarkdown(raw, Color.Blue)

        assertEquals("bold link text", result.text)
        assertEquals(2, result.spanStyles.size)

        val boldSpan = result.spanStyles[0]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)
        assertEquals(0, boldSpan.start)
        assertEquals(4, boldSpan.end)

        val linkSpan = result.spanStyles[1]
        assertEquals(Color.Blue, linkSpan.item.color)
        assertEquals(TextDecoration.Underline, linkSpan.item.textDecoration)
        assertEquals(5, linkSpan.start)
        assertEquals(9, linkSpan.end)
    }

    @Test
    fun mixed_allFormats_allAreStyled() {
        val raw = "**bold** *italic* ~~strike~~ [link](url)"
        val result = parseKudoMarkdown(raw, Color.Cyan)

        assertEquals("bold italic strike link", result.text)
        assertEquals(4, result.spanStyles.size)

        val boldSpan = result.spanStyles[0]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)
        assertEquals(0, boldSpan.start)
        assertEquals(4, boldSpan.end)

        val italicSpan = result.spanStyles[1]
        assertEquals(FontStyle.Italic, italicSpan.item.fontStyle)
        assertEquals(5, italicSpan.start)
        assertEquals(11, italicSpan.end)

        val strikeSpan = result.spanStyles[2]
        assertEquals(TextDecoration.LineThrough, strikeSpan.item.textDecoration)
        assertEquals(12, strikeSpan.start)
        assertEquals(18, strikeSpan.end)

        val linkSpan = result.spanStyles[3]
        assertEquals(Color.Cyan, linkSpan.item.color)
        assertEquals(TextDecoration.Underline, linkSpan.item.textDecoration)
        assertEquals(19, linkSpan.start)
        assertEquals(23, linkSpan.end)
    }

    @Test
    fun unterminated_bold_leftVerbatim() {
        val raw = "This **unfinished"
        val result = parseKudoMarkdown(raw, Color.Red)

        // No closing **, so the regex won't match — text is returned as-is
        assertEquals("This **unfinished", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun unterminated_italic_leftVerbatim() {
        val raw = "This *unfinished text"
        val result = parseKudoMarkdown(raw, Color.Red)

        // Single * requires non-* chars and closing *, so partial match is left verbatim
        assertEquals("This *unfinished text", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun unterminated_strike_leftVerbatim() {
        val raw = "This ~~unfinished"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("This ~~unfinished", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun incomplete_link_leftVerbatim() {
        val raw = "This [incomplete"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("This [incomplete", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun blockPrefix_notStripped_treatedAsLiteralText() {
        val raw = "1. First item"
        val result = parseKudoMarkdown(raw, Color.Red)

        // Block prefixes are intentionally left as literal text per the design
        assertEquals("1. First item", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun blockPrefix_quote_notStripped() {
        val raw = "> This is a quote"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("> This is a quote", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun nestedMarkers_innerMarkdownIgnored() {
        val raw = "**bold with *inner* attempt**"
        val result = parseKudoMarkdown(raw, Color.Red)

        // The regex is non-greedy (.+?) so ** matches first: **bold with *inner* attempt**
        // The content between ** is "bold with *inner* attempt" (with the * chars literal)
        assertEquals("bold with *inner* attempt", result.text)
        assertEquals(1, result.spanStyles.size)

        val boldSpan = result.spanStyles[0]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)
        assertEquals(0, boldSpan.start)
        assertEquals(25, boldSpan.end)
    }

    @Test
    fun adjacentFormatting_noGaps() {
        val raw = "**bold***italic*"
        val result = parseKudoMarkdown(raw, Color.Red)

        // ** matches "bold", then * matches "italic"
        assertEquals("bolditalic", result.text)
        assertEquals(2, result.spanStyles.size)

        val boldSpan = result.spanStyles[0]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)
        assertEquals(0, boldSpan.start)
        assertEquals(4, boldSpan.end)

        val italicSpan = result.spanStyles[1]
        assertEquals(FontStyle.Italic, italicSpan.item.fontStyle)
        assertEquals(4, italicSpan.start)
        assertEquals(10, italicSpan.end)
    }

    @Test
    fun whitespace_preserved() {
        val raw = "  **bold**  text  "
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("  bold  text  ", result.text)
        assertEquals(1, result.spanStyles.size)

        val boldSpan = result.spanStyles[0]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)
        assertEquals(2, boldSpan.start)
        assertEquals(6, boldSpan.end)
    }

    @Test
    fun specialChars_inPlainText_preserved() {
        val raw = "Hello @user #tag $5.00"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("Hello @user #tag $5.00", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun specialChars_insideMarkdown_preserved() {
        val raw = "**bold@123** *italic#xyz*"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("bold@123 italic#xyz", result.text)
        assertEquals(2, result.spanStyles.size)
    }

    @Test
    fun unicode_plainText() {
        val raw = "Xin chào 你好 🙏"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("Xin chào 你好 🙏", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun unicode_insideMarkdown() {
        val raw = "**Xin chào** *你好* ~~🙏~~"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("Xin chào 你好 🙏", result.text)
        assertEquals(3, result.spanStyles.size)
    }

    @Test
    fun link_withSpecialCharsInUrl() {
        val raw = "[click](https://example.com?foo=bar&baz=qux)"
        val result = parseKudoMarkdown(raw, Color.Red)

        // Only the label is captured and rendered; URL is ignored (not stored in AnnotatedString)
        assertEquals("click", result.text)
        assertEquals(1, result.spanStyles.size)
        assertEquals(Color.Red, result.spanStyles[0].item.color)
    }

    @Test
    fun singleCharBold() {
        val raw = "**a**"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("a", result.text)
        assertEquals(1, result.spanStyles.size)
        assertEquals(FontWeight.Bold, result.spanStyles[0].item.fontWeight)
        assertEquals(0, result.spanStyles[0].start)
        assertEquals(1, result.spanStyles[0].end)
    }

    @Test
    fun singleCharItalic() {
        val raw = "*a*"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("a", result.text)
        assertEquals(1, result.spanStyles.size)
        assertEquals(FontStyle.Italic, result.spanStyles[0].item.fontStyle)
        assertEquals(0, result.spanStyles[0].start)
        assertEquals(1, result.spanStyles[0].end)
    }

    @Test
    fun singleCharStrike() {
        val raw = "~~a~~"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals("a", result.text)
        assertEquals(1, result.spanStyles.size)
        assertEquals(TextDecoration.LineThrough, result.spanStyles[0].item.textDecoration)
        assertEquals(0, result.spanStyles[0].start)
        assertEquals(1, result.spanStyles[0].end)
    }

    @Test
    fun realWorldExample_vietnameseKudosMessage() {
        val raw = "**Cảm ơn** *bạn* vì ~~lười~~ *chăm chỉ* và [yêu thương](https://example.com) team!"
        val result = parseKudoMarkdown(raw, Color.Red)

        assertEquals(
            "Cảm ơn bạn vì lười chăm chỉ và yêu thương team!",
            result.text
        )
        // Spans: bold (**Cảm ơn**), italic (*bạn*), strike (~~lười~~), italic (*chăm chỉ*), link ([yêu thương])
        assertEquals(5, result.spanStyles.size)

        // Verify each span is correct (order: bold, italic, strike, italic, link)
        val boldSpan = result.spanStyles[0]
        assertEquals(FontWeight.Bold, boldSpan.item.fontWeight)

        val italicSpan1 = result.spanStyles[1]
        assertEquals(FontStyle.Italic, italicSpan1.item.fontStyle)

        val strikeSpan = result.spanStyles[2]
        assertEquals(TextDecoration.LineThrough, strikeSpan.item.textDecoration)

        val italicSpan2 = result.spanStyles[3]
        assertEquals(FontStyle.Italic, italicSpan2.item.fontStyle)

        val linkSpan = result.spanStyles[4]
        assertEquals(Color.Red, linkSpan.item.color)
        assertEquals(TextDecoration.Underline, linkSpan.item.textDecoration)
    }
}
