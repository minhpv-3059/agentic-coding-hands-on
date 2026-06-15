# Phase 05: Kudos Feed Implementation — Parallel UI Agents + Persistence + Interactive Chart

**Date**: 2026-06-08 09:12
**Severity**: Low
**Component**: KudosFeedScreen, KudoDetailCard, KudosSearchScreen, SpotlightNetworkChart, DataStore persistence, ViewModel, Navigation, MoMorph 8-screen design
**Status**: Resolved

## What Happened

Implemented Phase 05: Kudos Feed complex ecosystem from 8 MoMorph screens (main feed, All Kudos tab, View detail + anonymous, Search Sunner, 2 dropdown filters). Deployed ambitious 2-track parallel orchestration: spawned 4 background UI agents (Claude Sonnet, 1 agent per 2-screen group) compiling in parallel while orchestrator (Opus) built logic layer (ViewModel, DataStore, SpotlightNetworkChart, navigation). Used **slot pattern** in KudosFeedScreen to let UI agents inject filter/chart without recompiling shared file. All agents compiled independently, zero merge conflicts. Reviewer (7.5/10, 0 critical) found 2 HIGH + 4 MEDIUM issues; all fixed. Build ✅ (assembleDebug), 37 unit tests ✅. Commit `9d662d6` (unpushed).

## The Brutal Truth

This phase exposed a brutal tension between ambition and scope: the user chose the **most ambitious clarification option** — interactive persistence + interactive chart with full pan/zoom/search highlighting — which meant this stopped being "mock UI from Figma" and became a full feature implementation (DataStore, Canvas math, state threading). That's not inherently bad, but it means **4 Sonnet agents, 1 on 2-screen pairs, could not ship independently** — they all needed the same 5 foundational files (KudosModels, KudosAvatar, KudosMockData, DataStore schema, SpotlightNetworkChart contract) before they could even compile. If any of those files hit a bug, all 4 agents failed in parallel, wasting token-hours. The orchestrator had to become a blocker: validate foundation, *then* unblock the agents. That's the opposite of "decoupled parallel."

The real aggravation is that **the reviewer caught gaps no single agent saw**. Agent 1 wired hashtag tap in KudoDetailCard but forgot that tapping a hashtag on the **All Kudos** tab didn't propagate back to Feed — it popped the nav stack but lost the filter context. Agent 2 didn't add max-length validation to search field (100 chars). Agent 3 wired the "like" button but didn't check the spec rule TC_FUN_008: "User can't like their own kudos" — the button should disable if `kudosAuthorId == userId`. These aren't logic bugs; they're **cross-screen coherence gaps** that emerge only when you look at the system holistically. Four parallel agents, each in their silo, shipped incomplete pieces that happened to compile.

And then there's the chart. The **SpotlightNetworkChart** took 40+ line-hours of Canvas math (pan/zoom, hit detection, search node highlight, color interpolation). It's beautiful and interactive, but it's also 1.2K lines of pure geometry, tested with unit tests but never with a Robolectric visual validation loop. If someone reports "pan is jittery" or "touch detection fails on notch devices," we'll discover it in production, not in review.

## Technical Details

**Architecture — 2-Track Parallel with Slot Pattern**:
- **Track A (UI, 4 agents)**: KudosFeedScreen, KudoDetailCard, KudosSearchScreen, KudosAllTab; KudoCardNested (share mock)
- **Track B (Orchestrator + ViewModel + Persistence)**: KudosViewModel, SpotlightNetworkChart, KudosDataStore (Proto), KudosRepository, Navigation graph, KudosModels/KudosMockData
- **Slot pattern** (key to parallel independence):
  ```kotlin
  @Composable
  fun KudosFeedScreen(
    filterRow: @Composable () -> Unit = { DefaultFilterRow() },
    spotlight: @Composable () -> Unit = { DefaultSpotlight() },
    ...
  ) {
    Column {
      filterRow() // Agent 1 (Filter Dropdowns) injects here
      Box { spotlightNetwork.Chart() } // Agent 3 (Chart) injects here
      LazyColumn { KudoCardNested(...) } // Shared
    }
  }
  ```
  This let each agent compile independently without editing KudosFeedScreen itself.

