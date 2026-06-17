package com.sun.kudos_demo.feature.send

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for RichTextFormatter (Phase 06).
 *
 * Tests markdown-style inline and block formatting transformations on TextFieldValue.
 * The formatter wraps selections with markers (bold, italic, strike, link) or prefixes
 * lines (list, quote).
 *
 * NOTE: These tests are JVM-based and test the text transformation logic.
 * TextFieldValue construction may require runtime stubs; focus is on verifying
 * that the wrap/prefix logic produces correct text output.
 */
class RichTextFormatterTest {

    // ===================== Bold Formatting =====================

    @Test
    fun bold_WrapsSelection() {
        // "Hello [World] Compose" → "Hello **World** Compose"
        val value = TextFieldValue(
            text = "Hello World Compose",
            selection = TextRange(6, 11)  // "World"
        )
        val result = RichTextFormatter.apply(value, "bold")
        assertTrue(result.text.contains("**World**"))
        assertEquals("Hello **World** Compose", result.text)
    }

    @Test
    fun bold_EmptySelection_InsertsMarkers() {
        // "Hello | Compose" → "Hello**** Compose" (markers adjacent when no selection)
        val value = TextFieldValue(
            text = "Hello Compose",
            selection = TextRange(5, 5)  // Empty selection at position 5
        )
        val result = RichTextFormatter.apply(value, "bold")
        assertEquals("Hello**** Compose", result.text)
    }

    @Test
    fun bold_AtStartOfText() {
        val value = TextFieldValue(
            text = "World Compose",
            selection = TextRange(0, 5)  // "World"
        )
        val result = RichTextFormatter.apply(value, "bold")
        assertEquals("**World** Compose", result.text)
    }

    @Test
    fun bold_AtEndOfText() {
        val value = TextFieldValue(
            text = "Hello World",
            selection = TextRange(6, 11)  // "World"
        )
        val result = RichTextFormatter.apply(value, "bold")
        assertEquals("Hello **World**", result.text)
    }

    // ===================== Italic Formatting =====================

    @Test
    fun italic_WrapsSelection() {
        val value = TextFieldValue(
            text = "Hello World Compose",
            selection = TextRange(6, 11)  // "World"
        )
        val result = RichTextFormatter.apply(value, "italic")
        assertTrue(result.text.contains("*World*"))
        assertEquals("Hello *World* Compose", result.text)
    }

    @Test
    fun italic_EmptySelection_InsertsMarkers() {
        val value = TextFieldValue(
            text = "Hello Compose",
            selection = TextRange(5, 5)
        )
        val result = RichTextFormatter.apply(value, "italic")
        assertEquals("Hello** Compose", result.text)  // Markers adjacent
    }

    // ===================== Strike Formatting =====================

    @Test
    fun strike_WrapsSelection() {
        val value = TextFieldValue(
            text = "Hello World Compose",
            selection = TextRange(6, 11)  // "World"
        )
        val result = RichTextFormatter.apply(value, "strike")
        assertTrue(result.text.contains("~~World~~"))
        assertEquals("Hello ~~World~~ Compose", result.text)
    }

    @Test
    fun strike_EmptySelection_InsertsMarkers() {
        val value = TextFieldValue(
            text = "Hello Compose",
            selection = TextRange(5, 5)
        )
        val result = RichTextFormatter.apply(value, "strike")
        // Four tildes ~~ at start and ~~ at end, with empty selection and space after
        assertEquals("Hello~~~~ Compose", result.text)
    }

    // ===================== Link Formatting =====================

    @Test
    fun link_WrapsSelectionWithUrlSyntax() {
        val value = TextFieldValue(
            text = "Check this link",
            selection = TextRange(6, 10)  // "this"
        )
        val result = RichTextFormatter.apply(value, "link")
        assertTrue(result.text.contains("[this](https://"))
    }

    @Test
    fun link_EmptySelection_UsesDefaultLabel() {
        val value = TextFieldValue(
            text = "Check ",
            selection = TextRange(6, 6)  // Empty
        )
        val result = RichTextFormatter.apply(value, "link")
        assertEquals("Check [text](https://)", result.text)
    }

    @Test
    fun link_AtStartOfText() {
        val value = TextFieldValue(
            text = "Kudos",
            selection = TextRange(0, 5)
        )
        val result = RichTextFormatter.apply(value, "link")
        assertEquals("[Kudos](https://)", result.text)
    }

    @Test
    fun link_AtEndOfText() {
        val value = TextFieldValue(
            text = "Check this",
            selection = TextRange(6, 10)
        )
        val result = RichTextFormatter.apply(value, "link")
        assertEquals("Check [this](https://)", result.text)
    }

    // ===================== List Formatting =====================

    @Test
    fun list_PrefixesCurrentLine() {
        // "Hello World" (caret at 6, in middle of line) → "1. Hello World" (prefix at line start)
        val value = TextFieldValue(
            text = "Hello World",
            selection = TextRange(6, 6)
        )
        val result = RichTextFormatter.apply(value, "list")
        assertEquals("1. Hello World", result.text)
    }

