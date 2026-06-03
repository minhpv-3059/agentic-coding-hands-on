---
phase: "03"
title: Authentication — Login
priority: p0
status: done
blockedBy: []
---

# Phase 03 — Authentication

**Goal:** Implement Login screen pixel-perfect from MoMorph design.

**Status:** ✅ DONE — 2026-06-03

## MoMorph refs
- [iOS] Login: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/8HGlvYGJWq
- [iOS] Language dropdown: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/uUvW6Qm1ve

## Files Created
- `app/src/main/java/com/sun/kudos_demo/feature/auth/LoginScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/auth/LoginViewModel.kt`

## Completed Implementation

### LoginScreen.kt
- Background image (ROOT FURTHER keyvisual) with scrim overlay
- ROOT FURTHER logo + description text (VN/EN bilingual support)
- "Let's Explore Together" text alignment and styling
- Login button with 1-second mock auth delay → HOME navigation
- Language dropdown overlay (VN/EN toggle) with 48 dp touch target
- Edge-to-edge support: contentWindowInsets=0
- Material3 brand colors (gold accent, navy background)

### LoginViewModel.kt
- `AppLanguage` enum (VN, EN)
- `loading: StateFlow<Boolean>` for button state
- `selectedLanguage: StateFlow<AppLanguage>` for lang selection
- `onLoginClick(callback)` — 1s delay mock auth
- `setLanguage(lang)` — updates language state

### Assets Added
- `bg_login_keyvisual.png` (from MoMorph design, 1x resolution)
- `ic_logo_saa.png` (ROOT FURTHER logo)
- `img_root_further.png` (unused, part of design assets)
- `ic_google.xml` (vector drawable, pre-created)

### Navigation Integration
- `AppNavGraph.kt` — LOGIN is now startDestination
- `LoginScreen` route wired; login success navigates to HOME
- `KudosBottomNav` — navigationBarsPadding applied for safe area

## Known Limitations
- Strings hardcoded in Kotlin (not yet in strings.xml) — acceptable for demo phase
- ViewModel uses callback lambda (not SharedFlow) — acceptable for mock auth
- Background image at 1x resolution (no hdpi/xhdpi variants yet)
- Language selection is UI-only (not persisted or propagated app-wide)

## Integration contract
- On login success → navigate to `home` ✅
- Language dropdown → overlay composable (reusable) ✅

## Out of scope
- Real authentication API — mock/stub only in this phase ✅
- Password reset flow ✅
