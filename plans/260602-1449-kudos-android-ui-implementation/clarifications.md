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

## Session 2026-06-16 (Phase 09 — Secret Box)

- Q: 9 màn design Secret Box là animation frames hay reward tiers? → A: 1 MÀN nhiều state (không navigate 9 màn riêng): đóng/idle → tap → mở (animation) → reveal quà ngẫu nhiên (1/6) → Tiếp tục về đóng, count-1. State do ViewModel điều khiển.
- Q: Có animation không, dựng bằng gì? → A: Có. 3 màn đầu là VIDEO MP4 (user cung cấp), 6 màn quà là PNG (user cung cấp). Phát mp4 bằng Media3 ExoPlayer (bọc AndroidView trong Compose). Project chưa có Media3 → thêm dependency.
- Q: Mapping A/B/C video → screen? → A: A=kQk65hSYF2 (idle-loop box+sparkle), B=KUmv414uC9 (bấm mở/tap), C=IXpGakYRm5 (box bung + badge tròn TOUCH OF LIGHT). 3 FILE riêng: A loop khi chờ; tap → phát B rồi C → reveal PNG quà.
- Q: 6 ảnh quà PNG (320×320)? → A: B=_cWAEarZPi Khăn, C=scvV-OQCAJ Tem, D=wsI6gaO_yc Cốc, E=FvTOS7oCPU Áo Burberry, F=xptNUunBS_ Cốc+tem combo, G=-LIblaeusT (render fail — user xác nhận sản phẩm khi gửi asset).
- Q: Hình tròn ở video C dùng làm gì? → A: Là VỊ TRÍ ảnh quà PNG xuất hiện sau khi video mở xong. Căn PNG quà vào đúng vị trí/kích thước vòng tròn; crossfade liền mạch video C (320×320) → PNG quà (320×320) cùng box region.
- Q: Nút đóng quà (test case có "Tiếp tục" nhưng design không có)? → A: THÊM nút "Tiếp tục" theo test case (DoD: logic đúng spec). Dưới nhãn quà, dùng token Button sẵn có. Tap → về trạng thái đóng + count-1.
- Q: Prize chọn thế nào khi mở? → A: Random 1 trong pool quà mock mỗi lần mở (mock state, KISS). Không phụ thuộc backend.
- Q: Counter "05" vs Profile "Secret Box chưa mở 25 / đã mở 25"? → A: Tạo shared SecretBoxRepository in-memory (pattern như KudosRepository/NotificationsRepository). Màn Secret Box authoritative cho counter: unopened seed=5 ("05" design); opened seed=25 (Profile design). Mở box → unopened--, opened++. Profile đọc cùng repo → đồng bộ toàn app (Profile "chưa mở" đổi 25→5 theo repo).
- Q: count=0 thì sao? → A: Tap KHÔNG mở; subtitle đổi báo đã mở hết (vd "Bạn đã mở hết Secret Box"). Theo TC_SB_FUN_003.
- Q: Bottom nav hay back? → A: Detail flow — topnav có Back, title "Secret Box", KHÔNG bottom nav (tiền lệ Notifications/other-profile).
- Q: Entry point + nav? → A: Replace placeholder route SECRET_BOX bằng SecretBoxScreen. Noti "Secret Box" (đã trỏ SECRET_BOX từ phase 08) tự dùng màn thật. Awards tab entry (TC_SB_ACC_001) là Phase 10 chưa build → defer.
- Q: Asset cung cấp sao để không block? → A: Build placeholder SWAPPABLE trước (layout + animation pixel-chuẩn), user export 3 mp4 (→ res/raw) + 6 png (→ res/drawable-nodpi) song song → swap sau (tiền lệ phase 04/07). GiftBoxAnimation fallback frame tĩnh khi chưa có mp4 để app vẫn chạy.
- Q: Spec chính xác (title/subtitle/counter/panel)? → A: Title "KHÁM PHÁ SECRET BOX CỦA BẠN" Montserrat 700 18/24 #FFEA9E căn giữa; subtitle "Click vào box để mở" Montserrat 500 14/20 #FFFFFF; counter "05" Montserrat 700 18/24 #FFEA9E; box image 320.449×320.449 (1:1); panel nền #00101A radius 7.304 gap 24 padding 13.694/7.304; reward heading "Chúc mừng bạn đã nhận được phần quà từ BTC SAA 2025"; bg reuse keyvisual (bg_home_keyvisual) sau topnav.
- Q: Track A/B split? → A: Track A = 1 background implementer (SecretBoxScreen + GiftBoxAnimation ExoPlayer player + state UI closed/opening/reward + Tiếp tục, placeholder assets, pixel-perfect). Track B = Media3 dependency + res/raw + SecretBoxViewModel + SecretBoxRepository + SecretBoxMockData (reward pool) + nav route SECRET_BOX + Profile sync + integrate.

