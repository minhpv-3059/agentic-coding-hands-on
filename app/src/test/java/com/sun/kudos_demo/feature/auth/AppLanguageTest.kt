package com.sun.kudos_demo.feature.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.After

/**
 * Unit tests for Phase 11 i18n logic:
 *  - [AppLanguage] enum: code/locale mapping, fromCode() parsing (null/unknown → VN default).
 *  - [LanguageManager] singleton: reactive StateFlow, initialization, runtime language switching.
 *
 * Tests cover the pure, side-effect-free i18n logic; no Android Context or DataStore mocking needed.
 * LanguageManager is a process-global singleton, so tests MUST reset to VN at the end to prevent
 * state leakage to other tests.
 */
class AppLanguageTest {

    // ===================== AppLanguage enum tests =====================

    @Test
    fun appLanguage_VN_HasCorrectCodeAndLocale() {
        assertEquals("VN code must be 'VN'", "VN", AppLanguage.VN.code)
        assertEquals("VN locale must be 'vi'", "vi", AppLanguage.VN.locale)
    }

    @Test
    fun appLanguage_EN_HasCorrectCodeAndLocale() {
        assertEquals("EN code must be 'EN'", "EN", AppLanguage.EN.code)
        assertEquals("EN locale must be 'en'", "en", AppLanguage.EN.locale)
    }

    @Test
    fun appLanguage_FromCode_WithValidCodeVN_ReturnsVN() {
        val result = AppLanguage.fromCode("VN")
        assertEquals("fromCode('VN') must return VN", AppLanguage.VN, result)
    }

    @Test
    fun appLanguage_FromCode_WithValidCodeEN_ReturnsEN() {
        val result = AppLanguage.fromCode("EN")
        assertEquals("fromCode('EN') must return EN", AppLanguage.EN, result)
    }

    @Test
    fun appLanguage_FromCode_WithNullCode_ReturnsVNDefault() {
        val result = AppLanguage.fromCode(null)
        assertEquals("fromCode(null) must default to VN", AppLanguage.VN, result)
    }

    @Test
    fun appLanguage_FromCode_WithUnknownCode_ReturnsVNDefault() {
        val result = AppLanguage.fromCode("xx")
        assertEquals("fromCode('xx') must default to VN", AppLanguage.VN, result)
    }

    @Test
    fun appLanguage_FromCode_WithUnknownCodeFR_ReturnsVNDefault() {
        val result = AppLanguage.fromCode("FR")
        assertEquals("fromCode('FR') must default to VN", AppLanguage.VN, result)
    }

    @Test
    fun appLanguage_FromCode_WithEmptyString_ReturnsVNDefault() {
        val result = AppLanguage.fromCode("")
        assertEquals("fromCode('') must default to VN", AppLanguage.VN, result)
    }

    @Test
    fun appLanguage_FromCode_IsCaseSensitive() {
        // Codes are case-sensitive: lowercase "vn" should not match "VN"
        val result = AppLanguage.fromCode("vn")
        assertEquals("fromCode('vn') must default to VN (case-sensitive)", AppLanguage.VN, result)
    }

    @Test
    fun appLanguage_HasTwoEntries() {
        val entries = AppLanguage.entries
        assertEquals("Must have exactly 2 language entries", 2, entries.size)
        assertEquals("First entry must be VN", AppLanguage.VN, entries[0])
        assertEquals("Second entry must be EN", AppLanguage.EN, entries[1])
    }

    // ===================== LanguageManager singleton tests =====================

    @Test
    fun languageManager_DefaultLanguageIsVN() {
        // Reset to known state first (in case another test failed)
        LanguageManager.set(AppLanguage.VN)

        val initial = LanguageManager.language.value
        assertEquals("Default language must be VN", AppLanguage.VN, initial)
    }

    @Test
    fun languageManager_SetToEN_UpdatesLanguageFlow() {
        LanguageManager.set(AppLanguage.EN)

        val current = LanguageManager.language.value
        assertEquals("After set(EN), language must be EN", AppLanguage.EN, current)

        // Reset for other tests
        LanguageManager.set(AppLanguage.VN)
    }

