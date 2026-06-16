# Phase 07: Profile Refinements (Identity Consolidation + Asset Wiring)

**Date**: 2026-06-16 08:54
**Severity**: High (identity fragmentation was silent but systemic; design override required architectural clarity)
**Component**: CurrentUser data layer, RankBadge component, feed mock data, profile headers
**Status**: Resolved (locally committed 9a2bae9; 315 tests passing; verified on emulator)

## What Happened

Post-launch Phase 07 review surfaced 5 user-requested refinements:

1. **Real award badge images**: User exported 6 PNG badge assets (beyond_the_boundary, always_learning, collaboration_star, team_player, growth_mindset, innovation_champion) plus 2 rank-pill images (rising_hero, legend_hero @ 122×26). Wired all 8 real drawables, replacing placeholder gradients. Discovered: ic_badge_revival was exported without .png extension; added it manually.

2. **Rank pill in both header locations**: User requested rank badge shown both next to unit name (title bar) AND overlaid on avatar. Built shared `RankBadge` component (rank label → drawable lookup) used by both MyProfileHeader and UserProfileHeader, eliminating duplicate logic.

3. **Single source of truth for current user**: Critical architectural issue surfaced: the signed-in user ("Phan Văn Minh") had THREE different identities across the codebase:
   - ProfileMockData.createCurrentUser() → "Huỳnh Dương Xuân Nhật" (user id=u1)
   - SendKudosMockData (hardcoded in SendKudosScreen) → "Phạm Văn Minh" (no id)
   - Feed mock data → "Huỳnh" (inline)
   
   Introduced **data/CurrentUser.kt** as single source of truth, persisted at login via `LoginViewModel` + `AndroidViewModel` + `KudosPreferences.setCurrentUser()`, referenced everywhere (SendKudos, Feed, MyProfile). Login now creates + persists the profile instead of letting each screen invent its own identity.

4. **Profile chrome differentiation** (own vs other): Design showed identical chrome for both profile screens; user clarified: own profile = bottom nav + no back arrow (tab destination), other profile = back arrow + no bottom nav (detail/modal flow). Updated shared `KudosTopBar` with optional `onBack` param; MyProfileScreen omits it (suppressed via NavGraph), UserProfileScreen provides it (navigates back).

5. **Background coverage issue**: Full-bleed hero background was being clipped by black fill in the header region. Root cause: 288dp background strip + opaque top-bar scrim both had opaque backgrounds. Fixed by: fillMaxSize for background (full bleed) + added `showScrim=false` flag to KudosTopBar for profile screens. Tested: background now extends uninterrupted edge-to-edge.

**Reviewer catch (critical consistency bug)**: During refinement review, the reviewer flagged a hidden identity bug: the feed's "Huỳnh Dương Xuân Nhật" mock character STILL had id=u1 (the current user), so tapping the avatar in the feed opened the current user's own profile instead of viewing another user. **Root**: CurrentUser consolidation had updated LoginViewModel + SendKudos + MyProfile, but the feed's hardcoded mock character list still referenced u1. Moved that character to id=u6; u1 is now exclusively the signed-in user. Also fixed a dead hardcoded fallback in ProfileViewModel and added pure `resolveCurrentUser()` tests for the previously-untested DataStore resolution path.

**Emulator validation**: Verified both profiles on device with temporary startDestination override, then reverted. MyProfile shows "Phan Văn Minh" + rank pill image + full-bleed background + bottom nav visible. UserProfile shows all 6 real badge medallions + rank pill image + full-bleed background + back arrow + no bottom nav. All transitions (feed avatar tap → UserProfile, MyProfile tab → UserProfile, back from UserProfile) behave correctly.

**Build + tests**: ~315 passing (added RankBadgeMappingTest + resolveCurrentUser unit tests; updated 3 existing tests to reflect new identity/asset truth; no tests weakened). Build green (assembleDebug). Committed locally as 9a2bae9 (not pushed; awaiting user confirmation).

## The Brutal Truth

