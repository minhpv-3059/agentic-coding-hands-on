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

## Phase 03 — Screens [Pending]

Real screen composables replacing the placeholder destinations (phases 03–11 per plan).

## Phase 04 — Logo Asset [Pending]

Includes replacing the `KudosTopBar` logo placeholder with the actual drawable asset.

## Phase 04 — Data Layer [Pending]

## Phase 05 — Integration & Polish [Pending]
