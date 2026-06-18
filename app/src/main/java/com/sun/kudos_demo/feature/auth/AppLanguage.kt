package com.sun.kudos_demo.feature.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Supported UI languages.
 *  - [code]   : the short label shown in the dropdown ("VN" / "EN").
 *  - [locale] : the Android resource qualifier ("vi" / "en") used to resolve
 *               `res/values/strings.xml` (default = Vietnamese) vs `res/values-en/strings.xml`.
 */
enum class AppLanguage(val code: String, val locale: String) {
    VN("VN", "vi"),
    EN("EN", "en");

    companion object {
        /** Map a persisted code back to a language; unknown / null → default Vietnamese. */
        fun fromCode(code: String?): AppLanguage = entries.firstOrNull { it.code == code } ?: VN
    }
}

/**
 * App-wide current language as a reactive [StateFlow].
 *
 * The app root (MainActivity) observes [language] and overrides the Compose
 * `LocalContext` / `LocalConfiguration` locale, so every `stringResource(...)` re-resolves
 * live against the matching `values` / `values-en` bucket — no Activity recreation needed.
 *
 * This is a process-global holder (KISS for a single-Activity mock app); persistence is
 * orchestrated at the root via [com.sun.kudos_demo.data.KudosPreferences].
 */
object LanguageManager {
    private val _language = MutableStateFlow(AppLanguage.VN)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    /** Seed from persisted prefs at startup. */
    fun loadInitial(language: AppLanguage) { _language.value = language }

    /** Switch language at runtime — UI re-renders immediately; the root persists the change. */
    fun set(language: AppLanguage) { _language.value = language }
}
