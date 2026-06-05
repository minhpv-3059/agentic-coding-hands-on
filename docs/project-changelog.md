# Project Changelog

## [Unreleased] — Phase 04: Home Screen

### Added
- `feature/home/HomeScreen.kt` — scrollable Home screen: hero keyvisual + countdown, awards, kudos, note sections, and FAB
- `feature/home/HomeViewModel.kt` — `HomeUiState` / `CountdownState` StateFlow; live 1-second countdown to a demo target (launch + 20d 20h 20m); mock badge count and language toggle
- `feature/home/components/` — 9 composables: `HomeHeroSection`, `CountdownRow`, `HeroActionButtons`, `SectionHeader`, `HomeAwardsSection`, `AwardCard`, `HomeKudosSection`, `HomeNoteSection`, `HomeFab`
- DSEG7 Classic font (`res/font/dseg7_classic_regular.ttf`) for seven-segment countdown digits; license bundled at `assets/dseg_font_OFL_license.txt` (SIL OFL)
- Nav drawables: `ic_nav_home.xml`, `ic_nav_awards.xml`, `ic_nav_kudos.xml`, `ic_nav_profile.xml` (Figma vector exports)

### Changed
- `KudosTopBar.kt` — replaced `Text` logo placeholder with real `ic_logo_saa` drawable (48×44 dp); added `ic_vn_flag` image asset for VN locale; added `BadgedBox` notification badge; applies `statusBarsPadding()` + vertical gradient for edge-to-edge support
- `KudosBottomNav.kt` — `BottomNavTab` enum now references `@DrawableRes ic_nav_*` vector drawables instead of Material icons
- `AppNavGraph.kt` / `NavRoutes.kt` — HOME route wired to real `HomeScreen`

---

## [Unreleased] — Phase 03: Authentication Login

### Added
- `feature/auth/LoginScreen.kt` — Login screen with background keyvisual, ROOT FURTHER logo, bilingual description (VN/EN), login button with 1s mock auth, language dropdown overlay
- `feature/auth/LoginViewModel.kt` — `AppLanguage` enum (VN, EN), `loading: StateFlow<Boolean>`, `selectedLanguage: StateFlow<AppLanguage>`, `onLoginClick(callback)` for mock auth
- Assets: `drawable-nodpi/bg_login_keyvisual.png`, `drawable-nodpi/ic_logo_saa.png`, `drawable-nodpi/img_root_further.png`, `drawable/ic_google.xml`
- `lifecycle-viewmodel-compose 2.6.1` dependency

### Changed
- `AppNavGraph.kt` — LOGIN is now startDestination; login success navigates to HOME
- `KudosBottomNav.kt` — added `navigationBarsPadding()` for edge-to-edge safe area support

### Fixed
- Edge-to-edge support: `contentWindowInsets = WindowInsets(0)` in `KudosApp.kt` for proper safe area handling

---

## [Unreleased] — Phase 02: App Navigation Setup

### Added
- `navigation-compose 2.8.0` dependency
- `navigation/NavRoutes.kt` — sealed route constants and route-builder helpers for all 13 app destinations
- `navigation/AppNavGraph.kt` — `NavHost` wiring all 13 routes to placeholder composables
- `ui/KudosApp.kt` — root composable: `Scaffold` with `KudosBottomNav` + `AppNavGraph`
- `BottomNavTab` gained a `route` field linking each tab to its nav destination
- `MainActivity` now hosts `KudosApp()` as its sole content

---

## [Unreleased] — Phase 01: Design System

### Added
- `ui/theme/Color.kt` — 16 brand color tokens sourced from MoMorph design data (dark navy + gold palette: backgrounds, containers, borders, text, button states, error)
- `ui/theme/Type.kt` — `KudosTypography` with 11 Material3 text styles from `displayLarge` (52 sp, ExtraBold) to `labelSmall` (10 sp, Medium)
- `ui/theme/Theme.kt` — `KudosAppTheme` composable; dark-only `MaterialTheme` wrapper using `darkColorScheme`, no dynamic color
- `ui/components/KudosButton.kt` — three button composables: `KudosPrimaryButton` (gold pill, 56 dp), `KudosSecondaryButton` (outlined pill, 44 dp), `KudosTextButton` (text-only)
- `ui/components/KudosTopBar.kt` — `KudosTopBar` composable with logo placeholder, language selector, search and notification icon buttons
- `ui/components/KudosBottomNav.kt` — `KudosBottomNav` composable and `BottomNavTab` enum with 4 tabs: SAA2025, Awards, Kudos, Profile
