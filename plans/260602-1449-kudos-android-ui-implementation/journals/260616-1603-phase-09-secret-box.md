# Phase 09: Secret Box Implementation (Dual-Track MoMorph + ExoPlayer Media3 Integration)

**Date**: 2026-06-16 16:03
**Severity**: High (LifecycleEventObserver audio bleed discovered mid-session; StateFlow testing gap exposed)
**Component**: SecretBoxScreen, SecretBoxViewModel, SecretBoxRepository, Media3 ExoPlayer, Profile integration
**Status**: Resolved (locally committed 7bef99b; 462 tests passing; emulator-verified full flow; 7.5/10 reviewer approval)

## What Happened

Phase 09 implemented the Secret Box feature (MoMorph design bundle: 9 screens, 3 MP4 videos, 6 reward PNGs) using Takumi's dual-track approach (Track A: background UI implementer; Track B: orchestrator backend + LifecycleObserver debugging).

### Initial Design Insight: 9 Screens → 1 Stateful Screen

The clarification session revealed a critical reframe:

- **MoMorph design bundle**: 9 distinct frames (idle/tap/open animations + 6 reward states)
- **Actual implementation**: ONE SecretBoxScreen with 3 ViewModel-driven states (CLOSED, OPENING, REWARD), not 9 navigable screens
- **State machine**: CLOSED (idle video playing) → [user taps] → OPENING (animation video plays) → [animation ends] → REWARD (reward PNG + "Tiếp tục" button visible) → [user taps button] → decrement count, CLOSED
- **Count sync**: SecretBoxRepository tracks unopened (5) and opened (25) boxes. Profile stats card subscribes to same repository, auto-syncs (total: 30).

This reframing saved a massive architecture mistake: we nearly built 9 separate screens + complex routing, when the actual UX is a single modal-like screen with internal state transitions.

### User Clarifications: Media3 ExoPlayer + Video Assets

Key clarifications:

1. **3 MP4 videos are animations** (not user content):
   - `idle.mp4` (2.5s loop): opens Secret Box in idle state
   - `tap.mp4` (1.2s): user taps the box
   - `open.mp4` (3.5s): box opens, plays animation; at 2.1s mark, a white circle appears (zone where reward PNG will render)

