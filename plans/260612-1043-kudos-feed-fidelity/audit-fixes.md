# Kudos Feed — Fidelity Fix Spec (grouped by file)

Generated from per-section audit (build vs design fO0Kt19sZZ). Severity: H>M>L. [ASSET]=needs Figma export, skip code.

## `KudosFeedScreen.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/feed/KudosFeedScreen.kt`)
- **(LOW) Top inter-section gap may differ subtly from design**
  - current: KudosFeedScreen.kt uses verticalArrangement = spacedBy(24.dp) between major sections plus a 4dp leading Spacer; SectionHeader and its content use spacedBy(12.dp).
  - expected: Design section header sits at consistent 20px horizontal inset (matches build's padding(horizontal = 20.dp)). Vertical 24dp section gap is a reasonable match; only verify the eyebrow-to-card rhythm matches the ~12px header internal gaps seen in design.
  - FIX: No change required if visually matching; keep spacedBy(24.dp) between sections and spacedBy(12.dp) inside each section. Horizontal 20.dp inset already matches design startX=20.

## `SpotlightMockData.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/feed/SpotlightMockData.kt`)
- **(HIGH) Names overlap into an unreadable cramped blob (density + radius too high)**
  - current: 84 nodes placed on a golden-angle spiral with radius capped at 0.5 (sqrt(...)*0.5), so every name is crammed into the centre half of the 220dp box. Names overlap heavily and are illegible — the single worst defect in the section.
  - expected: Design (mms_B.7, panel 318x179px) scatters the 7 unique names across the FULL panel width/height with clear separation — names are sparse, dimly lit and individually readable, like a starfield. No two names overlap; the cloud fills the whole panel, not a central clump.
  - FIX: In SpotlightMockData.buildCloud(): reduce count from 84 to ~28-36, and let radius span the full panel — use `val radius = sqrt((i + 0.5f) / count) * 0.95f` (was *0.5f). This spreads names edge-to-edge. Then clamp x/y into the panel with a small inset (e.g. coerceIn(0.06f, 0.94f)) so words don't clip the rounded border. Lower count is the primary fix to stop overlap.
- **(LOW) Missing faint activity-ticker line near bottom of panel**
  - current: No ticker text is rendered.
  - expected: Design includes a low-opacity (opacity 0.3) tiny (4px) Montserrat-bold line near the bottom, e.g. '08:30PM Nguyễn Bá Chức đã nhận được một Kudos mới', repeated faintly as ambient activity.
  - FIX: Optionally add one faint Text overlay aligned BottomStart inside the Box: a sample 'HH:mmPM <name> đã nhận được một Kudos mới' string at ~8-9sp, KudosWhite.copy(alpha=0.30f), FontWeight.Bold. Low priority / nice-to-have for ambience.

## `AllKudosSection.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/feed/components/AllKudosSection.kt`)
- **(HIGH) Secret Box button is OUTSIDE the panel; should be INSIDE it**
  - current: KudosSecondaryButton được render NGOÀI StatsBlock, là phần tử riêng bên dưới panel (cách 16dp).
  - expected: Trong design, Button 'Mở Secret Box' (node 6885:9254) là phần tử con CUỐI CÙNG của frame 'Nội dung' (6885:9224) NẰM BÊN TRONG panel bo viền (6885:9223), cách row trên 12px, vẫn nằm trong padding 12px của panel.
  - FIX: Chuyển nút vào trong StatsBlock: thêm tham số onOpenSecretBox vào StatsBlock và render nút như phần tử cuối của Column (sau 2 row Secret Box), bỏ KudosSecondaryButton khỏi AllKudosSection. Dùng nút fill gold (xem finding kế).
- **(HIGH) Secret Box button dùng style Secondary (viền + nền mờ) thay vì Primary gold đặc**
  - current: KudosSecondaryButton: nền gold mờ (0x1AFFEA9E ~10%), chữ gold, có border KudosBorder, shape pill (RoundedCornerShape(50)), cao 44dp.
  - expected: Button node 6885:9254: nền GOLD ĐẶC rgba(255,234,158,1) = #FFEA9E, chữ tối (dark), KHÔNG border, bo góc 4px (không phải pill), cao 40px, padding 12px, căn giữa.
  - FIX: Thay KudosSecondaryButton bằng một nút nền gold đặc: dùng Material3 Button với containerColor=KudosGold, contentColor=KudosDarkText, shape=RoundedCornerShape(4.dp), height(40.dp), fillMaxWidth. (KudosPrimaryButton hiện là pill 56dp nên cần biến thể shape=4dp/height=40dp hoặc Button tùy biến tại chỗ.)
- **(LOW) Feed card vertical spacing 12dp vs design**
  - current: Feed KudosCard list uses Arrangement.spacedBy(12.dp) — matches design gap of 12px (node 6891:15986). No change needed for gap itself, but the gift panel above and the feed list below are spaced by the AllKudosSection outer spacedBy(16.dp).
  - expected: Design 'Danh sách Kudo' gap is 12px (correct). The block-level spacing between the gift panel and the first feed card and between feed list and View-all should remain ~16dp.
  - FIX: No change to the 12.dp card gap (it matches). Verify only that the panel container's own padding (added in finding #1) doesn't compound with the 16dp outer spacing creating an oversized gap; if so, this is acceptable.
- **(LOW) 'View all Kudos' link styling vs design button**
  - current: Bottom link is a centered Row of gold Text 'View all Kudos' + ArrowForward icon, full-width clickable, no fixed size.
  - expected: Design node 6891:15987 is a Button instance: fixed 136x32dp, centered, gap 8px, label + 24dp trailing icon, border-radius 4dp. Visually it is a compact centered text+arrow link (no fill in this state), which matches the build closely.
  - FIX: Optional: constrain the link to wrap-content centered (Modifier.align/CenterHorizontally on a sized Row) instead of fillMaxWidth so the tap target matches the 136x32 design footprint; bump icon size to ~20-24dp and gap to 8.dp to match. Low priority — appearance already close.

## `FeedHeroBanner.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/feed/components/FeedHeroBanner.kt`)
- **(HIGH) Tagline 'Hệ thống ghi nhận và cảm ơn' sai màu (trắng vs vàng)**
  - current: Text dùng color = KudosWhite (trắng) trong FeedHeroBanner.kt.
  - expected: Node 6885:9068: backgroundColor/fill = rgba(255,234,158,1) = vàng #FFEA9E (KudosGold), Montserrat 14sp weight 500.
  - FIX: Đổi `color = KudosWhite` của Text tagline → `color = KudosGold`.
- **(MEDIUM) [ASSET] Wordmark 'KUDOS' là text vàng thay vì artwork brand**
  - current: Text 'KUDOS' style displayLarge màu KudosGold, letterSpacing 4sp.
  - expected: Node 6885:9077 KUDOS là vector group 163x39 (logo wordmark Sun* Kudos, không phải font hệ thống).
  - FIX: Cần export ảnh wordmark KUDOS từ Figma và dùng Image thay Text. Tạm thời text vàng là xấp xỉ chấp nhận được. (MoMorph image API 403/500 → chưa tải được)
- **(MEDIUM) [ASSET] Hero key-visual dùng ảnh Home thay artwork riêng của Feed**
  - current: painterResource(R.drawable.bg_home_keyvisual) — dùng lại KV của Home.
  - expected: Node mm_media_bg / MM_MEDIA_Keyvisual BG (6885:9061): artwork dây vàng/xanh đặc trưng màn Kudos Feed.
  - FIX: Export KV của screen Kudos Feed và thay drawable. Hiện bg_home_keyvisual là xấp xỉ. (MoMorph image API 403/500 → chưa tải được)
- **(LOW) Logo mark KUDOS sai kích thước/tỉ lệ**
  - current: Image ic_kudos_logo size(44.dp) — vuông 44x44.
  - expected: Node 6885:9071 (mm_media_logo): 49x38 (rộng hơn cao), gap với wordmark 9px.
  - FIX: Đổi `Modifier.size(44.dp)` → `Modifier.size(width = 49.dp, height = 38.dp)`; đổi Row spacedBy(8.dp) → spacedBy(9.dp).

## `GiftRecipientsSection.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/feed/components/GiftRecipientsSection.kt`)
- **(HIGH) Gift panel missing bordered container (border + background + radius)**
  - current: GiftRecipientsSection is a plain transparent Column — no border, no background, no rounded panel. Title + rows sit directly on the dark page background (visible in build-08/build-09).
  - expected: Design node 6885:9255 is a rounded panel: border 0.794px solid #998C5F (KudosBorder), background #00070C (Details-Container-2, distinct near-black), border-radius 8dp, inner padding 12dp.
  - FIX: Wrap the outer Column in a panel: add a private val PanelShape = RoundedCornerShape(8.dp); on the Column modifier add .clip(PanelShape).background(Color(0xFF00070C)).border(1.dp, KudosBorder, PanelShape).padding(12.dp). Remove the caller's reliance on outer spacing. Define the #00070C as a theme color (e.g. KudosContainerDeep) in Color.kt rather than inline.
- **(MEDIUM) Panel title not centered**
  - current: Title '10 SUNNER NHẬN QUÀ MỚI NHẤT' is left-aligned (Column default start alignment in GiftRecipientsSection).
  - expected: In the design (frame image + build-07) the title is horizontally centered within the panel.
  - FIX: On the title Text add modifier = Modifier.fillMaxWidth() and textAlign = TextAlign.Center (import androidx.compose.ui.text.style.TextAlign). Keep the recipient rows left-aligned.
- **(MEDIUM) Recipient name colour is white, design uses gold**
  - current: GiftRecipientRow name Text uses color = KudosWhite (#FFFFFF).
  - expected: Design renders the recipient names in gold #FFEA9E (KudosGold) — matching the panel title accent; the white sub-line is only the gift description.
  - FIX: Change the name Text color from KudosWhite to KudosGold (line ~86). Keep giftDescription on KudosGray.
- **(LOW) Recipient name weight too light**
  - current: Name uses typography.labelMedium (12sp, Medium).
  - expected: Design names read as bold/semibold ~13-14sp (heavier than the gift sub-line).
  - FIX: Apply fontWeight = FontWeight.SemiBold on the name Text (style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)), or bump to labelLarge.
- **(LOW) Recipient avatar size larger than design**
  - current: KudoAvatar size = 36.dp in GiftRecipientRow.
  - expected: Design gift-row avatars are small (~24-28dp) — clearly smaller than the 36dp avatars used in the KudosCard participant rows.
  - FIX: Reduce KudoAvatar size to 24.dp (or 28.dp) in GiftRecipientRow; reduce the Spacer width from 10.dp to ~8.dp to match the panel's 8px gap.
- **(LOW) Inter-row spacing slightly over design**
  - current: Rows spaced with Arrangement.spacedBy(10.dp) plus each row adds vertical padding 4.dp (~18dp total gap).
  - expected: Design panel uses 8px gap between items.
  - FIX: Set the inner Column spacedBy(8.dp) and drop the per-row .padding(vertical = 4.dp) (or reduce to 2.dp) so the visual gap matches 8px.

## `HighlightCarousel.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/feed/components/HighlightCarousel.kt`)
- **(LOW) Pager '2/5' text size/weight smaller than design**
  - current: Pager Text uses labelMedium (~14sp) color KudosWhite — size roughly OK but weight likely Medium.
  - expected: Design pager node mms_B.5.2: fontSize 14px, fontWeight 700 (bold), letterSpacing 0.25px.
  - FIX: Set the pager Text to style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.25.sp). Color KudosWhite is fine.
