# Code Review — Phase 05: Kudos Feed
**Date:** 2026-06-08 | **Reviewer:** reviewer agent | **Build:** PASS | **Tests:** 37/37

## Scope
- Files: 26 Kotlin files (Track A + B, navigation, data)
- LOC: ~2900 total (feed/ + ui/components/)
- Focus: correctness vs specs/clarifications, code standards, architecture, security/robustness

## Overall Assessment
Phase 05 is well-structured. The two-track architecture (stateless screens + ViewModel/DataStore) is clean and consistent. Core business logic (filter AND, carousel reset, like guard, star badge, anonymous, Copy Link toast) is correctly implemented and unit-tested. Three distinct issues need attention: one correctness bug (hashtag click loses the tag on secondary screens), one convention violation (hardcoded hex colors pervasive in KudosCard.kt and KudoDetailCard.kt), and one spec gap (search input has no maxLength cap). Everything else is at MINOR or NIT severity.

---

## Critical Issues

*None.*

---

## High Priority

### H1 — Hashtag tap from ViewKudo/AllKudos navigates but loses the tag [correctness — TC_FUN_016/031]
**Files:** `navigation/KudosFeedNavigation.kt:108,133` | **Severity:** HIGH

```kotlin
// ViewKudoRoute and KudosAllRoute:
onHashtagClick = { navController.navigateSingleTop(NavRoutes.KUDOS_FEED) }
// tag string from the card is ignored — user lands on unfiltered Feed
```

Spec requires "tap hashtag sets filter to that tag." On the Feed itself this is wired correctly (`vm::applyHashtag`), but the two secondary screens navigate to KUDOS_FEED without applying the filter. The `applyHashtag()` needs to be called on the Feed's ViewModel. The problem is that the Feed VM and these routes have separate VM instances (each NavBackStackEntry owns its own VM), so the tag can't be passed by calling a method directly.

**Fix options (pick one):**
1. Navigate to `kudos/feed?hashtag=$tag` and have `KudosFeedRoute` read it from `savedStateHandle`.
2. Use a shared `NavBackStack.previousEntry` saved state: before navigating, call `navController.getBackStackEntry(NavRoutes.KUDOS_FEED).savedStateHandle["hashtag"] = tag`, then in `KudosFeedRoute` consume it via `LaunchedEffect`.
3. Extract a `KudosFilterViewModel` scoped to the nav graph (`viewModel(navController.getBackStackEntry(NavRoutes.KUDOS_FEED))`).

Option 2 is the least invasive for this mock-only phase.

### H2 — KudosSearchScreen BasicTextField has no maxLength cap [spec TC_FUN_034]
**File:** `feature/feed/KudosSearchScreen.kt:157–183` | **Severity:** HIGH

`BasicTextField(onValueChange = onQueryChange)` with no length guard. The Spotlight search correctly caps at 100 (`it.take(100)`, line 123 of SpotlightNetworkChart.kt), but the Sunner search does not. Spec/clarification notes max 100.

**Fix:**
```kotlin
// In SearchBar or in KudosSearchViewModel.updateQuery:
fun updateQuery(value: String) {
    _query.value = value.take(100)
}
```

---

## Medium Priority

### M1 — Hardcoded hex colors in KudosCard.kt (code-standards violation)
**File:** `ui/components/KudosCard.kt:195,209,245,317,328` | **Severity:** MEDIUM

Five instances of raw `Color(0xFF888888)`, `Color(0xFF555555)`, `Color(0xFF777777)`, `Color.Red`. The standard says "Never hardcode color hex values inside composables." `KudosGray` (`0xFF999999`) is the right substitute for the 0x888888/0x777777 variants. `KudosError` (`0xFFB3261E`) should replace `Color.Red` for the liked-heart tint (note: KudoDetailCard already uses `0xFFD4271D` for the same purpose — one should win, but neither should be inline).

**Recommended:** add `val KudosHeartActive = Color(0xFFD4271D)` to `Color.kt` (sourced from Figma node 6885:10175), use it in both `KudosCard` and `KudoDetailCard`. Replace the gray variants with `KudosGray`.

### M2 — KudoDetailCard hashtag row fires only `firstOrNull()` — remaining hashtags non-interactive
**File:** `feature/feed/components/KudoDetailCard.kt:121–129` | **Severity:** MEDIUM

```kotlin
text = kudo.hashtags.joinToString("  ") { "#$it" }
modifier = Modifier.clickable {
    kudo.hashtags.firstOrNull()?.let { onHashtagClick(it) }
}
```

The entire joined string is one clickable that always fires the first hashtag. KudosCard correctly renders individual tappable chips (`KudosHashtagRow`). KudoDetailCard should adopt the same approach.

