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
│   ├── CurrentUser.kt              — object singleton: single source of truth for the signed-in Sunner (ID = "u1", KudoUser "Phan Văn Minh"); referenced by ProfileMockData, SendKudosMockData, and feed mock data
│   ├── KudosPreferences.kt         — DataStore Preferences persistence: liked kudo IDs + recent search terms (Flow<Set<String>>); also persists current user id via setCurrentUser() / currentUserId: Flow<String?>
│   ├── KudosRepository.kt          — in-memory singleton; MutableStateFlow<List<Kudo>> seeded from KudosMockData; shared source of truth for feed + send feature modules
│   ├── NotificationsRepository.kt  — in-memory singleton; MutableStateFlow<List<AppNotification>> seeded from NotificationsMockData; exposes notifications: StateFlow, unreadCount: StateFlow<Int>, markRead(id), markAllRead(); observed by Home + Feed + Profile ViewModels for bell-badge sync
│   └── SecretBoxRepository.kt      — in-memory singleton; MutableStateFlow<SecretBoxCounts> (unopened/opened tallies); openOne() decrements unopened and increments opened; observed by SecretBoxViewModel and MyProfileViewModel so Profile stats card stays in sync
├── feature/
│   ├── auth/
│   │   ├── LoginScreen.kt   — login UI (key-visual, ROOT FURTHER logo, Google SSO button, language overlay)
│   │   └── LoginViewModel.kt — AndroidViewModel; AppLanguage enum, loading StateFlow, 1s mock auth; persists current user via KudosPreferences.setCurrentUser on login
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
│   ├── home/
│   │   ├── HomeScreen.kt    — scrollable Home: hero, countdown, awards, kudos, note sections, FAB
│   │   ├── HomeViewModel.kt — HomeUiState + CountdownState StateFlow; live 1s countdown; mock badge count
│   │   └── components/      — 9 composables: HomeHeroSection, CountdownRow, HeroActionButtons,
│   │                          SectionHeader, HomeAwardsSection, AwardCard,
│   │                          HomeKudosSection, HomeNoteSection, HomeFab
│   ├── send/
│   │   ├── SendKudosScreen.kt      — Send Kudos form: recipient search, danh hiệu dropdown, rich-text toolbar, message, hashtag multi-select (max 5), Photo Picker (max 5 images), anonymous toggle + nickname, validation
│   │   ├── SendKudosViewModel.kt   — form state, validation, submit() prepends to KudosRepository
│   │   └── CommunityStandardsScreen.kt — 10 community criteria + security section
│   ├── notifications/
│   │   ├── NotificationsScreen.kt   — detail screen: back arrow, no bottom nav, mark-all-read, per-type tap navigation; localized title (VN "Thông báo" / EN "Notifications")
│   │   ├── NotificationsViewModel.kt — reads NotificationsRepository; exposes NotificationsUiState; calls markRead / markAllRead
│   │   ├── NotificationModels.kt    — AppNotification data class, NotificationType enum (7 types)
│   │   ├── NotificationsMockData.kt — mock dataset
│   │   └── components/              — 4 composables: NotificationItem, NotificationsTopBar, MarkAllReadButton, NotificationIconMapper
│   ├── secretbox/
│   │   ├── SecretBoxScreen.kt      — detail screen (back arrow, no bottom nav): 3-state UI driven by SecretBoxPhase; CLOSED shows looping idle video + unopened counter; OPENING plays tap + open videos once; REWARD reveals a prize PNG + "Tiếp tục" button
│   │   ├── SecretBoxViewModel.kt   — plain ViewModel; combines local phase/reward MutableStateFlow with SecretBoxRepository.counts into SecretBoxUiState; onBoxTap / onOpenAnimationEnd / onContinue drive the state machine
│   │   ├── SecretBoxModels.kt      — SecretBoxPhase enum (CLOSED/OPENING/REWARD), SecretBoxReward data class (id, name, imageResName)
│   │   ├── SecretBoxMockData.kt    — 6-prize reward pool; randomReward() selects one on open; imageResName resolved at runtime via resources.getIdentifier
│   │   └── components/             — 5 composables: GiftBoxAnimation (ExoPlayer VideoView), GiftBoxPlaceholder, SecretBoxHeader, SecretBoxRewardView, SecretBoxTopBar
│   └── profile/
│       ├── MyProfileScreen.kt      — own-profile: header, stats card, kudos filter (Đã nhận / Đã gửi), kudos list, icon collection, secret-box CTA
│       ├── MyProfileViewModel.kt   — filter state, like toggle, language toggle; reads KudosRepository + KudosPreferences
│       ├── UserProfileScreen.kt    — other-user profile: header, award badges row, received-kudos list, Send Kudos CTA
│       ├── UserProfileViewModel.kt — resolves user from mock data; like toggle
│       ├── ProfileModels.kt        — ProfileKudosTab enum, ProfileStats, AwardBadge (icon: Int? — wired to real drawables via RankBadge)
│       ├── ProfileMockData.kt      — mock dataset seeding both profile ViewModels; references CurrentUser for signed-in identity
│       └── components/             — 11 composables: ProfileHeader, ProfileStatsCard, ProfileKudosFilter,
│                                      ProfileAwardBadges, ProfileIconCollection, ProfileReceivedKudosLabel,
│                                      ProfileSectionHeader, ProfileSendKudosCta,
│                                      UserProfileHeader, UserProfileSectionHeader,
│                                      RankBadge (maps badge label → img_rank_* pill image; shared by both profile headers)
├── navigation/
│   ├── NavRoutes.kt              — route constants + builder helpers (17 destinations); PROFILE_ME = "my-profile" (not "profile/me" — avoids PROFILE_USER wildcard capture); KUDOS_SEND_WITH_ARG for optional recipient pre-fill
│   ├── AppNavGraph.kt            — NavHost; LOGIN is startDestination; all major features wired to real screens
│   ├── KudosFeedNavigation.kt        — feed route composables (KudosFeedRoute, KudosAllRoute, ViewKudoRoute, KudosSearchRoute); slot injection for filters and Spotlight
│   ├── ProfileNavigation.kt          — profile route composables (MyProfileRoute, UserProfileRoute); follows same extraction pattern as KudosFeedNavigation
│   ├── NotificationsNavigation.kt    — NotificationsRoute composable; same extraction pattern
│   ├── SecretBoxNavigation.kt        — SecretBoxRoute composable; same extraction pattern; route: "secret-box"
│   └── SendKudosNavigation.kt        — send-kudos route composable
└── ui/
    ├── KudosApp.kt          — root composable: Scaffold (contentWindowInsets=0) + KudosBottomNav + AppNavGraph
    ├── theme/
    │   ├── Color.kt         — brand color tokens (20 constants; 3 added Phase 05: KudosAccentRed, KudosCardMuted, KudosCardFaint; 3 added Phase 06: KudosFormCream, KudosDropdownHighlight, KudosLinkRed; KudosContainer2 is a base token not a phase addition)
    │   ├── Type.kt          — KudosTypography (11 Material3 text styles)
    │   └── Theme.kt         — KudosAppTheme composable (dark-only, no dynamic color)
    └── components/
        ├── KudosButton.kt           — KudosPrimaryButton, KudosSecondaryButton, KudosTextButton
        ├── KudosTopBar.kt           — real ic_logo_saa drawable (48×44 dp), ic_vn_flag asset, BadgedBox bell, statusBarsPadding() + vertical gradient overlay; optional params: showScrim (default true — suppress gradient for full-bleed screens) and onBack (default null — renders back arrow when non-null)
        ├── KudosBottomNav.kt        — BottomNavTab enum uses @DrawableRes ic_nav_* Figma vector drawables; navigationBarsPadding() applied
        ├── KudoAvatar.kt            — circular avatar composable
        ├── KudosCard.kt             — shared card surface; renders kudo message via MarkdownText
        ├── MarkdownText.kt          — shared inline-markdown renderer: parseKudoMarkdown() → AnnotatedString (**bold**, *italic*, ~~strike~~, [label](url)); MarkdownText composable. Used by KudosCard, KudoDetailCard, and KudoPreviewDialog.
        ├── HashtagFilterDropdown.kt — overlay hashtag filter (AND logic with department)
        └── DepartmentFilterDropdown.kt — overlay department filter
