# Code Standards

## Language & Build

- Kotlin only (no Java)
- Jetpack Compose (no XML layouts)
- Material3 component library
- Kotlin DSL for Gradle

## File Size

Keep files under 200 lines. Split by responsibility if a file grows beyond that.

## Naming Conventions

| Artifact | Convention | Example |
|---|---|---|
| Files | PascalCase | `KudosButton.kt` |
| Composables | PascalCase | `KudosPrimaryButton` |
| Color tokens | PascalCase with `Kudos` prefix | `KudosGold` |
| Enums | PascalCase | `BottomNavTab` |
| Private shapes/constants | camelCase | `PillShape` |

## Theme Usage

- Always wrap screens in `KudosAppTheme` (not raw `MaterialTheme`)
- Consume colors via `MaterialTheme.colorScheme.*` wherever a Material3 role applies
- Reference brand tokens (e.g. `KudosGold`) directly only when no Material3 role maps cleanly (e.g. nav item indicator colors in `KudosBottomNav`)
- Never hardcode color hex values inside composables — use tokens from `Color.kt`

## Component Guidelines

### Buttons

Three variants in `KudosButton.kt` — use the right one for the use case:

| Composable | Shape | Height | Use case |
|---|---|---|---|
| `KudosPrimaryButton` | Pill (50% radius) | 56 dp | Primary CTA (e.g. login) |
| `KudosSecondaryButton` | Pill (50% radius) | 44 dp | Secondary action; pass `showExternalIcon = true` for external links |
| `KudosTextButton` | Default | — | Tertiary / inline action |

All button labels use `MaterialTheme.typography.labelLarge`.
Caller controls width via `modifier` — use `Modifier.fillMaxWidth()` for full-width buttons.

### Top Bar

`KudosTopBar` in `KudosTopBar.kt`:
- Height: 56 dp (below status bar), horizontal padding: 16 dp; applies `statusBarsPadding()` + vertical gradient for edge-to-edge legibility
- Slots: `ic_logo_saa` drawable 48×44 dp (left); language selector + search + notification icons (right)
- Language selector: shows `ic_vn_flag` image for VN locale, flag emoji for EN; meets 48 dp touch target via `minimumInteractiveComponentSize`
- Notification slot: `BadgedBox` with a gold 8 dp badge dot when `unreadCount > 0`

### Bottom Navigation

`KudosBottomNav` in `KudosBottomNav.kt`:
- Four tabs defined in `BottomNavTab` enum: `Saa2025`, `Awards`, `Kudos`, `Profile`
- Each tab carries a `@DrawableRes` icon field — Figma vector drawables (`ic_nav_home`, `ic_nav_awards`, `ic_nav_kudos`, `ic_nav_profile`); do not use Material icon references here
- Selected state: `KudosGold` icon + label, `KudosDivider` indicator background
- Unselected state: `KudosGray` icon + label
- `tonalElevation = 0.dp` (no tonal overlay); `navigationBarsPadding()` applied
- Tab order is defined by `BottomNavTab.entries` — add new tabs by extending the enum

## Typography

Use `KudosTypography` styles via `MaterialTheme.typography.*`. Do not create ad-hoc `TextStyle` values inside composables.

| Style | Size | Weight | Typical use |
|---|---|---|---|
| `displayLarge` | 52 sp | ExtraBold | Hero/splash text |
| `headlineLarge` | 26 sp | Bold | Section headers |
| `headlineMedium` | 22 sp | Bold | Sub-section headers |
| `headlineSmall` | 18 sp | SemiBold | Minor section headers |
| `titleLarge` | 16 sp | SemiBold | Card/screen titles |
| `titleMedium` | 14 sp | Medium | Secondary titles |
| `bodyLarge` | 16 sp | Normal | Primary body copy |
| `bodyMedium` | 14 sp | Normal | Secondary body copy |
| `bodySmall` | 12 sp | Normal | Captions, fine print |
| `labelLarge` | 14 sp | SemiBold | Button labels, active nav labels |
| `labelMedium` | 12 sp | Medium | Inactive nav labels |
| `labelSmall` | 10 sp | Medium | Small labels |

## ViewModel Conventions

- Screens without persistence → extend `ViewModel` (plain).
- Screens that read/write `KudosPreferences` (DataStore) → extend `AndroidViewModel` — requires `Application` context.
- Screens that read from `KudosRepository` only → extend plain `ViewModel` (repository is a process-level singleton, no `Application` context needed).
- Never pass `Context` into a plain `ViewModel`; inject it only via `AndroidViewModel`.

## Image Loading

Do not add Coil or Glide during the mock/development phase. For images sourced from the Android Photo Picker (local URIs), decode to `Bitmap` via `android.graphics.BitmapFactory` inside the ViewModel's coroutine scope. When the app moves to remote image URLs, introduce Coil at that point.

## Slot Pattern (Screen-level)

When a screen host needs to inject feature-specific UI (e.g., filter dropdowns, charts) without coupling the stateless composable to its dependencies, use named slot lambdas:

```kotlin
@Composable
fun KudosFeedScreen(
    filterRow: @Composable () -> Unit,  // slot: hashtag + dept dropdowns
    spotlight: @Composable () -> Unit,  // slot: SpotlightNetworkChart
    ...
)
```

The route composable (in `*Navigation.kt`) injects the real content; the screen composable stays testable and stateless. Use this pattern when a composable otherwise needs a ViewModel or context it shouldn't own directly.

### Kudo Message Rendering

Always render kudo message text via `MarkdownText` (in `ui/components/MarkdownText.kt`), never raw `Text`. This applies to feed cards (`KudosCard`, `KudoDetailCard`) and any preview surface (`KudoPreviewDialog`). Raw `Text` will display markdown markers literally instead of formatted output.