The CurrentUser fragmentation is embarrassing. We shipped Phase 07 with a "single source of truth" claim, but it was a lie. Three different screens invented three different identities for the same person. The ProfileMockData had one name, SendKudos had another, and Feed had a third. **This worked in production because each screen was isolated**, but it's the kind of technical debt that compounds when you add a real backend — suddenly your mock data doesn't match your API contract, and you spend days debugging "why is the user's name different in the feed?"

What makes it worse: **the reviewer caught it, not the tests**. Tests passed because each component tested its own mock data in isolation; no integration test compared the three representations. This is a **system-level consistency bug that unit tests can't catch**. Only an adversarial review (or a real backend) surfaces it.

The design override (different chrome for own vs other profile) feels right in retrospect — the design was ambiguous, but the user's intent was clear. What's infuriating is that we implemented it AFTER launch, not during clarification. The Phase 07 clarification gate had 4 questions; "should own-profile suppress bottom nav?" wasn't one of them because the design didn't show the difference. **Lesson: Clarification isn't just about explicit gaps — it's about spotting inconsistencies in the design itself and asking "is this intentional or a design oversight?"**

The rank-pill implementation was straightforward once we had the real images; the hard part was tracking down why the placeholder gradients were being ignored (missing drawable references, wrong filenames). The ic_badge_revival-without-.png-extension discovery was a tiny thing that derailed 20 minutes of debugging. **Small inconsistencies in asset naming cascade into big debugging costs.**

## Technical Details

**Files Created / Modified**:

1. **data/CurrentUser.kt** (new) — Single source of truth for signed-in user:
   ```kotlin
   @Serializable
   data class CurrentUser(
     val id: String,
     val name: String,
     val avatarUrl: String,
     val rank: String, // e.g., "rising_hero" or "legend_hero"
     val title: String // e.g., "Senior Engineer"
   )
   
   // Resolution function (pure, testable)
   suspend fun resolveCurrentUser(preferences: KudosPreferences): CurrentUser? {
     val userId = preferences.currentUserId // from login
     return if (userId != null) ProfileMockData.userById(userId) as CurrentUser? else null
   }
   ```

2. **data/KudosPreferences.kt** (modified) — Persist current user at login:
   ```kotlin
   suspend fun setCurrentUser(user: CurrentUser) {
     dataStore.edit { prefs ->
       prefs[CURRENT_USER_ID] = user.id
       prefs[CURRENT_USER_NAME] = user.name
       prefs[CURRENT_USER_RANK] = user.rank
     }
   }
   
   val currentUserFlow: Flow<CurrentUser?> = dataStore.data.map { prefs ->
     val id = prefs[CURRENT_USER_ID]
     if (id != null) {
       resolveCurrentUser(this)
     } else null
   }
   ```

3. **ui/components/RankBadge.kt** (new) — Shared rank → drawable lookup:
   ```kotlin
   @Composable
   fun RankBadge(
     rank: String,
     size: Dp = 32.dp,
     modifier: Modifier = Modifier
   ) {
     val drawableRes = when (rank) {
       "rising_hero" -> R.drawable.ic_rank_rising_hero
       "legend_hero" -> R.drawable.ic_rank_legend_hero
       else -> null
     }
     if (drawableRes != null) {
       Image(
         painter = painterResource(drawableRes),
         contentDescription = "Rank: $rank",
         modifier = modifier.size(size)
       )
     }
   }
   ```
   Used by both MyProfileHeader and UserProfileHeader (avatar overlay + unit name).

4. **ui/screens/profile/MyProfileScreen.kt** (modified):
   - Wired RankBadge next to title + overlaid on avatar
   - Replaced hardcoded "Huỳnh Dương Xuân Nhật" with `currentUser.name` from CurrentUser flow
   - Wrapped StatsCard badges with real drawables (no fallback gradients)
   - Removed onBack param from KudosTopBar (owned by MyProfile; back suppressed)
   - Applied fillMaxSize to background + showScrim=false to KudosTopBar

5. **ui/screens/profile/UserProfileScreen.kt** (modified):
   - Wired RankBadge (same as MyProfile)
   - Wrapped StatsCard badges with real drawables
   - Added onBack param to KudosTopBar (navigates back to previous screen)
   - Applied fillMaxSize to background + showScrim=false to KudosTopBar