### M3 — `canLike` guard never reaches KudosCard on Feed/AllKudos/AllKudosSection
**File:** `feature/feed/components/HighlightCarousel.kt:82`, `components/AllKudosSection.kt:82`, `feature/feed/AllKudosScreen.kt:90` | **Severity:** MEDIUM

`KudosCard` accepts `canLike: Boolean = true` (default = always enabled). `FeedUiState.canLike(kudo)` is correctly defined in the ViewModel, and `toggleLike` server-side is guarded against self-liking. However, the card UI itself is never told `canLike = false` at these call sites — the heart button remains visually active (full opacity, clickable) even for kudos the current user sent. The ViewModel guard prevents the DataStore write, but the user gets no visual feedback that liking is blocked.

**Fix:** pass `canLike = state.canLike(kudo)` (or pass `currentUserId` and compute inline) at each `KudosCard` call site.

### M4 — `FeedHeroBanner` uses ad-hoc typography (not `MaterialTheme.typography.displayLarge`)
**File:** `feature/feed/components/FeedHeroBanner.kt:49–52` | **Severity:** MEDIUM

```kotlin
fontSize = 52.sp,
fontWeight = FontWeight.ExtraBold,
letterSpacing = 6.sp
```

`KudosTypography.displayLarge` is defined as `52 sp / ExtraBold` (matches exactly). The code-standards doc says "Do not create ad-hoc TextStyle values inside composables." Use `MaterialTheme.typography.displayLarge` with a `letterSpacing` override at most.

---

## Minor Priority

### N1 — Three files exceed 200-line guideline
| File | Lines |
|---|---|
| `KudoDetailCard.kt` | 326 |
| `KudosSearchScreen.kt` | 294 |
| `KudosCard.kt` | 384 |

`KudosCard` is the worst offender. It has 5 private sub-composables that could each live in a separate file (`KudosCardContent.kt`, `KudosCardActions.kt`, `KudosCardParticipantsRow.kt`). `KudosSearchScreen` can split `RecentSection`/`ResultsSection`/`SearchBar` into a sibling file. `KudoDetailCard` — the agent noted difficulty splitting; as a reviewer the private functions `SenderRecipientRow`, `UserInfoSlot`, `ActionRow` are clean extraction candidates (new file `KudoDetailCardParts.kt`, ~120 lines each side). Not blocking, but a clear violation of the stated guideline.

### N2 — Typo in mock hashtag "Inspring" (should be "Inspiring")
**File:** `feature/feed/KudosMockData.kt:14`

`"Inspring"` appears in both `hashtags` list and kudo data. This is visible to users in the filter dropdown and on cards. Simple fix; confirm if this is the design-source spelling before changing.

### N3 — Two mock users share code `"CECV10"` (nhat u1 and nhan u2)
**File:** `feature/feed/KudosMockData.kt:30–31`

```kotlin
private val nhat = user("u1", "Huỳnh Dương Xuân Nhật", "CECV10", "Rising Hero")
private val nhan = user("u2", "Dương Xuân Huỳnh Nhân", "CECV10", "Legend Hero")
```

Probably intentional (design shows same department code for multiple people), but could confuse search results since search matches on `code`. Not a bug given the live-search substring decision, just a data oddity to document.

### N4 — `KudosFeedScreen` preview uses `androidx.compose.ui.graphics.Color(0xFF1E2D39)` inline
**File:** `feature/feed/KudosFeedScreen.kt:175`

Inside a `@Preview` block, so it won't ship to production. Cosmetically cleaner to use `KudosBgUpdate` (= `0xFF1E2D39`).

### N5 — `FeedHeroBanner` gradient midpoint uses `Color(0xFF0A1E2A)` not in token set; `Color.White.copy(alpha = 0.75f)` instead of `KudosWhite.copy(alpha = 0.75f)`
**File:** `feature/feed/components/FeedHeroBanner.kt:35,58`

Both are private to the composable body rather than named file-level constants. Could add `private val HeroGradientMid = Color(0xFF0A1E2A)` at file scope with a source comment, or add it to `Color.kt` if reused elsewhere.

---

## Addressed Known Concerns

### KudoDetailCard.kt — 326 lines: **Worth splitting**
Three private composables (`SenderRecipientRow` ~45 lines, `UserInfoSlot` ~60 lines, `ActionRow` ~75 lines) can move to `KudoDetailCardParts.kt`. Not a hard requirement to ship, but the guideline is clear and splitting is straightforward here.

### `searchUsers` substring match "CECV1" matches "CECV10": **Acceptable**
Confirmed as correct behavior per clarifications (live-search partial match). The `contains` semantics are intentional and documented. No action needed.

### ViewModels without Robolectric unit tests: **Acceptable for Phase 05**
The business logic that can be tested on the JVM lives in `KudosFeedLogic.kt` (pure functions, all tested). The ViewModel/DataStore layer is not tested — per clarifications this is in-scope deferral. The 37 tests cover the critical correctness rules. Robolectric setup can be deferred.