    @Test
    fun list_MultilineTextPrefixesCurrentLineOnly() {
        // "Line 1\nLine 2" (caret on second line at position 9) → "Line 1\n1. Line 2"
        val value = TextFieldValue(
            text = "Line 1\nLine 2",
            selection = TextRange(9, 9)  // On 'L' of "Line 2"
        )
        val result = RichTextFormatter.apply(value, "list")
        assertEquals("Line 1\n1. Line 2", result.text)
    }

    @Test
    fun list_AtStartOfText() {
        val value = TextFieldValue(
            text = "Hello",
            selection = TextRange(0, 0)
        )
        val result = RichTextFormatter.apply(value, "list")
        assertEquals("1. Hello", result.text)
    }

    @Test
    fun list_AtEndOfLine() {
        val value = TextFieldValue(
            text = "Hello\nWorld",
            selection = TextRange(11, 11)  // End of "World"
        )
        val result = RichTextFormatter.apply(value, "list")
        assertEquals("Hello\n1. World", result.text)
    }

    // ===================== Quote Formatting =====================

    @Test
    fun quote_PrefixesCurrentLine() {
        val value = TextFieldValue(
            text = "Hello World",
            selection = TextRange(6, 6)
        )
        val result = RichTextFormatter.apply(value, "quote")
        assertEquals("> Hello World", result.text)
    }

    @Test
    fun quote_MultilineTextPrefixesCurrentLineOnly() {
        val value = TextFieldValue(
            text = "Line 1\nLine 2",
            selection = TextRange(9, 9)
        )
        val result = RichTextFormatter.apply(value, "quote")
        assertEquals("Line 1\n> Line 2", result.text)
    }

    @Test
    fun quote_AtStartOfText() {
        val value = TextFieldValue(
            text = "Important",
            selection = TextRange(0, 0)
        )
        val result = RichTextFormatter.apply(value, "quote")
        assertEquals("> Important", result.text)
    }

    // ===================== Invalid Format =====================

    @Test
    fun apply_InvalidFormat_ReturnsUnchanged() {
        val value = TextFieldValue(
            text = "Hello World",
            selection = TextRange(0, 5)
        )
        val result = RichTextFormatter.apply(value, "invalid_format")
        assertEquals(value.text, result.text)
        assertEquals(value.selection, result.selection)
    }

    // ===================== Complex Scenarios =====================

    @Test
    fun multipleFormats_Applied_Sequentially() {
        // Apply bold
        var value = TextFieldValue(
            text = "Hello World",
            selection = TextRange(6, 11)
        )
        value = RichTextFormatter.apply(value, "bold")
        assertEquals("Hello **World**", value.text)

        // Now apply italic to "World" inside **
        value = TextFieldValue(
            text = "Hello **World**",
            selection = TextRange(8, 13)  // "World" inside **
        )
        value = RichTextFormatter.apply(value, "italic")
        assertTrue(value.text.contains("*"))  // Italic markers should be present
    }

    @Test
    fun formatEmptyText_BoldWrapsNothing() {
        val value = TextFieldValue(
            text = "",
            selection = TextRange(0, 0)
        )
        val result = RichTextFormatter.apply(value, "bold")
        assertEquals("****", result.text)  // Just the markers
    }

    @Test
    fun formatSpecialCharacters() {
        val value = TextFieldValue(
            text = "こんにちは 世界",
            selection = TextRange(0, 5)  // "こんにちは"
        )
        val result = RichTextFormatter.apply(value, "bold")
        assertTrue(result.text.contains("**"))
        assertTrue(result.text.contains("こんにちは"))
    }

    @Test
    fun linkWithSpecialCharactersInLabel() {
        val value = TextFieldValue(
            text = "Check đây đó",
            selection = TextRange(6, 12)  // "đây đó"
        )
        val result = RichTextFormatter.apply(value, "link")
        assertTrue(result.text.contains("[đây đó]"))
        assertTrue(result.text.contains("(https://"))
    }

    @Test
    fun quotePreservesExistingContent() {
        val value = TextFieldValue(
            text = "First line\nSecond line\nThird",
            selection = TextRange(23, 23)  // Start of "Third" (after second \n)
        )
        val result = RichTextFormatter.apply(value, "quote")
        // prefixLine finds the line start before position 23 (which is after second \n)
        // so it should add "> " at the start of "Third"
        assertTrue(result.text.contains("First line"))
        assertTrue(result.text.contains("Second line"))
        assertTrue(result.text.contains("> Third"))
    }

    @Test
    fun boldMultipleTimes_SameLine() {
        var value = TextFieldValue(
            text = "One Two Three",
            selection = TextRange(0, 3)  // "One"
        )
        value = RichTextFormatter.apply(value, "bold")
        assertEquals("**One** Two Three", value.text)

        // Now apply bold to "Three"
        value = TextFieldValue(
            text = "**One** Two Three",
            selection = TextRange(12, 17)
        )
        value = RichTextFormatter.apply(value, "bold")
        assertEquals("**One** Two **Three**", value.text)
    }