```

## Navigation

The app uses `navigation-compose 2.8.0` with a single `NavHost` defined in `AppNavGraph.kt`.

- All 13 route strings live in `NavRoutes.kt` — the sole source of truth for destination names.
- `KudosApp.kt` owns the `NavController` and passes it to both `KudosBottomNav` and `AppNavGraph`.
- `startDestination` is `NavRoutes.LOGIN`; after successful login the stack is popped and `HOME` becomes the root.
- `KudosBottomNav` is hidden on the login screen — only shown when the current route matches a `BottomNavTab` destination.
- Profile screens (`PROFILE_ME`, `PROFILE_USER`) suppress the global bottom bar — the guard lives in `KudosApp.kt` (`isProfileScreen` flag). `MyProfileScreen` renders its own bottom nav; `UserProfileScreen` is a detail screen with a back arrow (via `KudosTopBar(onBack=…, showScrim=false)`) and no bottom nav. Both screens use a full-bleed key-visual background.
- `PROFILE_ME` uses the distinct path `"my-profile"` (not `"profile/me"`) to prevent the `PROFILE_USER = "profile/{userId}"` wildcard from capturing it as `userId="me"`.
- Feature-specific route composables are extracted to dedicated navigation files (`KudosFeedNavigation.kt`, `ProfileNavigation.kt`, `SendKudosNavigation.kt`) to keep `AppNavGraph.kt` under 200 lines — follow this pattern for future feature modules.
- Hashtag cross-screen navigation: secondary screens (View / AllKudos) stash the tag on the Feed's `SavedStateHandle` and pop back, so the Feed ViewModel picks it up without re-composing.
- Real screens: LOGIN, HOME, KUDOS_FEED, KUDOS_ALL, KUDOS_VIEW, KUDOS_SEARCH, KUDOS_SEND, KUDOS_COMMUNITY_STANDARDS, PROFILE_ME, PROFILE_USER, NOTIFICATIONS, SECRET_BOX. Remaining routes still use placeholder composables.
- `NOTIFICATIONS` is a detail screen (back arrow, no bottom nav) — same treatment as `PROFILE_USER`. `KudosApp` suppresses the global bottom bar for this route.

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

Tokens added in Phase 06:
- `KudosFormCream` (`#FFF8E1`) — Send Kudos form surface background
- `KudosContainer2` (`#00070C`) — dark dropdown overlay background (recipient, danh hiệu, hashtag dropdowns)
- `KudosDropdownHighlight` (`rgba(255,234,158,0.20)` ≈ `#33FFEA9E`) — selected row highlight inside dark dropdowns
- `KudosLinkRed` (`#E46060`) — inline link color on the Send Kudos form toolbar ("Tiêu chuẩn cộng đồng")

