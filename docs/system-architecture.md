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
├── data/
│   └── KudosPreferences.kt  — DataStore Preferences persistence: liked kudo IDs + recent search terms (Flow<Set<String>>)
├── feature/
│   ├── auth/
│   │   ├── LoginScreen.kt   — login UI (key-visual, ROOT FURTHER logo, Google SSO button, language overlay)
│   │   └── LoginViewModel.kt — AppLanguage enum, loading StateFlow, 1s mock auth
│   ├── feed/
│   │   ├── KudosFeedScreen.kt  — main Feed: hero banner, highlight carousel, kudos list, stats, gift-recipients, Spotlight; slot pattern (filterRow, spotlight)
│   │   ├── AllKudosScreen.kt   — full kudos list with like, copy-link, hashtag-tap
│   │   ├── ViewKudoScreen.kt   — kudo detail; anonymous mode hides sender identity
│   │   ├── KudosSearchScreen.kt — live Sunner search + recent searches
│   │   ├── KudosFeedViewModel.kt — AndroidViewModel; combines KudosPreferences Flow; toggleLike, selectHashtag/Department, applyHashtag, toggleLanguage
│   │   ├── KudosSearchViewModel.kt — AndroidViewModel; live query filter + persisted recent searches
│   │   ├── KudosFeedLogic.kt   — pure testable logic: filterKudos, buildHighlights, applyLikes, searchUsers
│   │   ├── KudoModels.kt       — Kudo, KudoUser, KudoStats, GiftRecipient, SpotlightNode data models
│   │   ├── KudosMockData.kt, SpotlightMockData.kt — mock datasets
│   │   └── components/         — 9 composables: FeedHeroBanner, HighlightCarousel, AllKudosSection,
│   │                              KudoDetailCard, KudoImageGallery, SpotlightNetworkChart (Canvas pan/zoom),
│   │                              StatsBlock, GiftRecipientsSection, SendKudosPrompt, UserResultRow
│   └── home/
│       ├── HomeScreen.kt    — scrollable Home: hero, countdown, awards, kudos, note sections, FAB
│       ├── HomeViewModel.kt — HomeUiState + CountdownState StateFlow; live 1s countdown; mock badge count
│       └── components/      — 9 composables: HomeHeroSection, CountdownRow, HeroActionButtons,
│                              SectionHeader, HomeAwardsSection, AwardCard,
│                              HomeKudosSection, HomeNoteSection, HomeFab
├── navigation/
│   ├── NavRoutes.kt              — route constants + builder helpers (13 destinations)
│   ├── AppNavGraph.kt            — NavHost; LOGIN is startDestination; HOME + KUDOS_FEED wired to real screens
│   └── KudosFeedNavigation.kt    — feed route composables (KudosFeedRoute, KudosAllRoute, ViewKudoRoute, KudosSearchRoute); slot injection for filters and Spotlight
└── ui/
    ├── KudosApp.kt          — root composable: Scaffold (contentWindowInsets=0) + KudosBottomNav + AppNavGraph
    ├── theme/
    │   ├── Color.kt         — brand color tokens (19 constants; 3 added in Phase 05: KudosAccentRed, KudosCardMuted, KudosCardFaint)
    │   ├── Type.kt          — KudosTypography (11 Material3 text styles)
    │   └── Theme.kt         — KudosAppTheme composable (dark-only, no dynamic color)
    └── components/
        ├── KudosButton.kt           — KudosPrimaryButton, KudosSecondaryButton, KudosTextButton
        ├── KudosTopBar.kt           — real ic_logo_saa drawable (48×44 dp), ic_vn_flag asset, BadgedBox bell, statusBarsPadding() + vertical gradient overlay
        ├── KudosBottomNav.kt        — BottomNavTab enum uses @DrawableRes ic_nav_* Figma vector drawables; navigationBarsPadding() applied
        ├── KudoAvatar.kt            — circular avatar composable
        ├── KudosCard.kt             — shared card surface composable
        ├── HashtagFilterDropdown.kt — overlay hashtag filter (AND logic with department)
        └── DepartmentFilterDropdown.kt — overlay department filter
```

## Navigation

The app uses `navigation-compose 2.8.0` with a single `NavHost` defined in `AppNavGraph.kt`.

- All 13 route strings live in `NavRoutes.kt` — the sole source of truth for destination names.
- `KudosApp.kt` owns the `NavController` and passes it to both `KudosBottomNav` and `AppNavGraph`.
- `startDestination` is `NavRoutes.LOGIN`; after successful login the stack is popped and `HOME` becomes the root.
- `KudosBottomNav` is hidden on the login screen — only shown when the current route matches a `BottomNavTab` destination.
- Feed-specific route composables live in `KudosFeedNavigation.kt` (not `AppNavGraph.kt`) to keep both files under 200 lines — follow this pattern for future feature modules.
- Hashtag cross-screen navigation: secondary screens (View / AllKudos) stash the tag on the Feed's `SavedStateHandle` and pop back, so the Feed ViewModel picks it up without re-composing.
- Real screens: LOGIN, HOME, KUDOS_FEED, KUDOS_ALL, KUDOS_VIEW, KUDOS_SEARCH. Remaining routes still use placeholder composables.

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

Tokens added in Phase 05 (kudos card cream surface):
- `KudosAccentRed` (`#D4271D`) — active heart icon tint, hashtag accent
- `KudosCardMuted` (`#555555`) — secondary text on cream card (codes, links)
- `KudosCardFaint` (`#888888`) — tertiary text on cream card (timestamps, sub-labels)

## Key Dependencies

| Library | Version | Purpose |
|---|---|---|
| `navigation-compose` | 2.8.0 | In-app navigation |
| `lifecycle-viewmodel-compose` | 2.6.1 | ViewModel integration in Compose |
| `lifecycle-runtime-ktx` | (catalog) | Lifecycle-aware coroutines |
| `datastore-preferences` | 1.1.1 | Persistence: liked kudo IDs + recent search terms |
| DSEG7 Classic font (TTF) | — | Seven-segment countdown digits; bundled in `res/font/`; SIL OFL license in `assets/` |

## Persistence Layer

`data/KudosPreferences.kt` is the project's first persistence layer (introduced in Phase 05).

- **Storage**: `DataStore Preferences` (key-value, async, Flow-based) — do not use `SharedPreferences`.
- **Pattern**: expose `Flow<Set<String>>` for read; suspend functions for write. ViewModels combine these Flows into UI state via `combine`.
- **Scope**: `AndroidViewModel` required (needs `Application` context for DataStore) — feature ViewModels that touch persistence must extend `AndroidViewModel`, not plain `ViewModel`.
