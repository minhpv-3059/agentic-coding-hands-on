# System Architecture

## Overview

Kudos is an Android application built with Jetpack Compose and Material3. The app uses a dark-only theme based on a brand palette sourced from MoMorph design tokens.

## Module Structure

Single-module Android app (`app/`).

- `namespace`: `com.sun.kudos_demo`
- `minSdk`: 26
- `compileSdk`: 36
- Build system: Gradle (Kotlin DSL)

## Package Layout

```
com.sun.kudos_demo/
├── MainActivity.kt          — entry point, hosts KudosApp inside KudosAppTheme
├── feature/
│   ├── auth/
│   │   ├── LoginScreen.kt   — login UI (key-visual, ROOT FURTHER logo, Google SSO button, language overlay)
│   │   └── LoginViewModel.kt — AppLanguage enum, loading StateFlow, 1s mock auth
│   └── home/
│       ├── HomeScreen.kt    — scrollable Home: hero, countdown, awards, kudos, note sections, FAB
│       ├── HomeViewModel.kt — HomeUiState + CountdownState StateFlow; live 1s countdown; mock badge count
│       └── components/      — 9 composables: HomeHeroSection, CountdownRow, HeroActionButtons,
│                              SectionHeader, HomeAwardsSection, AwardCard,
│                              HomeKudosSection, HomeNoteSection, HomeFab
├── navigation/
│   ├── NavRoutes.kt         — route constants + builder helpers (13 destinations)
│   └── AppNavGraph.kt       — NavHost; LOGIN is startDestination; HOME wired to real HomeScreen
└── ui/
    ├── KudosApp.kt          — root composable: Scaffold (contentWindowInsets=0) + KudosBottomNav + AppNavGraph
    ├── theme/
    │   ├── Color.kt         — brand color tokens (16 constants)
    │   ├── Type.kt          — KudosTypography (11 Material3 text styles)
    │   └── Theme.kt         — KudosAppTheme composable (dark-only, no dynamic color)
    └── components/
        ├── KudosButton.kt   — KudosPrimaryButton, KudosSecondaryButton, KudosTextButton
        ├── KudosTopBar.kt   — real ic_logo_saa drawable (48×44 dp), ic_vn_flag asset, BadgedBox bell, statusBarsPadding() + vertical gradient overlay
        └── KudosBottomNav.kt — BottomNavTab enum uses @DrawableRes ic_nav_* Figma vector drawables; navigationBarsPadding() applied
```

## Navigation

The app uses `navigation-compose 2.8.0` with a single `NavHost` defined in `AppNavGraph.kt`.

- All 13 route strings live in `NavRoutes.kt` — the sole source of truth for destination names.
- `KudosApp.kt` owns the `NavController` and passes it to both `KudosBottomNav` and `AppNavGraph`.
- `startDestination` is `NavRoutes.LOGIN`; after successful login the stack is popped and `HOME` becomes the root.
- `KudosBottomNav` is hidden on the login screen — only shown when the current route matches a `BottomNavTab` destination.
- Real screens: LOGIN and HOME. Remaining routes still use placeholder composables.

## Theme System

`KudosAppTheme` wraps `MaterialTheme` with:
- `colorScheme`: `darkColorScheme` — maps brand tokens to Material3 roles
- `typography`: `KudosTypography` — 11 text styles (displayLarge … labelSmall)
- Dynamic color: disabled

All components consume colors via `MaterialTheme.colorScheme.*` except `KudosBottomNav`, which references brand tokens directly for nav item colors.

## Design Tokens

Color constants in `Color.kt` are the single source of truth for brand colors. They are sourced from MoMorph design data and must not be changed without re-fetching from MoMorph.

Primary accent: `KudosGold` (`#FFEA9E`) — used for primary buttons, active nav items, notification icon tint.
Background: `KudosBackground` (`#00101A`) — dark navy.

## Key Dependencies

| Library | Version | Purpose |
|---|---|---|
| `navigation-compose` | 2.8.0 | In-app navigation |
| `lifecycle-viewmodel-compose` | 2.6.1 | ViewModel integration in Compose |
| `lifecycle-runtime-ktx` | (catalog) | Lifecycle-aware coroutines |
| DSEG7 Classic font (TTF) | — | Seven-segment countdown digits; bundled in `res/font/`; SIL OFL license in `assets/` |
