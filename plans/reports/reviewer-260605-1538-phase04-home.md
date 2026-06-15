# Code Review — Phase 04: Home Screen
**Date:** 2026-06-05  
**Reviewer:** reviewer agent  
**Build:** clean (17/17 tests passing)

---

## Scope
- `feature/home/HomeScreen.kt` (144 lines)
- `feature/home/HomeViewModel.kt` (71 lines)
- `feature/home/components/`: AwardCard, CountdownRow, HeroActionButtons, HomeAwardsSection, HomeFab, HomeHeroSection, HomeKudosSection, HomeNoteSection (8 files)
- `ui/components/KudosTopBar.kt`, `KudosBottomNav.kt`
- `navigation/AppNavGraph.kt`, `NavRoutes.kt`
- `CountdownConversionTest.kt` (test)
- Resources: drawables, fonts

---

## Overall Assessment

The implementation is clean, well-structured, and idiomatic Compose. Architecture is correct (stateless screen + ViewModel state). Build is clean and all file sizes are under the 200-line limit. Nav wiring is correct and `startDestination = NavRoutes.LOGIN` is confirmed. No debug leftovers, no secrets. The main concerns are: a systemic edge-to-edge inset gap carried over from Phase 03 (still unresolved), one color-token violation, a stale doc entry, and a minor display bug in the countdown digit renderer.

---

## Critical Issues

None.

---

## High Priority

### H-01 — Edge-to-edge insets gap (systemic, carried over from Phase 03)
**File:** `KudosTopBar.kt`, `HomeHeroSection.kt`, `KudosApp.kt`  
**Problem:** `KudosApp` sets `contentWindowInsets = WindowInsets(0)` on the `Scaffold`, zeroing out system bar insets entirely. `enableEdgeToEdge()` is active in `MainActivity`. The result: the status bar (typically 24–32 dp) renders over `KudosTopBar`'s gradient and over the `HomeHeroSection` hero content. On devices with tall status bars or display cutouts, this causes visual overlap with the SAA logo and language selector.  
`KudosBottomNav` correctly uses `.navigationBarsPadding()` — only the top is broken.  
**Fix:** Add `Modifier.statusBarsPadding()` to `KudosTopBar`, or consume insets at the `Scaffold` level by removing `contentWindowInsets = WindowInsets(0)` and letting the Scaffold inject status-bar height into the top content. Also remove the hardcoded `padding(top = 56.dp)` in `HomeHeroSection` which approximates the TopBar height rather than deriving it from real insets.

---

## Medium Priority

### M-01 — Hardcoded color token violation in `AwardImagePlaceholder`
**File:** `AwardCard.kt:123`  
```kotlin
.background(Color(0x1AFFEA9E)) // low-alpha gold tint, no hardcoded token
```
`Color(0x1AFFEA9E)` is identical to `KudosSecondaryButtonNormal` which already exists in `Color.kt`. The comment is misleading ("no hardcoded token" — but there IS a token). `code-standards.md` mandates "Never hardcode color hex values inside composables."  
**Fix:** Replace with `KudosSecondaryButtonNormal` or add a dedicated `KudosAwardPlaceholderTint` token with a comment.

### M-02 — `CountdownUnit`: silent display corruption for values ≥ 100
**File:** `CountdownRow.kt:66`  
```kotlin
val tens = (value / 10).coerceIn(0, 9)
```
For `days = 100`: tens = 10, clipped to 9 → display "90" (shows 9|0 instead of wrapping to 3 digits). Practically harmless since the countdown starts at 20 days and the design only has 2-digit boxes, but the clipping silently shows a wrong value rather than clamping to "99" or asserting. The coerceIn range should at minimum be documented as the intentional design limit.  
**Fix:** Either document explicitly (`// Design only shows 2 digits; values ≥ 100 are clamped to 99 per spec`) or add `val display = value.coerceAtMost(99)` before the tens/ones split.

### M-03 — `SectionHeader` placed in `HomeAwardsSection.kt` but shared across sections
**File:** `HomeAwardsSection.kt:101`  
`SectionHeader` is a reusable UI component used by both `HomeAwardsSection` and `HomeKudosSection`. Placing it in a feature-specific file breaks separation of concerns; future sections (Phase 06, 10, etc.) will have an awkward import from a sibling feature file.  
**Fix:** Move `SectionHeader` to `ui/components/SectionHeader.kt` before more callers are added.

---

## Minor Issues

### N-01 — Stale `[iOS]` platform tag in KDoc
**File:** `HomeScreen.kt:27`  
```kotlin
* [iOS] Home screen — stateless / presentational.
```
`[iOS]` is a stray cross-platform tag with no meaning in this Android-only project.  
**Fix:** Remove the `[iOS]` prefix.

### N-02 — Stale code-standards.md docs entry
**File:** `docs/code-standards.md` (Top Bar section)  
> "Logo is currently a `Text` placeholder ("SAA 2025"); replace with actual drawable in a later phase"  

`KudosTopBar` now uses `Image(painterResource(R.drawable.ic_logo_saa))` — the drawable has been added (Phase 04 or earlier). The doc is stale.  
**Fix:** Update the code-standards Top Bar section to reflect that the logo is now an `Image` asset.

### N-03 — `KudosTopBar` gradient stops have no design-node comment
**File:** `KudosTopBar.kt:52–57`  
`code-standards.md` requires design-specific gradients to carry a comment referencing their source. The three intermediate stops (`0x4D00101A`, `0x3300101A`, `0x0000101A`) only reference "matching LoginHeader" — not a Figma node ID or percentage values from the spec.  
**Fix:** Add inline comment: `// Figma: opacity stops 30%/20%/0% of KudosBackground — LoginHeader overlay`

