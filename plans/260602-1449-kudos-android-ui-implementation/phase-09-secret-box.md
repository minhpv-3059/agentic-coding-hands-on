---
phase: "09"
title: Secret Box
priority: p1
status: todo
blockedBy: ["phase-04-home"]
---

# Phase 09 — Secret Box

**Goal:** Implement secret box feature with all open states and animation.

## MoMorph refs
- [iOS] Open secret box: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/kQk65hSYF2
- [iOS] Open secret box- action bấm mở: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/KUmv414uC9
- [iOS] Open secret box- trạng thái Standby (state A): https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/IXpGakYRm5
- [iOS] Open secret box- trạng thái Standby (state B): https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/_cWAEarZPi
- [iOS] Open secret box- trạng thái Standby (state C): https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/scvV-OQCAJ
- [iOS] Open secret box- trạng thái Standby (state D): https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/wsI6gaO_yc
- [iOS] Open secret box- trạng thái Standby (state E): https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/FvTOS7oCPU
- [iOS] Open secret box- trạng thái Standby (state F): https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/xptNUunBS_
- [iOS] Open secret box- trạng thái Standby (state G): https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/-LIblaeusT

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/secretbox/SecretBoxScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/secretbox/SecretBoxViewModel.kt`
- `app/src/main/java/com/sun/kudos_demo/ui/components/GiftBoxAnimation.kt`

## Note
The 7 "Standby" states likely represent animation frames or reward tiers — inspect each design to confirm before implementing.

## Out of scope
- Real reward/unlock API — mock state transitions only