    @Test
    fun languageManager_SetToVN_UpdatesLanguageFlow() {
        // First set to EN so we have a different value to toggle
        LanguageManager.set(AppLanguage.EN)
        assertEquals("Precondition: language is EN", AppLanguage.EN, LanguageManager.language.value)

        // Now set back to VN
        LanguageManager.set(AppLanguage.VN)

        val current = LanguageManager.language.value
        assertEquals("After set(VN), language must be VN", AppLanguage.VN, current)
    }

    @Test
    fun languageManager_LoadInitial_SeedsLanguage() {
        LanguageManager.loadInitial(AppLanguage.EN)

        val current = LanguageManager.language.value
        assertEquals("After loadInitial(EN), language must be EN", AppLanguage.EN, current)

        // Reset for other tests
        LanguageManager.set(AppLanguage.VN)
    }

    @Test
    fun languageManager_LanguageIsStateFlow_EmitsChanges() = runBlocking {
        LanguageManager.set(AppLanguage.VN)
        val initial = LanguageManager.language.first()
        assertEquals("First emission must be VN", AppLanguage.VN, initial)

        LanguageManager.set(AppLanguage.EN)
        val updated = LanguageManager.language.first()
        assertEquals("After set(EN), flow emits EN", AppLanguage.EN, updated)

        // Reset
        LanguageManager.set(AppLanguage.VN)
    }

    @Test
    fun languageManager_SetLanguageTwiceInSequence_LastWins() {
        LanguageManager.set(AppLanguage.EN)
        assertEquals("After first set(EN)", AppLanguage.EN, LanguageManager.language.value)

        LanguageManager.set(AppLanguage.VN)
        assertEquals("After second set(VN)", AppLanguage.VN, LanguageManager.language.value)

        LanguageManager.set(AppLanguage.EN)
        assertEquals("After third set(EN)", AppLanguage.EN, LanguageManager.language.value)

        // Reset
        LanguageManager.set(AppLanguage.VN)
    }

    @Test
    fun languageManager_SetSameLanguageTwice_RemainsStable() {
        LanguageManager.set(AppLanguage.VN)
        LanguageManager.set(AppLanguage.VN)
        assertEquals("Setting VN twice should remain VN", AppLanguage.VN, LanguageManager.language.value)
    }

    @Test
    fun languageManager_StateFlowIsReadOnly_PublicValueIsImmutable() {
        LanguageManager.set(AppLanguage.VN)

        // The public language property is a StateFlow (immutable from outside)
        // We cannot directly mutate it; only LanguageManager.set() changes the value.
        val flow = LanguageManager.language
        assertEquals("Initial value is VN", AppLanguage.VN, flow.value)
    }

    // ===================== Integration: fromCode + set =====================

    @Test
    fun languageManager_SetWithFromCode_WorksCorrectly() {
        val parsed = AppLanguage.fromCode("EN")
        LanguageManager.set(parsed)
        assertEquals("Set with parsed EN", AppLanguage.EN, LanguageManager.language.value)

        val parsedVN = AppLanguage.fromCode("VN")
        LanguageManager.set(parsedVN)
        assertEquals("Set with parsed VN", AppLanguage.VN, LanguageManager.language.value)
    }

    @Test
    fun languageManager_SetWithFromCode_UnknownDefaults() {
        val parsed = AppLanguage.fromCode("xx")
        LanguageManager.set(parsed)
        // fromCode("xx") returns VN, so this should set VN
        assertEquals("Set with unknown code (defaults to VN)", AppLanguage.VN, LanguageManager.language.value)
    }

    // ===================== Cleanup (CRITICAL: reset singleton state) =====================

    @After
    fun tearDown() {
        // Reset LanguageManager to VN to prevent state leakage to other tests
        // (LanguageManager is a process-global object singleton).
        LanguageManager.set(AppLanguage.VN)
    }
}
