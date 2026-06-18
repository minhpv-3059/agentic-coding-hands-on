# Phase 07 Profile — Adversarial Code Review

**Date:** 2026-06-15  
**Reviewer:** reviewer agent  
**Build/Tests:** pass (305 unit tests)

---

## Scope

| | |
|---|---|
| New files | 16 (ProfileModels, ProfileMockData, MyProfileViewModel, UserProfileViewModel, MyProfileScreen, UserProfileScreen, 10 components, ProfileNavigation) |
| Changed files | 5 (NavRoutes, AppNavGraph, SendKudosNavigation, KudosFeedNavigation, KudosApp) |
| Test files | 4 (ProfileMockDataTest, ProfileModelsTest, MyProfileViewModelTest, UserProfileViewModelTest) |
| Total LOC (new) | ~1 700 |

---

## Overall Assessment

Solid mock-data phase. Architecture is consistent with the existing feed/send patterns. The routing fix is correctly implemented (PROFILE_ME → "my-profile"). Most issues below are low-to-minor. Two worth fixing before ship: (1) the `isProfileScreen` check in `KudosApp` uses the template string `"profile/{userId}"` as a literal rather than matching the live route, so the global bottom bar will appear on the other-user profile screen, duplicating the in-screen bar. (2) `navigateSingleTop(NavRoutes.KUDOS_SEND)` in the Feed still navigates to the base path, which works (Compose Nav matches optional-arg routes on base paths) but is easy to misread; worth a comment.

---

## Critical Issues

_None._

---

## Major Issues

### M1 — `KudosApp.isProfileScreen` never matches `PROFILE_USER` at runtime

**File:** `app/src/main/java/com/sun/kudos_demo/ui/KudosApp.kt:31`

```kotlin
val isProfileScreen = currentRoute == NavRoutes.PROFILE_ME
    || currentRoute == NavRoutes.PROFILE_USER   // "profile/{userId}" — literal!
```

`NavRoutes.PROFILE_USER = "profile/{userId}"`. The Compose Navigation back-stack entry's `destination.route` for a parameterised destination **is** the template string `"profile/{userId}"`, so this comparison is actually correct at the infrastructure level — Compose Nav does not substitute the argument value into the route for `destination.route`. Quick test: `backStackEntry?.destination?.route` for `profile/s2` returns `"profile/{userId}"`, not `"profile/s2"`. So the comparison works.

**Correction:** On closer inspection this is fine. The composable is registered with `NavRoutes.PROFILE_USER` as its route, and `destination.route` returns that template string unchanged. No bug here. Downgraded from Major to a documentation note.

_No action required; comment in the file is already adequate._

---

## Minor Issues

### Mi1 — `beyond_boundary` id mismatch: placeholder gets wrong gradient colour

**Files:**  
- `ProfileMockData.kt:45` — id is `"beyond_the_boundary"`  
- `UserProfileScreen.kt:219` (preview) — id is `"beyond_boundary"`  
- `ProfileAwardBadges.kt:127, 148` — gradient switch uses `"beyond_boundary"`

`ProfileMockData.awardBadges` (the live data source used by `UserProfileViewModel`) has `id = "beyond_the_boundary"`. The gradient `when` branch in `AwardBadgePlaceholder` and the `Preview`'s inline list both use `"beyond_boundary"`. Result: in production (VM-driven path), the "BEYOND THE BOUNDARY" badge always falls through to the grey `else` branch instead of getting its purple gradient. The preview uses the wrong id too, masking the bug.

**Fix:** Align all usages to a single id. The mock-data definition is the source of truth.

```kotlin
// ProfileMockData.kt (already correct) — keep as "beyond_the_boundary"
// ProfileAwardBadges.kt:127
"beyond_the_boundary" -> Brush.radialGradient(listOf(Color(0xFFCE93D8), Color(0xFF4A148C)))
// ProfileAwardBadges.kt:148 (Preview)
AwardBadge("beyond_the_boundary", "BEYOND THE BOUNDARY"),
// UserProfileScreen.kt:219 (Preview)
AwardBadge("beyond_the_boundary", "BEYOND THE BOUNDARY"),
```

Also confirmed by test `ProfileMockDataTest.awardBadges_IdAndLabelCorrect_BeyondTheBoundary` — it asserts the id is `"beyond_the_boundary"`, which means the test **passes** but the placeholder rendering in production silently falls through to grey. The test gap: no test exercises `AwardBadgePlaceholder` gradient selection.

---

### Mi2 — `onBack` param accepted but never called in `UserProfileScreen`

**File:** `UserProfileScreen.kt:66`

