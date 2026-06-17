# Project Changelog

## [Unreleased] — Phase 11: Full-App Runtime i18n + Bug Fix (2026-06-17)

### Added
- Per-feature string resource files: `res/values/strings_<feature>.xml` + `res/values-en/strings_<feature>.xml` — one pair per feature module (Home, Feed, Awards, Profile, Notifications, Secret Box, Send Kudos, Community Standards); shared/cross-screen keys remain in `res/values/strings.xml`
- Runtime i18n now covers **all main screens**: Home, Feed (KudosFeed, AllKudos, ViewKudo, KudosSearch), Awards, Profile (MyProfile, UserProfile), Notifications, Secret Box, Send Kudos, Community Standards, shared `KudosCard`, and feed filter dropdowns
- `KudosTopBar` self-manages a VN/EN dropdown panel and drives `LanguageManager.set()` — every header screen gets the language switcher without extra wiring

### Changed
- `AwardContent` data class, `SecretBoxReward` data class, `AppNotification` data class — user-text fields (`title`, `description`, `name`, etc.) converted from `String` to `@StringRes Int`; resolved via `stringResource(...)` at the call site, keeping data classes free of `Context`
- `MainActivity` now wraps the base `Activity` with `ContextThemeWrapper` (replacing `createConfigurationContext`) before passing it as the locale-overridden context to `CompositionLocalProvider`

### Fixed
- **Send Kudos photo-picker crash** — using `createConfigurationContext` detached the `Activity` from its `ComponentActivity` identity, breaking `LocalActivityResultRegistryOwner` (used internally by the Android Photo Picker). Switching to `ContextThemeWrapper(baseContext, theme)` preserves the `Activity`'s composition-local chain while still applying the locale override, eliminating the crash

### Notes
- Mock/user-generated content (kudo messages, user names, hashtags) is intentionally not localized — this data is always Vietnamese regardless of the selected locale

---

## [Unreleased] — Phase 11: Supporting Screens (2026-06-17)

### Added
- `feature/rules/RulesScreen.kt` — Rules/Thể lệ detail screen (back arrow, no bottom nav); reachable from Home award card "Chi tiết ↗"
- `feature/rules/components/RulesHeroSection.kt`, `RulesIconGrid.kt` — two composable components for the Rules screen
- `feature/error/ErrorScreen.kt` — shared full-screen error scaffold: robot illustration (`img_error_robot.png`), localized message, CTA button
- `feature/error/AccessDeniedScreen.kt` — 403 Access Denied screen (wraps `ErrorScreen`; wired from Rules demo trigger)
- `feature/error/NotFoundScreen.kt` — 404 Not Found screen (wraps `ErrorScreen`; wired from Rules demo trigger)
- `ui/components/LanguageDropdown.kt` — reusable language selector composable extracted from `LoginScreen`; shared across screens that display the VN/EN toggle
- `res/values/strings.xml` — Vietnamese string resources (default locale); first use of Android string resources in the project
- `res/values-en/strings.xml` — English string resource overrides
- Assets: `img_error_robot.png` (error illustration), `ic_uk_flag.png` (EN flag for language selector)

### Changed
- `feature/auth/AppLanguage.kt` — `LanguageManager` process-global `StateFlow` singleton added (`language: StateFlow<AppLanguage>`, `loadInitial()`, `set()`); `AppLanguage` enum gains `locale` field (`"vi"` / `"en"`)
- `data/KudosPreferences.kt` — `languageCode: Flow<String>` + `setLanguageCode(code: String)` added; language selection is now DataStore-persisted across sessions
- `MainActivity` — observes `LanguageManager.language` and wraps `KudosApp` in `CompositionLocalProvider(LocalContext + LocalConfiguration)` with a locale-overridden `Context`; locale switches at runtime without Activity recreation
- `navigation/NavRoutes.kt` — `RULES = "rules"`, `ERROR_403 = "error/403"`, `ERROR_404 = "error/404"` route constants added
- `navigation/AppNavGraph.kt` — `RULES`, `ERROR_403`, `ERROR_404` wired to real screens; Home "Chi tiết ↗" navigates to `RULES`; Rules demo callbacks navigate to error routes
- Login, Rules, and Error screens — user-facing strings migrated to `stringResource(...)` calls backed by `strings.xml`; other screens intentionally still use hardcoded Vietnamese strings (i18n migration is incremental)

### Tests
- 26 test files green; build `assembleDebug` PASS; full test suite passing

---

## [Unreleased] — Phase 10: Awards (2026-06-17)