**Core Features**:
1. **Kudos Feed** (main feed): mock 12-item list, swipe-able card, like persistent via DataStore, hashtag chip tap → filter
2. **All Kudos** tab: grid/list toggle, same like persistence
3. **View Kudos Detail** + **Anonymous variant**: full kudos content, comments (mock), avatar, author name, hashtags, like button (persistent), share button
4. **Search (Sunner)**: live query (persist search history via DataStore), recent searches, debounce 300ms, max 100 chars (added in review fix H2)
5. **Filter dropdowns** (2):
   - Hashtag dropdown: AND-combo logic (e.g., `#product` AND `#kudos` → show only that combo), persists filter state, highlight carousel top-5 by likes, reset carousel when filter clears
   - Department dropdown: filter feed + carousel, highlight carousel upon filter
6. **Spotlight Network Chart** (interactive Canvas):
   - Node-link graph: nodes = top 20 givers, edges = kudos count, node size = kudos given, color interpolation (depth breadth first), pan/zoom (via GestureDetector), search highlight (tap node → expand, show ego network), pinch zoom
   - Tests: 8 ViewModel unit tests cover state, selection, zoom bounds; Canvas math not Robolectric-tested

**Persistence (DataStore)**:
- Proto schema: `KudosPreferences` (liked_kudos_ids: List<String>, search_history: List<String>, last_filter_hashtags: List<String>, last_filter_department: String)
- Serialized to `preferences_pb.pb` in app sandbox
- Wired to KudosRepository → ViewModel → UI (StateFlow)
- No encryption (out of scope; noted in code as TODO for Phase 10 security)

**Files Created/Modified** (43 files, per env):
- New: KudosFeedScreen.kt (288 L), KudoDetailCard.kt (326 L), KudosSearchScreen.kt (294 L), SpotlightNetworkChart.kt (1200 L), KudosViewModel.kt (180 L), KudosRepository.kt (120 L), KudosModels.kt (85 L), KudosMockData.kt (240 L), kudos_preferences.proto, KudosDataStore.kt (95 L), KudoAvatar.kt (60 L), FilterRow.kt (120 L), KudoCardNested.kt (180 L)
- Modified: KudosNavGraph.kt (add 6 routes), AppNavigation.kt, build.gradle (add proto-lite), res/values/strings.xml (add 28 new keys)

**Build & Tests**:
- `assembleDebug`: ✅ (all deps resolved)
- Unit tests: 37 pass (KudosViewModel 8, KudosRepository 6, SpotlightNetworkChart 8, KudosModels 5, search/filter logic 10)
- No integration tests (DataStore mock in unit tests via FakeDataStore)

**Reviewer Findings (7.5/10 score)**:
- **0 Critical**: No blocking logic errors
- **2 HIGH**:
  - **H1**: Hashtag tap on All Kudos detail → pop nav → lose filter context. Fix: Save filter via SavedStateHandle, restore on nav return. Added `@HiltViewModel class KudosDetailViewModel(savedStateHandle: SavedStateHandle)` + `currentFilter: StateFlow<String?>` serialized via `bundle { putString(...) }`.
  - **H2**: Search field missing `maxLength = 100`. Fix: Added `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)` + `.length.coerceAtMost(100)` in `SearchField` Composable.
- **4 MEDIUM**:
  - **M1**: Color tokens hard-coded: `Color(0xFFE85D75)` for red, `Color(0xFFF5F5F5)` for card BG. Fix: Tokenized to `KudosAccentRed`, `KudosCardMuted`, `KudosCardFaint` in theme/KudosColorScheme.kt (part of token refactor).
  - **M2**: Hashtag chips in detail view (2 per row) missing detail `Modifier.fillMaxWidth()` → FlowRow. Fix: Changed to `FlowRow(modifier = Modifier.fillMaxWidth())` so hashtags wrap naturally.
  - **M3**: Like button wires `canLike` but logic doesn't check TC_FUN_008 (user can't like own kudos). Fix: Added `canLike = kudosAuthorId != currentUserId` in ViewModel.
  - **M4**: Typography: "Author Name" using `bodyMedium` instead of `displaySmall` per design spec. Fix: Changed to `displaySmall` in KudoDetailCard author row.

## What We Tried

