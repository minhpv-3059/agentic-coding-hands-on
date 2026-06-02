---
phase: "06"
title: Send Kudos Flow
priority: p0
status: todo
blockedBy: ["phase-05-kudos-feed"]
---

# Phase 06 — Send Kudos Flow

**Goal:** Implement full Send Kudos form with dropdowns, validation states, and community standards.

## MoMorph refs
- [iOS] Sun*Kudos_Gửi lời chúc Kudos: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/PV7jBVZU1N
- [iOS] Sun*Kudos_Viết Kudo_default: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/7fFAb-K35a
- [iOS] Sun*Kudos_Lỗi chưa điền hết: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/0le8xKnFE_
- [iOS] Sun*Kudos_Tiêu chuẩn cộng đồng: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/xms7csmDhD
- [iOS] Sun*Kudos_Gửi lời chúc Kudos_dropdown hashtag: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/aKWA2klsnt
- [iOS] Sun*Kudos_Gửi lời chúc Kudos_dropdown tên người nhận: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/5MU728Tjck
- [iOS] Ẩn danh: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/p9vFVBE_tc

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/send/SendKudosScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/send/SendKudosViewModel.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/send/CommunityStandardsScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/ui/components/RecipientDropdown.kt`
- `app/src/main/java/com/sun/kudos_demo/ui/components/AnonymousToggle.kt`

## Integration contract
- On submit success → navigate back to feed with refresh
- Validation: recipient + hashtag + message required
- Anonymous toggle → hides sender identity in kudo card

## Out of scope
- Real submit API — mock success/error