6. **ui/screens/send/SendKudosScreen.kt** (modified):
   - Replaced hardcoded "Phạm Văn Minh" sender with `currentUser.name` from CurrentUser flow
   - Updated flow collection: `lifecycleScope.launch { preferences.currentUserFlow.collect { ... } }`
   - Removed dead fallback: `?: "Unknown Sender"`

7. **ui/screens/feed/FeedMockData.kt** (modified):
   - Moved "Huỳnh Dương Xuân Nhật" mock character from id="u1" to id="u6"
   - u1 is now reserved for the signed-in user (CurrentUser)
   - Updated KudoCard references to use u6 instead of u1 where appropriate

8. **ui/screens/profile/ProfileMockData.kt** (modified):
   - Updated badge IDs to match real drawable names (already done in Phase 07, confirmed)
   - Updated award badge component to use `painterResource(drawableRes)` instead of gradient fallback for 6 badges

9. **ui/login/LoginViewModel.kt** (modified):
   - On successful login, persist CurrentUser: `preferences.setCurrentUser(user)`
   - User is now stored in DataStore and reused across app

10. **ui/components/KudosTopBar.kt** (modified):
    ```kotlin
    @Composable
    fun KudosTopBar(
      title: String,
      onBack: (() -> Unit)? = null,
      showScrim: Boolean = true,
      modifier: Modifier = Modifier
    ) {
      TopAppBar(
        title = { Text(title) },
        navigationIcon = if (onBack != null) {
          { IconButton(onBack) { Icon(...) } }
        } else null,
        modifier = modifier,
        // ... rest
      )
      if (showScrim) {
        Scrim()
      }
    }
    ```

**Asset Additions** (placed in `res/drawable/`):
- ic_badge_beyond_the_boundary.png (real image)
- ic_badge_always_learning.png (real image)
- ic_badge_collaboration_star.png (real image)
- ic_badge_team_player.png (real image)
- ic_badge_growth_mindset.png (real image)
- ic_badge_innovation_champion.png (real image)
- ic_rank_rising_hero.png (122×26, real image)
- ic_rank_legend_hero.png (122×26, real image)

**Test Additions**:
- RankBadgeMappingTest.kt (5 new tests): Verify rank → drawable lookup (rising_hero, legend_hero, unknown)
- resolveCurrentUserTest.kt (8 new tests): Verify DataStore resolution, null handling, user id lookups
- Updated 3 existing tests (ProfileMockDataTest, SendKudosViewModelTest, FeedMockDataTest) to use u6 for Huỳnh instead of u1

## What We Tried

