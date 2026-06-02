---
date: 2026-06-02
phase: "02"
scope: Navigation scaffold
score: 9/10
status: DONE
---

# Code Review — Phase 02: App Navigation Setup

## Scope
- Files: NavRoutes.kt (32 L), AppNavGraph.kt (72 L), KudosApp.kt (54 L), KudosBottomNav.kt (82 L), MainActivity.kt (20 L)
- Total: 260 LOC across 5 files
- Focus: Completeness vs blueprint, Compose Navigation correctness, type safety, YAGNI/KISS/DRY

## Overall Assessment

Solid, clean scaffold. All 13 required routes are present and registered, bottom nav is correctly wired, build compiles successfully. Two minor issues worth addressing before Phase 03 writes auth-guard logic, and one legacy-theme carryover from Phase 01 that could cause edge-to-edge glitches on API 26–27.

---

## Critical Issues

None.

---

## High Priority

### H1 — `profile/me` vs `profile/{userId}` route collision risk (warning)

`PROFILE_ME = "profile/me"` and `PROFILE_USER = "profile/{userId}"` share the same path depth. Compose Navigation 2.8 does prioritize literal routes over parameterized ones, and the current registration order (literal first, template second) is correct. However, this is fragile: if the order in `AppNavGraph.kt` is ever inverted, `profile/me` will silently route to `UserProfileScreen` with `userId = "me"`. The fix is to either rename `PROFILE_ME` to a distinct path (e.g., `"profile/self"`) or add a comment in `AppNavGraph.kt` flagging the ordering dependency so it survives future edits.

Fix option:
```kotlin
// IMPORTANT: PROFILE_ME must be registered BEFORE PROFILE_USER.
// Nav-compose matches literal routes before parameterized ones only when registered first.
composable(NavRoutes.PROFILE_ME) { ... }
composable(route = NavRoutes.PROFILE_USER, ...) { ... }
```

### H2 — `startDestination = HOME` with no auth guard (suggestion)

`AppNavGraph` defaults `startDestination = NavRoutes.HOME`, which is correct per scope (auth guard is Phase 03). However, there is no TODO/FIXME comment noting this is intentional. When Phase 03 author looks at this, the absence of context could cause them to assume it's an oversight rather than a deliberate deferral. Add a one-line comment: `// TODO Phase 03: replace with auth-aware start destination`.

---

## Medium Priority

### M1 — `BottomNavTab` enum case naming inconsistency

`SAA2025`, `Awards`, `Kudos`, `Profile` — three are PascalCase, `SAA2025` is SCREAMING_CAPS with numeric suffix. Kotlin enum convention is PascalCase for all entries. Suggest renaming to `Saa2025` for consistency. Low practical impact but affects readability scans.

### M2 — `android:Theme.Material.Light.NoActionBar` theme (carryover from Phase 01)

`themes.xml` extends `android:Theme.Material.Light.NoActionBar` (Material 1), not a Material3 theme. With `enableEdgeToEdge()` and `minSdk = 26`, on API 26–27 devices the status bar icon tint may not respond correctly to the window flags set by `enableEdgeToEdge()`. Not a Phase 02 defect, but Phase 02 code calls `enableEdgeToEdge()` in `MainActivity`, so it's surfaced here. Should be fixed in the theme layer before release. The Compose theme (`KudosAppTheme`) is unaffected; only the XML-level activity theme is the issue.

---

## Low Priority

### L1 — `PlaceholderScreen` is `private` in `AppNavGraph.kt`

`private` is correct for a file-scoped helper. No issue. Noted as a positive practice.

### L2 — `dp` import in `KudosBottomNav.kt` with `0.dp` literal

`androidx.compose.ui.unit.dp` is imported specifically for the `tonalElevation = 0.dp` call. Consider whether `tonalElevation` is needed at all here — setting it to `0.dp` is the default. Minor KISS violation; removing the explicit `0.dp` removes the import as well. No functional impact.

---

## Positive Observations

- **100% route coverage.** All 13 routes from the blueprint nav tree are present in both `NavRoutes.kt` and `AppNavGraph.kt` with correct path strings.
- **`navigateToTab` extension is correct.** `popUpTo(graph.startDestinationId)` + `saveState = true` + `launchSingleTop = true` + `restoreState = true` is the canonical Compose Navigation pattern for tab switching with saved state. No bugs here.
- **Argument extraction uses `.orEmpty()`** instead of `!!` — safe null handling on `BackStackEntry.arguments`.
- **`showBottomBar` gating is correct.** Kotlin 2.0 smart-casts `selectedTab` to non-null inside the guarded block even through the aliased boolean — confirmed by successful `compileDebugKotlin`.
- **All files under 200-line limit.** Largest is KudosBottomNav.kt at 82 lines.
- **Dependency declared correctly.** `androidx.navigation:navigation-compose:2.8.0` is in `libs.versions.toml` and `app/build.gradle.kts`.
- **`KudosApp` accepts injected `NavHostController`** — testable without activity context.

---

## Edge Cases Found

1. **Back-press from HOME with no back stack:** User presses system back at `home` → activity finishes (default behavior). Correct for a home tab. No action needed in Phase 02; Phase 03 auth guard will decide if login should intercept this.
2. **`kudos/view/{id}` with empty id:** If `kudosView("")` is called, route resolves to `kudos/view/` — trailing slash. Nav-compose will not match `kudos/view/{id}` for an empty segment; navigation will silently fail (no crash, just no-op). Should be validated at call sites in later phases.
3. **Concurrent recomposition of `currentRoute`:** `currentBackStackEntryAsState()` is a `State<NavBackStackEntry?>` — reads are snapshot-safe in Compose. No race condition.

---

## Recommended Actions

1. (H1) Add ordering comment above `PROFILE_ME` composable in `AppNavGraph.kt` — 1 line, no logic change.
2. (H2) Add `// TODO Phase 03` comment at `startDestination` parameter — 1 line.
3. (M1) Rename `SAA2025` enum entry to `Saa2025` for naming consistency.
4. (M2) File a follow-up task to update `themes.xml` to extend a Material3 theme before first device test on API 26–27 hardware.
5. (L2) Remove `tonalElevation = 0.dp` (and its import) from `KudosBottomNav` if the default value is acceptable.

## Metrics

- Build: PASS (`compileDebugKotlin` UP-TO-DATE, no errors)
- Route completeness: 13/13 (100%)
- File size compliance: 5/5 files under 200 lines
- Linting issues: 0 (syntax/compile); 2 style (enum naming, unused default param)

---

**Score: 9/10**

**Status:** DONE

**Summary:** All acceptance criteria met. Routes complete, bottom nav correctly wired, single-top/saveState/restoreState navigation pattern is correct, build passes. Minor fragility in profile route ordering and a carryover theme issue are the only items worth actioning before Phase 03.
