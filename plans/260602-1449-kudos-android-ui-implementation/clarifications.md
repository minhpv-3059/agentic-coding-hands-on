# Clarifications & Lessons Learned

## Session 2026-06-03

- Q: Background images từ MoMorph `get_media_files` có đúng visual với design không? → A: **KHÔNG đáng tin cậy**. `get_media_files` trả về raw source asset của Figma node. Khi node dùng CSS `background-position` + `background-size` offset/scale (vd: `-794px -96px / 409%`), raw file ≠ rendered appearance trong frame. **Luôn dùng Figma export trực tiếp cho background fill images.**
- Q: Làm thế nào detect node có CSS transform issue? → A: Dùng `get_node` trên node đó, kiểm tra `styles.background` field. Nếu có offset (vd: `-794px`) hoặc scale > 100% (vd: `409.122%`) → raw asset sẽ sai, cần Figma export.
- Q: Quy trình đúng để lấy background image asset → A: Ưu tiên theo thứ tự: (1) Figma export trực tiếp từ user, (2) `get_frame_image` rồi crop layer BG bằng ImageMagick, (3) `get_figma_image` với nodeId. KHÔNG dùng `get_media_files` cho background fill nodes có CSS offset.
- Q: `bg_login_keyvisual.png` phase 03 đã được fix chưa? → A: Đã fix ngày 2026-06-04. File đúng từ `~/Downloads/bg_login.png` (Figma export) đã thay thế file sai từ MoMorph S3.

## Session 2026-06-04 (Phase 04 — Home)

