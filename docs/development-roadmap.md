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

## Phase 05 — Kudos Feed [Complete]

Implemented the full Kudos Feed feature (8 MoMorph screens) with UI, persistence, and pure logic layer.

**Delivered:**
- `feature/feed/KudosFeedScreen.kt` + `AllKudosScreen.kt` + `ViewKudoScreen.kt` + `KudosSearchScreen.kt` — 4 screens; stateless Compose UI with slot pattern (`filterRow`, `spotlight`)
- `feature/feed/KudosFeedViewModel.kt` + `KudosSearchViewModel.kt` — `AndroidViewModel`; combine Flow from `KudosPreferences`; like toggle, hashtag/department filter, language toggle, live search
- `feature/feed/KudosFeedLogic.kt` — pure testable logic: `filterKudos`, `buildHighlights`, `applyLikes`, `searchUsers` (37 unit tests passing)
- `feature/feed/components/` — 9 composables including `SpotlightNetworkChart` (Canvas-based, pan/zoom + search highlight) and `HighlightCarousel` (top-5 by likes)
- `data/KudosPreferences.kt` — first persistence layer; DataStore Preferences for liked kudo IDs + recent searches
- `navigation/KudosFeedNavigation.kt` — feed route composables extracted to dedicated file; slot injection pattern
- `ui/components/HashtagFilterDropdown.kt` + `DepartmentFilterDropdown.kt` — AND-logic overlay filters
- `ui/components/KudoAvatar.kt` + `KudosCard.kt` — shared card/avatar components
- `ui/theme/Color.kt` — 3 new tokens: `KudosAccentRed`, `KudosCardMuted`, `KudosCardFaint`
- `androidx.datastore:datastore-preferences:1.1.1` dependency added

## Phase 07 — Profile [Complete]

Implemented two profile screens (own profile + other-user profile) from MoMorph design; introduced `feature/profile/` package and `ProfileNavigation.kt`; fixed a nav route wildcard-capture bug; added 119 unit tests (suite now 305 passing).

**Delivered:**
- `feature/profile/` — `ProfileModels.kt`, `ProfileMockData.kt`, `MyProfileViewModel.kt`, `UserProfileViewModel.kt`, `MyProfileScreen.kt`, `UserProfileScreen.kt`, 10 components
- `navigation/ProfileNavigation.kt` — `MyProfileRoute` + `UserProfileRoute` extracted (same pattern as `KudosFeedNavigation.kt`)
- `PROFILE_ME` route fixed to `"my-profile"` (was `"profile/me"` — captured by `PROFILE_USER` wildcard); `PROFILE_USER` wired to `UserProfileScreen`
- `KUDOS_SEND` gains optional `recipient` query-param for pre-fill from other-user profile CTA (`KUDOS_SEND_WITH_ARG`)
- `KudosApp` suppresses global bottom bar on profile routes (each profile screen embeds its own)

**Deferred:** 6 award-badge images pending Figma export — placeholder `AwardBadge(icon=null)` layout in place

## Phase 06 — Send Kudos [Complete]

Implemented the Send Kudos flow end-to-end, introduced the first cross-feature shared state store, and wired the live feed to reflect newly submitted kudos.

**Delivered:**
- `feature/send/SendKudosScreen.kt` + `SendKudosViewModel.kt` — full Send Kudos form: recipient search, danh hiệu dropdown, rich-text markdown toolbar (`RichTextFormatter`), multi-line message, hashtag multi-select (max 5), Photo Picker (max 5 images, bitmap thumbnails via `BitmapFactory`), anonymous toggle + nickname, form validation
- `feature/send/CommunityStandardsScreen.kt` — 10 community-standard criteria + security section
- `data/KudosRepository.kt` — in-memory singleton (`MutableStateFlow<List<Kudo>>`), seeded from `KudosMockData`, shared across `send` and `feed` feature modules; `submit()` prepends new kudos to the live feed
- `KudosFeedViewModel` refactored to read from `KudosRepository` (combine of 5 flows); `ViewKudoRoute` uses `kudosRepository.kudoById`
- `KUDOS_COMMUNITY_STANDARDS` route added; `KUDOS_SEND` wired to real `SendKudosRoute`
- `KudosFormCream` color token added
- No new gradle dependencies (Photo Picker via existing `activity-compose 1.8.0`; thumbnails via built-in `BitmapFactory`)
