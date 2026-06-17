# Code Review — Phase 08 Notifications
**Date:** 2026-06-16 | **Reviewer:** reviewer agent | **Branch:** feat/phase-07-profile

## Scope
- Files reviewed: 14 (8 feature, 3 test, 1 nav, 1 theme, 1 build.gradle)
- LOC total: ~1 000
- Build: passing (assembleDebug) | Tests: 361/361

---

## Overall Assessment
Architecture is clean and consistent with prior phases. The shared-repo badge-sync
pattern works correctly at runtime. Two layout/UX correctness issues need fixing;
the rest are minor.

---

## Critical Issues
_None._

---

## Important Issues

### I-1 — Scrollable Column missing `weight(1f)` → list will NOT scroll
**File:** `NotificationsScreen.kt:91-108`

```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(...)
        .verticalScroll(rememberScrollState())   // ← bug
) { ... }
```

The parent is `Column(Modifier.fillMaxSize())`. In Compose, a child that has no
`weight()` modifier is measured with **unbounded** main-axis constraints, so the inner
Column expands to content height and `verticalScroll` never gets a viewport to scroll
within. The rounded container also clips overflow silently. With exactly 7 items today
the clipping is invisible, but adding a notification or switching to a device with a
shorter viewport will cut the list.

**Fix:**
```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .weight(1f)           // ← bounds the viewport
        .padding(horizontal = 20.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(...)
        .verticalScroll(rememberScrollState())
) { ... }
```
Precedent: `KudosSearchScreen.kt:125` and `:206` both use `.weight(1f)` on their
scrollable children inside the same outer `Column(fillMaxSize)` pattern.

---

### I-2 — Tapping inline "Tiêu chuẩn cộng đồng" link does NOT mark the notification read
**Files:** `NotificationsNavigation.kt:37`, `NotificationItem.kt:103`

`onStandardsLink` fires only `navController.navigateSingleTop(...)` — it never calls
`vm.markRead(notif.id)`. The outer `Row.clickable` (which does call `markRead`) is
swallowed by the inner `Text.clickable` in Compose's event-propagation model, so a user
who taps only the link never clears the unread dot.

Currently `n5 (CONTENT_HIDDEN)` starts as `isRead = true` in mock data, so this is
invisible today. It becomes a real bug the moment any CONTENT_HIDDEN notification is
seeded as unread.

**Fix:** In `NotificationsNavigation.kt`, wrap `onStandardsLink` to also mark read:

```kotlin
onStandardsLink = {
    vm.markRead(notif_id_of_CONTENT_HIDDEN) // need to pass the notif through
    navController.navigateSingleTop(NavRoutes.KUDOS_COMMUNITY_STANDARDS)
}
```

The cleanest approach: pass `onStandardsLink: (AppNotification) -> Unit` or move the
standards-link action into a dedicated `onStandardsClick(notif)` lambda that mirrors
`onItemClick`.

---

## Minor Issues

### M-1 — `Color.kt` comment for `KudosNotiGreen` is stale
**File:** `Color.kt:44`

```kotlin
val KudosNotiGreen = Color(0xFF4CAF50)   // SECRET_BOX + BADGE_COLLECTED: gift / badge icon
```

`SECRET_BOX` actually uses `KudosGold` (as intended per the mapper). The comment
incorrectly lists SECRET_BOX. Update to: `// BADGE_COLLECTED: shield/badge icon`.

---

### M-2 — `Thread.sleep(50)` in `NotificationsRepositoryTest` is flaky
**File:** `NotificationsRepositoryTest.kt:56, 299`

`unreadCount` is a derived StateFlow via `.map { }.stateIn(scope, SharingStarted.Eagerly, …)`
running on `Dispatchers.Default`. Reading `.value` immediately after `markAllRead()` from
the test thread may observe a stale value if the Default dispatcher hasn't scheduled the
emission yet. `Thread.sleep(50)` is a timing patch that passes on fast CI machines but
may fail under CPU contention.

The robust fix is `kotlinx-coroutines-test` + `runTest { advanceUntilIdle() }` paired
with a test dispatcher injected into `NotificationsRepository`. For the current in-memory
singleton (no constructor injection) a pragmatic short-term option is to directly assert
the invariant via `.notifications.value.count { !it.isRead }` (synchronously readable)
instead of `.unreadCount.value`, which is what most of the other tests already do.

Low priority — all 361 tests pass — but flag for if CI starts flaking.

---

### M-3 — `REVIEW_REQUEST` tap silently no-ops with no user feedback
**File:** `NotificationsNavigation.kt:55`

```kotlin
NotificationType.REVIEW_REQUEST -> Unit  // Admin screen is out of scope
```

Per clarification this is intentional. However the user has no indication the tap
registered at all (no toast, no visual state change). At minimum, `markRead` is still
called (line 33) before dispatch, so the unread dot clears — that's the correct behavior.
Consider a brief `Snackbar("Admin review is not available in this app")` as a future
polish item, but this does not block the phase.

---

### M-4 — Preview in `NotificationsScreen.kt` duplicates mock data
**File:** `NotificationsScreen.kt:117-175`

The private `previewNotifications` list manually copies 7 entries that already exist in
`NotificationsMockData.notifications`. If mock text changes, the preview silently diverges.