- **(LOW) Pager nav buttons are bordered circular buttons; design 'mms_B.5_slide' uses plain gold chevrons**
  - current: CarouselNavButton draws a 32dp circle with KudosContainer fill + 1dp border around each chevron, flanking the '2/5' text.
  - expected: Design pager (mms_B.5_slide) shows two plain chevron icons (IC) directly flanking the '2/5' label — no filled circle / bordered button background.
  - FIX: Simplify CarouselNavButton to a plain Icon (gold when enabled, gray when disabled) without the .background()/.border()/CircleShape so it matches the borderless chevrons in the design pager row.

## `SendKudosPrompt.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/feed/components/SendKudosPrompt.kt`)
- **(HIGH) Send prompt shape sai: build là pill bo tròn hết, design là chữ nhật bo nhẹ**
  - current: RoundedCornerShape(24.dp) → viên thuốc bo tròn hoàn toàn (PromptShape trong SendKudosPrompt.kt).
  - expected: Node mms_A.1_Button (6885:9083): border-radius 4px — chữ nhật bo góc nhẹ, cao 40px.
  - FIX: Đổi `private val PromptShape = RoundedCornerShape(24.dp)` → `RoundedCornerShape(4.dp)`.
- **(HIGH) Send prompt: chữ placeholder sai màu và sai canh lề**
  - current: Label màu KudosWhite.copy(alpha=0.6f) (xám mờ), canh trái (Row start), icon nằm sát trái.
  - expected: Node I6885:9083;28:2014: màu trắng đặc rgba(255,255,255,1), textAlign center; trong design icon + label nằm giữa pill (width label 278px trên nền 335px, canh giữa).
  - FIX: Đổi label `color = KudosWhite` (bỏ alpha 0.6f). Thêm `horizontalArrangement = Arrangement.Center` cho Row để icon+text canh giữa.
