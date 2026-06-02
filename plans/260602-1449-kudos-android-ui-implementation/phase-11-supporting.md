---
phase: "11"
title: Supporting Screens
priority: p2
status: todo
blockedBy: ["phase-02-navigation"]
---

# Phase 11 — Supporting Screens

**Goal:** Implement language selector, rules/thể lệ, and error pages.

## MoMorph refs
- [iOS] Thể lệ: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/zIuFaHAid4
- [iOS] Language dropdown: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/uUvW6Qm1ve
- [iOS] Access denied: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/k-7zJk2B7s
- [iOS] Not Found: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/sn2mdavs1a

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/rules/RulesScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/error/AccessDeniedScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/error/NotFoundScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/ui/components/LanguageDropdown.kt`

## Integration contract
- Language dropdown: reusable across Login + Settings
- Error screens: shown when nav guard blocks route or 404 route hit
- Thể lệ: static content screen, accessible from feed header

## Out of scope
- i18n/l10n runtime switching — UI toggle only