`onBack: () -> Unit` is in the screen's signature but is never passed to any composable inside (no `BackHandler`, no back arrow in the top bar). It is wired at the call site (`ProfileNavigation.kt:75`: `onBack = { navController.popBackStack() }`) so there is no crash, but the parameter is dead weight at the screen level. Either:
- Wire it (add `BackHandler(onBack = onBack)` or a back arrow icon in `KudosTopBar`), or  
- Remove it from the signature if intentional (no explicit back affordance per design).

---

### Mi3 — Feed "Send Kudos" button navigates to bare `KUDOS_SEND` path

**File:** `KudosFeedNavigation.kt:93`

```kotlin
onSendKudos = { navController.navigateSingleTop(NavRoutes.KUDOS_SEND) }
```

The registered composable route is `KUDOS_SEND_WITH_ARG` (`"kudos/send?recipient={recipient}"`). Compose Navigation **does** match a bare `"kudos/send"` navigate call to a composable registered as `"kudos/send?recipient={recipient}"` when the arg has a `defaultValue` — so this works. However it is non-obvious and will confuse the next reader. Add a brief comment:

```kotlin
// Bare base path is valid — the optional `recipient` arg defaults to ""
onSendKudos = { navController.navigateSingleTop(NavRoutes.KUDOS_SEND) }
```

Same applies to `AppNavGraph.kt:55`.

---

### Mi4 — `ProfileKudosFilter` uses raw `TextUnit` constructor instead of `.sp` extension

**File:** `ProfileKudosFilter.kt:89–93`

```kotlin
letterSpacing = androidx.compose.ui.unit.TextUnit(
    0.25f,
    androidx.compose.ui.unit.TextUnitType.Sp
)
```

Every other component in this feature (and the rest of the codebase) writes `0.25.sp`. The verbose constructor is functionally identical but inconsistent. Use `0.25.sp`. Also the FQ-name imports should be at the top of the file.

---

### Mi5 — `ProfileMockData.allProfileKudos` builds map eagerly at class-load time, re-calling `userById` and `receivedKudosFor` for every user in the index

**File:** `ProfileMockData.kt:85–90`

```kotlin
private val allProfileKudos: Map<String, Kudo> = buildMap {
    (myReceivedKudos + mySentKudos).forEach { put(it.id, it) }
    userIndex.keys.forEach { id ->
        receivedKudosFor(userById(id), id).forEach { put(it.id, it) }
    }
}
```

For a mock-data object this is acceptable. But `receivedKudosFor` calls `KudosMockData.kudos.take(5)` per invocation, which is O(1) at mock scale. No real performance concern here — flagged only for awareness if the user pool grows.

---

### Mi6 — `UserProfileRoute` reads `SavedStateHandle` via `UserProfileViewModel` but `viewModel()` call doesn't pass a factory

**File:** `ProfileNavigation.kt:54`, `UserProfileViewModel.kt:45`

```kotlin
val vm: UserProfileViewModel = viewModel()
```

`UserProfileViewModel` constructor takes `(app: Application, savedStateHandle: SavedStateHandle)`. Compose `viewModel()` / `AndroidViewModel` + `SavedStateHandle` combo is supported by the default `CreationExtras` mechanism in `Activity`/`NavBackStackEntry` since Navigation 2.5+ — so this works automatically when the composable is inside a `NavHost` destination. No bug, but it is worth a comment for future maintainers, since it looks like `SavedStateHandle` is magically injected.

---

## Nits

**N1 — Typo in data and tests: `"ROOT FUTHER"` should be `"ROOT FURTHER"`**  
`ProfileMockData.kt:46`, `ProfileMockDataTest.kt:64, 110`, `ProfileAwardBadges.kt:149`, `UserProfileScreen.kt:221`. Design content has the typo too (MoMorph source) — if so, keep it but document it; if fixable, fix it.

**N2 — `ProfileAwardBadges` row has no `weight` on badge items → 7+ items will overflow**  
Fixed slotted items with `Modifier.width(64.dp)` in a `Row`. Six items at 64dp + 5×4dp gap = 404dp, which fits 375dp… barely. With 6×64 = 384 + 5×4 = 404dp this actually **clips** on a 375dp screen. The row has `fillMaxWidth` but no `weight` or `wrapContentWidth`. Consider using `Arrangement.SpaceEvenly` or limiting total width. Low risk since count is hard-coded to 6, but should be verified on device.  
(6 × 64 + 5 × 4 = 404 > 375 — the Row will overflow or clip without `clip` set.)

