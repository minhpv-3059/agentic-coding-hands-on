---
phase: "05"
title: Kudos Feed
priority: p0
status: todo
blockedBy: ["phase-04-home"]
---

# Phase 05 — Kudos Feed

**Goal:** Implement Kudos feed screens including list, view, search, and filters.

## MoMorph refs
- [iOS] Sun*Kudos (main feed): https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/fO0Kt19sZZ
- [iOS] Sun*Kudos_All Kudos: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/j_a2GQWKDJ
- [iOS] Sun*Kudos_View kudo: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/T0TR16k0vH
- [iOS] Sun*Kudos_View kudo ẩn danh: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/5C2BL6GYXL
- [iOS] Sun*Kudos_Searching: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/hldqjHoSRH
- [iOS] Sun*Kudos_Search Sunner: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/3jgwke3E8O
- [iOS] Sun*Kudos_dropdown hashtag: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/V5GRjAdJyb
- [iOS] Sun*Kudos_dropdown phòng ban: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/76k69LQPfj

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/feed/KudosFeedScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/feed/KudosFeedViewModel.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/feed/ViewKudoScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/feed/KudosSearchScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/ui/components/KudosCard.kt`
- `app/src/main/java/com/sun/kudos_demo/ui/components/HashtagFilterDropdown.kt`
- `app/src/main/java/com/sun/kudos_demo/ui/components/DepartmentFilterDropdown.kt`

## Integration contract
- Kudo card tap → navigate to `kudos/view/{id}`
- Anonymous kudo renders without sender info
- Search bar → inline search state transition

## Out of scope
- Real API — mock list data only
