---
phase: "07"
title: Profile Screens
priority: p0
status: todo
blockedBy: ["phase-04-home"]
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
