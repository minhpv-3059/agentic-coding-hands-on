---
phase: "02"
title: App Navigation Setup
priority: critical
status: done
blockedBy: ["phase-01-design-system"]
---

# Phase 02 — App Navigation Setup

**Goal:** Setup Compose Navigation with all routes and bottom nav shell.

## Navigation structure
```
NavGraph
├── auth/login          → LoginScreen
├── home                → HomeScreen
├── kudos
│   ├── feed            → KudosFeedScreen
│   ├── view/{id}       → ViewKudoScreen
│   └── send            → SendKudosScreen
├── profile
│   ├── me              → MyProfileScreen
│   └── {userId}        → UserProfileScreen
├── notifications       → NotificationsScreen
├── secret-box          → SecretBoxScreen
├── awards              → AwardsScreen
├── rules               → RulesScreen
└── error
    ├── 403             → AccessDeniedScreen
    └── 404             → NotFoundScreen
```

## Files to create
- `app/src/main/java/com/sun/kudos_demo/navigation/AppNavGraph.kt`
- `app/src/main/java/com/sun/kudos_demo/navigation/NavRoutes.kt`
- `app/src/main/java/com/sun/kudos_demo/ui/KudosApp.kt` — root composable with bottom nav

## Dependencies
- `androidx.navigation:navigation-compose` must be added to `app/build.gradle.kts`

## Out of scope
- No actual screen content — placeholder composables only
- No auth flow guard (Phase 03)