- **(MEDIUM) Send prompt: màu nền và viền chưa đúng tone vàng**
  - current: background = KudosContainer; border = KudosBorder.copy(alpha=0.4f).
  - expected: Node 6885:9083: background rgba(255,234,158,0.10) (gold 10%), border 1px solid #998C5F (gold-brown đặc, không alpha).
  - FIX: background(KudosGold.copy(alpha = 0.10f)); border(1.dp, Color(0xFF998C5F), PromptShape) (bỏ alpha 0.4f).
- **(MEDIUM) Send prompt cao hơn design (padding dọc 14dp)**
  - current: padding(horizontal = 20.dp, vertical = 14.dp) → pill cao ~46dp+.
  - expected: Node 6885:9083: height 40px, padding 10px (đều), text lineHeight 20.
  - FIX: Đổi `padding(horizontal = 20.dp, vertical = 14.dp)` → `padding(10.dp)` và cân nhắc thêm `.height(40.dp)` để khớp design.

## `SpotlightBoard.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/feed/components/SpotlightBoard.kt`)
- **(HIGH) Names overlap into an unreadable cramped blob (density + radius too high)**
  - current: 84 nodes placed on a golden-angle spiral with radius capped at 0.5 (sqrt(...)*0.5), so every name is crammed into the centre half of the 220dp box. Names overlap heavily and are illegible — the single worst defect in the section.
  - expected: Design (mms_B.7, panel 318x179px) scatters the 7 unique names across the FULL panel width/height with clear separation — names are sparse, dimly lit and individually readable, like a starfield. No two names overlap; the cloud fills the whole panel, not a central clump.
  - FIX: In SpotlightMockData.buildCloud(): reduce count from 84 to ~28-36, and let radius span the full panel — use `val radius = sqrt((i + 0.5f) / count) * 0.95f` (was *0.5f). This spreads names edge-to-edge. Then clamp x/y into the panel with a small inset (e.g. coerceIn(0.06f, 0.94f)) so words don't clip the rounded border. Lower count is the primary fix to stop overlap.