## Key Dependencies

| Library | Version | Purpose |
|---|---|---|
| `navigation-compose` | 2.8.0 | In-app navigation |
| `lifecycle-viewmodel-compose` | 2.6.1 | ViewModel integration in Compose |
| `lifecycle-runtime-ktx` | (catalog) | Lifecycle-aware coroutines |
| `datastore-preferences` | 1.1.1 | Persistence: liked kudo IDs + recent search terms |
| `media3-exoplayer` + `media3-ui` | 1.4.1 | Video playback (Secret Box idle/open animations from `res/raw/*.mp4`); first video dependency in project |
| DSEG7 Classic font (TTF) | — | Seven-segment countdown digits; bundled in `res/font/`; SIL OFL license in `assets/` |

## Persistence Layer

`data/KudosPreferences.kt` is the project's DataStore persistence layer (introduced in Phase 05).

- **Storage**: `DataStore Preferences` (key-value, async, Flow-based) — do not use `SharedPreferences`.
- **Pattern**: expose `Flow<Set<String>>` for read; suspend functions for write. ViewModels combine these Flows into UI state via `combine`.
- **Scope**: `AndroidViewModel` required (needs `Application` context for DataStore) — feature ViewModels that touch persistence must extend `AndroidViewModel`, not plain `ViewModel`. `LoginViewModel` was promoted to `AndroidViewModel` in the Phase 07 refinement to support `setCurrentUser` at login.