**Fix:** Replace with `NotificationsMockData.notifications` directly in the `@Preview`
call. Preview-only usage does not affect production binary size.

---

### M-5 — `NotificationsViewModel.language` is never updated — EN title is dead code
**File:** `NotificationsViewModel.kt:29`

`private val language = MutableStateFlow(AppLanguage.VN)` has no setter. The `titleFor(EN)`
branch in the `combine` lambda is unreachable. This is explicitly documented as deferred to
Phase 11 in `clarifications.md`, so it is acceptable — just note it here for Phase 11 pickup.

---

### M-6 — `MarkAllReadButton` padding order: visual side-effect with `height` + `padding`
**File:** `MarkAllReadButton.kt:38-40`

```kotlin
Modifier
    .height(40.dp)
    .clickable(...)
    .padding(vertical = 8.dp)
```

`padding` placed _after_ `clickable` shrinks the touch target by 16dp vertically relative
to the 40dp height. The clickable area is 40dp (correct), but the content is squeezed to
24dp (enough for a 24dp icon but leaves 0 breathing room). Not a layout break with the
current content, but counter-intuitive ordering. Convention is `padding` before content
composables; moving `padding` before `clickable` would expand the touch target inclusively.

---

## Architecture / Correctness Notes

### Coroutine correctness: `combine` + `stateIn` in `KudosFeedViewModel`
`combine(flow1..5) { … }.combine(NotificationsRepository.unreadCount) { state, unread → … }.stateIn(...)`
is **correct**. The chained `.combine` on a cold Flow creates a single cold pipeline;
`stateIn` at the end subscribes it once. No double-subscription or dropped emission risk.

### HomeViewModel badge sync: no leak
`observeUnreadNotifications()` launches in `viewModelScope` which is cancelled on VM
`onCleared()`. No coroutine leak.

### `NotificationsRepository` singleton scope
`CoroutineScope(SupervisorJob() + Dispatchers.Default)` on an `object` is acceptable for
an app-process singleton with no lifecycle (equivalent to `GlobalScope` with a named
supervisor job). The `SharingStarted.Eagerly` keeps `unreadCount` always hot, which is
correct for a badge that must be ready before any screen subscribes.

### Navigation targets
All 7 mock kudo IDs referenced in navigation (`k7`, `k6`) verified to exist in
`KudosMockData`. REVIEW_REQUEST no-nav is documented and intentional.

### `navigateSingleTop` visibility
Defined `internal` in `KudosFeedNavigation.kt` (package `com.sun.kudos_demo.navigation`).
`NotificationsNavigation.kt` is in the same package — the function is accessible. ✓

### Icon mapper swap-readiness
`NotificationIconMapper.kt` is self-contained: one `data class NotificationIconSpec` + one
`when` expression. Swapping Material icon to a real SVG asset means changing only the
`icon = Icons.Outlined.X` line per type. Figma component IDs are documented inline. ✓

### File sizes
All files are ≤ 200 lines (largest: `NotificationsScreen.kt` at 195 lines). ✓

---

## Test Quality
- `NotificationsViewModelTest`: pure function tests on `titleFor()` — solid, no coroutine
  complexity needed.
- `NotificationsMockDataTest`: validates seed invariants against the immutable object — correct
  strategy for an order-independent singleton.
- `NotificationsRepositoryTest`: well-designed order-independent assertions; the two
  `Thread.sleep(50)` calls (lines 56, 299) are the only fragility (see M-2).

---

## Positive Observations
- Two-track architecture (presentational UI / logic repository) followed cleanly.
- `NotificationsRepository.markAllRead()` uses a single atomic `.update {}` — no TOCTOU.
- `markRead` is idempotent (`if (!it.isRead)` guard inside the update lambda).
- Bottom nav correctly absent from `NotificationsScreen` (detail flow — per clarification).
- `SharingStarted.WhileSubscribed(5_000)` on `NotificationsViewModel.uiState` consistent
  with all other ViewModels in the project.
- DRY: no new nav helper introduced; existing `navigateSingleTop` reused.

---

## Recommended Actions (priority order)
1. **[I-1]** Add `.weight(1f)` to the scrollable notifications Column in `NotificationsScreen.kt`.
2. **[I-2]** Wire `vm.markRead` inside `onStandardsLink` for CONTENT_HIDDEN notifications.
3. **[M-1]** Fix the `Color.kt` comment for `KudosNotiGreen`.
4. **[M-4]** Replace `previewNotifications` with `NotificationsMockData.notifications`.
5. **[M-2]** Replace `Thread.sleep` with coroutine-test approach (low urgency).

---

## Metrics
- Type coverage: 100% (no `any` usage, full data classes)
- Test coverage: 3 test files, ~50 test cases covering all public functions
- Linting issues: 0 compilation errors (build passing)
- Files > 200 lines: 0

---

## Verdict
**APPROVE_WITH_NITS** — Score: **8.0 / 10**

I-1 (scroll layout) và I-2 (mark-read on standards link) là 2 bugs hành vi thật sự
nhưng không phá vỡ demo hiện tại do mock data. Tất cả critical paths (badge sync,
markRead/markAllRead, navigation, coroutine lifecycle) đúng và an toàn.
Fixes cho I-1/I-2 có thể làm trong cùng PR hoặc task riêng nhỏ.
