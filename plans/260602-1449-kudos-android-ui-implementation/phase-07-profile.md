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

**Delivered 2026-06-15:**

### Files Created
- `feature/profile/{ProfileModels.kt, ProfileMockData.kt, MyProfileViewModel.kt, UserProfileViewModel.kt, MyProfileScreen.kt, UserProfileScreen.kt}`
- `feature/profile/components/{ProfileHeader, ProfileIconCollection, ProfileStatsCard, ProfileKudosFilter, ProfileSectionHeader, UserProfileHeader, ProfileAwardBadges, ProfileSendKudosCta, ProfileReceivedKudosLabel, UserProfileSectionHeader}.kt`
- `navigation/ProfileNavigation.kt`
- 4 test files for Profile logic (ProfileModels, ViewModels x2)

### Files Modified
- `navigation/{NavRoutes, AppNavGraph, SendKudosNavigation, KudosFeedNavigation}.kt` — added PROFILE_ME, PROFILE_USER routes + recipient pre-fill wiring
- `ui/KudosApp.kt` — suppressed global bottom nav on profile routes

### Verification
- Both screens pixel-faithful to Figma design, verified on emulator
- KUDOS_SEND recipient pre-fill working (tapped user profile → send kudos button)
- 305 unit tests pass (186 existing + 119 new profile tests)
- Route conflict fixed: `profile/me` wildcard collision resolved
- Reviewer score 7.5 → all findings addressed (badge gradient, layout overflow, dead code)

### Follow-up
- 6 award-badge real images to be exported from Figma (currently placeholders; user responsible, documented in clarifications.md)
