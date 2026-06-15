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

## Previews

Every component file must include at least one `@Preview` using `KudosAppTheme` with `backgroundColor = 0xFF00101A` (or the relevant surface color) so previews render on the correct dark background.