### `KudoImageGallery` uses `Icons.Filled.Add` as placeholder: **Acceptable**
This is a placeholder for a phase where no image-loading library is present and real thumbnails are unavailable. The icon has correct `contentDescription`. No functional issue.

---

## Edge Cases Verified

- **Empty list**: both `HighlightCarousel` and `AllKudosSection` show `EmptyKudosHint` ("Hiện tại chưa có Kudos nào."). `GiftRecipientsSection` shows "Chưa có dữ liệu." for empty recipients. All correct.
- **Anonymous kudo**: `sender == null`, `isAnonymous == true` → alias shown, person icon avatar, no sender clickable, `canLike` check falls through to `kudo.sender?.id != currentUserId` returning `true` (null != "u1") — anonymous kudos are likeable. Intentional per design (sender identity unknown, so no self-like conflict).
- **Star badge boundaries**: `starLevel()` correctly uses >= not > for 10/20/50. Verified in `KudoModels.kt:43–48`.
- **DataStore concurrent read/write**: Two separate VM instances (Feed + AllKudos) both observe `prefs.likedKudoIds`. DataStore is concurrency-safe; both receive the same updates via Flow. No race condition.
- **Recent search seed order**: `asReversed().forEach { addRecentSearch(it.id) }` correctly seeds [s4→s1] such that after prepend order the displayed list is [s1, s4] (original order). Correct.
- **Whitespace-only query in search**: `"  ".isEmpty()` is false → `ResultsSection` shown with empty list (no hint displayed). Minor UX gap — user sees blank results instead of Recent. `searchUsers` correctly returns `emptyList()` for blank queries.

---

## Positive Observations

- **Clean architecture**: pure logic functions in `KudosFeedLogic.kt` fully separate from Android; unit-testable without Robolectric. The `applyLikes` double-application (once on carousel, once on allKudos) is correctly avoided by passing the already-liked set.
- **ViewModel guard at two levels**: `toggleLike()` checks `sender?.id == CURRENT_USER_ID` server-side; `FeedUiState.canLike()` exposes the guard for UI-layer consumption. Defense in depth.
- **DataStore ordering**: recent searches use a newline-joined string to preserve insertion order (a Preferences `stringSetOf` would lose order). The `toIdList()` extension handles blank-line filtering.
- **Navigation**: `PROFILE_ME` registered before parameterized `PROFILE_USER` to avoid "me" being captured as a userId. Correct.
- **Carousel reset**: `LaunchedEffect(kudos)` on list identity change → `animateScrollToPage(0)`. Spec-correct without needing a key derivation trick.
- **Spotlight**: golden-angle spiral layout is deterministic across recompositions (no `Math.random`). Pan/zoom bounds (`0.6f..4f`) are reasonable. No crash path on empty nodes/edges (forEach on empty list is safe).
- **KudoAvatar.kt** — deterministic color palette avoids random-on-recompose; handles null name gracefully.

---

## Recommended Actions (Prioritized)

1. **[H1] Fix hashtag tap from ViewKudo/AllKudos** — pass tag via `savedStateHandle` before navigating to KUDOS_FEED, consume in `KudosFeedRoute` via `LaunchedEffect`.
2. **[H2] Add `take(100)` in `KudosSearchViewModel.updateQuery`** — one line.
3. **[M1] Purge inline hex colors from `KudosCard.kt`** — add `KudosHeartActive` token, replace gray variants with `KudosGray`.
4. **[M2] Fix `KudoDetailCard` hashtag row** — replace single clickable with per-chip `KudosHashtagRow` pattern from `KudosCard`.
5. **[M3] Pass `canLike` to `KudosCard` at all three call sites** — one param per site.
6. **[M4] Replace ad-hoc typography in `FeedHeroBanner`** — use `displayLarge` + `letterSpacing` override.
7. **[N1] Split `KudosCard.kt` and `KudosSearchScreen.kt`** — reduces largest files by ~40%.

---

## Metrics
- Type Coverage: No `any` usage; all types explicit. 100%.
- Test Coverage: 37 unit tests on pure logic functions; ViewModel layer deferred.
- Hardcoded colors: 8 instances across 2 files (KudosCard.kt ×5, KudoDetailCard.kt ×2 non-token, FeedHeroBanner.kt ×2).
- Files >200 lines: 3 (KudoDetailCard 326, KudosSearchScreen 294, KudosCard 384).

---

## Unresolved Questions

1. Is `"Inspring"` the intended spelling (copied from Figma) or a typo? Determines whether N2 should be fixed.
2. On hashtag tap from ViewKudo/AllKudos — is navigating to an unfiltered Feed acceptable as a V1 behavior (defer H1 fix), or must the filter apply immediately?