- Q: Nguồn ảnh background key-visual hero màn Home? → A: User export từ Figma (`~/Downloads/mm_media_bg.png`, 375x812), đã copy vào `drawable-nodpi/bg_home_keyvisual.png` và verify khớp design.
- Q: Countdown timer hiển thị gì khi ngày sự kiện 26/12/2025 đã qua? → A: Đếm ngược tới ngày demo tương lai (live tick). Target = launch time + 20d 20h 20m nên khởi đầu khớp design (20/20/20) và chạy nhảy số mỗi giây.
- Q: Header Home build theo hướng nào? → A: Nâng cấp `KudosTopBar` dùng chung (logo ảnh, cờ VN + dropdown ngôn ngữ, search, chuông + badge chưa đọc).
- Q: Bottom nav dùng tabs nào (contract phase-04 cũ ghi Home/Feed/Profile/Notifications)? → A: Theo design (Critical Rule #1): SAA 2025/Awards/Kudos/Profile — đã có `KudosBottomNav`, giữ nguyên.
- Q: Trạng thái Awards loading/empty/error + redirect API 401/403? → A: Out-of-scope (mock data only). Render loaded state với mock; bỏ qua API states ở phase này.
- Q: Điều hướng tới screen chưa build (Search, Award detail)? → A: Wire tới placeholder route có sẵn; thêm route SEARCH placeholder; Award detail → AWARDS placeholder. Language switcher mở dropdown tại chỗ (reuse pattern Login).

## Session 2026-06-05 (Phase 04 — asset wiring + countdown fix)

- Q: 4 nhóm asset Home (cờ VN, logo Kudos FAB, banner Kudos, trophy) xử lý sao? → A: User export Figma. Đã wire: `ic_vn_flag.xml` (vector rect đỏ + sao vàng), `ic_kudos_logo.xml` (vector, lấy path solid bỏ gradient-multiply), `img_kudos_banner.png`, `img_award_top_talent.png`. Nav bar đổi sang 4 icon SVG thật → vector `ic_nav_{home,awards,kudos,profile}.xml`.
- Q: Countdown digit chưa khớp design (kiểu số)? → A: Design dùng font 7-segment "Digital Numbers" (node 6885:8993). Đã nhúng **DSEG7 Classic** (SIL OFL) tại `res/font/dseg7_classic_regular.ttf` + license ở `assets/`, thêm hiệu ứng ghost-segment (chữ "8" alpha 0.12 phía sau). Ô digit: frosted gradient trắng→trong opacity 0.5 + viền vàng 0.5dp + radius 8 + 32×56dp (node 6885:8992).
- Q: Trophy Top Project / Top Project Leader chưa có ảnh, tự download được không? → A: KHÔNG — `get_figma_image` 500 + `get_media_file` 401, máy không có rasterizer/PIL. Chốt placeholder cho 2 card; defer download sang **Phase 10** (trophy dùng chung Awards tab). Đã note ở `phase-10-awards.md`.

## Asset Verification Protocol (áp dụng từ Phase 04 trở đi)

- Q: Khi nào cần verify asset trước khi dùng? → A: **Luôn luôn** với background fill images (type=RECTANGLE với `background: url(...)`). Sau khi download, dùng `Read` tool để preview image và so sánh với `get_frame_image` của screen.

## Pending Assets — Phase 04 Home (PAUSED 2026-06-04, resume 2026-06-05)

Trạng thái: UI + backend + tích hợp nav-icon **DONE**, `./gradlew assembleDebug` **PASS**. Tạm dừng TRƯỚC Temper/Inspect/Deliver theo yêu cầu user.
Nav-bar icons (4) đã dùng SVG Figma thật (→ `drawable/ic_nav_{home,awards,kudos,profile}.xml`).
Còn 4 nhóm asset màn Home dùng **placeholder** (Figma render API 500 → export trực tiếp từ Figma). Mỗi mục: vị trí UI · Figma node · tên file export · file code cần swap.

1. **Trophy award (×3)** — Section "Hệ thống giải thưởng", ảnh 160×160 ở đỉnh mỗi card cuộn ngang (vòng vàng + tên giải: TOP TALENT / TOP PROJECT…).
   - Figma: `mms_4.2_award list` (6885:9032) → mỗi `mm_media_Picture-Award` 160×160 (`I6885:9033;72:2115`, `I6885:9034;72:2115`, `I6885:9035;72:2115`)
   - Export → `~/Downloads`: `img_award_top_talent.png`, `img_award_top_project.png`, `img_award_3.png` (160×160)
   - Swap: `feature/home/components/AwardCard.kt` → `AwardImagePlaceholder`
2. **Banner Kudos** — Section "Sun* Kudos", banner 335×145 (nền tối + vệt vàng chéo + logo Sun* đỏ + chữ "KUDOS").
   - Figma: `mms_5.2_mm_media_Sunkudos` / group SunKudos (6885:9042)
   - Export → `img_kudos_banner.png` (335×145)
   - Swap: `feature/home/components/HomeKudosSection.kt` → `KudosBannerPlaceholder`
3. **Icon Kudos "S" ở FAB** — Nút nổi góc dưới-phải, icon BÊN PHẢI (logo Sun* Kudos). Icon bút bên trái đã OK.
   - Figma: `MM_MEDIA_IC_Kudos Logo` (`I6885:9058;75:2166`, 24×24)
   - Export → `ic_kudos_logo.svg` (24×24) → convert sang vector drawable
   - Swap: `feature/home/components/HomeFab.kt` → `Icons.Filled.Favorite`
4. **Cờ VN** — Header trên cùng, cờ bên trái cụm "VN ▼" (language switcher).
   - Figma: `MM_MEDIA_IC VN Flag` (`I6885:9057;88:1829;65:2466`, 24×24)
   - Export → `ic_vn_flag.svg` (24×24) → convert sang vector drawable
   - Swap: `ui/components/KudosTopBar.kt` → emoji 🇻🇳

Thứ tự resume: export → swap code các file trên → `./gradlew assembleDebug` → Temper (tester) → Inspect (reviewer) → Deliver (PM + docs + commit + journal).
SVG→vector drawable: dùng lại script Python đã convert nav icons (trích path `d` + viewBox → `<vector><path android:fillColor android:pathData/>`).

## Session 2026-06-08 (Phase 05 — Kudos Feed)

- Q: Mức độ tương tác (thả tim, lọc Hashtag/Phòng ban, carousel, tap hashtag) với ràng buộc mock data? → A: Đầy đủ + lưu trạng thái — tương tác cục bộ trên mock state, persist liked-kudos + recent-search qua DataStore; bỏ behavior cần backend (ngày x2, admin config, đồng bộ DB realtime).
- Q: Filter Hashtag/Phòng ban dùng bottom sheet (specs) hay dropdown (design)? → A: Dropdown overlay neo dưới nút theo design (Critical Rule #1 — design authoritative, có 2 frame dropdown riêng).
- Q: Spotlight Board làm tới đâu? → A: Network chart tương tác đầy đủ — nodes/edges + pan/zoom + live search highlight node, trên mock graph data.
- Q: Màn Search wire từ đâu và scope? → A: Icon search top bar → KudosSearchScreen; recent list (nút X xóa) + filter kết quả khi gõ chạy mock state + persist recent; tap kết quả → profile placeholder.
- Q: Màn All Kudos (j_a2GQWKDJ) routing? → A: Thêm route kudos/all + AllKudosScreen.kt riêng (app bar back + title), reuse KudosCard; link "View all Kudos" điều hướng tới đó.
- Q: Điều hướng tới màn chưa build (Send/Profile/Secret Box)? → A: Theo precedent Phase 04 — wire tới placeholder route: Send→KUDOS_SEND, sender/recipient/Sunner→PROFILE_USER, Secret Box→SECRET_BOX.
- Q: Behavior đã chốt từ specs/test cases (không cần hỏi)? → A: Carousel = top 5 theo heart desc, reset card 1 khi đổi filter; filter Hashtag AND Phòng ban, lọc cả carousel + feed; tap hashtag set filter; empty text "Hiện tại chưa có Kudos nào." / "Chưa có dữ liệu"; star badge 1@10/2@20/3@50 kudos; like 1/user, sender không like bài mình; Copy Link toast "Link copied — ready to share!".

## Session 2026-06-12 (Phase 06 — Send Kudos)

- Q: Submit "Gửi đi" thành công → kudo mới có vào feed không? → A: Prepend vào shared in-memory repository (object KudosRepository giữ MutableStateFlow seed từ KudosMockData.kudos). Refactor KudosFeedViewModel đọc từ repo flow thay vì list tĩnh; SendKudosViewModel.submit() prepend kudo mới → hiện đầu feed khi pop back.
- Q: Toolbar định dạng (Bold/Italic/Strike/List/Link/Quote) hoạt động thế nào? → A: Functional đầy đủ — áp dụng định dạng thật vào message bằng AnnotatedString/rich-text state trong Compose (BasicTextField + visual transformation hoặc rich-text composable thủ công).
- Q: Đính kèm ảnh (Image, tối đa 5)? → A: Android Photo Picker thật (ActivityResultContracts.PickMultipleVisualMedia, maxItems=5), hiển thị thumbnail từ URI; xoá từng ảnh bằng nút X.
- Q: Field "Danh hiệu" (optional) kiểu nhập gì? → A: Dropdown chọn option (user chốt — pattern dropdown như recipient/hashtag, ref frame dropdown aKWA2klsnt). Single-select từ list danh hiệu mock (vd: "Người truyền động lực cho tôi", "Đồng đội tin cậy"…); optional; chevron mở overlay neo dưới field. Danh hiệu hiển thị tại spotlight chart theo helper text.
- Q: Người nhận (B.1/B.2)? → A: Dropdown search single-select từ KudosMockData.searchableUsers, item = avatar + tên + code (vd CECV1); required; filter theo text gõ; chevron + overlay (ref frame 5MU728Tjck).
- Q: Hashtag (E.1/E.2)? → A: Multi-select tối đa 5 từ danh sách hashtag (mở rộng KudosMockData.hashtags để khớp design: BE OPTIMISTIC, WASSHOI, BE A TEAM, High-perorming, BE PROFESSIONAL, THINK OUTSIDE THE BOX, GET RISKY, GO FAST); required; chip có nút X; dropdown checkmark item đã chọn (ref frame aKWA2klsnt).
- Q: Ẩn danh (B.7/B.8/G)? → A: Checkbox "Gửi lời cám ơn và ghi nhận ẩn danh"; khi tick hiện field "Nickname ẩn danh" (default placeholder). Kudo tạo ra set isAnonymous=true + anonymousAlias=nickname → ẩn sender trong card feed (đã có sẵn ở KudoModels).
- Q: Validation (màn Lỗi chưa điền hết 0le8xKnFE_)? → A: required = Người nhận + Lời nhắn + Hashtag. Thiếu → banner đỏ "Bạn cần điền đủ Người nhận, Lời nhắn gửi và Hashtag để gửi Kudos!" (node 6885:10124) phía trên cụm nút; highlight field thiếu.
- Q: Entry tới Community Standards (xms7csmDhD)? → A: Link "Tiêu chuẩn cộng đồng" trong khu vực Danh hiệu (B.5 Awards Information Navigation Links) → navigate route KUDOS_COMMUNITY_STANDARDS. Màn static: logo banner ROOT FURTHER (file_or_image, dùng placeholder/text nếu render fail) + section Tiêu chuẩn cộng đồng (10 tiêu chí) + Tiêu chuẩn bảo mật (specs A/B/C node 6885:10829/10848/10854).
- Q: Entry tới Send Kudos? → A: Thay PlaceholderScreen route KUDOS_SEND bằng SendKudosScreen; FAB feed + Home onSendKudos đã wire tới KUDOS_SEND từ phase 04/05.
- Q: Nút Huỷ? → A: Navigate back, không lưu. Nút Gửi đi success → toast "Đã gửi Kudos!" + prepend + pop back to feed.
- Q: Track A/B split? → A: Track A = 2 background implementer agents (Agent 1: SendKudosScreen + sub-components UI presentational; Agent 2: CommunityStandardsScreen). Track B = SendKudosViewModel + KudosRepository + feed refactor + nav routes + integrate (rich-text, photo picker, validation, submit).

## Session 2026-06-15 (Phase 07 — Profile)

- Q: 6 huy hiệu award màu trên Profile người khác (REVIVAL, TOUCH OF LIGHT, STAY GOLD, FLOW TO HORIZON, BEYOND THE BOUNDARY, ROOT FUTHER) xử lý ảnh thế nào? → A: User export ảnh thật từ Figma. Build với AwardBadgePlaceholder swappable (layout pixel-chuẩn) trong khi chờ; swap sang drawable thật khi user cung cấp.
- Q: Nút CTA "Gửi lời cảm ơn và ghi nhận tới {tên}" trên Profile người khác điều hướng đâu? → A: Mở Send Kudos + điền sẵn người nhận. Thêm optional arg recipient vào route KUDOS_SEND (kudos/send?recipient={userId}); SendKudosViewModel pre-select người nhận từ id.
- Q: Bộ lọc KUDOS dropdown Đã nhận (5) / Đã gửi (5) trên Profile của tôi hoạt động ra sao? → A: Lọc thật trên mock — Đã nhận = kudos recipient là tôi, Đã gửi = kudos sender là tôi. Reuse pattern dropdown overlay neo dưới nút (như feed/send).
- Q: Phạm vi wiring điều hướng? → A: Wire cả hai — thay placeholder PROFILE_ME → MyProfileScreen, PROFILE_USER → UserProfileScreen(userId); tra user theo id từ mock, id lạ → fallback an toàn. Tap user ở Feed/Search mở UserProfileScreen.
- Q: Avatar hero + icon collection 6 vòng tròn tối (màn của tôi)? → A: Avatar = KudoAvatar placeholder màu theo tên (ảnh thật không đáng tin — tiền lệ). Icon collection của tôi = 6 vòng tròn tối rỗng đúng design (không nhãn, không ảnh).
- Q: Giá trị stats (màn của tôi)? → A: Lấy đúng design: Kudos nhận 5, Kudos gửi 25, Tim nhận 25, Secret Box đã mở 25, Secret Box chưa mở 25.
- Q: Hero background key-visual? → A: Reuse drawable/bg_home_keyvisual.png (cùng key-visual xoáy màu như Home).

## Session 2026-06-16 (Phase 07 — Profile refinements)

- Q: Ảnh 6 huy hiệu award + 2 rank pill (rising/legend hero) đã export? → A: User đã export vào ~/Downloads (ic_badge_*.png 64×64, rising_hero/legend_hero.png 122×26). Copy vào res/drawable-nodpi: img_badge_*.png (6) + img_rank_{legend,rising}_hero.png (2). Wire: AwardBadge.icon = R.drawable.img_badge_*; rank pill = component RankBadge (map "Legend Hero"/"Rising Hero" → img_rank_*).
- Q: Rank pill ảnh dùng ở đâu? → A: 2 chỗ — pill cạnh unit_name (thay Box+border+Text cũ) VÀ overlay ở đáy avatar. Áp dụng cả ProfileHeader (own) + UserProfileHeader (other).
- Q: Identity user đăng nhập? → A: Tạo data/CurrentUser.kt (Phan Văn Minh, id u1, CEVC1, Legend Hero) làm single source of truth. Login (LoginViewModel→AndroidViewModel) persist KudosPreferences.setCurrentUser(u1); MyProfileViewModel đọc currentUserId resolve user. ProfileMockData/SendKudosMockData/feed CURRENT_USER_ID đều trỏ về CurrentUser (fix lệch identity 3 nơi). Feed character "Huỳnh Dương Xuân Nhật" đổi id u1→u6 để u1 chỉ thuộc current user.
- Q: Flow own vs other profile khác nhau? → A: OVERRIDE design (design chrome 2 màn giống nhau). Other profile = nút back ở header (KudosTopBar onBack) + ẩn bottom nav (detail flow, back về màn trước vd search). Own profile = giữ bottom nav, không back. UserProfileScreen bỏ selectedTab/onTabSelected, thêm onBack.
- Q: Background bị nền đen che (header + vùng badge)? → A: Vẽ bg_home_keyvisual full-bleed (fillMaxSize) thay vì dải 288dp; KudosTopBar thêm showScrim (profile=false) để key-visual hiện sau header. Swirl xuyên suốt header→hero→badge, fade tối cho content dưới.

## Session 2026-06-16 (Phase 08 — Notifications)

- Q: Title màn 'Notifications' (design EN) hay 'Thông báo' (test case VN)? → A: Localize theo ngôn ngữ — VN → "Thông báo", EN → "Notifications". Title driven by AppLanguage (default VN nên mặc định "Thông báo"); when(language) trong NotificationsViewModel/Screen.
- Q: Badge chưa đọc nằm đâu + có bottom nav không? → A: Bám design — màn Notifications có nút back, KHÔNG bottom nav (detail flow). Badge chưa đọc ở chuông top-bar (Home/Feed). 'Đánh dấu đọc tất cả' + tap item cập nhật unread count chung → badge chuông Home/Feed giảm theo. OVERRIDE contract cũ của phase ("bottom nav badge").
- Q: Tap từng loại noti điều hướng đâu (nhiều màn chưa build)? → A: Wire màn đã có + placeholder. Kudos received/Heart received → View kudo (KUDOS_VIEW); Level up & Badge collected → Profile của tôi (PROFILE_ME); Content hidden → View kudo + inline link "Tiêu chuẩn cộng đồng ↗" → COMMUNITY_STANDARDS; Secret Box → placeholder SECRET_BOX (Phase 09); Admin Review → placeholder (ngoài scope app). Theo tiền lệ Phase 04/05.
- Q: Seed trạng thái chưa đọc + persist? → A: Đúng design — seed 7 item theo design, chỉ item đầu chưa đọc (1 chấm đỏ, khớp 100% design). Badge chuông Home/Feed lấy từ repo = 1 (đổi từ mock cứng 3). Shared object NotificationsRepository in-memory (như KudosRepository), KHÔNG DataStore.
- Q: Thời gian hiển thị (relative time)? → A: Dùng nguyên chuỗi tĩnh từ design (mock): "15 phút trước", "1 giờ trước", "1 ngày trước", "1 tháng trước". Không tính từ timestamp thật (mock data, KISS).
- Q: Empty state khi không có noti? → A: Không có trong design → out-of-scope phase này (seed luôn đủ 7 item).
- Q: Track A/B split? → A: Track A = 1 background implementer agent (NotificationsScreen + components UI presentational từ Figma, pixel-perfect). Track B = NotificationsViewModel + NotificationsRepository + nav routes + wire badge Home/Feed về repo + integrate read-state/navigation.
- Q: 7 icon noti (Figma media-raster, render API 500/401) xử lý sao? → A: User export SVG trực tiếp (ic_receive_noti/heart_plus/gift/star/warning/notes/flag). Convert → vector drawable res/drawable/ic_*.xml giữ nguyên fillColor: receive #3F95FF, heart #EE5850, gift #FFEA9E, star #00FBFF, warning #FFD900, notes #2AD32D, flag #B51097. NotificationIconMapper trả @DrawableRes; NotificationItem render Icon(painter, tint=Unspecified). Gỡ dep material-icons-extended (project vốn cố ý tránh — RichTextToolbar), xoá 6 token KudosNoti* dead. Verify emulator: 7 icon render đúng. Residual icon ĐÃ ĐÓNG → UI ~100%.
- Q: Chấm badge chuông ở header (Home/Feed) màu gì? → A: ĐỎ (KudosAccentRed #D4271D), không phải gold/kem (trước trông trắng) — nhất quán với unread dot B.1.3 của list noti. Sửa KudosTopBar Badge containerColor.
