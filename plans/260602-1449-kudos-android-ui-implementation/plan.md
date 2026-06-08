---
title: Kudos Android UI Implementation
status: in_progress
created: 2026-06-02
fileKey: 9ypp4enmFmdK3YAFJLIu6C
stack: Android / Jetpack Compose / Material3
package: com.sun.kudos_demo
blockedBy: []
blocks: []
---

# Kudos Android UI Implementation Plan

## Overview

Implement all iOS-mobile screens from MoMorph design into Android Jetpack Compose.
Stack: Kotlin + Compose + Material3 | minSdk 26 | targetSdk 36

## ⚠️ Asset Extraction Protocol (đọc trước khi implement)

**`get_media_files` KHÔNG đáng tin cho background fill images.**
Khi Figma node có CSS `background-position` offset hoặc `background-size` > 100%, raw S3 file ≠ rendered visual.

Cách detect: `get_node` → xem `styles.background`. Nếu có offset số âm hoặc scale >100% → **yêu cầu user export từ Figma trực tiếp**.

Xem chi tiết: [`clarifications.md`](./clarifications.md)

## Screen Inventory (iOS screens — 38 screens total)

Screens prefixed `[iOS]` are the authoritative mobile screens. Web/desktop screens excluded.

### Design System (non-screen frames)
| Frame | screen_id | Purpose |
|-------|-----------|---------|
| Button | `1wjEyOVU4v` | Button variants |
| Color | `B-HozgdIJd` | Color tokens |
| Typography | `a8VSeudT6N` | Text styles |
| Icon | `rCAXfPH2gn` | Icon set |
| Navigation bar | `VeF77wVOUW` | Bottom nav |
| Top Navigation | `j3ey-zXK89` | App bar |
| Component | `dSQdI_iZpt` | Shared components |

### iOS App Screens
| Flow | Screen | screen_id | Priority |
|------|--------|-----------|----------|
| Auth | [iOS] Login | `8HGlvYGJWq` | P0 |
| Home | [iOS] Home | `OuH1BUTYT0` | P0 |
| Kudos Feed | [iOS] Sun*Kudos | `fO0Kt19sZZ` | P0 |
| Kudos Feed | [iOS] Sun*Kudos_All Kudos | `j_a2GQWKDJ` | P0 |
| Kudos Feed | [iOS] Sun*Kudos_View kudo | `T0TR16k0vH` | P0 |
| Kudos Feed | [iOS] Sun*Kudos_View kudo ẩn danh | `5C2BL6GYXL` | P0 |
| Kudos Feed | [iOS] Sun*Kudos_Searching | `hldqjHoSRH` | P1 |
| Kudos Feed | [iOS] Sun*Kudos_Search Sunner | `3jgwke3E8O` | P1 |
| Kudos Feed | [iOS] Sun*Kudos_dropdown hashtag | `V5GRjAdJyb` | P1 |
| Kudos Feed | [iOS] Sun*Kudos_dropdown phòng ban | `76k69LQPfj` | P1 |
| Send Kudos | [iOS] Sun*Kudos_Gửi lời chúc Kudos | `PV7jBVZU1N` | P0 |
| Send Kudos | [iOS] Sun*Kudos_Viết Kudo_default | `7fFAb-K35a` | P0 |
| Send Kudos | [iOS] Sun*Kudos_Lỗi chưa điền hết | `0le8xKnFE_` | P1 |
| Send Kudos | [iOS] Sun*Kudos_Tiêu chuẩn cộng đồng | `xms7csmDhD` | P1 |
| Send Kudos | [iOS] Sun*Kudos_Gửi lời chúc Kudos_dropdown hashtag | `aKWA2klsnt` | P1 |
| Send Kudos | [iOS] Sun*Kudos_Gửi lời chúc Kudos_dropdown tên người nhận | `5MU728Tjck` | P1 |
| Profile | [iOS] Profile bản thân | `hSH7L8doXB` | P0 |
| Profile | [iOS] Profile người khác | `bEpdheM0yU` | P1 |
| Notifications | [iOS] Notifications | `_b68CBWKl5` | P1 |
| Secret Box | [iOS] Open secret box | `kQk65hSYF2` | P1 |
| Secret Box | [iOS] Open secret box- action bấm mở | `KUmv414uC9` | P1 |
| Secret Box | [iOS] Open secret box- trạng thái Standby (x7 states) | `IXpGakYRm5`...`xptNUunBS_` | P2 |
| Awards | [iOS] Award_Best Manager | `7y195PPTxQ` | P2 |
| Awards | [iOS] Award_MVP | `b2BuS8HYIt` | P2 |
| Awards | [iOS] Award_Signature 2025 - Creator | `O98TwiHaJe` | P2 |
| Awards | [iOS] Award_Top project | `FQoJZLkG_d` | P2 |
| Awards | [iOS] Award_Top project leader | `QQvsfK3yaK` | P2 |
| Awards | [iOS] Award_Top talent | `c-QM3_zjkG` | P2 |
| Rules | [iOS] Thể lệ | `zIuFaHAid4` | P2 |
| Language | [iOS] Language dropdown | `uUvW6Qm1ve` | P2 |
| Error | [iOS] Access denied | `k-7zJk2B7s` | P2 |
| Error | [iOS] Not Found | `sn2mdavs1a` | P2 |

## Phases

| Phase | Title | Status | Screens |
|-------|-------|--------|---------|
| 01 | Design System & Theme | ✅ done | Color, Typography, Button, Icons, Nav |
| 02 | App Navigation Setup | ✅ done | Nav structure, bottom nav |
| 03 | Authentication | ✅ done | Login |
| 04 | Home | ✅ done | Home |
| 05 | Kudos Feed | ✅ done | Feed, All Kudos, View, Search, Filters |
| 06 | Send Kudos Flow | ☐ todo | Send form, dropdowns, validation |
| 07 | Profile | ☐ todo | Own profile, Others' profile |
| 08 | Notifications | ☐ todo | Notifications list |
| 09 | Secret Box | ☐ todo | Box states, open animation |
| 10 | Awards | ☐ todo | 6 award types |
| 11 | Supporting Screens | ☐ todo | Rules, Language, Error pages |

## Key Dependencies

- Phase 01 (Design System) must complete before ALL other phases
- Phase 02 (Navigation) must complete before phases 03–11
- Phase 03 (Auth) before Phase 04+
- Phase 05 (Feed) before Phase 06 (Send Kudos uses feed context)

## MoMorph refs
- fileKey: `9ypp4enmFmdK3YAFJLIu6C`
- Base URL: `https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/{screenId}`