    @Test
    fun italicAndStrikeSameLine() {
        var value = TextFieldValue(
            text = "Important Obsolete",
            selection = TextRange(0, 9)  // "Important"
        )
        value = RichTextFormatter.apply(value, "italic")
        assertEquals("*Important* Obsolete", value.text)

        // Apply strike to "Obsolete"
        value = TextFieldValue(
            text = "*Important* Obsolete",
            selection = TextRange(12, 20)
        )
        value = RichTextFormatter.apply(value, "strike")
        assertEquals("*Important* ~~Obsolete~~", value.text)
    }

    // ===================== Phase 06 Review Fixes (A3: Idempotency) =====================

    @Test
    fun list_PrefixAppliedTwice_IsIdempotent() {
        // Apply list prefix once
        var value = TextFieldValue(
            text = "Hello World",
            selection = TextRange(6, 6)
        )
        value = RichTextFormatter.apply(value, "list")
        assertEquals("1. Hello World", value.text)

        // Apply list prefix again on the same line — should NOT stack
        value = TextFieldValue(
            text = "1. Hello World",
            selection = TextRange(8, 8)  // Caret inside "Hello"
        )
        value = RichTextFormatter.apply(value, "list")
        // Text should remain unchanged (idempotent)
        assertEquals("1. Hello World", value.text)
    }

    @Test
    fun quote_PrefixAppliedTwice_IsIdempotent() {
        // Apply quote prefix once
        var value = TextFieldValue(
            text = "Important note",
            selection = TextRange(5, 5)
        )
        value = RichTextFormatter.apply(value, "quote")
        assertEquals("> Important note", value.text)

        // Apply quote prefix again — should NOT stack
        value = TextFieldValue(
            text = "> Important note",
            selection = TextRange(7, 7)
        )
        value = RichTextFormatter.apply(value, "quote")
        // Text should remain unchanged (idempotent)
        assertEquals("> Important note", value.text)
    }

    @Test
    fun list_MultilineIdempotency_OnlyAffectsCurrentLine() {
        // Multiple lines, apply list to first line
        var value = TextFieldValue(
            text = "Line 1\nLine 2",
            selection = TextRange(3, 3)  // On first line
        )
        value = RichTextFormatter.apply(value, "list")
        assertEquals("1. Line 1\nLine 2", value.text)

        // Apply list again to first line — should be idempotent
        value = TextFieldValue(
            text = "1. Line 1\nLine 2",
            selection = TextRange(5, 5)
        )
        value = RichTextFormatter.apply(value, "list")
        assertEquals("1. Line 1\nLine 2", value.text)  // Unchanged

        // Apply list to second line (not yet prefixed)
        value = TextFieldValue(
            text = "1. Line 1\nLine 2",
            selection = TextRange(11, 11)  // On "Line 2"
        )
        value = RichTextFormatter.apply(value, "list")
        assertEquals("1. Line 1\n1. Line 2", value.text)  // Only second line gets prefix
    }

    @Test
    fun quote_AndBoldDoNotConflict_Idempotency() {
        // Apply quote to a line
        var value = TextFieldValue(
            text = "Important",
            selection = TextRange(0, 0)
        )
        value = RichTextFormatter.apply(value, "quote")
        assertEquals("> Important", value.text)

        // Apply bold to text AFTER quote prefix — wrapping should not affect prefix
        value = TextFieldValue(
            text = "> Important",
            selection = TextRange(2, 11)  // "Important" (after "> ")
        )
        value = RichTextFormatter.apply(value, "bold")
        assertEquals("> **Important**", value.text)

        // Apply quote again — should be idempotent (no stacking)
        value = TextFieldValue(
            text = "> **Important**",
            selection = TextRange(5, 5)
        )
        value = RichTextFormatter.apply(value, "quote")
        assertEquals("> **Important**", value.text)
    }

    @Test
    fun list_IdempotentAfterManualEdit() {
        // User manually types "1. " at start of line, then applies list formatting
        var value = TextFieldValue(
            text = "1. Already listed",
            selection = TextRange(8, 8)
        )
        // Apply list format to a line that already starts with "1. "
        value = RichTextFormatter.apply(value, "list")
        // Should detect existing prefix and not add another
        assertEquals("1. Already listed", value.text)
    }

    @Test
    fun blockFormatIdempotency_BoldAndItalicNotAffected() {
        // Bold/italic wrapping should be unaffected by block-format idempotency
        var value = TextFieldValue(
            text = "**Bold** text",
            selection = TextRange(0, 0)
        )
        value = RichTextFormatter.apply(value, "list")
        assertEquals("1. **Bold** text", value.text)

        // Reapply list — should be idempotent
        value = TextFieldValue(
            text = "1. **Bold** text",
            selection = TextRange(4, 4)
        )
        value = RichTextFormatter.apply(value, "list")
        assertEquals("1. **Bold** text", value.text)

        // Bold should still be there
        assertTrue(value.text.contains("**Bold**"))
    }
}
