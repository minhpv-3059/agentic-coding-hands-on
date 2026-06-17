---
phase: "09"
title: Secret Box
priority: p1
status: done
blockedBy: ["phase-04-home"]
---

# Phase 09 — Secret Box

**Goal:** 1 màn Secret Box nhiều state (đóng → mở → reveal quà), animation bằng video MP4 (Media3 ExoPlayer) + ảnh quà PNG. Đồng bộ counter với Profile.

## MoMorph refs
- [iOS] Open secret box: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/kQk65hSYF2
- [iOS] Open secret box- action bấm mở: https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/KUmv414uC9
- [iOS] Standby A (box bung + badge): https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/IXpGakYRm5
- [iOS] Standby B–G (6 quà): _cWAEarZPi · scvV-OQCAJ · wsI6gaO_yc · FvTOS7oCPU · xptNUunBS_ · -LIblaeusT
- Clarifications: ./clarifications.md (Session 2026-06-16 — Phase 09)

## Flow (state machine)
1. **Idle** — video A loop · title "KHÁM PHÁ SECRET BOX CỦA BẠN" / "Click vào box để mở" · "Secret box chưa mở 05"
2. **Opening** (tap khi count>0) — phát video B (tap) → video C (bung + badge tròn)
3. **Reward** — video C end → crossfade PNG quà ngẫu nhiên (1/6) căn vào vị trí vòng tròn · heading "Chúc mừng bạn đã nhận được phần quà từ BTC SAA 2025" · tên quà · nút "Tiếp tục"
4. **Tiếp tục** → về Idle, `unopened--`, `opened++`
5. **count=0** → tap no-op, subtitle báo đã mở hết

## Specs (authoritative, từ MCP)
- Title: Montserrat 700 18/24 `#FFEA9E` center · Subtitle: Montserrat 500 14/20 `#FFFFFF` · Counter "05": Montserrat 700 18/24 `#FFEA9E`
- Box/media region: 320.449×320.449 (1:1) · Panel `#00101A` radius 7.304 gap 24 · bg reuse `bg_home_keyvisual`
- Detail flow: topnav Back + title "Secret Box", KHÔNG bottom nav

## Assets (user export — MCP render fail 500)
- Video (→ `res/raw/`): `secretbox_idle.mp4` (A), `secretbox_tap.mp4` (B), `secretbox_open.mp4` (C)
- PNG quà 320×320 (→ `res/drawable-nodpi/`): khăn, tem, cốc, áo, combo, G (6 ảnh)
- Build PLACEHOLDER swappable trước; swap khi user cung cấp. GiftBoxAnimation fallback frame tĩnh khi thiếu mp4.

## Files to create
- `feature/secretbox/SecretBoxScreen.kt` — màn + state UI (Track A)
- `feature/secretbox/components/GiftBoxAnimation.kt` — ExoPlayer video player + crossfade reward (Track A)
- `feature/secretbox/components/SecretBoxRewardView.kt` — reward PNG + nhãn + Tiếp tục (Track A)
- `feature/secretbox/SecretBoxViewModel.kt` — state machine + open/continue (Track B)
- `data/SecretBoxRepository.kt` — shared in-memory unopened/opened count (Track B)
- `data/SecretBoxMockData.kt` — reward pool (Track B)

## Files to modify
- `app/build.gradle.kts` + `gradle/libs.versions.toml` — thêm Media3 ExoPlayer (Track B)
- `navigation/*` — replace placeholder SECRET_BOX → SecretBoxScreen (Track B)
- Profile ViewModel/MockData — đọc Secret Box count từ SecretBoxRepository (Track B sync)

## Out of scope
- Real reward/unlock API — mock state transitions only
- Awards-tab entry (Phase 10 chưa build)

## Success criteria (DoD)
1. UI pixel-khớp design từng state (verify emulator).
2. Logic: tap mở (count>0) → animation → reveal → Tiếp tục → count-1; count=0 no-op; theo TC_SB_*.
3. Unit tests cho SecretBoxViewModel + SecretBoxRepository; build `assembleDebug` PASS.
4. Counter đồng bộ Profile qua shared repo.

## Shipped (2026-06-16)

**Status:** ✅ Fully implemented, tested, reviewed, verified on emulator.

**Quality Summary:**
- **UI:** Pixel-exact 3-state flow (CLOSED → OPENING → REWARD) verified on emulator
- **Logic:** Full state machine: open box (count > 0) → play tap video → play open video with badge → crossfade to random reward PNG (1 of 6) → "Tiếp tục" button → reset to CLOSED with count—1; count=0 shows "đã mở hết"
- **Assets:** 3× MP4 (Media3 ExoPlayer 1.4.1, res/raw) + 6× reward PNG (res/drawable-nodpi); real assets exported by user
- **Repository:** Shared SecretBoxRepository (unopened seed 5 / opened 25) synced with Profile stats card — verified opening a box decrements unopened and increments opened, Profile auto-updates
- **Tests:** 462 total unit tests PASS (101 new for Secret Box, incl. 5 real ViewModel StateFlow tests); `assembleDebug` PASS
- **Code Quality:** Reviewer concerns H-01 (lifecycle pause), M-01 (reward visibility source), M-02 (VM StateFlow tests) ALL FIXED per reviewer feedback
- **Known:** ~34 MB MP4 inflate APK size (acceptable for internal AIDD demo; flag for Play Store release consideration)
- **Open Gap:** E2E/instrumented tests remain project-wide deferred item (not Secret Box specific)