## Shared In-Memory State

`data/KudosRepository.kt` is the app-process in-memory shared state store (introduced in Phase 06).

- **Pattern**: Kotlin `object` singleton; holds a `MutableStateFlow<List<Kudo>>` seeded from `KudosMockData` at init time. Exposes an immutable `StateFlow` + a `kudoById(id)` helper.
- **Cross-feature use**: `KudosFeedViewModel` combines `KudosRepository.kudos` with `KudosPreferences` flows; `SendKudosViewModel.submit()` calls `KudosRepository.prepend(kudo)` so the submitted kudo appears at the top of the feed immediately. `ViewKudoRoute` resolves the displayed kudo via `KudosRepository.kudoById`.
- **Scope**: process-lifetime only (no disk persistence). The store resets on process death — this is intentional for the current mock phase. Migrate to a Room-backed repository when real API integration begins.

## Notifications Shared State

`data/NotificationsRepository.kt` is the in-memory shared state store for notifications (introduced in Phase 08).

- **Pattern**: Kotlin `object` singleton; same structure as `KudosRepository`. Holds a `MutableStateFlow<List<AppNotification>>` seeded from `NotificationsMockData`. Derives `unreadCount: StateFlow<Int>` via `map + stateIn(Eagerly)`.
- **Cross-feature use**: `NotificationsViewModel` calls `markRead(id)` / `markAllRead()`. `HomeViewModel`, `KudosFeedViewModel`, and profile ViewModels observe `unreadCount` so the bell badge in `KudosTopBar` stays in sync across all screens after any read event.
- **Scope**: process-lifetime only (no disk persistence) — same intentional constraint as `KudosRepository`.

## Secret Box Shared State

`data/SecretBoxRepository.kt` is the in-memory shared state store for Secret Box counts (introduced in Phase 09).

- **Pattern**: Kotlin `object` singleton; same structure as `KudosRepository` and `NotificationsRepository`. Holds a `MutableStateFlow<SecretBoxCounts>` seeded from design values (unopened=5, opened=25).
- **Cross-feature use**: `SecretBoxViewModel` reads `counts` to gate the open action and display the counter. `MyProfileViewModel` also combines `SecretBoxRepository.counts` into `ProfileUiState` — so tapping "Tiếp tục" on the Secret Box screen decrements the counter visible on the Profile stats card without any additional signaling.
- **Video assets**: 3 MP4 files in `res/raw/` (`secretbox_idle.mp4`, `secretbox_tap.mp4`, `secretbox_open.mp4`) played via `androidx.media3` ExoPlayer (1.4.1) — the first use of video playback in the project.
- **Scope**: process-lifetime only (no disk persistence) — same intentional constraint as the other repositories.

## Signed-in User Identity

`data/CurrentUser.kt` is the single source of truth for the mock signed-in Sunner.

- **Pattern**: Kotlin `object` singleton with a `const val ID` and a pre-built `KudoUser profile`. No async reads needed — consumed directly at call sites.
- **Session persistence**: `KudosPreferences.setCurrentUser(id)` writes the id to DataStore at login; `currentUserId: Flow<String?>` exposes it for future profile hydration.
- **Cross-feature references**: `ProfileMockData.CURRENT_USER_ID`, `SendKudosMockData.currentUser`, and feed mock data all reference `CurrentUser` — ensuring the same name, code, and badge display consistently across profile, send, and feed.

## Image Handling

The app intentionally avoids image-loading libraries (no Coil, no Glide) for bitmap thumbnails in the Send Kudos photo picker. Selected images from the Android Photo Picker are decoded synchronously via `android.graphics.BitmapFactory` in the ViewModel's coroutine scope. This keeps the dependency surface minimal during the mock phase. Add Coil when the feature moves to remote URLs.
