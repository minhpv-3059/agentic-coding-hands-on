---
phase: "10"
title: Awards
priority: p2
status: done
blockedBy: ["phase-07-profile"]
---

# Phase 10 — Awards

**Goal:** Implement award detail screens for all 6 award types.
**Status:** ✅ DONE (2026-06-17)

## Completion Summary

Shipped single-screen Awards tab (`AwardsScreen.kt`) with dropdown selector for all 6 award types. User selects award → display title, description, quantity+unit, and value rows (Signature 2025-Creator has 2 value rows). Layout matches Home (content panel + KudosTopBar overlay); bottom nav integration wired. Trophy assets extracted from MoMorph S3 + composited via PIL. Home Awards Section wired with real trophies for Top Project and Top Project Leader. Pre-selection from Home working (user navigates from Home award card → Awards tab defaults to selected award).

**Deviation from contract:** Screen named `AwardsScreen.kt` (not `AwardDetailScreen.kt`) — single consolidated tab+dropdown, not separate detail screens per type.

**Assets:** All 6 trophy images created and wired: MVP, Best Manager, Signature 2025-Creator, Top Project, Top Project Leader, Top Talent.

**Tests:** 541 unit tests pass (0 fail); AwardDataTest, AwardViewModelTest, AwardViewModelStateFlowTest added. Reviewed 8.5/10, 0 critical issues. Emulator visual verification: layout, dropdown, Signature 2-value-rows, Home wiring — all confirmed.

## MoMorph refs
- [iOS] Award_MVP: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/b2BuS8HYIt
- [iOS] Award_Best Manager: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/7y195PPTxQ
- [iOS] Award_Signature 2025 - Creator: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/O98TwiHaJe
- [iOS] Award_Top project: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/FQoJZLkG_d
- [iOS] Award_Top project leader: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/QQvsfK3yaK
- [iOS] Award_Top talent: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/c-QM3_zjkG

## Files created/modified
- **New:** `feature/awards/{AwardsScreen.kt, AwardContent.kt, AwardData.kt, AwardViewModel.kt}`
- **New:** `feature/awards/components/{AwardKvSection.kt, AwardHeaderSection.kt, AwardTrophyCard.kt, AwardsKudosSection.kt}`
- **New:** `navigation/{AwardsNavigation.kt, NAV_ROUTE updates}`
- **Modified:** `ui/KudosApp.kt` (bottom-nav tab match logic), `feature/home/components/HomeAwardsSection.kt` (real trophy wiring)
- **Assets:** `res/drawable-nodpi/img_award_{mvp,best_manager,signature_creator,top_project,top_project_leader,top_talent}.png`, `res/drawable/ic_award_{badge,diamond,flag}.xml`, `res/drawable/ic_kudos_wordmark.xml`
- **Tests:** `AwardDataTest.kt, AwardViewModelTest.kt, AwardViewModelStateFlowTest.kt`
