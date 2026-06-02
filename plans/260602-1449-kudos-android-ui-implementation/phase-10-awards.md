---
phase: "10"
title: Awards
priority: p2
status: todo
blockedBy: ["phase-07-profile"]
---

# Phase 10 — Awards

**Goal:** Implement award detail screens for all 6 award types.

## MoMorph refs
- [iOS] Award_MVP: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/b2BuS8HYIt
- [iOS] Award_Best Manager: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/7y195PPTxQ
- [iOS] Award_Signature 2025 - Creator: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/O98TwiHaJe
- [iOS] Award_Top project: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/FQoJZLkG_d
- [iOS] Award_Top project leader: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/QQvsfK3yaK
- [iOS] Award_Top talent: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/c-QM3_zjkG

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/awards/AwardDetailScreen.kt` — parametric, covers all types
- `app/src/main/java/com/sun/kudos_demo/feature/awards/AwardViewModel.kt`

## Integration contract
- Single `AwardDetailScreen` parameterized by award type enum
- Accessible from profile screen → awards section

## Out of scope
- Award eligibility logic — display only
