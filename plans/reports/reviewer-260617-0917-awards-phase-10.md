# Code Review — Phase 10 Awards Feature
**Date:** 2026-06-17  
**Reviewer:** reviewer agent (Staff Engineer)  
**Branch:** feat/phase-09-secret-box (Phase 10 commits)  
**Build:** assembleDebug PASS | Tests: 534 PASS

---

## Scope
- **Files reviewed:** 11 source files + 2 test files
- **LOC:** ~700 production, ~390 test
- **Focus:** correctness, arch consistency, Compose best practices, crash paths

---

## Overall Assessment
Solid implementation. Architecture is consistent with sibling features (Home, Notifications, SecretBox). State hoisting is clean. No business logic in composables. Route integration is correct and follows established patterns. All critical paths are safe. Minor issues are cosmetic.

---

## Critical Issues
None.

---

## Important

### 1. `AwardViewModelTest` tests the data model, not the ViewModel — `SavedStateHandle` pre-select path is untested
**File:** `app/src/test/.../awards/AwardViewModelTest.kt`  
**Severity:** Important  

Every test in `AwardViewModelTest` calls `state.copy()` directly on `AwardsUiState`. No `AwardViewModel` instance is ever constructed. The ViewModel's core behavior — reading `NavRoutes.ARG_AWARD` from `SavedStateHandle` and calling `AwardData.byId()` to pre-select an award — is **not exercised at all**. The test file's own comment acknowledges this ("these unit tests focus on the pure state logic").

This matters because the pre-select path (Home award card → Awards tab pre-selected) is the only differentiated behavior of the ViewModel vs. a plain state holder.

**Fix:** Add tests that construct `AwardViewModel(SavedStateHandle(mapOf(NavRoutes.ARG_AWARD to "top_project")))` and assert `uiState.value.selected.id == "top_project"`. Requires `androidx.lifecycle:lifecycle-viewmodel-testing` or Robolectric, which are already on the test classpath (SecretBoxViewModelTest uses `SecretBoxViewModel()` directly with a coroutine test rule — same approach viable here with a `@Before` `Dispatchers.setMain(UnconfinedTestDispatcher())`).

---

## Minor

### 2. Missing `Arrangement` import in `AwardTrophyCard.kt` — verbose FQN usage
**File:** `app/src/main/.../awards/components/AwardTrophyCard.kt`, lines 71, 76, 143, 148, 167  
**Severity:** Minor  

`AwardTrophyCard` uses the fully-qualified `androidx.compose.foundation.layout.Arrangement.spacedBy(...)` in 5 places while the three other files in the same feature (`AwardKvSection`, `AwardHeaderSection`, `AwardsKudosSection`) all import `Arrangement` and use the short form. Inconsistent import discipline within the same feature package.

**Fix:** Add `import androidx.compose.foundation.layout.Arrangement` and replace the 5 FQN usages.

### 3. Dropdown trigger `Text` has no `overflow` or `maxLines` guard
**File:** `app/src/main/.../awards/components/AwardHeaderSection.kt`, line 80–85  
**Severity:** Minor  

The trigger `Row` is `height(40.dp)` fixed. The `Text` for `selected.dropdownLabel` uses `Modifier.weight(1f)` but no `maxLines = 1` / `overflow = TextOverflow.Ellipsis`. The longest label is "MVP (Most Valuable Person)" (26 chars). At 14sp on the constrained width (248dp ≈ 74% of 335dp), this currently fits. However, if a future award label is longer, the text will visually overflow the 40dp row height and clip without ellipsis, which looks broken rather than truncated gracefully.

**Fix:** Add `maxLines = 1, overflow = TextOverflow.Ellipsis` to the trigger Text.

### 4. `SectionHeader` imported from `feature/home/components` — cross-feature dependency
**Files:** `awards/components/AwardHeaderSection.kt:30`, `awards/components/AwardsKudosSection.kt:21`  
**Severity:** Minor (pre-existing pattern, not introduced by Phase 10)  

`SectionHeader` lives in `feature/home/components/SectionHeader.kt`. Awards importing from Home creates an intra-app coupling between sibling features. This pre-dates Phase 10 but is now amplified (a second feature depends on it). If Home is ever refactored, Awards breaks silently.

**Note for future:** `SectionHeader` should migrate to `ui/components` (shared). Not a blocker now.

---

## Observations

### O1. `AwardsKudosSection` duplicates `HomeKudosSection` — intentional, documented
Identical text/layout. The KDoc on `AwardsKudosSection` explicitly calls this out as feature-isolation choice (Awards doesn't import from Home's content layer). Clarifications.md confirms Track A/B split. No action needed.

