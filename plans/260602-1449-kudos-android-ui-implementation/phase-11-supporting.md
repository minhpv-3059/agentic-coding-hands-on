---
phase: "11"
title: Supporting Screens
priority: p2
status: done
blockedBy: ["phase-02-navigation"]
---

# Phase 11 — Supporting Screens

**Goal:** Implement language selector, rules/thể lệ, and error pages.

**Status:** ✅ Complete (2026-06-17 — verified build pass, 26 unit-test files green incl. +20 new i18n tests, 4 screens confirmed on emulator-5554)

## Delivered

1. **Language dropdown + FULL runtime i18n infrastructure**
   - `feature/auth/AppLanguage.kt` (enum + `LanguageManager` StateFlow singleton)
   - `ui/components/LanguageDropdown.kt` (LanguageTrigger + LanguageDropdownPanel + languageFlagRes)
   - `KudosPreferences.languageCode`/`setLanguageCode` (DataStore persist)
   - `MainActivity` locale override via CompositionLocalProvider(LocalContext+LocalConfiguration) — switches VN↔EN live, no Activity recreation
   - Text migrated for Login + Rules + Error screens; Home/Feed top-bar switcher drives global LanguageManager

2. **Rules / Thể lệ** (`feature/rules/RulesScreen.kt` + components)
   - Detail-flow screen with key-visual header, 4 hero pills, 6 collection icons, National Kudos section
   - Close/Write Kudos buttons wired to nav routes

3. **Error screens** (`feature/error/ErrorScreen.kt` + AccessDeniedScreen + NotFoundScreen)
   - Shared parametric ErrorScreen scaffold
   - Robot "404" illustration + UK flag icon extracted from MoMorph S3
   - 403 reuses 404 robot per design spec

4. **Navigation** (`navigation/AppNavGraph.kt`)
   - RULES/ERROR_403/ERROR_404 placeholders replaced with real screens
   - Home Kudos "Chi tiết ↗" routed to RULES
   - Demo footer on Rules enables 403/404 access
   - "Go back to Home" CTA → Home

## Deviations from contract

- **i18n scope EXPANDED**: Contract specified "UI toggle only"; delivered full runtime i18n infrastructure with persistent language state, live stringResource resolution, and locale override. User decision rationale: test case FUN_007/008 required text switching on app-wide demand.
- **Text migration limited**: Infra complete + migrated Login/Rules/Error screens this phase; other 8 screens (home/feed/send/profile/awards/noti/secretbox) remain hardcoded VN to avoid regression. Deferred migration aligns with AIDD risk tolerance.
- **Error screens parametric**: Implemented shared ErrorScreen scaffold (DRY) instead of 2 separate designs; 403 and 404 share robot illustration per spec.
- **Demo footer on Rules**: Added temporary link to access 403/404 since mock app has no backend trigger; 404 also set as NavHost fallback for unknown routes.

## MoMorph refs
- [iOS] Thể lệ: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/zIuFaHAid4
- [iOS] Language dropdown: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/uUvW6Qm1ve
- [iOS] Access denied: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/k-7zJk2B7s
- [iOS] Not Found: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/sn2mdavs1a
