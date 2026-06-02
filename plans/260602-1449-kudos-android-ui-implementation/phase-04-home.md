---
phase: "04"
title: Home Screen
priority: p0
status: todo
blockedBy: ["phase-03-auth"]
---

# Phase 04 — Home Screen

**Goal:** Implement Home screen with bottom nav integration.

## MoMorph refs
- [iOS] Home: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/OuH1BUTYT0
- Navigation bar: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/VeF77wVOUW

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/home/HomeScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/home/HomeViewModel.kt`

## Integration contract
- Bottom nav: Home / Kudos Feed / Profile / Notifications tabs
- FAB or action button → navigate to `kudos/send`

## Out of scope
- Real API data — mock data only
