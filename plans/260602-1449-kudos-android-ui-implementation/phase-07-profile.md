---
phase: "07"
title: Profile Screens
priority: p0
status: done
blockedBy: ["phase-04-home"]
completedDate: 2026-06-15
---

# Phase 07 — Profile

**Goal:** Implement own profile and other users' profile screens.

## MoMorph refs
- [iOS] Profile bản thân: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/hSH7L8doXB
- [iOS] Profile người khác: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/bEpdheM0yU

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/profile/MyProfileScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/profile/UserProfileScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/profile/ProfileViewModel.kt`

## Integration contract
- Own profile: editable fields, shows sent/received kudos count + badges
- Others' profile: read-only, "Send Kudos" CTA → navigate to `kudos/send?recipient={userId}`

## Out of scope
- Edit profile API — UI state only

## Completion

**Delivered 2026-06-15 + Refinement 2026-06-16:**

### Files Created
- `feature/profile/{ProfileModels.kt, ProfileMockData.kt, MyProfileViewModel.kt, UserProfileViewModel.kt, MyProfileScreen.kt, UserProfileScreen.kt}`
- `feature/profile/components/{ProfileHeader, ProfileIconCollection, ProfileStatsCard, ProfileKudosFilter, ProfileSectionHeader, UserProfileHeader, ProfileAwardBadges, ProfileSendKudosCta, ProfileReceivedKudosLabel, UserProfileSectionHeader, RankBadge}.kt`
- `data/CurrentUser.kt` (signed-in identity singleton)
- `navigation/ProfileNavigation.kt`
- 4 test files for Profile logic (ProfileModels, ViewModels x2, RankBadgeMappingTest)

### Files Modified
- `navigation/{NavRoutes, AppNavGraph, SendKudosNavigation, KudosFeedNavigation}.kt` — added PROFILE_ME, PROFILE_USER routes + recipient pre-fill wiring
- `ui/KudosApp.kt` — suppressed global bottom nav on profile routes
- `ui/KudosTopBar.kt` — added showScrim param (profile=false)
- `feature/send/SendViewModel.kt` — reads CurrentUser.kt
- `feature/feed/FeedViewModel.kt` — reads CurrentUser.kt; moved "Huỳnh Dương Xuân Nhật" from u1 to u6
- `feature/profile/{MyProfileViewModel, UserProfileViewModel}.kt` — integrated CurrentUser.kt identity reads
- `res/drawable-nodpi/` — 6 award-badge images (img_badge_*) + 2 rank pills (img_rank_legend_hero, img_rank_rising_hero)

### Verification
- Both screens pixel-faithful to Figma design, verified on emulator
- KUDOS_SEND recipient pre-fill working (tapped user profile → send kudos button)
- 315 unit tests pass (186 existing + 129 new; includes RankBadgeMappingTest + resolveCurrentUser tests)
- Route conflict fixed: `profile/me` wildcard collision resolved
- Reviewer findings closed: badge identity split (RankBadge component), dead fallback removed, resolve path fully tested
- Single signed-in identity persisted at login: MyProfileViewModel → AndroidViewModel + KudosPreferences.setCurrentUser

### Design Implementation
- Full-bleed key-visual background on both profile screens (MyProfile + UserProfile)
- Own-vs-other chrome: UserProfileScreen is detail view (back arrow + no bottom nav); MyProfileScreen is main (bottom nav, no back)
- RankBadge component unifies rank pill rendering (label + Figma image) in both profile headers (name-line pill + avatar overlay)