### Added
- `feature/awards/AwardsScreen.kt` — Awards bottom-nav TAB: dropdown selecting among 6 award types (MVP, Best Manager, Signature 2025-Creator, Top Project, Top Project Leader, Top Talent); selecting updates the Award Information Block (title, description, quantity+unit, 1–2 value rows); display-only (eligibility logic out of scope)
- `feature/awards/AwardContent.kt` — award info block composable rendering title, description, quantity+unit, and value rows from the selected award
- `feature/awards/AwardData.kt` — data definitions and mock dataset for all 6 award types
- `feature/awards/AwardViewModel.kt` — plain `ViewModel`; holds selected award `StateFlow`; `onAwardSelected()` drives dropdown state; accepts optional pre-select arg from nav
- `feature/awards/components/` — 4 composables: `AwardHeaderSection`, `AwardKvSection`, `AwardTrophyCard`, `AwardsKudosSection`
- `navigation/AwardsNavigation.kt` — `AwardsRoute` composable; route: `awards?award={award}` (optional pre-select arg)
- 6 trophy PNG assets in `res/drawable-nodpi/img_award_*.png` — extracted from MoMorph S3 and composited (ring + name label) via PIL
- 3 award section icon drawables: `ic_award_*.xml`
- `ic_kudos_wordmark.xml` drawable

### Changed
- `navigation/NavRoutes.kt` — `AWARDS = "awards"`, `AWARDS_WITH_ARG = "awards?award={award}"`, `ARG_AWARD = "award"` constants added; `awards(awardId)` builder added
- `navigation/AppNavGraph.kt` — `AWARDS_WITH_ARG` wired to `AwardsRoute`; Home award card "Chi tiết" taps navigate with pre-selected award id
- `ui/KudosApp.kt` — bottom-nav tab-highlight strips query string (base route match) so the Awards tab stays highlighted while `awards?award=…` is the current destination
- `feature/home/components/AwardCard.kt` (or equivalent) — Top Project and Top Project Leader award cards now display real trophy PNGs (`img_award_top_project`, `img_award_top_project_leader`) replacing Phase 04 placeholders

### Tests
- `AwardDataTest`, `AwardViewModelTest`, `AwardViewModelStateFlowTest` — new unit tests; full suite: 541/541 passing; `assembleDebug` PASS

---

## [Unreleased] — Phase 09: Secret Box (2026-06-16)

### Added
- `feature/secretbox/SecretBoxModels.kt` — `SecretBoxPhase` enum (CLOSED / OPENING / REWARD), `SecretBoxReward` data class (`id`, `name`, `imageResName` — resolved at runtime via `resources.getIdentifier`)
- `feature/secretbox/SecretBoxMockData.kt` — 6-prize reward pool (khăn, tem, cốc, áo thun, combo, phần quà); `randomReward()` picks one on open
- `feature/secretbox/SecretBoxScreen.kt` — single screen driving 3 ViewModel states: idle looping video (`CLOSED`) → open animation videos (`OPENING`) → prize PNG + "Tiếp tục" button (`REWARD`); screen is a detail flow (back arrow, no bottom nav)
- `feature/secretbox/SecretBoxViewModel.kt` — plain `ViewModel`; combines local `phase`/`reward` `MutableStateFlow` with `SecretBoxRepository.counts`; exposes `SecretBoxUiState`; `onBoxTap()`, `onOpenAnimationEnd()`, `onContinue()` drive the state machine
- `feature/secretbox/components/` — 5 composables: `GiftBoxAnimation`, `GiftBoxPlaceholder`, `SecretBoxHeader`, `SecretBoxRewardView`, `SecretBoxTopBar`
- `data/SecretBoxRepository.kt` — in-memory `object` singleton; `MutableStateFlow<SecretBoxCounts>` seeded (unopened=5, opened=25); `openOne()` moves one from unopened to opened; same pattern as `KudosRepository` / `NotificationsRepository`
- `navigation/SecretBoxNavigation.kt` — `SecretBoxRoute` composable (same extraction pattern as other feature nav files); route: `"secret-box"`
- Video assets: `res/raw/secretbox_idle.mp4`, `res/raw/secretbox_tap.mp4`, `res/raw/secretbox_open.mp4` — **first use of video in the app**
- 6 reward PNG assets in `res/drawable-nodpi/` (`img_secretbox_scarf`, `img_secretbox_stamps`, `img_secretbox_mug`, `img_secretbox_tshirt`, `img_secretbox_combo`, `img_secretbox_gift`)

### Changed
- `feature/profile/MyProfileViewModel.kt` — now combines `SecretBoxRepository.counts` into `ProfileUiState`; `secretBoxOpened` and `secretBoxUnopened` fields on the stats card stay in sync after the user opens a box
- `navigation/NavRoutes.kt` — `SECRET_BOX = "secret-box"` route constant added
- `navigation/AppNavGraph.kt` — `SECRET_BOX` wired to `SecretBoxRoute`