- **(HIGH) Word font sizes far too large for the panel**
  - current: layoutWords() uses fontSize = (9f + node.weight*5f).sp → roughly 11.5–16.5sp. In a 220dp-tall box these glyphs are enormous relative to the panel and force overlap.
  - expected: Design name texts are tiny: fontSize ~1.93px on the 318px design canvas (~9–10sp scaled, but most words read smaller). Sizes vary subtly (weighting), not 9–16sp. The cloud reads as many small dim labels, not a few big ones.
  - FIX: In layoutWords() change fontSize to a smaller range, e.g. fontSize = (6f + node.weight * 4f).sp (≈7.5–11.5sp). Combined with fewer nodes and larger radius this eliminates overlap and matches the sparse-small-label look.
- **(HIGH) "388 KUDOS" wrong colour/weight/size/position — overlaps names**
  - current: Rendered KudosGold, FontWeight.Bold, titleLarge (~22sp), aligned TopStart with 12dp padding. It is large, gold, top-left and sits ON TOP of the name cloud, overlapping names (clearly visible in build-07.png).
  - expected: Design node mms_B.7.1_388 KUDOS: small (fontSize 10.4px), Montserrat weight 400 (regular, NOT bold), colour WHITE, positioned near TOP-CENTRE of the panel (startX≈156 of 318 width). It is a quiet header label, not a hero gold title, and does not collide with names.
  - FIX: Change the Text overlay (lines 104-111): style = MaterialTheme.typography.labelMedium (≈12sp) with FontWeight.Normal, color = KudosWhite (not KudosGold). Use Modifier.align(Alignment.TopCenter) with a small top padding (~10dp) instead of TopStart. Keep it as a non-zoomed overlay.
