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
- Height: 56 dp, horizontal padding: 16 dp
- Slots: logo (left), language selector + search + notification icons (right)
- Logo is currently a `Text` placeholder ("SAA 2025"); replace with actual drawable in a later phase
- Language selector meets 48 dp touch target via `minimumInteractiveComponentSize`

### Bottom Navigation

`KudosBottomNav` in `KudosBottomNav.kt`:
- Four tabs defined in `BottomNavTab` enum: `SAA2025`, `Awards`, `Kudos`, `Profile`
- Selected state: `KudosGold` icon + label, `KudosDivider` indicator background
- Unselected state: `KudosGray` icon + label
- `tonalElevation = 0.dp` (no tonal overlay)
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

## Previews

Every component file must include at least one `@Preview` using `KudosAppTheme` with `backgroundColor = 0xFF00101A` (or the relevant surface color) so previews render on the correct dark background.
