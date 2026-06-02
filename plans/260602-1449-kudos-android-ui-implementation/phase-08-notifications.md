---
phase: "08"
title: Notifications
priority: p1
status: todo
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
