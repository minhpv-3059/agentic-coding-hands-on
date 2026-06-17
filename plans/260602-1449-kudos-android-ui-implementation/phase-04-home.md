---
phase: "04"
title: Home Screen
priority: p0
status: done
blockedBy: ["phase-03-auth"]
---

# Phase 04 — Home Screen

**Goal:** Implement Home screen with bottom nav integration.

## MoMorph refs
- [iOS] Home: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/OuH1BUTYT0
- Navigation bar: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/VeF77wVOUW

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/home/HomeScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/home/HomeViewModel.kt`

## Integration contract
- Bottom nav: Home / Kudos Feed / Profile / Notifications tabs
- FAB or action button → navigate to `kudos/send`

## Out of scope
- Real API data — mock data only

## Completion (2026-06-05)

**Shipped:**
- Home screen (iOS) in Jetpack Compose with mock data: HomeScreen.kt, HomeViewModel.kt, 8x feature/home/components (HomeHeroSection, HeroActionButtons, CountdownRow, HomeAwardsSection, AwardCard, SectionHeader, HomeKudosSection, HomeNoteSection, HomeFab).
- Shared upgrades: KudosTopBar (real logo, VN flag, search, bell-with-badge, edge-to-edge fix), KudosBottomNav (real Figma icons as vector drawables).
- Navigation wired (HOME → HomeScreen, launchSingleTop double-tap prevention), NavRoutes.SEARCH placeholder added.
- ViewModel: live countdown (target launch + 20d20h20m), language toggle, mock unread badge.
- Assets: bg_home_keyvisual.png, img_kudos_banner.png, img_award_top_talent.png, ic_nav_{home,awards,kudos,profile}.xml, ic_vn_flag.xml, ic_kudos_logo.xml, DSEG7 seven-segment font (SIL OFL).
- Quality: build clean, 17/17 unit tests pass, review 7.5/10 (edge-to-edge + 3 Medium findings applied).
- DEFERRED to Phase 10: Top Project & Top Project Leader trophy images remain styled placeholders (only Top Talent exported).
