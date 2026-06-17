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

1. **FULL-APP runtime i18n migration (expanded from "UI toggle only")**
   - `feature/auth/AppLanguage.kt` (enum + `LanguageManager` StateFlow singleton + prefs persist)
   - `ui/components/LanguageDropdown.kt` (LanguageTrigger + LanguageDropdownPanel + cờ VN/EN)
   - `MainActivity` locale override via CompositionLocalProvider(LocalContext+LocalConfiguration) — live VN↔EN switch on every screen, no Activity recreation
   - **Per-feature string resources:** `res/values/strings_{home,feed,award,profile,noti,secretbox,send}.xml` + `values-en/` counterparts; shared `strings.xml` (card/filter/rules/error/login keys). Screens migrated: Login, Rules, Access Denied, Not Found (initial phase) **+ Home, Feed (AllKudos/View/Search), Awards, Profile (own+other), Notifications, Secret Box, Send Kudos + community standards, + shared KudosCard + feed dropdowns**. KudosTopBar self-manages VN/EN dropdown on every header screen.
   - Award/SecretBox/Notification data classes refactored: user-text String fields → @StringRes Int; tests updated to resource-id assertions (obsolete NotificationsViewModelTest removed).
   - Bug fix: Send-Kudos crash (ContextThemeWrapper locale wrapping) resolved.
   - Verified: build PASS, 26 unit-test files green, all main screens + shared components localize live VN↔EN on emulator-5554.

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

- **i18n scope EXPANDED — NOW COMPLETE FULL-APP**: Contract specified "UI toggle only"; expanded to full runtime i18n infrastructure with persistent language state, live stringResource resolution across **all 12 main screens + shared components**. Rationale: test case FUN_007/008 required app-wide text switching. No regression — comprehensive unit-test verification (26 files green). Migration complete: Login, Rules, Access Denied, Not Found, Home, Feed, Awards, Profile, Notifications, Secret Box, Send Kudos (+ Community Standards) + KudosCard + filter dropdowns all localize VN↔EN live.
- **Error screens parametric**: Implemented shared ErrorScreen scaffold (DRY) instead of 2 separate designs; 403 and 404 share robot illustration per spec.
- **Demo footer on Rules**: Added temporary link to access 403/404 since mock app has no backend trigger; 404 also set as NavHost fallback for unknown routes.

## MoMorph refs
- [iOS] Thể lệ: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/zIuFaHAid4
- [iOS] Language dropdown: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/uUvW6Qm1ve
- [iOS] Access denied: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/k-7zJk2B7s
- [iOS] Not Found: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/sn2mdavs1a