### Dependencies
- `androidx.media3:media3-exoplayer` + `androidx.media3:media3-ui` 1.4.1 — ExoPlayer-backed video playback (first video dependency in project)
- `kotlinx-coroutines-test` 1.7.3 — first `testImplementation` dependency enabling `MainDispatcherRule` + `runTest` for ViewModel `StateFlow` tests

### Tests
- 101 new unit tests across 5 files (`SecretBoxRepositoryTest`, `SecretBoxMockDataTest`, `SecretBoxModelsTest`, `SecretBoxViewModelTest`, `SecretBoxViewModelStateFlowTest`); full suite: 462/462 passing

---

## [Unreleased] — Phase 08: Notifications (2026-06-16)

### Added
- `feature/notifications/NotificationModels.kt` — `AppNotification` data class (id, type, isRead, actor, timestamp, payload), `NotificationType` enum (7 types)
- `feature/notifications/NotificationsMockData.kt` — seeded mock dataset
- `feature/notifications/NotificationsScreen.kt` — detail screen: back arrow, no bottom nav, localized title (VN "Thông báo" / EN "Notifications"), mark-all-read action, per-notification read dot, per-type navigation on tap
- `feature/notifications/NotificationsViewModel.kt` — reads `NotificationsRepository`; exposes `NotificationsUiState`; calls `markRead` / `markAllRead`
- `feature/notifications/components/` — 4 composables: `NotificationItem`, `NotificationsTopBar`, `MarkAllReadButton`, `NotificationIconMapper`
- `data/NotificationsRepository.kt` — app-process `object` singleton; `MutableStateFlow<List<AppNotification>>` seeded from `NotificationsMockData`; exposes `notifications: StateFlow`, `unreadCount: StateFlow<Int>`, `markRead(id: String)`, `markAllRead()`
- `navigation/NotificationsNavigation.kt` — `NotificationsRoute` composable (same extraction pattern as `KudosFeedNavigation.kt`)

### Changed
- `navigation/NavRoutes.kt` — `NOTIFICATIONS = "notifications"` route constant added
- `navigation/AppNavGraph.kt` — `NOTIFICATIONS` wired to `NotificationsRoute`; Home/Feed/Profile top-bar `onNotifications` callbacks now navigate to `NOTIFICATIONS`
- `feature/home/HomeViewModel.kt`, `KudosFeedViewModel.kt`, profile ViewModels — observe `NotificationsRepository.unreadCount` so the bell badge stays in sync across all screens after mark-read

### Tests
- 47 new unit tests; full suite: 361/361 passing

### Known residuals
- 7 notification-type icons use Material Icons with design-matched tints (Figma SVG export API unavailable at implementation time — swappable later)

---

## [Unreleased] — Phase 07: Profile (refinement, 2026-06-16)

### Added
- 6 award-badge drawables (`img_badge_beyond_boundary`, `img_badge_flow_to_horizon`, `img_badge_revival`, `img_badge_root_futher`, `img_badge_stay_gold`, `img_badge_touch_of_light`) and 2 rank-pill drawables (`img_rank_legend_hero`, `img_rank_rising_hero`) — real Figma exports in `res/drawable-nodpi/`; resolves the deferred placeholder
- `feature/profile/components/RankBadge.kt` — shared composable mapping a badge label string to the appropriate `img_rank_*` pill image; used in both `ProfileHeader` and `UserProfileHeader` (name-line + avatar overlay)
- `data/CurrentUser.kt` — `object` singleton: single source of truth for the signed-in Sunner (`ID = "u1"`, `profile = KudoUser("Phan Văn Minh", "CEVC1", "Legend Hero")`); referenced by `ProfileMockData`, `SendKudosMockData`, and feed mock data

### Changed
- `data/KudosPreferences.kt` — `setCurrentUser(id: String)` + `currentUserId: Flow<String?>` added; persists the current user id at login
- `feature/auth/LoginViewModel.kt` — promoted to `AndroidViewModel` (needs `Application` for `KudosPreferences`); calls `prefs.setCurrentUser(CurrentUser.ID)` on successful login
- `ui/components/KudosTopBar.kt` — two new optional params: `showScrim: Boolean = true` (suppresses gradient when `false`) and `onBack: (() -> Unit)? = null` (renders back-arrow when non-null); both default to prior behavior — fully backwards compatible
- `feature/profile/UserProfileScreen.kt` — other-user profile is now a detail screen: passes `onBack` to `KudosTopBar` (back arrow rendered), no bottom navigation; full-bleed key-visual background applied; own profile (`MyProfileScreen`) retains bottom nav
- Feed mock data: Huỳnh character re-identified from `u1` → `u6` so `CurrentUser.ID = "u1"` unambiguously maps to the signed-in Sunner

