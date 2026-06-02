---
phase: "01"
title: Design System & Theme
priority: critical
status: done
---

# Phase 01 — Design System & Theme

**Goal:** Extract design tokens from MoMorph and wire into Compose theme before any screen work.

## MoMorph refs (design system frames)
- Color: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/B-HozgdIJd
- Typography: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/a8VSeudT6N
- Button: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/1wjEyOVU4v
- Icon: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/rCAXfPH2gn
- Navigation bar: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/VeF77wVOUW
- Top Navigation: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/j3ey-zXK89
- Component: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/dSQdI_iZpt

## Files to create/modify
- `app/src/main/java/com/sun/kudos_demo/ui/theme/Color.kt` — brand color palette
- `app/src/main/java/com/sun/kudos_demo/ui/theme/Type.kt` — text styles
- `app/src/main/java/com/sun/kudos_demo/ui/theme/Theme.kt` — Material3 theme
- `app/src/main/java/com/sun/kudos_demo/ui/components/KudosButton.kt` — button variants
- `app/src/main/java/com/sun/kudos_demo/ui/components/KudosTopBar.kt` — top app bar
- `app/src/main/java/com/sun/kudos_demo/ui/components/KudosBottomNav.kt` — bottom nav

## Out of scope
- No screen-level layout — components only
- No business logic or API calls

## Success criteria
- Compose preview renders button/text/nav correctly with brand colors
- Theme compiles without errors

## Completed

✅ **Color.kt** — 16 brand color tokens extracted from MoMorph Color variable collection (primary, secondary, tertiary, surface, error, neutral variants)
✅ **Type.kt** — Material3 typography scale with 11 named styles (KudosTypography): displayLarge, displayMedium, displaySmall, headlineLarge, headlineMedium, headlineSmall, titleLarge, titleMedium, bodyLarge, bodyMedium, labelLarge
✅ **Theme.kt** — KudosAppTheme composable applying KudosColorScheme + KudosTypography, dark-only, no dynamic colors
✅ **KudosButton.kt** — KudosPrimaryButton, KudosSecondaryButton, KudosTextButton variants with proper theming
✅ **KudosTopBar.kt** — KudosTopBar composable with logo, language selector, search field, notifications icon
✅ **KudosBottomNav.kt** — KudosBottomNav with 4 tabs (SAA 2025, Awards, Kudos, Profile), BottomNavTab enum routing
✅ **Compilation** — All files compile cleanly with no errors
