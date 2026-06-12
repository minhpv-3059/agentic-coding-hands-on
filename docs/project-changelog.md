# Project Changelog

## [Unreleased] — Phase 06: Send Kudos

### Added
- `feature/send/SendKudosScreen.kt` — Send Kudos form: recipient search dropdown, danh hiệu (title) dropdown, functional rich-text markdown toolbar (`RichTextFormatter`), multi-line message field, hashtag multi-select (max 5), real Android Photo Picker (max 5 images, decoded to bitmap thumbnails via `BitmapFactory`), anonymous toggle + nickname field, form validation
- `feature/send/SendKudosViewModel.kt` — form state, validation logic, and `submit()` that prepends the new kudo to `KudosRepository` so it surfaces immediately at the top of the feed
- `feature/send/CommunityStandardsScreen.kt` — 10 community-standard criteria + security section; navigates from Send Kudos form
- `data/KudosRepository.kt` — **first cross-feature in-memory shared state store**; `object` singleton holding a `MutableStateFlow<List<Kudo>>` seeded from `KudosMockData`; exposes `kudoById(id)` helper

### Changed
- `feature/feed/KudosFeedViewModel.kt` — refactored to read from `KudosRepository` (combines 5 flows) instead of reading the static `KudosMockData` object directly; live feed now reflects submitted kudos without restart
- `feature/feed/ViewKudoScreen.kt` — `ViewKudoRoute` now resolves kudo by ID via `KudosRepository.kudoById` instead of a static list lookup
- `navigation/AppNavGraph.kt` — `KUDOS_COMMUNITY_STANDARDS` route added; `KUDOS_SEND` now resolves to the real `SendKudosRoute`
- `ui/theme/Color.kt` — 1 new token: `KudosFormCream` (form surface background)

### Dependencies
- No new gradle dependencies: Photo Picker uses existing `activity-compose 1.8.0`; bitmap thumbnails decoded via built-in `android.graphics.BitmapFactory` (no Coil)

---

## [Unreleased] — Phase 05: Kudos Feed

### Added
- `feature/feed/KudosFeedScreen.kt` — main Feed screen: hero banner, highlight carousel (top-5 by likes), all-kudos list, stats block, gift-recipients section, send-kudos prompt, Spotlight network chart; slot pattern for `filterRow` and `spotlight`
- `feature/feed/AllKudosScreen.kt` — full list with like, copy-link, hashtag-tap filter
- `feature/feed/ViewKudoScreen.kt` — kudo detail with anonymous mode (hides sender identity when `isAnonymous=true`)
- `feature/feed/KudosSearchScreen.kt` — live search Sunner + recent searches list
- `feature/feed/KudosFeedViewModel.kt` — `AndroidViewModel`; combines `KudosPreferences` Flow into `KudosFeedUiState`; `toggleLike`, `selectHashtag`, `selectDepartment`, `applyHashtag`, `toggleLanguage`
- `feature/feed/KudosSearchViewModel.kt` — `AndroidViewModel`; live query filter + persisted recent searches via `KudosPreferences`
- `feature/feed/KudosFeedLogic.kt` — pure, testable functions: `filterKudos`, `buildHighlights`, `applyLikes`, `searchUsers` (37 unit tests, all passing)
- `feature/feed/KudoModels.kt` — `Kudo`, `KudoUser`, `KudoStats`, `GiftRecipient`, `SpotlightNode` data models
- `feature/feed/KudosMockData.kt`, `feature/feed/SpotlightMockData.kt` — mock datasets
- `feature/feed/components/` — 9 composables: `FeedHeroBanner`, `HighlightCarousel`, `AllKudosSection`, `KudoDetailCard`, `KudoImageGallery`, `SpotlightNetworkChart` (pan/zoom + search highlight, Canvas-based), `StatsBlock`, `GiftRecipientsSection`, `SendKudosPrompt`, `UserResultRow`
- `data/KudosPreferences.kt` — **first persistence layer** in project; `DataStore Preferences` for liked kudo IDs + recent search terms; exposes `Flow<Set<String>>`
- `navigation/KudosFeedNavigation.kt` — route composables (`KudosFeedRoute`, `KudosAllRoute`, `ViewKudoRoute`, `KudosSearchRoute`) extracted from `AppNavGraph` to keep file size manageable; slot injection for filter dropdowns and Spotlight
- `ui/components/HashtagFilterDropdown.kt`, `ui/components/DepartmentFilterDropdown.kt` — overlay dropdown filters (AND logic: hashtag + department applied simultaneously)
- `ui/components/KudoAvatar.kt`, `ui/components/KudosCard.kt` — shared card/avatar components

### Changed
- `navigation/AppNavGraph.kt` — `kudos/feed`, `kudos/all`, `kudos/view/{id}`, `kudos/search` routes wired to real screens via `KudosFeedNavigation.kt`
- `ui/theme/Color.kt` — 3 new design tokens: `KudosAccentRed` (`#D4271D`, active heart + hashtag), `KudosCardMuted` (`#555555`, secondary text on cream card), `KudosCardFaint` (`#888888`, tertiary text/timestamps)

### Dependencies
- `androidx.datastore:datastore-preferences:1.1.1` added to `app/build.gradle.kts`

---

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
