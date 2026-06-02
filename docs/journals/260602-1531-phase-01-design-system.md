# Phase 01: Design System & Theme — Complete

**Date**: 2026-06-02 15:35
**Severity**: Low
**Component**: UI / Theme / Design System
**Status**: Resolved

## What Happened

Completed Phase 01 (Design System & Theme) for Kudos Android app. Built foundational Compose theme layer: color tokens from MoMorph design spec (navy + gold), Material3 typography, button variants, and navigation components. All files compile and align with design spec.

## The Brutal Truth

This phase felt like detective work. MoMorph design system frames returned empty node trees — no component specs, no style metadata. Had to reverse-engineer patterns from actual app screens (Login, Home, Kudos Feed) instead of working from authoritative design system frames. It's frustrating because the design *exists* in Figma, but the API couldn't surface the detail. Wasted 20 minutes fetching empty node lists before pivoting to screen-based approach.

The upside: app screens gave clearer patterns anyway. Lesson: design system frames don't always export as usable data — sometimes you learn faster from the real screens.

## Technical Details

**Files created:**
- `app/src/main/kotlin/com/example/kudos/ui/theme/Color.kt` — 16 brand tokens (navy `#00101a`, gold `#ffea9e`), Material3 semantic colors
- `app/src/main/kotlin/com/example/kudos/ui/theme/Type.kt` — `KudosTypography` with 11 text styles (headline, title, body, label)
- `app/src/main/kotlin/com/example/kudos/ui/theme/Theme.kt` — `KudosAppTheme` composable, dark scheme only
- `app/src/main/kotlin/com/example/kudos/ui/components/KudosButton.kt` — `KudosPrimaryButton`, `KudosSecondaryButtonNormal`, `KudosTextButton`
- `app/src/main/kotlin/com/example/kudos/ui/components/KudosTopBar.kt` — top bar with logo text, language selector (48dp tap target), search/notification icons
- `app/src/main/kotlin/com/example/kudos/ui/components/KudosBottomNav.kt` — `BottomNavTab` enum, 4-tab navigation (SAA 2025 / Awards / Kudos / Profile)

**Key technical decisions:**
1. **Renamed `Typography` → `KudosTypography`** — avoids class shadowing `androidx.compose.material3.Typography`. Prevents ambiguous imports in future files using the theme. Caught by reviewer; this is a real gotcha in Compose.
2. **Removed `fillMaxWidth()` from `KudosPrimaryButton`** — buttons don't force width; caller passes modifier. Respects composition principle, reduces surprises later.
3. **`KudosSecondaryButtonNormal` uses `containerColor`** — Material3 secondary button applies background color via `containerColor` field, not `backgroundColor`. Had to verify against Material3 API docs.
4. **`minimumInteractiveComponentSize()` + `Role.DropdownList` on language selector** — hits 48dp touch target minimum without explicit size. Accessibility requirement, not optional.
5. **TopBar uses `MaterialTheme.colorScheme.*` instead of raw hex** — theme colors respond to future overrides (e.g., if we ever add light mode, high-contrast mode). One line change in Theme.kt, all components update automatically.

**API gaps:**
- `get_frame` on MoMorph design system frames returned empty `children: []` for all 12 component definitions — no node hierarchy, no style properties
- Frame images API returned 404 for ~70% of design frames; only succeeded on full app screens
- `Icons.Default.OpenInNew` missing from Material3 icons; used unicode "↗" instead (works, not elegant)

## What We Tried

1. **Fetch MoMorph design system frames directly** — `get_frame(design-system-frame-id)` → empty node list. Tried 3 different frame IDs, all empty.
2. **Download design system specs as CSV** — returned column headers but no rows (no components indexed in spec sheet)
3. **Use MoMorph frame images for component visual reference** — 404 on most frames; switched to downloading actual app screen images (Login, Home, Kudos Feed) which succeeded
4. **Extract icon from Material3 Icons Extended library** — `Icons.Outlined` / `Icons.Filled` don't have `OpenInNew`; checked Extended set — not there either. Fallback to unicode character.

## Root Cause Analysis

**MoMorph API limitation:** Design system frames aren't indexed or exported the same way as full-screen mockups. The design exists in Figma but the API surface doesn't expose component-level detail. This is a MoMorph architecture gap, not user error.

**Why it stung:** Spent 20 minutes assuming I was missing an API parameter or query. Didn't realize early that the empty response meant "this frame type isn't exported"; should have checked MoMorph docs first or asked team upfront: "Are design system frames queryable?"

**Why it didn't kill the phase:** App screens gave clearer patterns anyway. Real screens show how tokens are *used*, not just how they're named. Sometimes working backwards from concrete examples is faster than parsing abstract component specs.

## Lessons Learned

1. **Design system frames may not export via API** — If you get empty node list on a design frame, assume the API doesn't support that frame type. Don't retry. Pivot to concrete screens or ask the design team.
2. **`minimumInteractiveComponentSize()` is non-negotiable** — Don't skip it thinking "the button looks big enough." It's a Material3 accessibility standard and will fail design review (or worse, user accessibility audit).
3. **Avoid shadowing Material3 classes** — Name custom type wrappers distinctly (e.g., `KudosTypography` not `Typography`). Saves cognitive load and prevents import ambiguity bugs later.
4. **Caller controls layout, component controls appearance** — Buttons should define shape/color/padding, not width. Width is layout concern. This keeps components reusable.
5. **Raw hex colors are technical debt** — Always wire through `MaterialTheme.colorScheme` so theme changes propagate. Future you (or a teammate) will add light mode and thank you.

## Next Steps

1. **Phase 02 — Screens & Navigation** (dependent): Integrate these theme/button/nav components into Login, Home, and Kudos Feed screens
2. **Language selector dropdown** — Currently a menu placeholder; needs actual language list binding + LocaleManager integration (deferred to Phase 03 or later if not blocking)
3. **Icon library audit** — Document which Material3 icons we're using; flag missing ones early (e.g., `OpenInNew` — consider migrating to Feather or custom SVGs if icon set gaps grow)
4. **Accessibility review** — Run with screen reader before Phase 02 completion; check tap targets, label contrast, semantic roles

**Owner:** Complete, ready for Phase 02 planning.