## Session 2026-06-17 (Phase 10 — Awards)

- Q: 6 màn Award (MVP/Best Manager/Signature Creator/Top Project/Top Project Leader/Top Talent) là 6 màn riêng hay 1 màn? → A: 1 MÀN duy nhất = tab Awards ở bottom nav, có dropdown đổi loại giải → Award Information Block cập nhật theo (title, badge name, mô tả, số lượng+đơn vị, giá trị giải). Khớp contract "single parametric screen". Layout 6 màn giống hệt, chỉ khác data.
- Q: Awards là detail-flow (back, không bottom nav) hay tab chính? → A: Tab chính. Route "awards" đã được KudosApp Scaffold map sang BottomNavTab.Awards → bottom nav global tự hiện + Awards active. Màn render giống Home (content scroll + KudosTopBar overlay), KHÔNG tự vẽ bottom nav.
- Q: Giải mặc định khi mở + pre-select từ Home? → A: Pre-select theo award bấm 'Chi tiết' ở Home; mở từ tab Awards (không qua Home) → mặc định MVP. Thêm arg award-id tùy chọn vào route awards (awards?award={id}).
- Q: Nguồn ảnh cúp 6 giải (vòng vàng glow + tên giải)? → A: Trích trực tiếp từ MoMorph S3 (get_media_files đã hoạt động lại 2026-06-17; get_figma_image vẫn 500). Cúp = 2 lớp: vòng+khung kính+bệ 160×160 (DÙNG CHUNG mọi giải, mix-blend screen + glow #FAE287) + raster tên giải (vd 'MVP' 56×25) overlay giữa. Ghép bằng ImageMagick → 6 ảnh img_award_<key>.png 160×160. get_figma_image lỗi nhưng get_media_files trả URL S3 raw đúng visual (đã verify MVP).
- Q: Nút 'Chi tiết ↗' cuối màn (mục Phong trào ghi nhận / Sun* Kudos) đi đâu? → A: Màn Thể lệ (NavRoutes.RULES) — Phase 11 chưa build nên wire tới placeholder RULES.
- Q: Wire ảnh cúp thật vào 3 card Awards ở Home? → A: Có — sau khi trích asset, set mockAwards[].image cho Top Talent/Top Project/Top Project Leader (Top Talent đã có; thay placeholder 🏆 của 2 card còn lại).
- Q: Dropdown chọn giải dùng pattern nào? → A: Overlay neo dưới field theo tiền lệ feed/send/profile (không bottom sheet). Liệt kê đủ 6 giải; chọn → đóng overlay + cập nhật Info Block.
- Q: Tên file màn? → A: Contract ghi 'AwardDetailScreen.kt' nhưng thực chất là tab Awards 1 màn → đặt AwardsScreen.kt cho đúng ngữ nghĩa (ghi chú deviation ở report).
- Q: Track A/B split? → A: Track A = 1 background implementer (AwardsScreen + components presentational pixel-perfect: hero KV, award highlight header + dropdown selector, trophy card, info block stat rows, kudos movement section, Chi tiết button; + trích & ghép 6 ảnh cúp vào res/drawable-nodpi). Track B = AwardData (enum + content 6 giải verbatim từ specs), AwardViewModel, AwardsNavigation route wrapper, NavRoutes/AppNavGraph (awards?award arg), Home trophy wiring, integrate.

## Session 2026-06-17 (Phase 11 — Supporting Screens)

- Q: Language dropdown — i18n "UI toggle only" (contract) hay full runtime i18n (test case LANGDD FUN_007/008 đổi text Login tức thì)? → A: FULL runtime i18n thật. Xây hạ tầng global: enum AppLanguage chuyển khỏi LoginViewModel ra shared, LanguageManager (object + StateFlow, persist qua KudosPreferences/DataStore), LocalAppLanguage CompositionLocal provide ở app root, override LocalConfiguration+LocalContext locale (KHÔNG cần AppCompat, KHÔNG recreate Activity) để stringResource resolve live theo values + values-en. Switch hoạt động app-wide thật.
- Q: Phạm vi migrate text? → A: Infra đầy đủ + migrate text NGAY cho Login + Rules + Access denied + Not Found (các màn có specs/test-case localization). 8 màn cũ (feed/send/profile/awards/noti/home/secretbox) giữ hardcode VN, migrate dần sau (ngoài scope phase 11) — tránh regression toàn app (~500+ string). Switch vẫn reactive đúng; màn chưa migrate hiển thị VN ở cả 2 ngôn ngữ tới khi migrate.
- Q: Cơ chế switching? → A: Compose locale override — App root đọc LanguageManager.languageFlow, bọc CompositionLocalProvider(LocalConfiguration provides cfg(locale), LocalContext provides ctx.createConfigurationContext(cfg)); stringResource tự lấy đúng values/values-en. Default VN. Persist DataStore qua KudosPreferences.setLanguage/languageFlow.
- Q: LanguageDropdown component? → A: Tách ui/components/LanguageDropdown.kt presentational dùng chung (selected AppLanguage + expanded + onToggle/onDismiss + onSelect). Refactor Login + KudosTopBar (Home/Feed) dùng chung; onSelect gọi LanguageManager.setLanguage. 2 option VN (cờ VN + "VN") / EN (cờ Anh + "EN"), default VN, VN đầu list. Cần asset cờ Anh (ic_uk_flag) — trích S3 hoặc vector.
- Q: Asset thiếu (robot "404" + pill New/Super Hero)? → A: Trích từ MoMorph S3 (get_media_files OK từ 2026-06-17). Robot illustration: Not Found node 6885:9487 (mms_3.1) — DÙNG CHUNG cho cả 403 (node 6885:9529 mms_2.1 cùng "mm_media_Not Found"). Hero pill 4.2: trích New Hero + Super Hero từ node 6885:10905; Rising/Legend đã có (img_rank_rising/legend_hero). 6 icon 4.3 tái dùng img_badge_* (revival/touch_of_light/stay_gold/flow_to_horizon/beyond_boundary/root_futher). Kudos Quốc Dân "Root Further" reuse img_root_further. Fallback placeholder swappable nếu node fail.
- Q: Màn lỗi 403/404 trigger thế nào (app mock, không backend)? → A: Route đã có (error/403, error/404). Wire 404 làm fallback NavHost cho route không tồn tại (composable route catch-all / unknown deep-link). Thêm link demo nhỏ để mở được 403+404 (vd trong màn Thể lệ hoặc debug entry). CTA "Go back to Home" → popUpTo(HOME). Back arrow + swipe-back → popBackStack.
- Q: Layout 403 vs 404? → A: Cùng scaffold ErrorScreen(title, message, illustration, ctaText) tham số hoá (DRY). 403 title "Access Denied" (giữ nguyên design), 404 title "NOT FOUND", cùng robot illustration + mô tả "The resource you're looking for doesn't exist or has been removed." + nút "Go back to Home". Nền navy phẳng (#0B1A24-ish, KHÔNG key-visual), back arrow top-left, không bottom nav.
- Q: Màn Thể lệ chrome + entry? → A: Detail flow — KudosTopBar back arrow + title "Thể lệ" trên nền key-visual xoáy (reuse bg_home_keyvisual, showScrim=false như Profile), nội dung cuộn, KHÔNG bottom nav. Entry: thêm 1 link/icon "Thể lệ" ở khu Feed/Home + giữ Awards "Chi tiết ↗" (Phase 10). Nút "Viết Kudos" → KUDOS_SEND; nút "Đóng" + back arrow → popBackStack.
- Q: Track A/B split? → A: Track A = 3 background implementer (1: RulesScreen + sub-sections pixel-perfect; 2: ErrorScreen shared + AccessDeniedScreen + NotFoundScreen; 3: LanguageDropdown component) — build hardcode VN từ design trước, mỗi agent tự build được. Track B = trích assets S3, i18n infra (AppLanguage shared + LanguageManager + prefs.language + LocalAppLanguage + locale override + strings.xml vn/en cho 4 màn in-scope), nav wiring (replace placeholder RULES/403/404, 404 fallback, demo entry, Rules feed entry), refactor Login/KudosTopBar dùng LanguageDropdown, swap asset placeholder, integrate + migrate string 4 màn sang stringResource.
- Q: Scope của i18n sau khi user test? → A: Mở rộng FULL-APP — migrate text mọi màn chính (Home/Feed/Awards/Profile/Noti/SecretBox/Send + KudosCard + filters) sang string resources per-feature (strings_<feature>.xml + values-en); KudosTopBar tự mở dropdown panel + drive global LanguageManager; fix crash Send (ContextThemeWrapper). Mock data giữ nguyên (không dịch). Build PASS + 26 unit-test files green + tất cả màn main + shared components xác nhận i18n live VN↔EN on emulator.