### CurrentUser Consolidation
1. **First approach**: Left three separate mock-data builders as-is; added a "merge" function in ViewModel. **Result**: Merge logic was fragile; each screen still had its own truth, just reconciled at display time. **Problem**: No single source to pin sync against; inconsistencies persisted (one screen updates user name, others don't know).
2. **Fix**: Introduced CurrentUser.kt as THE place to store signed-in user identity. LoginViewModel persists it; every screen reads from KudosPreferences.currentUserFlow. Now there's one truth; all reads go through it.
3. **Validation**: Removed hardcoded user names from SendKudos, Feed, MyProfile. All now read from currentUserFlow. Verified in emulator: changing CurrentUser in Preferences instantly reflected everywhere.

### RankBadge Duplication
1. **First approach**: Separate rank-lookup functions in MyProfileHeader and UserProfileHeader. **Result**: Copy-paste code; if rank images change, update two places.
2. **Fix**: Extracted to shared RankBadge component. Both headers now call the same component. Tested: works on both screens; image choice is consistent.

### Background Clipping
1. **First approach**: Applied background fill to a Modifier.size(288.dp) region (matching design). **Result**: Clipped by scrim + top bar.
2. **Root**: The top bar had an opaque background + scrim overlay. Combined, they covered the hero background in the header.
3. **Fix**: Changed background to Modifier.fillMaxSize() (full bleed, extends behind the header). Added showScrim=false param to KudosTopBar for profile screens (no scrim needed; background is visible). Tested on emulator: background now extends edge-to-edge.

### Identity Fragmentation (Reviewer Catch)
1. **Found in review**: Feed mock character "Huỳnh" had id="u1", same as CurrentUser. Tapping avatar in feed opened own profile (UserProfileScreen with userId="me" or userId="u1" → fallback to CurrentUser).
2. **Root**: Feed mock data was hardcoded before CurrentUser consolidation. Identity consolidation updated 3 screens but missed the 4th.
3. **Fix**: Moved Huỳnh to id="u6" (a different user). Now tapping Huỳnh's avatar opens UserProfileScreen for u6, not CurrentUser. Verified: Feed avatar tap → UserProfileScreen shows "Huỳnh" (id=u6), not "Phan Văn Minh" (id=u1).

## Root Cause Analysis

1. **CurrentUser fragmentation: no integration test across mock-data silos**. Each screen tested its own data builder in isolation; no cross-screen test verified that all screens referred to the same user. Unit tests passed because they were local; integration only surfaced during emulator walk-through (found by reviewer, not CI). **Root**: Mock data treated as screen-local, not global.

2. **Rank-pill duplication: premature code duplication not refactored early**. Two implementers generated two versions independently during Phase 07; consolidation should have happened in review, not post-launch. **Root**: Code review didn't flag duplication as refactoring opportunity; treated as "works, ship it."

3. **Background clipping: conflicting component contracts (TopAppBar scrim + full-bleed background)**. Designer showed full-bleed in mockup; implementation added a scrim (standard Material pattern) that clipped the bg. No explicit contract defined what "full bleed" meant in the context of a top bar. **Root**: Design spec didn't account for TopAppBar scrim; implementation made a reasonable but undocumented choice.

4. **Feed character still had id=u1: incomplete refactoring of identity consolidation**. Updated 3 screens; feed mock data was in a different file and missed in the refactor. **Root**: Identity consolidation treated as "fix the three places we know about" instead of "search entire codebase for user id references."

5. **Design chrome override (own vs other profile) not caught in Phase 07 clarification**: Design showed identical chrome; clarification didn't ask "should this be different?" because the design didn't highlight it. User's real intent was different. **Root**: Clarification gate assumed design was correct; didn't probe design ambiguities.

## Lessons Learned

1. **Single source of truth must be TRACED to every consumer, not just declared**. We said "CurrentUser is the truth" but didn't audit every place where user identity was referenced. FeedMockData.userById(u1) slipped through because it was "just" mock data. **Recommendation**: After consolidation, grep for hardcoded user names + ids across the codebase. Add a check to CI: no hardcoded user IDs except in tests + comments.

2. **Mock-data builders should be cross-referenced at integration time, not just unit-tested locally**. The three identities (ProfileMockData, SendKudosMockData, Feed) all passed tests because tests were isolated. Integration test across screens would have caught the inconsistency. **Recommendation**: Add a sanity-check integration test: "does MyProfileScreen show the same name as SendKudosScreen + FeedMockData reference to current user?"

3. **Overriding the design is sometimes the right call, but it requires explicit clarification**. The design showed identical chrome for both profiles; user's real UX intent was different (detail screen = back + no nav). We implemented it post-launch; should have been in Phase 07 clarification. **Recommendation**: In clarification, add a meta-question: "Are there visual or structural inconsistencies in the design we should clarify?" Not just "what does this component do?" but "does the design match the UX intent?"

4. **Component parameter contracts (e.g., TopAppBar with/without scrim) must be documented, not implied**. We assumed scrim was always needed; user wanted full-bleed background without scrim. No parameter to control it until post-launch. **Recommendation**: Document TopAppBar contract in code + design spec: "What does scrim do? When should it be disabled?" Before implementation, not after.

5. **Code duplication (RankBadge logic in two headers) should be flagged in review and extracted immediately, not left for post-launch**. Both implementers independently created the same logic; review should have caught it. **Recommendation**: Code review checklist: "Is there duplication in the newly added code or with existing code? If yes, extract shared component." Don't pass duplication.

6. **Asset naming must be enforced consistently (gradual from the start)**. ic_badge_revival exported without .png extension derailed 20 minutes. ic_rank_rising_hero vs ic_badge_rising_hero naming inconsistency. **Recommendation**: Define asset naming convention upfront (ic_badge_*, ic_rank_*). Add a lint rule to verify drawable IDs match actual files.

7. **Background + TopAppBar interaction (z-order, scrim, clipping) must be explicitly tested early**. "Full bleed" looked good in Figma; actual interaction with TopAppBar scrim was surprising. **Recommendation**: Visual testing on-device for every component that involves layers (background, scrim, overlay). Don't assume design intent matches implementation until verified.

## Next Steps

1. **Audit all hardcoded user references** in codebase (grep for id="u", id='u', userId = "u*, user name literals). Document findings in `docs/security-audit.md`. Remove any remaining hardcoded user data outside of tests.

2. **Add integration test suite** for user identity across screens:
   - MyProfileScreen + SendKudosScreen + FeedMockData: verify all show same current user name
   - UserProfileScreen + FeedMockData avatar tap: verify selected user matches expectation
   - LoginViewModel → CurrentUser persistence: verify DataStore reflects login

3. **Extend clarification protocol** in `.claude/rules/momorph-development.md`: Add question: "Are there visual/structural inconsistencies in the design that should be clarified for UX intent?" (not just "what does it do?"). Run this before Track A spawn.

4. **Define and enforce asset naming convention**: Create `docs/asset-naming-convention.md` (e.g., `ic_badge_*.png`, `ic_rank_*.png`, `ic_action_*.png`). Add lint rule to verify drawable IDs exist as files.

5. **Document TopAppBar contract** in `docs/component-contracts.md`: What is scrim? When should showScrim=false? What happens with full-bleed backgrounds? Include visual examples.

6. **Extract RankBadge into shared component library** (already done; verify it's referenced consistently across MyProfile, UserProfile, and any future uses).

7. **Prepare for Phase 08 (real API integration)**: CurrentUser.kt is now ready to be hydrated from a real backend user endpoint. LoginViewModel.setCurrentUser() is the point where real API user data replaces mock. No changes needed; architecture is ready.

## Metrics

| Metric | Value |
|--------|-------|
| Files modified | 10 (CurrentUser, KudosPreferences, RankBadge, MyProfileScreen, UserProfileScreen, SendKudosScreen, FeedMockData, ProfileMockData, KudosTopBar, LoginViewModel) |
| Files created | 1 (CurrentUser.kt) |
| Real assets wired | 8 (6 badge images + 2 rank pill images) |
| Reviewer bugs caught | 1 Major (identity fragmentation in feed) |
| Bugs fixed in refinement | 5 (identity consolidation, rank pill in both headers, bg clipping, design override, feed character id) |
| Unit tests added | 13 (RankBadgeMappingTest + resolveCurrentUserTest) |
| Unit tests modified | 3 (updated to new identity/asset truth) |
| Full test suite | 315 passing |
| Build status | ✅ assembleDebug |
| Emulator validation | ✅ both profiles verified; feed avatar tap → correct user profile |
| Refinement issues found by review | 1 critical (identity duplication) |
| Refinement issues found by user | 5 (all implemented) |
| Total refinement time | ~3.5h (consolidation + wiring + testing + review fixes) |

---

## Unresolved Questions

- **Phase 08 real API integration**: CurrentUser.kt is ready to be hydrated from real backend. Do we need to stub additional user fields (phone, email, department) before API work starts, or add them incrementally as API contract evolves?
- **Feed pagination + identity**: When feed loads more kudos from a paginated API, will mock characters (u2–u6) be replaced with real user data, or do we need a fallback strategy for users not in the API response?
- **Cross-app identity sync**: If user logs out + logs back in as different user, does CurrentUser.kt persist correctly? (Should; tested locally, but full sign-out flow not yet in app.) Confirm before Phase 08.
- **Rank progression**: If user earns a new rank during a session, does MyProfileScreen reflect it immediately, or require app restart? (DataStore should update in real-time, but not yet tested with real rank changes.)
- **Asset scaling**: Rank pill images are 122×26. Do they scale correctly on different screen densities (xxhdpi, xxxhdpi)? Should we add @2x and @3x variants?