- **(HIGH) Search field placed full-width BELOW panel instead of as an in-panel pill**
  - current: A standard full-width OutlinedTextField with leading search icon sits as a separate row below the 220dp panel (visible in build-07.png), with a 'Không tìm thấy sunner phù hợp' supporting text below it.
  - expected: Design node mms_B.7.3_Tìm kiếm sunner is a tiny rounded PILL INSIDE the panel, anchored top-left (startX≈27, width≈63px, height≈11px, border-radius 13.4px, semi-transparent gold fill rgba(255,234,158,0.10), gold border #998C5F). It overlays the cloud in the top-left corner, it is not a separate full-width field beneath the panel.
  - FIX: Move the search input INSIDE the Box (as an overlay aligned TopStart with ~10dp padding) and restyle it as a compact pill: a Row with RoundedCornerShape(50), background KudosGold.copy(alpha=0.10f), 1dp KudosBorder border, small search icon + BasicTextField/placeholder at ~11-12sp. Remove the full-width OutlinedTextField and the Spacer(8.dp) below the panel. (Note: code may intentionally keep a usable search affordance; at minimum reposition/restyle to a pill overlay to match design.)
- **(MEDIUM) Panel background is flat dark fill — missing warm keyvisual glow + dark gradient**
  - current: Canvas draws a flat KudosContainer2 rectangle as the board background. No glow, no gradient, no image — a plain dark panel.
  - expected: Design background (Root further mo rong 1) is a keyvisual image covered by a linear-gradient rgba(0,0,0,0.70) → dark, producing a warm orange/teal glow (the hero swirl) bleeding in from the right edge over a near-black base. The panel has a subtle gold border (#998C5F, ~0.3px) and 8px corner radius.
  - FIX: Replace drawRect(KudosContainer2) with the keyvisual: draw bg_home_keyvisual scaled to cover, then overlay a Brush.verticalGradient/solid Color.Black.copy(alpha=0.70f) scrim so names stay legible. Add a 1dp border in Color(0xFF998C5F) on the clipped Box (clip already RoundedCornerShape(12.dp); design uses 8px). Glow can approximate the design even with the placeholder keyvisual.
- **(MEDIUM) Panel height too tall vs design proportion**
  - current: Box height fixed at 220.dp for the full screen width (~360dp) → aspect ratio ~1.6:1.
  - expected: Design panel is 318x159px (≈2:1, wider/shorter). The cloud area reads as a wide letterbox band, not a tall block.
  - FIX: Reduce height to ~180.dp (or use Modifier.aspectRatio(2f)) so the panel matches the ~2:1 design proportion. Corner radius 8.dp to match design (currently 12.dp).
- **(LOW) Missing faint activity-ticker line near bottom of panel**
  - current: No ticker text is rendered.
  - expected: Design includes a low-opacity (opacity 0.3) tiny (4px) Montserrat-bold line near the bottom, e.g. '08:30PM Nguyễn Bá Chức đã nhận được một Kudos mới', repeated faintly as ambient activity.
  - FIX: Optionally add one faint Text overlay aligned BottomStart inside the Box: a sample 'HH:mmPM <name> đã nhận được một Kudos mới' string at ~8-9sp, KudosWhite.copy(alpha=0.30f), FontWeight.Bold. Low priority / nice-to-have for ambience.
- **(LOW) Word colours: low-opacity white range is plausible but verify against dim design**
  - current: Non-matched words use KudosWhite.copy(alpha = 0.35f + weight*0.30f) → 0.35–0.65 alpha. Matched words turn KudosGold bold.
  - expected: Design names are white at varying dimness over the dark/glow background; matched-name gold highlight is a reasonable interaction (not visible in static design). Current alpha range is acceptable but the heavy overlap (other findings) makes them look muddy rather than dim.
  - FIX: Keep the white + alpha approach; once density/size/radius are fixed the dim-white scatter will read correctly. Consider widening alpha slightly (e.g. 0.30f + weight*0.45f) for more depth. No structural change needed.

## `StatsBlock.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/feed/components/StatsBlock.kt`)
- **(HIGH) Secret Box button is OUTSIDE the panel; should be INSIDE it**
  - current: KudosSecondaryButton được render NGOÀI StatsBlock, là phần tử riêng bên dưới panel (cách 16dp).
  - expected: Trong design, Button 'Mở Secret Box' (node 6885:9254) là phần tử con CUỐI CÙNG của frame 'Nội dung' (6885:9224) NẰM BÊN TRONG panel bo viền (6885:9223), cách row trên 12px, vẫn nằm trong padding 12px của panel.
  - FIX: Chuyển nút vào trong StatsBlock: thêm tham số onOpenSecretBox vào StatsBlock và render nút như phần tử cuối của Column (sau 2 row Secret Box), bỏ KudosSecondaryButton khỏi AllKudosSection. Dùng nút fill gold (xem finding kế).
- **(HIGH) Màu giá trị (số) sai: trắng thay vì gold**
  - current: Text value dùng color = KudosWhite (#FFFFFF).
  - expected: Value 'Highlight Số' (6885:9229 / 6885:9242): màu GOLD #FFEA9E (backgroundColor rgba(255,234,158,1) = fill chữ), Montserrat 700.
  - FIX: Trong StatRow đổi Text(value) color = KudosWhite → KudosGold.
- **(MEDIUM) Màu nền panel sai (#101417 thay vì #00070C)**
  - current: background(KudosContainer) = #101417.
  - expected: Panel 6885:9223 background var(--Details-Container-2) = #00070C (KudosContainer2 đã có sẵn trong theme).
  - FIX: Đổi .background(KudosContainer) → .background(KudosContainer2). Import KudosContainer2.
- **(MEDIUM) Viền panel quá mờ (alpha 0.3) so với design viền gold đặc**
  - current: .border(1.dp, KudosBorder.copy(alpha = 0.3f), BlockShape) — viền vàng-olive rất nhạt.
  - expected: Panel 6885:9223 border 0.794px solid #998C5F (KudosBorder full opacity, alpha=1).
  - FIX: Bỏ .copy(alpha = 0.3f): .border(1.dp, KudosBorder, BlockShape).
- **(MEDIUM) Màu nhãn (label) sai: xám thay vì trắng**
  - current: Text label dùng color = KudosGray (#999999).
  - expected: Label '...nhận được:' (6885:9228): màu TRẮNG #FFFFFF, Montserrat 14px, weight 300 (Light), letterSpacing 0.25px.
  - FIX: Đổi label color = KudosGray → KudosWhite. Cân nhắc fontWeight = FontWeight.Light (300) cho đúng (hiện bodySmall mặc định Normal).
- **(LOW) Cỡ chữ value lớn hơn design (titleMedium ~16sp vs 14sp)**
  - current: Text value dùng style = MaterialTheme.typography.titleMedium (~16sp).
  - expected: Value 14px / lineHeight 20px / weight 700 / letterSpacing 0.25px. Label & value cùng cỡ 14px, chỉ khác weight & màu.
  - FIX: Đổi value style sang bodySmall.copy(fontWeight = FontWeight.Bold) hoặc fontSize=14.sp, fontWeight=Bold, letterSpacing=0.25.sp để khớp 14px/700.
- **(LOW) Padding panel và khoảng cách row chưa khớp**
  - current: padding(16.dp); verticalArrangement spacedBy(10.dp).
  - expected: Panel padding 12px (6885:9223); gap giữa các row 12px (frame Nội dung 6885:9224 gap=12px). Gap nội bộ label↔value là 6.35px (space-between).
  - FIX: Đổi .padding(16.dp) → .padding(12.dp); spacedBy(10.dp) → spacedBy(12.dp).
- **(LOW) [ASSET] Badge x2 / tim: design có icon lửa, build chỉ có chữ 'x2'**
  - current: Row 'Số tim' hiển thị nhãn + badge text 'x2' (nền gold mờ) + value. Không có biểu tượng lửa.
  - expected: Row 'Số tim' (6885:9235) có group icon lửa 'mms_S_Group 435' (24x29px) đứng trước value, kèm 'x2' chồng trên ngọn lửa (badge lửa cam/đỏ), không phải pill chữ vàng.
  - FIX: Thay badge text 'x2' bằng một icon lửa (emoji 🔥 hoặc asset xuất từ Figma) với 'x2' overlay đặt sát trước value. Nếu giữ text, dùng nền cam/đỏ thay vì gold mờ. Cần asset để khớp tuyệt đối.

## `SectionHeader.kt`  (`app/src/main/java/com/sun/kudos_demo/feature/home/components/SectionHeader.kt`)
- **(HIGH) SectionHeader divider: short gold bar vs full-width faint line**
  - current: SectionHeader.kt renders a 40dp wide x 2dp tall gold bar (KudosBorder #998C5F) under the eyebrow.
  - expected: Design 'Rectangle 26' is a FULL-WIDTH line (336px = full content width) x 1px, colored dark gray #2E3940 (KudosDivider). It is a subtle separator, not a gold accent bar.
  - FIX: Replace the Box width/height/color: use Modifier.fillMaxWidth().height(1.dp).background(KudosDivider) instead of .width(40.dp).height(2.dp).background(KudosBorder). Import fillMaxWidth and KudosDivider; drop KudosBorder import.

## `DepartmentFilterDropdown.kt`  (`app/src/main/java/com/sun/kudos_demo/ui/components/DepartmentFilterDropdown.kt`)
- **(HIGH) Filter pills are fully-rounded; design uses 4px rounded-rect**
  - current: Both HashtagFilterDropdown and DepartmentFilterDropdown use DropdownPillShape/DeptPillShape = RoundedCornerShape(50) (fully rounded capsule), vertical padding 6dp, no fixed height.
  - expected: Design dropdown node 6885:9088: border-radius 4px (slightly rounded rectangle), fixed height 40px, padding 8px, border 1px #998C5F, bg rgba(255,234,158,0.10), label + chevron justified space-between, width ~129px.
  - FIX: Change DropdownPillShape/DeptPillShape to RoundedCornerShape(4.dp). Add .height(40.dp), .padding(horizontal = 8.dp), and use Arrangement.SpaceBetween with a fillMaxWidth/fixed-width Row so label is left and chevron right (currently they sit adjacent with a 4dp Spacer).
- **(MEDIUM) Filter pill label color/weight differs from design**
  - current: Inactive label color = KudosGray, style labelMedium. Border inactive = KudosBorder.copy(alpha=0.6f).
  - expected: Design label node: fontSize 14px, weight 400, fill near-white (#FFFFFF), Montserrat; border #998C5F (KudosBorder solid, no 0.6 alpha).
  - FIX: Set inactive labelColor = KudosWhite (or a light gray ~#E0E0E0) instead of KudosGray, and inactive pillBorderColor = KudosBorder (drop .copy(alpha=0.6f)). Keep labelMedium (14sp) which matches.

## `HashtagFilterDropdown.kt`  (`app/src/main/java/com/sun/kudos_demo/ui/components/HashtagFilterDropdown.kt`)
- **(HIGH) Filter pills are fully-rounded; design uses 4px rounded-rect**
  - current: Both HashtagFilterDropdown and DepartmentFilterDropdown use DropdownPillShape/DeptPillShape = RoundedCornerShape(50) (fully rounded capsule), vertical padding 6dp, no fixed height.
  - expected: Design dropdown node 6885:9088: border-radius 4px (slightly rounded rectangle), fixed height 40px, padding 8px, border 1px #998C5F, bg rgba(255,234,158,0.10), label + chevron justified space-between, width ~129px.
  - FIX: Change DropdownPillShape/DeptPillShape to RoundedCornerShape(4.dp). Add .height(40.dp), .padding(horizontal = 8.dp), and use Arrangement.SpaceBetween with a fillMaxWidth/fixed-width Row so label is left and chevron right (currently they sit adjacent with a 4dp Spacer).
- **(MEDIUM) Filter pill label color/weight differs from design**
  - current: Inactive label color = KudosGray, style labelMedium. Border inactive = KudosBorder.copy(alpha=0.6f).
  - expected: Design label node: fontSize 14px, weight 400, fill near-white (#FFFFFF), Montserrat; border #998C5F (KudosBorder solid, no 0.6 alpha).
  - FIX: Set inactive labelColor = KudosWhite (or a light gray ~#E0E0E0) instead of KudosGray, and inactive pillBorderColor = KudosBorder (drop .copy(alpha=0.6f)). Keep labelMedium (14sp) which matches.

## `KudosBottomNav.kt`  (`app/src/main/java/com/sun/kudos_demo/ui/components/KudosBottomNav.kt`)
- **(HIGH) Bottom nav background color & shape: opaque dark slab vs translucent gold rounded bar**
  - current: KudosBottomNav.kt uses Material3 NavigationBar with containerColor = KudosContainer (#101417, fully opaque), square top corners, no blur.
  - expected: Design nav bar container is rgba(255,234,158,0.15) (translucent warm-gold tint over the dark bg) with border-radius 20px 20px 0 0 (rounded top corners) and backdrop-filter blur(20px). It reads as a frosted gold-tinted floating bar, not a solid black slab.
  - FIX: Wrap content in a Surface/Box with clip(RoundedCornerShape(topStart=20.dp, topEnd=20.dp)) and background = KudosGold.copy(alpha = 0.15f) layered over KudosBackground; set NavigationBar containerColor = Color.Transparent. Add a faint top corner radius. (True blur/backdrop-filter is not natively available in Compose; approximate with the translucent gold tint over the dark background.)
- **(MEDIUM) Active tab shows Material pill indicator not present in design**
  - current: NavigationBarItem uses default Material3 pill indicator behind the active icon (indicatorColor = KudosDivider), producing a visible rounded rectangle behind the Kudos icon.
  - expected: Design active state is ONLY the gold-colored icon + gold label (#FFEA9E). There is no filled pill/indicator shape behind the active icon.
  - FIX: Set indicatorColor = Color.Transparent in NavigationBarItemDefaults.colors (or build a custom Row of tab items without the Material indicator). Keep selectedIconColor/selectedTextColor = KudosGold.
- **(LOW) Nav label typography slightly undersized**
  - current: Nav labels use MaterialTheme.typography.labelSmall = 10sp Medium.
  - expected: Design tab label is 12px Montserrat weight 400 (regular). Build is 2sp smaller and a touch heavier.
  - FIX: Use labelMedium (12sp) or an explicit TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal) for the nav label Text.

## `KudosButton.kt`  (`app/src/main/java/com/sun/kudos_demo/ui/components/KudosButton.kt`)
- **(HIGH) Secret Box button dùng style Secondary (viền + nền mờ) thay vì Primary gold đặc**
  - current: KudosSecondaryButton: nền gold mờ (0x1AFFEA9E ~10%), chữ gold, có border KudosBorder, shape pill (RoundedCornerShape(50)), cao 44dp.
  - expected: Button node 6885:9254: nền GOLD ĐẶC rgba(255,234,158,1) = #FFEA9E, chữ tối (dark), KHÔNG border, bo góc 4px (không phải pill), cao 40px, padding 12px, căn giữa.
  - FIX: Thay KudosSecondaryButton bằng một nút nền gold đặc: dùng Material3 Button với containerColor=KudosGold, contentColor=KudosDarkText, shape=RoundedCornerShape(4.dp), height(40.dp), fillMaxWidth. (KudosPrimaryButton hiện là pill 56dp nên cần biến thể shape=4dp/height=40dp hoặc Button tùy biến tại chỗ.)

## `KudosCard.kt`  (`app/src/main/java/com/sun/kudos_demo/ui/components/KudosCard.kt`)
- **(HIGH) Card TITLE rendered gold + too large; design is dark, small, centered, bold**
  - current: kudo.title ("IDOL GIỚI TRẺ") uses style titleMedium (~16sp, weight 500) and color KudosGold, left-aligned.
  - expected: Design node IDOL GIỚI TRẺ: fontSize 10px, fontWeight 700, color #00101A (dark, same as message), textAlign center, letterSpacing 0.23px. It is a small bold centered dark caption, NOT a large gold heading.
  - FIX: In KudosCardContent, change the title Text to: style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.23.sp), color = KudosDarkText, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(). Remove color = KudosGold.
- **(MEDIUM) Card border uses neutral KudosBorder instead of gold #FFEA9E**
  - current: KudosCard border = 1.dp KudosBorder (#998C5F neutral gold-gray).
  - expected: Design card border = 1px solid #FFEA9E (Colors-Primary, bright gold) on bg #FFF8E1.
  - FIX: Change .border(1.dp, KudosBorder, CardShape) to use a bright-gold token (#FFEA9E). Add/use a KudosCardBorder = Color(0xFFFFEA9E) and reference it. Card padding 8/8/12/8 vs design uniform 8px 12px — acceptable.
- **(MEDIUM) [ASSET] Avatar size: build 36dp vs design 24px**
  - current: KudosParticipantSlot uses KudoAvatar(size = 36.dp).
  - expected: Design avatar (mm_media_img inside Infor) is 24x24px.
  - FIX: Change KudoAvatar size from 36.dp to 24.dp in KudosParticipantSlot so sender/recipient avatars match design scale and the row stays compact.
- **(LOW) Message font weight/size slightly off; design 10px/14px line-height justified**
  - current: Message uses bodySmall (typically ~12-14sp) with default alignment.
  - expected: Design message node: fontSize 10px, weight 400, lineHeight 14px, color #00101A, textAlign justified. Truncation '...' matches (3 lines compact).
  - FIX: Use style = MaterialTheme.typography.labelSmall.copy(lineHeight = 14.sp), color = KudosDarkText, textAlign = TextAlign.Justify for the message Text. Keep maxLines 3 (compact) / Ellipsis.
- **(LOW) timeRange color: design gray #999999, build faint token**
  - current: timeRange Text color = KudosCardFaint, style labelSmall.
  - expected: Design time node mms_B.4.1: fontSize 10px, weight 500, color #999999 (mid gray).
  - FIX: Verify KudosCardFaint ≈ #999999; if it is lighter, set timeRange color to a #999999 gray token. Bump weight to Medium (labelSmall is usually Medium already — confirm).
- **(LOW) Hashtag row: design is a single truncated text line, build is per-chip clickable Texts**
  - current: KudosHashtagRow renders each tag as a separate '#tag' Text with 4dp spacing, takes first 5 then '…'.
  - expected: Design hashtag node renders one continuous 10px red (#D4271D) string '#Dedicated #Inspring #Dedicated #Inspring #Dedicated #Inspring...' truncated with ellipsis on the line (single TEXT node, weight 400). Color matches.
  - FIX: Acceptable functionally (per-chip enables tap-to-filter). To match visually, constrain the Row to one line with maxLines=1 behavior or render as a single soft-wrap-disabled line; reduce inter-tag spacing to ~2dp and ensure font size 10sp. Low priority — keep clickable chips.
- **(LOW) Faint gold message container box not rendered**
  - current: Message text sits directly in the content Column with no background or border.
  - expected: Design wraps message in Frame 425: border 0.463px #FFEA9E, bg rgba(255,234,158,0.40), border-radius 5.55px, padding 4px. (Visually near-invisible on #FFF8E1 card, sub-pixel border.)
  - FIX: Optional: wrap the message Text in a Box with RoundedCornerShape(6.dp), background KudosGold.copy(alpha=0.40f) over the card, 1.dp gold border, padding 4.dp. Low impact since it is barely visible; include only if pixel-matching.
- **(LOW) Recipient star (hoa thị) rendered as ★ glyph next to name; design has no inline star in highlight card**
  - current: KudosParticipantSlot appends '★'.repeat(starLevel) in KudosGold beside the recipient name when recipientKudosCount qualifies.
  - expected: Highlight card 'trao nhận' row in design shows avatar + name + code + Legend/Rising badge only — no separate inline gold star glyph beside the name (the badge pill conveys hero level).
  - FIX: Confirm star requirement against spec (TC_FUN_006 mentions hoa thị). If the star is a real spec requirement keep it; otherwise the design region does not show it — consider gating it off for the compact highlight card to match the design.
- **(LOW) Send/forward arrow between sender and recipient: design uses a paper-plane/send glyph, build uses ArrowForward**
  - current: Center icon between participants = Icons.AutoMirrored.Filled.ArrowForward (a plain right arrow).
  - expected: Design 'mms_B.3.4_Icon mũi tên' renders as a send/forward (paper-plane-like) glyph in the rendered frame between the two avatars.
  - FIX: If a paper-plane is desired, swap to Icons.AutoMirrored.Filled.Send. Plain arrow is an acceptable approximation; low priority.
- **(LOW) Feed card title colour gold on cream — verify contrast vs design**
  - current: KudosCard content title uses KudosGold (#FFEA9E) on cream #FFF8E1 card (KudosCardContent).
  - expected: Design card titles ('IDOL GIỚI TRẺ') appear as a darker gold/amber for legibility on the cream card. Light gold on cream is low-contrast.
  - FIX: Verify the design title token; if it is a deeper amber (not the same #FFEA9E used on dark surfaces) introduce a KudosCardTitleGold (e.g. ~#B8860B/darker) and use it for KudosCardContent title instead of KudosGold. Confirm against node 6885:8424 title style before changing.

## `KudosTopBar.kt`  (`app/src/main/java/com/sun/kudos_demo/ui/components/KudosTopBar.kt`)
- **(LOW) Language pill: gap và padding chưa khớp**
  - current: Row ngôn ngữ: horizontalArrangement spacedBy(4.dp), padding(horizontal = 4.dp); icon arrow 16dp.
  - expected: Node I6885:9065;88:1829: 90x32, gap 8px, padding 4px 0 4px 8px, radius 4px.
  - FIX: Đổi spacedBy(4.dp) → spacedBy(8.dp); padding(start = 8.dp, top = 4.dp, bottom = 4.dp). (chênh lệch nhỏ, ưu tiên thấp)
- **(LOW) Khoảng cách giữa các action icon (lang/search/bell) hơi hẹp**
  - current: Row actions: horizontalArrangement = spacedBy(4.dp).
  - expected: Node actions (I6885:9065;88:1828): gap 10px giữa language, search, notification.
  - FIX: Đổi spacedBy(4.dp) ở Row actions → spacedBy(10.dp) (lưu ý IconButton 40dp đã có khoảng đệm sẵn, có thể giảm bù).
- **(LOW) Bell icon màu vàng thay vì trắng**
  - current: Notification Icon tint = KudosGold (vàng).
  - expected: Trong design các icon header (search, chuông) đều trắng; chỉ badge dot là đỏ/cam. Chuông không phải vàng.
  - FIX: Đổi tint của notification Icon từ KudosGold → KudosWhite cho đồng bộ với search. Badge giữ màu cảnh báo (đỏ/cam).

## `Type.kt`  (`app/src/main/java/com/sun/kudos_demo/ui/theme/Type.kt`)
- **(LOW) Font family is system default, design uses Montserrat**
  - current: Type.kt and SectionHeader use FontFamily.Default (system sans, Roboto on Android) for the eyebrow, gold title, and nav labels.
  - expected: Design specifies fontFamily 'Montserrat' for the eyebrow (12px/400), section title (22px/500), and nav labels (12px/400).
  - FIX: Bundle Montserrat (res/font) and set a Montserrat FontFamily on the relevant Typography styles (bodySmall, headlineMedium, labelMedium). Affects all sections globally, so coordinate as a theme-level change.
