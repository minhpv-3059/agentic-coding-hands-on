package com.sun.kudos_demo.feature.notifications

import com.sun.kudos_demo.feature.auth.AppLanguage
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for NotificationsViewModel title localization (Phase 08).
 *
 * Tests the titleFor function which localizes the screen title:
 * - VN → "Thông báo"
 * - EN → "Notifications"
 *
 * This module does NOT require coroutines-test since titleFor is a pure function
 * and StateFlow .value reads are synchronous.
 */
class NotificationsViewModelTest {

    // ===================== Title Localization =====================

    @Test
    fun titleFor_WithVietnamLanguage_ReturnsVietnamesTitle() {
        val title = titleFor(AppLanguage.VN)

        assertEquals("Thông báo", title)
    }

    @Test
    fun titleFor_WithEnglishLanguage_ReturnsEnglishTitle() {
        val title = titleFor(AppLanguage.EN)

        assertEquals("Notifications", title)
    }

    @Test
    fun titleFor_VietnamAndEnglish_AreDifferent() {
        val vnTitle = titleFor(AppLanguage.VN)
        val enTitle = titleFor(AppLanguage.EN)

        assertNotEquals("Titles must differ by language", vnTitle, enTitle)
    }

    @Test
    fun titleFor_VietnamTitle_IsNotEmpty() {
        val title = titleFor(AppLanguage.VN)

        assertFalse("Title must not be empty", title.isBlank())
        assertTrue("Title must have content", title.length > 0)
    }

    @Test
    fun titleFor_EnglishTitle_IsNotEmpty() {
        val title = titleFor(AppLanguage.EN)

        assertFalse("Title must not be empty", title.isBlank())
        assertTrue("Title must have content", title.length > 0)
    }

    @Test
    fun titleFor_VietnamTitle_ContainsExpectedText() {
        val title = titleFor(AppLanguage.VN)

        // Should match exactly
        assertEquals("Thông báo", title)
        // Should contain expected characters
        assertTrue(title.contains("Thông"))
        assertTrue(title.contains("báo"))
    }

    @Test
    fun titleFor_EnglishTitle_ContainsExpectedText() {
        val title = titleFor(AppLanguage.EN)

        // Should match exactly
        assertEquals("Notifications", title)
        // Should start with expected capital letter
        assertTrue(title.startsWith("N"))
    }

    @Test
    fun titleFor_IsCaseSensitive() {
        val title = titleFor(AppLanguage.EN)

        assertEquals("Notifications", title)
        assertNotEquals("notifications", title)
        assertNotEquals("NOTIFICATIONS", title)
    }

    @Test
    fun titleFor_HasCorrectLength_Vietnamese() {
        val title = titleFor(AppLanguage.VN)

        // "Thông báo" is 9 characters (including space)
        assertEquals(9, title.length)
    }

    @Test
    fun titleFor_HasCorrectLength_English() {
        val title = titleFor(AppLanguage.EN)

        // "Notifications" is 13 characters
        assertEquals(13, title.length)
    }

    @Test
    fun titleFor_ReturnsConsistentValue_Vietnamese() {
        val first = titleFor(AppLanguage.VN)
        val second = titleFor(AppLanguage.VN)

        assertEquals("Should return same title on repeated calls", first, second)
    }

    @Test
    fun titleFor_ReturnsConsistentValue_English() {
        val first = titleFor(AppLanguage.EN)
        val second = titleFor(AppLanguage.EN)

        assertEquals("Should return same title on repeated calls", first, second)
    }

    @Test
    fun titleFor_AllLanguagesHaveTitles() {
        // Test all enum values
        for (lang in AppLanguage.values()) {
            val title = titleFor(lang)
            assertNotNull("Title must be provided for language $lang", title)
            assertFalse("Title must not be blank for language $lang", title.isBlank())
        }
    }

    @Test
    fun titleFor_NoExtraWhitespace() {
        val vnTitle = titleFor(AppLanguage.VN)
        val enTitle = titleFor(AppLanguage.EN)

        // Should not have leading/trailing spaces
        assertEquals(vnTitle, vnTitle.trim())
        assertEquals(enTitle, enTitle.trim())

        // No double spaces
        assertFalse(vnTitle.contains("  "))
        assertFalse(enTitle.contains("  "))
    }

    @Test
    fun titleFor_VietnamAndEnglish_BothCapitalized() {
        val vnTitle = titleFor(AppLanguage.VN)
        val enTitle = titleFor(AppLanguage.EN)

        // Both should start with capital letter
        assertTrue("VN title should start with capital", vnTitle[0].isUpperCase())
        assertTrue("EN title should start with capital", enTitle[0].isUpperCase())
    }
}