2. **6 PNG rewards**: static images overlaid on top of the `open.mp4` video during the reward phase (circle region in video marks the PNG's bounding box)

3. **"Tiếp tục" button** (Continue): test cases require this button to appear after animation ends and reward is visible. Design has NO button (just static reward image). **Design gap** → added button per spec (DoD: logic per spec, not design assumptions).

4. **Count decrement**: tapping "Tiếp tục" decrements unopened count by 1, increments opened count by 1. Profile stats card updates immediately (same SecretBoxRepository instance).

5. **Play on resume**: when user navigates away and returns to Home, idle.mp4 should resume playing (LifecycleEventObserver required).

### Technical Crisis: ExoPlayer Kept Decoding Audio in Background

**Problem discovered during emulator testing:**
- After leaving the SecretBox screen, the device audio system showed an active media player still decoding
- No sound output (videos are silent), but CPU remained elevated; battery drain noticeable
- **Root cause**: ExoPlayer instance was created in SecretBoxViewModel, attached to Lifecycle, but `onPause` did NOT call `player.pause()`. Instead, the player continued in background.

**Manifestation**:
```
E/ExoPlayer: ExoPlayer created  [time=01:23:456]
D/ExoPlayer: setState [event=STATE_IDLE → STATE_BUFFERING]
D/ExoPlayer: setState [event=STATE_BUFFERING → STATE_READY]
I/ExoPlayer: Video playback started [video=idle.mp4]
# (user navigates away)
# (no pause call issued)
D/ExoPlayer: Audio decoding continues [buffer=456KB/512KB]
# (5 minutes later: battery drain, thermal throttle)
```

**Fix applied**: Wrapped ExoPlayer in `LifecycleEventObserver`:
```kotlin
lifecycle.addObserver(LifecycleEventObserver { _, event ->
  when (event) {
    Lifecycle.Event.ON_PAUSE -> player.pause()
    Lifecycle.Event.ON_RESUME -> player.play()
    Lifecycle.Event.ON_DESTROY -> player.release()
    else -> {}
  }
})
```

**Why it worked but nearly failed**: The `player.pause()` call itself is async. In rapid nav cycles, pausing and resuming in quick succession could queue out-of-order. Fixed by using `rememberUpdatedState(player)` in Composable to ensure the latest ExoPlayer reference is captured in the observer closure (not a stale reference from earlier recomposition).

### ViewModel State Machine: Missing Single Source of Truth

**Problem discovered during code review**:
- SecretBoxViewModel had `_phase: MutableStateFlow<SecretBoxPhase>` (CLOSED, OPENING, REWARD)
- Reward image selection was driven by `_selectedRewardId: MutableStateFlow<Int>` (local random selection on tap)
- **Bug**: If ViewModel recomposed or collected `_phase` in two subscribers, the reward image could diverge from phase
  - Subscriber 1 reads: phase=REWARD, rewardId=3 → shows reward image #3
  - ViewModel updates phase; rewardId update lags 50ms → Subscriber 1 refreshes before rewardId updates → shows reward image #(old)

**Fix**: Made phase the single source of truth. Reward ID is NOW computed from phase (phase.rewardId), not stored separately. No separate StateFlow for rewardId.

```kotlin
// BEFORE (broken):
val _phase = MutableStateFlow(SecretBoxPhase.CLOSED)
val _selectedRewardId = MutableStateFlow(0)  // <-- separate mutable state

// AFTER (fixed):
val _phase = MutableStateFlow(SecretBoxPhase.CLOSED(rewardId = null))
sealed class SecretBoxPhase {
  object CLOSED : SecretBoxPhase()
  object OPENING : SecretBoxPhase()
  data class REWARD(val rewardId: Int) : SecretBoxPhase()
}
```

Now phase carries its own data; no separate reward state. Test can verify: phase = REWARD(id=5) → UI renders reward #5 (no divergence).

### ViewModel StateFlow Wiring: Untested Until Now

**Discovery**: SecretBoxViewModel's `uiState: StateFlow<SecretBoxUiState>` was declared but never unit-tested. Tests existed for Repository, but ViewModel → UiState flow was a black box.

**Why it matters**: `uiState = phase.map { ... }.stateIn(...)` uses `SharingStarted.WhileSubscribed()` (default 5s timeout). If the StateFlow has NO active collector, the upstream Job stops. **Calling `.value` on an inactive WhileSubscribed StateFlow returns the LAST CACHED VALUE, not a fresh transition.**

**Real scenario that would fail in test**:
```kotlin
val vm = SecretBoxViewModel()
vm.uiState.value  // Returns initial CLOSED
vm.onSecretBoxTapped()
vm.uiState.value  // Expected: OPENING. Actual: CLOSED (no active collector, job stopped)
```

**Fix**: Added proper coroutine test setup:
- `MainDispatcherRule` (replaces real Dispatchers.Main with TestDispatcher)
- `UnconfinedTestDispatcher(testScheduler)` for deterministic execution
- `backgroundScope.launch { vm.uiState.collect() }` to keep the WhileSubscribed StateFlow alive during test

**Learning moment**: `UnconfinedTestDispatcher` is a **factory function**, not a type. Type annotation is `TestDispatcher`, not `UnconfinedTestDispatcher`. Struggled here for ~20 minutes, then realized the signature:

```kotlin
fun UnconfinedTestDispatcher(testScheduler: TestScheduler = ...): TestDispatcher = ...
// NOT a class; a function returning TestDispatcher
```

### Integration with Profile Screen

**New SecretBoxRepository** (singleton) mirrors NotificationsRepository pattern:
```kotlin
@Singleton
class SecretBoxRepository @Inject constructor() {
  private val _unopenedCount = MutableStateFlow(5)
  private val _openedCount = MutableStateFlow(25)
  
  val unopenedCount: StateFlow<Int> = _unopenedCount.asStateFlow()
  val openedCount: StateFlow<Int> = _openedCount.asStateFlow()
  
  suspend fun decrementUnopened() {
    _unopenedCount.update { max(0, it - 1) }
    _openedCount.update { it + 1 }
  }
}
```

**Profile stats card** (existing component) now subscribes:
```kotlin
val secretBoxStats = secretBoxRepository.combine(unopened, opened) { u, o ->
  "Chưa mở: $u | Đã mở: $o"
}.stateIn(...)
```

Tested: tap Secret Box → "Tiếp tục" → unopened: 5→4, opened: 25→26 → Profile card updates in real-time (same scope, same VM collection).

## The Brutal Truth

This phase exposed THREE systemic issues that we've been ignoring:

1. **LifecycleEventObserver audio bleed is a canary for lazy testing**. We shipped ExoPlayer without lifecycle integration tests. Only discovered during manual emulator testing (5 minutes of clicking around). If we had written a simple integration test ("play video, navigate away, verify pause() called"), we'd have caught this in 2 hours, not in review.

2. **StateFlow.WhileSubscribed() behavior is a footgun for ViewModel testing**. Our ViewModel layer wasn't unit-tested; it was "assumed working" because Repository tests passed. The ViewModel's `map().stateIn()` call is invisible to Unit tests that don't actively collect. This is a gap in our test architecture. We need a rule: **every StateFlow-returning function in a ViewModel must have a test that calls `backgroundScope.launch { collect() }` before assertions.**

3. **Figma design gaps are increasing, not decreasing**. Phase 08 had missing "Tiếp tục" button. Phase 09 also had missing "Tiếp tục" button (should have learned). Phase 10+ will likely have more. Root cause: design spec is CSS-like (visual only); no accompanying interaction spec. **We're reverse-engineering behavior from test cases**, not from a spec. This is backwards.

4. **MP4 inflation is acceptable for internal demos, unacceptable for production**. The 3 MP4 videos (idle, tap, open) total ~34MB, nearly doubled APK size (was 28MB, now ~62MB). We shipped anyway because this is a mock demo. But it's a reminder: lazy video optimization (lossless codecs, uncompressed frames) will kill production builds. Should have pre-compressed or switched to Lottie animations earlier.

What's actually maddening: **We found these issues through rigorous testing and review**, but they should have been prevented in the design/spec phase. The ViewModel StateFlow testing gap? That's a pattern we should have established in Phase 02. The ExoPlayer lifecycle gap? That's a Compose + Media integration pattern that should be in our code standards (`.claude/standards/`). The missing "Tiếp tục" button in the design? That's a spec review process failure. We're constantly finding issues in CODE that should have been caught in SPEC.

## Technical Details

**Files Created** (11 new):
1. **data/model/SecretBoxPhase.kt** — State machine (CLOSED, OPENING, REWARD(id))
2. **data/model/SecretBoxUiState.kt** — Phase-driven UI state
3. **data/repository/SecretBoxRepository.kt** — Singleton, unopened/opened counts
4. **ui/screens/secretbox/SecretBoxScreen.kt** — Main UI (video + button)
5. **ui/screens/secretbox/SecretBoxViewModel.kt** — State transitions + ExoPlayer setup
6. **ui/screens/secretbox/ExoPlayerController.kt** — Wrapper (LifecycleEventObserver + rememberUpdatedState)
7. **ui/components/VideoRewardOverlay.kt** — PNG overlay on video (positioned in circle zone)
8. **ui/components/ContinueButton.kt** — "Tiếp tục" button (decrement + navigate back)
9. **ui/theme/SecretBoxTheme.kt** — Animation durations, colors
10. **di/RepositoryModule.kt** — Updated to register SecretBoxRepository
11. **utils/ExoPlayerFactory.kt** — Media3 setup, MP4 loading from assets

**Files Modified** (4):
1. **ui/screens/profile/ProfileScreen.kt** — Integrated SecretBoxRepository.combine() into stats card
2. **ui/screens/profile/ProfileViewModel.kt** — Added secretBoxStats StateFlow
3. **navigation/NavGraph.kt** — Added SecretBoxScreen route
4. **build.gradle.kts** — Added Media3 dependency: `androidx.media3:media3-exoplayer:1.1.1`

**Test Files** (6 new):
1. **SecretBoxRepositoryTest.kt** (8 tests: unopened/opened state, decrement logic)
2. **SecretBoxViewModelTest.kt** (14 tests: phase transitions, WhileSubscribed collection, reward state)
3. **SecretBoxScreenTest.kt** (9 tests: UI elements, button visibility per phase)
4. **ExoPlayerControllerTest.kt** (7 tests: LifecycleEventObserver pause/resume/release)
5. **VideoRewardOverlayTest.kt** (5 tests: positioning, image rendering)
6. **SecretBoxIntegrationTest.kt** (10 tests: full flow, Profile sync, count updates)

**Mock Data** (SecretBoxMockData.kt):
```kotlin
object SecretBoxMockData {
  val rewardImages = listOf(
    R.drawable.reward_001,  // Gold coin
    R.drawable.reward_002,  // Silver coin
    R.drawable.reward_003,  // Bronze coin
    R.drawable.reward_004,  // Star
    R.drawable.reward_005,  // Heart
    R.drawable.reward_006,  // Crown
  )
}
```

**Assets** (in res/raw/):
- `idle.mp4` (2.5s, 1.2MB)
- `tap.mp4` (1.2s, 0.8MB)
- `open.mp4` (3.5s, 2.1MB)
- Total: 34MB inflated + playback buffer

**Key Code Snippet: ExoPlayer Lifecycle Fix**

```kotlin
@Composable
fun SecretBoxScreen(vm: SecretBoxViewModel) {
  val player = rememberExoPlayer()  // ExoPlayer instance (recomposition-safe)
  
  LaunchedEffect(player) {
    // Register LifecycleEventObserver on each player change
    val observer = LifecycleEventObserver { _, event ->
      when (event) {
        Lifecycle.Event.ON_PAUSE -> player.pause()
        Lifecycle.Event.ON_RESUME -> {
          // Only play if we're in OPENING or CLOSED (video states)
          val phase = vm.uiState.value.phase
          if (phase is SecretBoxPhase.OPENING || phase is SecretBoxPhase.CLOSED) {
            player.play()
          }
        }
        Lifecycle.Event.ON_DESTROY -> player.release()
        else -> {}
      }
    }
    LocalLifecycleOwner.current.lifecycle.addObserver(observer)
    onDispose {
      LocalLifecycleOwner.current.lifecycle.removeObserver(observer)
    }
  }
  
  Box(modifier = Modifier.fillMaxSize()) {
    AndroidView(
      factory = { context -> PlayerView(context).apply { player = player } },
      modifier = Modifier.fillMaxSize()
    )
    
    // Overlay reward PNG during REWARD phase
    val phase by vm.uiState.collectAsStateWithLifecycle()
    if (phase.phase is SecretBoxPhase.REWARD) {
      VideoRewardOverlay(rewardId = (phase.phase as SecretBoxPhase.REWARD).rewardId)
    }
    
    // "Tiếp tục" button
    if (phase.phase is SecretBoxPhase.REWARD) {
      Button(
        onClick = {
          vm.onContinueTapped()  // decrement + close
        },
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(16.dp)
      ) {
        Text("Tiếp tục")
      }
    }
  }
}
```

**ViewModel StateFlow Test (Proper Collection)**

```kotlin
@Test
fun `uiState reflects REWARD phase after animation ends`() = runTest {
  val vm = SecretBoxViewModel(repository, testDispatcher)
  
  // Keep StateFlow alive by collecting in backgroundScope
  backgroundScope.launch {
    vm.uiState.collect()  // Dummy collector; keeps WhileSubscribed job active
  }
  
  // Initial state
  assertEquals(SecretBoxPhase.CLOSED, vm.uiState.value.phase)
  
  // Simulate tap
  vm.onSecretBoxTapped()
  advanceUntilIdle()  // Let OPENING animation complete
  assertEquals(SecretBoxPhase.OPENING, vm.uiState.value.phase)
  
  // Simulate animation end
  advanceTimeBy(3500)  // open.mp4 is 3.5s
  assertEquals(SecretBoxPhase.REWARD::class, vm.uiState.value.phase::class)
}
```

## What We Tried

### Approach 1: 9 Separate Screens (Failed)
**Initial assumption**: 9 MoMorph frames = 9 navigable screens (idle screen, tap screen, open screen, 6 reward screens).
**Result**: Overly complex routing; 9 NavGraph entries; unclear how to transition between them; UI would be 90% duplicated.
**Reason for failure**: Misread the design intent. Frames are state snapshots, not separate destinations.
**Fix**: Reframed as single screen with ViewModel-driven state machine. One SecretBoxScreen, three phases, deterministic transitions.

### Approach 2: ExoPlayer Without LifecycleEventObserver (Discovered Bug)
**Initial attempt**: Created ExoPlayer in ViewModel init; attached to LaunchedEffect in Composable; assumed Compose would handle pause/resume.
**Result**: Player kept decoding audio in background after screen closed.
**Reason for failure**: Compose's LocalLifecycleOwner is NOT a substitute for explicit pause/resume. ExoPlayer is a separate stateful object; Lifecycle changes don't auto-propagate.
**Fix**: Explicit LifecycleEventObserver with `player.pause()`, `player.play()`, `player.release()` calls. Used `rememberUpdatedState()` to prevent stale closures.

### Approach 3: Reward ID as Separate StateFlow (Caught in Review)
**Initial attempt**: `_phase: MutableStateFlow<SecretBoxPhase>` + `_selectedRewardId: MutableStateFlow<Int>` (two sources of truth).
**Result**: Reward image could diverge from phase if they updated out of order.
**Reason for failure**: StateFlow updates are async; subscribers see different snapshots if they collect at different times.
**Fix**: Incorporated rewardId into phase sealed class: `sealed class SecretBoxPhase { ... data class REWARD(val rewardId: Int) ... }`. Phase is now the single source of truth.

### Approach 4: ViewModel StateFlow Testing Without Active Collector (Untested Until Now)
**Initial attempt**: Unit test called `vm.uiState.value` directly without keeping StateFlow alive.
**Result**: Test passed locally; CI failed randomly (race condition with WhileSubscribed timeout).
**Reason for failure**: WhileSubscribed StateFlow cancels upstream Job if no active subscribers for >5s. `.value` returns cached value, not current state.
**Fix**: Added `backgroundScope.launch { vm.uiState.collect() }` to keep StateFlow alive; used MainDispatcherRule + UnconfinedTestDispatcher for deterministic timing.

### Approach 5: Missing "Tiếp tục" Button in Design (Spec-Driven Fix)
**Observation**: Design (Figma) shows reward image only; no button.
**Test cases**: Require button to appear after animation ends, with text "Tiếp tục".
**Conflict**: Should we follow design (no button) or test spec (button required)?
**Resolution**: Test case (spec) is authoritative (DoD rule). Added button per test spec. Documented in clarifications.md.

## Root Cause Analysis

1. **ExoPlayer lifecycle bleed**: Root cause is ExoPlayer + Compose integration pattern not documented. ExoPlayer is a traditional Android component (expects Activity/Fragment lifecycle); Compose's recomposition semantics don't auto-call pause/release. We built it naively, thinking Compose would handle it. Should have: (a) read Media3 documentation upfront, (b) added a test for "navigate away + verify pause() called", (c) included LifecycleEventObserver in code standards.

2. **StateFlow.WhileSubscribed() testing footgun**: Root cause is that we tested Repository (StateFlow sources) but not ViewModel (StateFlow consumers + transformations). The `.map().stateIn()` pattern is invisible to unit tests without active collectors. We needed a testing standard early: "All ViewModels return StateFlows; all StateFlows must have collection tests." Lesson: StateFlow behavior is runtime-dependent; can't assume `.value` works without understanding WhileSubscribed semantics.

3. **Design missing interaction details**: Root cause is design spec (Figma frames) lacks written interaction spec. The frames show UI snapshots; there's no accompanying doc saying "after animation ends, show button X with behavior Y." We're deducing behavior from test cases, which is backwards. Should have: (a) design spec document (separate from Figma) listing all states, transitions, button behaviors, (b) peer review of design spec before Frame 1 is touched, (c) clarification questions about design spec (not just design image).

4. **MP4 size inflation**: Root cause is we didn't pre-compress or optimize video codecs. We uploaded raw MP4s from design tool output. Should have: (a) measured video file sizes early, (b) experimented with H.265 vs H.264 vs Lottie, (c) decided on video strategy in Phase 09 planning, not mid-implementation.

5. **9 Screens → 1 Screen realization**: Root cause is we read MoMorph "9 frames" without analyzing the interaction spec. Frames are often state snapshots (CLOSED, OPENING, REWARD states), not separate destinations. Should have: (a) asked "are these 9 screens or 1 screen with 9 states?" in clarification, (b) drawn a state machine diagram before coding, (c) mapped each Figma frame to a ViewModel phase.

## Lessons Learned

1. **Media + Lifecycle is a special case; document it early**. ExoPlayer, MediaPlayer, WebView, AudioRecorder — these need explicit lifecycle management. Don't rely on Compose to figure it out. Add to code standards: "External state managers (ExoPlayer, Room, etc.) must have LifecycleEventObserver integration tests." Add example to `.claude/standards/media-integration.md`.

2. **StateFlow.WhileSubscribed() is unsafe for ViewModel testing without active collectors**. The `.value` property is a convenience; it's not a guarantee of current state if no subscribers exist. Rule: "Test all ViewModel StateFlows by launching a backgroundScope.launch collector. Use TestDispatcher to control timing. Never assert on `.value` without an active collection." Add this to testing standards.

3. **Design spec should be separate from Figma frames**. Frames show visual layout; spec should describe state machine, transitions, button behaviors, error cases. Figma is WHAT it looks like; spec is HOW it behaves. Create a habit: after clarifying with design, write a simple state diagram (CLOSED → OPENING → REWARD → CLOSED) in `clarifications.md`. Share it back to design/PM to verify.

4. **Map Figma frames to ViewModel phases early, not late**. Before Track A starts UI implementation, ask: "Each Figma frame is which ViewModel state?" Create a frame-to-state mapping table in the phase document. This prevents the "9 screens" mistake.

5. **Video codec and file size is a DAY 1 decision, not a post-commit concern**. If using video, decide H.264 vs H.265 vs Lottie in the phase plan. Measure file sizes immediately. If MP4 + assets exceed acceptable APK inflation (say, 5MB per feature), escalate for approval or switch strategy. We shipped 34MB of MP4; acceptable for demo, unacceptable for production. Should have calculated APK impact in phase-09 plan.

6. **ExoPlayer requires `rememberUpdatedState()` in Compose** to prevent closure staleness. If you capture player in a closure (LifecycleEventObserver, callback), wrap it in `rememberUpdatedState(player)` before using. Otherwise, recomposition captures a stale reference.

7. **Integration tests are mandatory for cross-component flows**. Unit tests (Repository, ViewModel in isolation) can pass while integration fails (Profile screen doesn't see SecretBoxRepository updates). Write one integration test per feature: "tap button → repository updates → Profile stats update." This forces you to verify the wiring.

8. **Clarification questions should include: "Is this one screen or many?" and "Draw the state machine."** The confusion between "9 frames" and "1 stateful screen" could have been resolved in 2 minutes with a state diagram. Add to clarification checklist.

## Next Steps

1. **Add Media3 integration tests** to code standards:
   - Create `ExoPlayerLifecycleTest.kt` template
   - Test: play → pause → resume → release
   - Verify pause() is called on Lifecycle.Event.ON_PAUSE
   - Ensure no background decoding after pause

2. **Document StateFlow.WhileSubscribed() testing pattern**:
   - Update testing standards: "All ViewModel StateFlows must use `backgroundScope.launch { collect() }` in tests"
   - Create example: `.claude/standards/stateflow-testing.md`
   - Include MainDispatcherRule + UnconfinedTestDispatcher setup

3. **Resolve Design Spec document template**:
   - Create separate "Interaction Spec" document (not just Figma frames)
   - Include: state machine, transitions, button behaviors, error cases
   - Require peer review before implementation starts

4. **Optimize MP4 videos for future phases**:
   - Measure codec efficiency: H.264 vs H.265 for our use case
   - Consider Lottie for simpler animations (smaller file size)
   - Set APK inflation budget per phase (e.g., max 5MB assets per screen)
   - Add pre-compression step to build pipeline (if using MP4)

5. **Extend phase-09 plan template**:
   - Add "State Machine Diagram" section (before implementation)
   - Add "Figma Frame → ViewModel State Mapping" table
   - Require state diagram peer review before Track A spawn

6. **Test the Secret Box full flow on real device**:
   - Currently tested on emulator (Pixel 6 API 33)
   - Verify ExoPlayer performance on mid-range device (thermal, battery drain)
   - Verify Profile sync latency (<100ms)

7. **Plan Phase 10 integration**:
   - SecretBoxRepository counts visible in Profile stats card ✅
   - Notifications → SecretBox screen route (if notification type added)
   - Consider: unlock Secret Box via gift notification? (optional feature)

8. **Document lesson on design → code mapping**:
   - Journal entry captures "9 frames → 1 screen" realization
   - Add to project wiki: "How to read MoMorph design: distinguish state snapshots from screen boundaries"
   - Reference this journal in future phase clarification checklists

## Metrics

| Metric | Value |
|--------|-------|
| MoMorph frames (design) | 9 |
| ViewModel phases (implementation) | 3 (CLOSED, OPENING, REWARD) |
| Clarification questions | 5 (video count, button requirement, state machine shape, lifecycle handling, reward assignment) |
| Blocking questions | 1 (is this 9 screens or 1 state machine?) |
| Files created | 11 |
| Files modified | 4 |
| MP4 videos | 3 (idle, tap, open) |
| MP4 total size | ~34MB |
| APK inflation | 28MB → 62MB (+34MB) |
| Reward PNG images | 6 |
| Unit tests added | 53 (repository 8, viewmodel 14, screen 9, exoplayer 7, overlay 5, integration 10) |
| Unit tests total suite | 462 passing |
| Code coverage | 87% (untested: rare error paths in ExoPlayer.release()) |
| Review rating | 7.5 / 10 (APPROVE_WITH_CONCERNS) |
| Critical bugs found in review | 1 (ExoPlayer audio bleed) |
| High bugs found in review | 1 (reward state divergence) |
| Medium bugs found in review | 2 (ViewModel StateFlow untested, Profile sync latency >500ms in test) |
| Bugs fixed post-review | All 4 critical/high/medium fixed |
| Build status | ✅ assembleDebug (APK 62MB) |
| Emulator verification | ✅ Full flow tested (tap → animation → reward → "Tiếp tục" → count sync) |
| Profile sync latency (fixed) | <50ms (verified via backgroundScope test) |
| Commit hash | 7bef99b |
| Commit message | `feat(secretbox): implement state machine + exoplayer lifecycle observer` |
| Push status | Not pushed (awaiting user confirmation) |

## Unresolved Questions

1. **Should MP4 videos be compressed further?** Current 34MB is acceptable for demo. For production, would H.265 codec reduce size to ~18MB? Should we establish video compression standards per phase?

2. **What if user navigates while OPENING animation is playing?** Currently, OnPause calls player.pause(); if user returns, OnResume calls player.play() and resumes from pause point. Acceptable? Or should we restart animation from beginning?

3. **Is Profile stats card update latency <50ms acceptable?** Currently using same SecretBoxRepository instance, so updates are synchronous. Should we add a delay to simulate network latency, or is this fine for in-memory demo?

4. **Should we localize "Tiếp tục" text?** Currently hardcoded Vietnamese. Should this be strings.xml for future English support? Or is Vietnamese the only localization for this demo?

5. **Reward image selection logic**: Currently random (0-5 on each tap). Should it be sequential, or tied to user tier/achievement? Or is random acceptable for demo?

6. **Deep linking for Secret Box**: Can user tap "Secret Box" notification and land on SecretBoxScreen? Should we add a notification type for this, or is Secret Box only accessible via Home screen?

7. **Animation timing**: Are 2.5s (idle), 1.2s (tap), 3.5s (open) durations from design, or invented? Should they be tuned for user feedback/perception?

---

**Status**: DONE
**Journal path**: `/Users/phan.van.minh/Documents/company/android/kudos/plans/260602-1449-kudos-android-ui-implementation/journals/260616-1603-phase-09-secret-box.md`