### Tests
- ~315 unit tests passing (up from 305 after first Phase 07 delivery)

---

## [Unreleased] — Phase 07: Profile

### Added
- `feature/profile/ProfileModels.kt` — `ProfileKudosTab` enum, `ProfileStats`, `AwardBadge` (`icon: Int?` — null until Figma export lands; layout renders a styled placeholder)
- `feature/profile/ProfileMockData.kt` — mock dataset for both profile screens
- `feature/profile/MyProfileScreen.kt` + `MyProfileViewModel.kt` — own-profile screen: header, stats card, kudos filter (Đã nhận / Đã gửi), scrollable kudos list, icon collection, secret box CTA
- `feature/profile/UserProfileScreen.kt` + `UserProfileViewModel.kt` — other-user profile screen: header, award badges row, received-kudos list, "Send Kudos" CTA
- `feature/profile/components/` — 10 composables: `ProfileHeader`, `ProfileStatsCard`, `ProfileKudosFilter`, `ProfileAwardBadges`, `ProfileIconCollection`, `ProfileReceivedKudosLabel`, `ProfileSectionHeader`, `ProfileSendKudosCta`, `UserProfileHeader`, `UserProfileSectionHeader`
- `navigation/ProfileNavigation.kt` — `MyProfileRoute`, `UserProfileRoute` (same extraction pattern as `KudosFeedNavigation.kt`)

### Changed
- `navigation/NavRoutes.kt` — `PROFILE_ME` changed from `"profile/me"` to `"my-profile"` (fixes wildcard-capture bug: old path matched `PROFILE_USER = "profile/{userId}"` with `userId="me"`); `KUDOS_SEND_WITH_ARG` constant added (`kudos/send?recipient={recipient}`); `kudosSend(recipientId)` builder added
- `navigation/AppNavGraph.kt` — `PROFILE_ME` + `PROFILE_USER` wired to real `MyProfileRoute` / `UserProfileRoute`; `KUDOS_SEND` updated to `KUDOS_SEND_WITH_ARG` pattern (optional `recipient` arg for pre-fill from other-user profile CTA)
- `ui/KudosApp.kt` — `isProfileScreen` guard added: global bottom bar suppressed when route is `PROFILE_ME` or `PROFILE_USER` (each profile screen renders its own bottom nav per design)

### Tests
- 119 new unit tests covering `MyProfileViewModel`, `UserProfileViewModel`, profile filter logic; total suite: 305 passing

---

## [Unreleased] — Phase 06: Send Kudos (fidelity fix round 2, 2026-06-15)

### Changed
- `feature/send/SendKudosScreen.kt` — nickname field switched to `BasicTextField` (no text clipping), matching the recipient field pattern; "Tiêu chuẩn cộng đồng" link relocated from form body to the markdown toolbar row and recolored `KudosLinkRed`; vertical dividers added between markdown toolbar buttons.
- `feature/send/CommunityStandardsScreen.kt` — key-visual artwork background applied; typography and spacing corrected per design.
- `ui/theme/Color.kt` — 1 new token: `KudosLinkRed` (`#E46060`) — inline link color on the Send Kudos form toolbar.

---

## [Unreleased] — Phase 06: Send Kudos (post-delivery fix, 2026-06-15)

### Added
- `ui/components/MarkdownText.kt` — shared inline-markdown renderer: `parseKudoMarkdown(raw, linkColor): AnnotatedString` handles **bold**, *italic*, ~~strikethrough~~, `[label](url)`; `MarkdownText` composable wraps it. Used by `KudosCard` and `KudoDetailCard` so kudos sent with the rich-text toolbar render formatted instead of showing raw markers; also powers the Send Kudos preview dialog.
- `feature/send/components/KudoPreviewDialog.kt` — "Xem trước Kudo" preview dialog; reuses `KudosCard` to show the full composed kudo card before submission.

### Changed
- `ui/theme/Color.kt` — 2 new tokens: `KudosContainer2` (`#00070C`, dark dropdown overlay background), `KudosDropdownHighlight` (`rgba(255,234,158,0.20)`, selected dropdown row highlight). Recipient, danh hiệu, and hashtag dropdowns corrected to dark backgrounds per design.
- `ui/components/KudosCard.kt`, `feature/feed/components/KudoDetailCard.kt` — kudo message rendering switched from raw `Text` to `MarkdownText`.
- Send Kudos action buttons shaped as 4 dp rounded-rect (local to the send screen); shared pill buttons (`KudosButton.kt`) unchanged.

---

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