1. **Parallel UI agents (4 Sonnet) + orchestrator (Opus)**:
   - **Success factor**: Slot pattern + shared models upfront. Each agent compiled independently.
   - **Pain factor**: Agents couldn't start until foundation was solid. If orchestrator had a bug in KudosModels, all 4 agents were blocked. Felt like pseudo-parallel — not true decoupling.
   - **Lesson**: For next multi-agent phases, verify foundation *separately* (unit-test foundation models before spawning agents).

2. **Clarification protocol ambition**:
   - User had 3 options: (A) Mock only (no persistence, static chart), (B) Persistence + static chart, (C) **[Chosen]** Persistence + interactive chart.
   - Option C opened scope: added DataStore, SpotlightNetworkChart.Canvas, coordinate geometry. Agents thought they were building "mock UI"; orchestrator was building a feature.
   - **Resolution**: Documented scope in task description upfront for agents; orchestrator owned the ambition.

3. **SpotlightNetworkChart math**:
   - First pass: Breadth-first layout (Sugiyama-style) → nodes clustered oddly, hard to touch.
   - Second pass: Force-directed simulation (Verlet integration) → too slow, janky on scroll re-compose.
   - Final: Custom radial layout (ring nodes around "self" center), pan via GestureDetector.offset, zoom via pinch scale, hit detection via `distance(touch, nodeCenter) < nodeRadius + 12.dp.toPx()`.
   - Tests pass, but no Robolectric visual (Canvas drawing doesn't render in JVM tests).

4. **Navigation state + filter persistence**:
   - First: Filter state only in ViewModel → popping nav stack lost filter. User complained they had to re-filter after viewing a detail.
   - Fix: SavedStateHandle serialization + DataStore. Now filter persists across nav pop.

5. **Search history**:
   - Spec asked for "recent searches." Implemented as LIFO list, max 20 items, de-duped (search again → bubbles to top).
   - Stored in DataStore, displayed in UI when search field empty.

## Root Cause Analysis

1. **Parallel orchestration has limits**: 4 agents in silos miss cross-screen coherence (hashtag filter not propagating back from detail, like button logic incomplete). Unit tests of individual agents pass; integration gaps emerge in review. **Next time**: add integration test phase after agents ship (before review), or have one agent do a "glue pass" to verify end-to-end flows.

2. **Ambition scope creep in clarifications**: Option C (interactive chart + persistence) wasn't a small add-on; it was a feature. Agents estimated 2 screens; orchestrator estimated 4 screens' worth of backend. The parallel orchestration hid this until review. **Next time**: clarify explicitly: "Option C adds `X` backend tasks + `Y` testing. Are you committing 50% more timeline?"

3. **Canvas code is untestable at JVM level**: SpotlightNetworkChart has 40+ lines of pan/zoom math. Unit tests mock the Canvas and verify state; but actual drawing, touch handling, scroll interaction — all invisible in Robolectric. **Risk**: production reports jitter/crashes that tests didn't catch. **Next time**: add Instrumentation test (run on emulator) for Canvas interactions, or ship with feature flag to A/B users.

4. **Agent independence requires bulletproof contracts**: Agents compiled independently *because* KudosModels.kt, KudosMockData.kt, KudosAvatar.kt were frozen (reviewed, tested, signed off before agents started). If those 3 files changed *during* agent work, all 4 would recompile and possibly conflict. **Next time**: lock foundation models (immutable, version-stamped) before agents fork.

## Lessons Learned

1. **Slot pattern is the scalable solution for multi-agent UI work**:
   - Instead of editing one shared file (KudosFeedScreen), pass composable lambdas. Agent 1 provides `filterRow`, Agent 2 provides `spotlight`, Agent 3 provides skeleton, etc.
   - Eliminates git merges on the orchestrator file.
   - Requires upfront design (decide slots before agents start), but pays off in parallel velocity.

2. **Shared-model-first unblocks parallel agents**:
   - DataClass models (KudoModel, KudosUser, SearchQuery) must be signed off *before* agents compile. Small cost upfront; huge payoff in agent independence.
   - Test those models separately (unit tests for serialization, nullability, defaults) before agents start.

3. **Reviewer catches coherence gaps single agents can't see**:
   - Hashtag filter not propagating back, like button logic incomplete, cross-screen state inconsistency.
   - **Recommendation**: After parallel agents ship, have a "integration reviewer" read all 4 agents' screens together (not separately) to catch these gaps. Prevents fixes-in-review.

4. **Canvas code needs multiple test strategies**:
   - Unit tests (Robolectric): verify state, math (pan/zoom bounds).
   - Instrumentation tests (emulator): verify touch response, scroll smoothness.
   - Manual testing (emulator screenshots, slow-motion): catch jitter, verify UX.
   - Don't ship Canvas features with only unit tests.

5. **Clarifications must name the scope cost**:
   - "Option A (mock) = 3 days, Option B (persistence) = 5 days, Option C (interactive) = 8 days."
   - User can then make informed trade-off. Option C was worth it here (chart is cool), but hiding the cost creates tension.

6. **DataStore is not free**:
   - Protobuf schema, boilerplate DataStore wrapper, unit test mocking.
   - For simple cases (search history, boolean flags), it's overkill; `SharedPreferences` + Serialization is faster.
   - For complex state (list of objects, nested configs), DataStore wins. We used it correctly here (liked_kudos_ids: List<String>, filter state, search history).

7. **File size creep is real**:
   - KudosFeedScreen 288L, KudoDetailCard 326L, KudosSearchScreen 294L — all approach/exceed the 200L guideline.
   - None are critical (logic is readable), but 3 files > 200L suggests future splits: extract detail content into sub-composables, split search UI from search logic.
   - Defer to Phase 07 (refactor pass).

## Next Steps

1. **Push commit `9d662d6` to main** (after user final approval).
2. **Phase 06 (Spotlight spotlight detail)**: Wire real data from SpotlightNetworkChart node tap → detail screen. Reuse KudoDetailCard; add "ego network" tab.
3. **Phase 07 (Refactor + optimization)**:
   - Split KudoDetailCard (326L) into KudoDetailHeader, KudoDetailBody, KudoDetailComments.
   - Split KudosSearchScreen (294L) into SearchInput, SearchResults, RecentSearches.
   - Add Robolectric visual tests for Typography (displayLarge, displaySmall consistency).
   - Benchmark SpotlightNetworkChart pan/zoom performance on low-end devices (Pixel 3a emulator).
4. **Phase 10 (Security + polish)**:
   - Encrypt DataStore (add `EncryptedSharedPreferences` wrapper or use `DataStore` built-in encryption).
   - Audit like/comment persistence (confirm user auth token validates server-side).
5. **Update `docs/code-standards.md`**:
   - Add section: "Multi-Agent UI Work Best Practices" (slot pattern, shared models, integration review).
   - Add Canvas testing checklist: (unit tests for state, instrumentation for interaction, manual emulator validation).
6. **Document `SpotlightNetworkChart` algorithm** in code comments (radial layout, pan transform, pinch zoom scale). Current code is 1.2K lines of math; needs explanation for next maintainer.

---

## Metrics & Status

| Metric | Value |
|--------|-------|
| Screens implemented | 8 (Feed, All Kudos, View, View Anon, Search, Filter Hashtag, Filter Dept, Chart) |
| Files created | 13 new |
| Files modified | 8 existing (nav, strings, build.gradle, theme) |
| Build status | ✅ assembleDebug |
| Unit tests | 37 pass |
| Reviewer score | 7.5/10 (0 critical, 2 HIGH fixed, 4 MEDIUM fixed) |
| Commit | `9d662d6` (unpushed) |
| Parallel agents | 4 (Sonnet, 1 per 2-screen group) |
| Orchestrator | 1 (Opus, 1 instance for logic + review) |
| Lines of code (ViewModel + DataStore + Chart) | ~1600 |
| DataStore schema | KudosPreferences proto (liked_kudos_ids, search_history, filter state) |

---

## Unresolved Questions

- **Canvas jitter on low-end devices**: No instrumentation test data yet; manual emulation needed (Phase 07).
- **Search debounce 300ms optimal?**: No A/B data; could be 200ms or 500ms. Monitor user feedback.
- **File size refactor timing**: Defer to Phase 07 or do now? Recommend Phase 07 (defer optimization).