**N3 — Hardcoded Vietnamese strings in components**  
`"Bộ sưu tập icon của tôi"` in `UserProfileScreen.kt:151` and `ProfileIconCollection.kt:78`; `"Sun* Annual Awards 2025"` in two section headers; `"Mở Secret Box 🎁"` in `ProfileStatsCard.kt:83`. Consistent with existing screens but noted as an open systemic gap (see memory: phase03-auth-findings).

**N4 — `ProfileStatsCard` button label contains an emoji (`🎁`)**  
Breaks the project rule "Only use emojis if the user explicitly requests it." Verify against design spec; if design has the emoji, add a comment citing MoMorph node.

**N5 — `UserProfileContent` is `private` but lives in the same file as the exported `UserProfileScreen`**  
Fine as-is (file is 232 lines, just over 200). No action needed unless further growth requires a split.

**N6 — `awardBadges_HasExactlyFourBadges` test name says "Four" but asserts 6**  
`ProfileMockDataTest.kt:51`. Test passes because `assertEquals(6, ...)` is correct, but the name is wrong and will confuse future readers.

---

## Edge Cases Found (Scouting)

- **Empty `recipientId` in `LaunchedEffect`:** `SendKudosRoute` guards with `isNotBlank()` — correct.  
- **Unknown `userId` from deep link:** `ProfileMockData.userById` always returns a non-null fallback — no crash path.  
- **`userId == ""` (empty string from `orEmpty()`):** Falls through to `userIndex[""]` → null → `fallbackUser("")` — safe, screen renders with fallback identity.  
- **Profile self-loop:** Clicking a sender on `MyProfileScreen` navigates to `profileUser(user.id)`. If `user.id == CURRENT_USER_ID` this opens `UserProfileRoute` for the current user rather than `MyProfileRoute`. Functionally harmless in the mock-data phase but may be surprising.

---

## Positive Observations

- Routing fix is correct and complete: no leftover `"profile/me"` literals anywhere in the codebase. The constant renaming is clean.
- `KudosApp` global bottom bar suppression is the right approach and is correctly triggered by the template-string route comparison.
- `UserProfileViewModel` reads `userId` from `SavedStateHandle` — correct, single source of truth.
- `toggleLike` guard in `MyProfileViewModel` is correct: checks `sender?.id == CURRENT_USER_ID` to block liking own-sent kudos.
- `LaunchedEffect(recipientId)` key is correct — reruns only if the id changes, not on every recomposition.
- Test coverage is broad for pure state/data classes; all 305 tests pass.
- Components are presentational/stateless where appropriate; only `ProfileKudosFilter` holds local `expanded` state, which is the right level.
- File sizes are acceptable: `UserProfileScreen.kt` (232) extracts `UserProfileContent` inline; `MyProfileScreen.kt` (257) is a root screen with preview — both defensible.
- DRY: both profile screens reuse `KudosTopBar`, `KudosBottomNav`, `KudosCard`, `KudoAvatar` without duplication.

---

## Recommended Actions (Priority Order)

1. **[Mi1]** Fix `"beyond_the_boundary"` id mismatch in `ProfileAwardBadges.kt` (preview + gradient switch) and `UserProfileScreen.kt` preview. — 3-line change, no logic impact.
2. **[Mi2]** Decide and act on the dead `onBack` param in `UserProfileScreen` — either wire `BackHandler` or remove the param.
3. **[N2]** Verify `ProfileAwardBadges` row renders correctly at 375dp (6 × 64dp items may clip). Use `Arrangement.SpaceEvenly` or constrain individual item width.
4. **[Mi3, Mi4]** Add comment on bare `KUDOS_SEND` navigate in `KudosFeedNavigation`; fix `TextUnit` constructor to `.sp`.
5. **[N6]** Fix test name `awardBadges_HasExactlyFourBadges` → `_HasExactlySixBadges`.
6. **[N4]** Confirm emoji in "Mở Secret Box 🎁" is from design spec.

---

## Metrics

| | |
|---|---|
| Type coverage | High — all public API types explicit; no untyped `Any` |
| Test coverage | Good — all ViewModel state, MockData, models covered; no coverage for `ProfileAwardBadges` gradient logic (N2/Mi1 lurks here) |
| Linting | 1 confirmed inconsistency (Mi4 raw TextUnit constructor) |
| Hardcoded strings | Same systemic gap as prior phases; no regression |

---

## Score: 7.5 / 10

**Ship verdict: SHIP with Mi1 fix before merge.** The `beyond_the_boundary` id mismatch silently renders the wrong gradient colour in production (grey instead of purple for one badge). All other issues are minor/nit and can be addressed in the next pass. No crashes, no auth holes, no data leaks, no N+1 queries, no race conditions.
