---
phase: "06"
title: Send Kudos Flow
priority: p0
status: done
blockedBy: []
---

# Phase 06 — Send Kudos Flow

**Goal:** Implement full Send Kudos form with dropdowns, validation states, and community standards.

**Status:** ✅ Completed — build PASS, 152 unit tests PASS.

## Completed Deliverables

**UI Components:**
- SendKudosScreen, SendKudosFormContent, SendKudosErrorBanner
- RecipientField (search dropdown, horizontal label-left layout), DanhHieuField dropdown
- RichTextToolbar (markdown: bold, italic, strike, link, list, quote)
- MessageField, HashtagSection (multi-select, max 5, white chips), ImageAttachRow (photo thumbnails)
- AnonymousSection toggle
- CommunityStandardsScreen + CommunityStandardsContent (10 criteria, security section, ROOT banner)

**Logic & State:**
- SendKudosViewModel: recipient+message+hashtag validation, submit→prepend, double-tap guard, recipient search, hashtag cap
- RichTextFormatter: functional markdown on selection
- SendKudosMockData + SendKudosUiState
- KudosRepository: in-memory store (addKudo prepends, kudoById)
- KudosFeedViewModel refactored to read from KudosRepository

**Behavior:**
- Real Android Photo Picker (max 5 images, bitmap thumbnail decode, no Coil)
- New kudo appears at top of feed/All Kudos
- Navigation: KUDOS_SEND → SendKudosRoute, KUDOS_COMMUNITY_STANDARDS → route
- "Tiêu chuẩn cộng đồng" link wired

## Files Created/Modified
- `feature/send/SendKudosScreen.kt`, `SendKudosViewModel.kt`, `CommunityStandardsScreen.kt`
- `ui/components/RecipientDropdown.kt`, `RichTextToolbar.kt`, `MessageField.kt`, `HashtagSection.kt`, `ImageAttachRow.kt`, `AnonymousToggle.kt`
- `data/KudosRepository.kt`
- `viewmodel/KudosFeedViewModel.kt` (refactored)
- Mock data + unit tests for validation, formatting, repository

## Quality Assurance
- Code review: 20 findings (2 medium, 18 low), all fixed and verified
- Test coverage: 152 unit tests pass
- Design compliance: validated against MoMorph specs (clarifications.md Session 2026-06-12)
