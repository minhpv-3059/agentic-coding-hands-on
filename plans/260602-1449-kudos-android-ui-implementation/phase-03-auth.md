---
phase: "03"
title: Authentication — Login
priority: p0
status: todo
blockedBy: ["phase-02-navigation"]
---

# Phase 03 — Authentication

**Goal:** Implement Login screen pixel-perfect from MoMorph design.

## MoMorph refs
- [iOS] Login: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/8HGlvYGJWq
- [iOS] Language dropdown: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/uUvW6Qm1ve

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/auth/LoginScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/auth/LoginViewModel.kt`

## Integration contract
- On login success → navigate to `home`
- Language dropdown → overlay composable (reusable)

## Out of scope
- Real authentication API — mock/stub only in this phase
- Password reset flow