### O2. `onKudosDetail` → `NavRoutes.RULES` (placeholder) — intentional per clarifications
`AwardsNavigation.kt:28` routes the "Chi tiết ↗" button to the RULES placeholder. Clarifications.md (line 133) explicitly records: "wire to placeholder RULES — Phase 11 not built yet." Correct.

### O3. Navigation: bare `"awards"` → `AWARDS_WITH_ARG` optional-arg composable — correct
`BottomNavTab.Awards.route = "awards"` and `Home.onAboutAward` both navigate to the bare `"awards"` string, while only `AWARDS_WITH_ARG` ("awards?award={award}") is registered. This is identical to the `KUDOS_SEND` / `KUDOS_SEND_WITH_ARG` pattern already proven working in the codebase. Jetpack Navigation resolves the bare path to the optional-query-arg route using the `defaultValue = ""`. Correct.

### O4. Tab highlight logic — correct
`KudosApp.kt` strips `?` suffix via `substringBefore("?")` before tab matching. `destination.route` for the Awards entry is the template string `"awards?award={award}"`, so `baseRoute = "awards"` correctly matches `BottomNavTab.Awards.route = "awards"`. ✓

### O5. Route arg pre-select pipeline — correct end-to-end
`navArgument(ARG_AWARD) { defaultValue = "" }` → SavedStateHandle populated → `takeIf { it.isNotBlank() }` guards empty default → `AwardData.byId(null)` falls back to MVP. `AwardData.byId` documented and tested for null/empty/unknown → MVP fallback. No crash path.

### O6. `rememberScrollState()` inline in modifier — consistent with sibling screens
`HomeScreen.kt:72` and `NotificationsScreen.kt:100` use the same inline pattern. Not a bug — `rememberScrollState()` uses `remember{}` internally and is called in a valid `@Composable` context. Consistent.

### O7. No hardcoded hex colors in production code — all through theme tokens
Comments reference hex values for documentation (e.g., `#2E3940`, `#998C5F`) but code uses `KudosDivider`, `KudosBorder`, `KudosSecondaryButtonNormal`, etc. All tokens are defined in `Color.kt`. ✓

### O8. `AwardData.awards.first()` in `byId()` fallback — theoretical risk if list is empty
`awards.first()` throws `NoSuchElementException` if `awards` is empty. Since this is a static `val`, it can never be empty at runtime, but there's no defensive guard. `firstOrNull() ?: throw IllegalStateException(...)` would be more explicit. Not a real risk.

### O9. E2E / instrumented tests still absent — open DoD gap (pre-existing, not Phase 10 specific)
Per `aidd-project-requirements.md`, E2E tests are a DoD requirement. This gap predates Phase 10. Phase 10 adds no E2E coverage, consistent with every prior phase.

---

## Positive Observations
- **Clean state hoisting:** `AwardsScreen` is fully stateless; all state lives in `AwardViewModel`.
- **id-based dropdown comparison** (`award.id == selected.id`) is more robust than object equality.
- **`AwardData.byId()` fallback** covers null/empty/unknown cleanly via `firstOrNull ?: first()`.
- **`selectAward()` closes dropdown atomically** in a single `_uiState.update {}` — no race window.
- **`observeUnreadNotifications()`** correctly uses `viewModelScope.launch` + `collect`, mirroring the same pattern in `HomeViewModel` and `KudosFeedViewModel`.
- **Two Previews for `AwardsScreen`** (default MVP + Signature 2-row) covering both layout variants.
- **Test coverage of `AwardData`** is thorough: order, uniqueness, quantities, value counts, fallback paths.

---

## Recommended Actions
1. **[Important]** Add `AwardViewModel` integration tests that construct the VM with `SavedStateHandle` to cover the pre-select path (see §1 above).
2. **[Minor]** Add `maxLines = 1, overflow = TextOverflow.Ellipsis` to the dropdown trigger `Text` in `AwardHeaderSection.kt:85`.
3. **[Minor]** Add `import androidx.compose.foundation.layout.Arrangement` to `AwardTrophyCard.kt` and remove the 5 FQN usages.
4. **[Backlog]** Move `SectionHeader` to `ui/components` to eliminate the cross-feature dependency.

---

## Metrics
- **Critical issues:** 0
- **Important issues:** 1 (ViewModel test coverage gap)
- **Minor issues:** 3
- **Hardcoded colors in production code:** 0
- **Crash paths identified:** 0

---

## Score: 8.5 / 10

**Rationale:** Zero critical or security issues. Architecture is clean and consistent with established patterns. The pre-select route arg pipeline works correctly. The only meaningful gap is that `AwardViewModel`'s core behavior (SavedStateHandle → pre-select) goes untested; everything else is minor style/defensive coding.

---

**Status:** DONE  
**Summary:** Phase 10 Awards is production-ready for a display-only feature. One important gap (ViewModel integration test for pre-select path) and three minor items. No crash risks or breaking changes.