### N-04 — `LazyRow` items without stable keys
**File:** `HomeAwardsSection.kt:82`  
```kotlin
items(awards) { award -> ... }
```
No `key` lambda. For a static 3-item mock list this is harmless, but establishes a bad pattern.  
**Fix:** `items(awards, key = { it.id }) { award -> ... }`

### N-05 — `HomeFab` clickable zones lack `role = Role.Button` semantics
**File:** `HomeFab.kt:71`, `HomeFab.kt:97`  
Both `Box(Modifier.clickable(...))` call sites have `contentDescription` on the Icon child but no semantic role on the `clickable`. TalkBack will announce the element as "image" rather than "button".  
**Fix:** `Modifier.clickable(role = Role.Button, onClick = ...)`

### N-06 — `AwardCard` "Chi tiết" link has no accessibility role
**File:** `AwardCard.kt:88`  
Same pattern as N-05 — `Row(Modifier.clickable(...))` with no `role`.  
**Fix:** `Modifier.clickable(role = Role.Button, onClick = onDetailClick)`

### N-07 — `mockAwards` is a public top-level `val`
**File:** `HomeAwardsSection.kt:38`  
`val mockAwards = listOf(...)` is package-visible from anywhere. It leaks a UI concern (mock data) into the public API of the package. Fine for this mock-only phase, but should be `private val` if only the default parameter uses it, or moved to a test fixture / `HomeViewModel` when real data arrives.  
**Fix (low urgency):** `private val mockAwards` (HomeAwardsSection provides a public default param already).

---

## Edge Cases Scouted

- **Countdown terminal state:** `while(true)` loop in `HomeViewModel.startCountdown()` breaks on `remaining == 0L`, but there is a 1-second race window: if `remaining` is 1–999 ms, the loop fires one more delay then computes 0 → breaks cleanly next iteration. Not a real bug (`coerceAtLeast(0L)` covers it), but worth noting.
- **ViewModel `targetTimeMs` computed at instantiation:** If the VM is destroyed and recreated (process death, config change clears back stack) the countdown resets to 20d20h20m. Acceptable for mock phase; note for when real data is wired.
- **`navigateOnce` lambda recreated on every recomposition:** `AppNavGraph.kt:46` — `val navigateOnce` is declared inside the composable body. Technically fine (stable navController), no performance issue with 3 tabs, but a `remember { }` wrapper or extraction would be cleaner.

---

## Positive Observations

- Architecture is correct: `HomeScreen` is fully stateless; all state lives in `HomeViewModel`. The ViewModel exposes `StateFlow` and `collectAsState()` is used at the nav graph level — separation is clean.
- `startDestination = NavRoutes.LOGIN` confirmed correct (not HOME).
- `launchSingleTop` applied correctly to both tab nav and in-screen actions to prevent duplicate back stack entries.
- Countdown `toCountdown()` is `internal` — correctly scoped for unit testing without polluting the public API.
- `CountdownConversionTest` covers zero, boundary, and mid-range cases well (9/10 cases).
- All files are under 200 lines. All Previews wrap in `KudosAppTheme`. Typography exclusively via `MaterialTheme.typography.*`.
- `KudosBottomNav` correctly uses `navigationBarsPadding()`.
- `PROFILE_ME` registered before `PROFILE_USER` in the nav graph — correct ordering to prevent "me" being matched as `userId`. Good defensive pattern.
- `AwardImagePlaceholder` placeholder strategy is well-documented with Phase 10 reference.
- `HomeFab` gold glow shadow uses `KudosGold` token correctly with explanation comment.
- `DigitalFont` `FontFamily` extracted to a private file-level constant in `CountdownRow.kt` — avoids re-instantiation per recomposition.

---

## Recommended Actions (prioritized)

1. **H-01** — Add `statusBarsPadding()` to `KudosTopBar` and remove the hardcoded `padding(top = 56.dp)` in `HomeHeroSection`. This is a systemic fix.
2. **M-01** — Replace `Color(0x1AFFEA9E)` in `AwardCard` with `KudosSecondaryButtonNormal`.
3. **M-02** — Document or clamp the 2-digit display limit in `CountdownUnit`.
4. **M-03** — Move `SectionHeader` to `ui/components/` before more feature screens adopt it.
5. **N-01/N-02** — Fix stale `[iOS]` tag and update code-standards doc.
6. **N-05/N-06** — Add `role = Role.Button` to FAB and AwardCard clickable zones.

---

## Metrics

| Metric | Value |
|--------|-------|
| Files reviewed | 14 |
| Max file size | 144 lines (HomeScreen.kt) — within limit |
| Files > 200 lines | 0 |
| Test coverage | 17/17 passing; countdown logic well-covered |
| Hardcoded colors | 1 violation (AwardCard.kt:123); 2 justified design gradients with partial comments |
| startDestination | NavRoutes.LOGIN ✓ |
| @Preview in every file | Yes ✓ |
| KudosAppTheme in all Previews | Yes ✓ |
| Typography via MaterialTheme | Yes ✓ |

---

## Unresolved Questions

- Should `SectionHeader` be promoted to `ui/components/` now (Phase 04) or deferred until a second screen needs it (Phase 06+)? Recommend Phase 04 cleanup to prevent coupling.
- Is the 2-digit countdown limit a hard design constraint (document it) or should the UI gracefully handle 3-digit days (unlikely given 20-day start)?