```kotlin
// Correct
MarkdownText(text = kudo.message, style = MaterialTheme.typography.bodyMedium)

// Wrong — shows raw markers (**bold** instead of bold)
Text(text = kudo.message)
```

### Action Button Shapes (Screen-local vs Shared)

Shared pill buttons (`KudosPrimaryButton`, `KudosSecondaryButton`) keep their 50% radius shape globally. Screen-local action buttons (e.g., the send-screen toolbar actions) may use a different shape (e.g., 4 dp rounded-rect) defined locally in that screen's component file — do not modify `KudosButton.kt` for screen-specific variants.

## Pure Logic Files

Isolate non-UI, non-Android logic into dedicated `*Logic.kt` files (e.g., `KudosFeedLogic.kt`). These files:
- Contain only `fun` with value-type parameters — no Compose, no Android imports
- Can be unit-tested without instrumentation
- Are the first target for unit tests in each feature module

## ExoPlayer-in-Compose Lifecycle Pattern

When embedding `ExoPlayer` via `AndroidView` inside a Compose screen (e.g., `GiftBoxAnimation`):

1. Create the player inside `remember { ExoPlayer.Builder(context).build() }` — one instance per composition.
2. Pause/resume the player in response to `Lifecycle.Event.ON_STOP` / `ON_START` using a `LifecycleEventObserver` registered inside `DisposableEffect(player, lifecycleOwner)`. This prevents the player decoding and emitting audio while the app is in the background.
3. Release the player in the `onDispose` block of the same `DisposableEffect`.
4. Use `rememberUpdatedState(phase)` for any value captured by the observer/listener so the observer always reads the current phase without being re-registered.
5. Control playback mode and media items in a `LaunchedEffect(phase)` block — not inside the observer.

```kotlin
DisposableEffect(player, lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> player.pause()
            Lifecycle.Event.ON_START -> if (currentPhase != SecretBoxPhase.REWARD) player.play()
            else -> Unit
        }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
        player.release()
    }
}
```

Do not add Coil or any image-loading library for video — use Media3 ExoPlayer for MP4 assets from `res/raw/`.

## ViewModel StateFlow Tests (MainDispatcherRule + runTest)

For ViewModels that expose `StateFlow` built with `combine(...).stateIn(WhileSubscribed(...))`, use `kotlinx-coroutines-test` with `UnconfinedTestDispatcher`:

1. Add `@get:Rule val mainDispatcherRule = MainDispatcherRule()` to the test class — this swaps `Dispatchers.Main` for a test dispatcher so coroutines launched by `viewModelScope` run synchronously.
2. Activate the `StateFlow` inside `runTest` by collecting it on a `backgroundScope` with `UnconfinedTestDispatcher(testScheduler)` — this triggers the `WhileSubscribed` upstream so `.value` reflects mutations immediately.
3. Reset any `object` singleton repositories in `@Before` and `@After` via an `internal fun resetForTest()` on the repository — ensures test order independence.

```kotlin
@get:Rule val mainDispatcherRule = MainDispatcherRule()

@Test fun example() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
    vm.onBoxTap()
    assertEquals(SecretBoxPhase.OPENING, vm.uiState.value.phase)
}
```

This pattern applies to any ViewModel that uses `SharingStarted.WhileSubscribed` — not just SecretBox.

## String Resources and Localization

All main screens use Android string resources for user-facing text. Runtime locale switching (VN ↔ EN) is handled by `LanguageManager` + `CompositionLocalProvider` in `MainActivity` — no Activity recreation required.

**File layout:**
- `res/values/strings.xml` — shared/cross-feature Vietnamese keys (default locale). Add here first.
- `res/values-en/strings.xml` — shared English overrides. Must mirror every key in the VN file.
- `res/values/strings_<feature>.xml` — per-feature VN strings. Use a feature-specific prefix for all keys (e.g. `home_`, `awards_`, `feed_`, `profile_`, `notifications_`, `secretbox_`, `send_`) to prevent key collisions across features.
- `res/values-en/strings_<feature>.xml` — per-feature EN overrides. Must mirror the corresponding VN file.

**Adding new strings:**
1. Add the VN string to `res/values/strings_<feature>.xml` with the feature prefix.
2. Add the EN override to `res/values-en/strings_<feature>.xml`.
3. Use `stringResource(R.string.feature_key)` inside the composable.

**`@StringRes` in data classes:** When a data class field holds user-visible text, declare it as `@StringRes Int` (not `String`). Resolve it via `stringResource(field)` at the composable call site. Never call `context.getString(...)` inside a composable.

```kotlin
// Data class — store the resource ID
data class AwardContent(
    @StringRes val title: Int,
    @StringRes val description: Int,
)

// Composable — resolve at render time
Text(text = stringResource(award.title))
```

**Do not** use `LocalContext.current.getString(...)` inside composables — `stringResource(...)` is required so the lookup resolves against the `CompositionLocal`-overridden locale.

**ContextThemeWrapper pitfall (MainActivity locale wrapping):** Use `ContextThemeWrapper(this, theme)` to build the locale-overridden context — do **not** use `createConfigurationContext`. `createConfigurationContext` detaches the `Activity` from its `ComponentActivity` identity, breaking `LocalActivityResultRegistryOwner` (used by `rememberLauncherForActivityResult` / Photo Picker) and causing a crash. See `system-architecture.md` → "Localization / i18n Architecture" for the full explanation.

**Mock/user-generated content** (kudo messages, names, hashtags) is intentionally not localized — hardcoded Vietnamese is correct for these fields.

## Previews

Every component file must include at least one `@Preview` using `KudosAppTheme` with `backgroundColor = 0xFF00101A` (or the relevant surface color) so previews render on the correct dark background.
