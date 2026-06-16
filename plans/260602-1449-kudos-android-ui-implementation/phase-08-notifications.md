---
phase: "08"
title: Notifications
priority: p1
status: done
blockedBy: ["phase-04-home"]
---

# Phase 08 — Notifications

**Goal:** Implement notifications list screen.

## MoMorph refs
- [iOS] Notifications: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/_b68CBWKl5

## Files to create
- `app/src/main/java/com/sun/kudos_demo/feature/notifications/NotificationsScreen.kt`
- `app/src/main/java/com/sun/kudos_demo/feature/notifications/NotificationsViewModel.kt`

## Integration contract
- Notification item tap → navigate to relevant kudo/profile
- Unread badge on bottom nav icon

## Out of scope
- Push notification integration — UI only

## Shipped

**UI (~100% visual match, verified on emulator):** `NotificationsScreen.kt` + 4 components (top bar, mark-all button, item, icon mapper). Full-bleed key-visual bg, 7 colored notification types, unread red dot on first item, inline "Tiêu chuẩn cộng đồng" link. 7 icons are real Figma SVG exports → vector drawables (`res/drawable/ic_*.xml`) with baked fill colors.

**Logic:** `NotificationModels.kt` (7-type enum), `NotificationsMockData.kt` (7 seed + 1 unread), `NotificationsRepository.kt` (in-memory + `unreadCount` StateFlow), `NotificationsViewModel.kt` (localized VN/EN title), `NotificationsNavigation.kt` (per-type nav mapping).

**Integration:** AppNavGraph wired; HomeViewModel + KudosFeedViewModel sync badge from repository (mark-read syncs across views).

**Quality:** 47 new unit tests, 361/361 suite passing, 0 regressions. Reviewer APPROVE_WITH_NITS 8.0/10; 2 important issues fixed.

**Icon assets (resolved):** user exported the 7 icons from Figma → converted to vector drawables, swapped in via the icon mapper, dropped the temporary `material-icons-extended` dependency. Header bell badge corrected gold→red (`KudosAccentRed`). Verified on emulator — no residual.
