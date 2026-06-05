# Development Roadmap

## Phase 01 — Design System [Complete]

Established the foundational UI layer: brand color tokens, typography scale, app theme, and shared components.

**Delivered:**
- `ui/theme/Color.kt` — 16 brand color tokens (dark navy + gold palette, sourced from MoMorph)
- `ui/theme/Type.kt` — `KudosTypography` with 11 Material3 text styles
- `ui/theme/Theme.kt` — `KudosAppTheme` (dark-only, no dynamic colors)
- `ui/components/KudosButton.kt` — Primary, Secondary, Text button variants
- `ui/components/KudosTopBar.kt` — top app bar (logo placeholder, language, search, notifications)
- `ui/components/KudosBottomNav.kt` — bottom nav with `BottomNavTab` enum (4 tabs)

## Phase 02 — App Navigation Setup [Complete]

Wired the full navigation skeleton: route constants, `NavHost`, and root composable shell.

**Delivered:**
- `navigation/NavRoutes.kt` — route constants + builders for all 13 destinations
- `navigation/AppNavGraph.kt` — `NavHost` with placeholder composables for every route
- `ui/KudosApp.kt` — root composable (`Scaffold` + bottom-nav shell)
- `BottomNavTab.route` field added; `MainActivity` now hosts `KudosApp()`

## Phase 03 — Authentication Login [Complete]

Implemented Login screen pixel-perfect from MoMorph design with bilingual support (VN/EN).

**Delivered:**
- `feature/auth/LoginScreen.kt` — background keyvisual, ROOT FURTHER logo, description text, login button, language dropdown overlay
- `feature/auth/LoginViewModel.kt` — `AppLanguage` enum, loading state, language selection, 1s mock auth
- Assets: `bg_login_keyvisual.png`, `ic_logo_saa.png`, `ic_google.xml`
- Nav: LOGIN is startDestination; login success → HOME navigation
- Edge-to-edge support with safe area handling (navigationBarsPadding)

## Phase 04 — Home Screen [Complete]

Implemented the Home screen from MoMorph design with live countdown and upgraded shared components.

**Delivered:**
- `feature/home/HomeScreen.kt` + `HomeViewModel.kt` — scrollable Home with mock data; `HomeUiState`/`CountdownState` via `StateFlow`; live 1-second countdown (target: launch + 20d 20h 20m)
- `feature/home/components/` — 9 composables: hero section, countdown row, awards section, kudos section, note section, FAB, section header, action buttons, award card
- DSEG7 Classic font (`res/font/dseg7_classic_regular.ttf`); OFL license in `assets/`
- `KudosTopBar` upgraded: real `ic_logo_saa` drawable, `ic_vn_flag` asset, notification `BadgedBox`, `statusBarsPadding()` + gradient overlay
- `KudosBottomNav` upgraded: `BottomNavTab` now uses Figma vector drawables (`ic_nav_*`) instead of Material icons
- HOME route wired to real `HomeScreen` in `AppNavGraph`

**Deferred to Phase 10:** award trophy images (Top Project, Top Project Leader) — still placeholders

## Phase 05 — Data Layer [Pending]

## Phase 06 — Integration & Polish [Pending]
