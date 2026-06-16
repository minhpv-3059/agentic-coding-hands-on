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

## Asset notes (cross-phase — ĐỌC khi bắt đầu Phase 10)

Phase 10 phải download/trích ảnh trophy cho các loại giải. **Các trophy này dùng CHUNG với Awards section ở màn Home (Phase 04)** → làm Phase 10 nhớ wire luôn vào Home, đừng để sót.

Trạng thái trophy ở Home hiện tại (`feature/home/components/HomeAwardsSection.kt` → `mockAwards`, field `AwardItem.image: Int?`):
- ✅ **Top Talent** — `R.drawable.img_award_top_talent` (đã có, user export 2026-06-05)
- ⏳ **Top Project** — placeholder 🏆, CẦN download (Figma node `mm_media_Picture-Award` = `I6885:9034;72:2115` trong `mms_4.2_award list` 6885:9032)
- ⏳ **Top Project Leader** — placeholder 🏆 (design card 3 chỉ có glow chung, không có chữ tên riêng)

**Khi có ảnh:** copy vào `res/drawable-nodpi/img_award_<name>.png` rồi set `mockAwards[].image = R.drawable.img_award_<name>` — `AwardCard.kt` đã tự fallback placeholder khi `image == null`, nên chỉ cần gán là xong.

⚠️ `get_figma_image` (500) + `get_media_file` (401) đang lỗi (2026-06-05) → không tự download bytes được; cần user export Figma trực tiếp hoặc chờ API hồi phục.
