# Clarifications & Lessons Learned

## Session 2026-06-03

- Q: Background images từ MoMorph `get_media_files` có đúng visual với design không? → A: **KHÔNG đáng tin cậy**. `get_media_files` trả về raw source asset của Figma node. Khi node dùng CSS `background-position` + `background-size` offset/scale (vd: `-794px -96px / 409%`), raw file ≠ rendered appearance trong frame. **Luôn dùng Figma export trực tiếp cho background fill images.**
- Q: Làm thế nào detect node có CSS transform issue? → A: Dùng `get_node` trên node đó, kiểm tra `styles.background` field. Nếu có offset (vd: `-794px`) hoặc scale > 100% (vd: `409.122%`) → raw asset sẽ sai, cần Figma export.
- Q: Quy trình đúng để lấy background image asset → A: Ưu tiên theo thứ tự: (1) Figma export trực tiếp từ user, (2) `get_frame_image` rồi crop layer BG bằng ImageMagick, (3) `get_figma_image` với nodeId. KHÔNG dùng `get_media_files` cho background fill nodes có CSS offset.
- Q: `bg_login_keyvisual.png` phase 03 đã được fix chưa? → A: Đã fix ngày 2026-06-04. File đúng từ `~/Downloads/bg_login.png` (Figma export) đã thay thế file sai từ MoMorph S3.

## Asset Verification Protocol (áp dụng từ Phase 04 trở đi)

- Q: Khi nào cần verify asset trước khi dùng? → A: **Luôn luôn** với background fill images (type=RECTANGLE với `background: url(...)`). Sau khi download, dùng `Read` tool để preview image và so sánh với `get_frame_image` của screen.
