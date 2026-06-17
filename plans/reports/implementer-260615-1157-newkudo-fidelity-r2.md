# Implementer Report — New Kudo Fidelity R2
Date: 2026-06-15

## Issues Fixed

### Issue 1: Nickname (ẩn danh) field clips text

**Authoritative design value (node 6885:9914):**
- height: 40dp, border: `#998C5F` (KudosBorder), background: `#FFF`, border-radius: 3.574px ≈ 4dp

**Fix (`AnonymousSection.kt`):**
Replaced Material3 `TextField` (min-height 56dp causes clipping at 40dp) with the `BasicTextField`-inside-`Box` pattern from `RecipientField.kt`:
```
Box(height=40dp, border, background, padding=11dp horizontal, contentAlignment=CenterStart) {
    BasicTextField(singleLine=true, decorationBox shows placeholder when empty)
}
```

**Emulator validation:** Checked anonymous checkbox → "DoremonTestNicknameLong" rendered fully at correct height, no clipping visible.

---

### Issue 2: "Tiêu chuẩn cộng đồng" link — wrong position + wrong color

**Authoritative design values:**
- Position: node 6885:9931 — last child of toolbar row 6885:9918, `flex: 1 0 0`, right-aligned (justifyContent: center inside Button frame at x=176..343)
- Color: node 6885:9933 `backgroundColor: rgba(228, 96, 96, 1)` — NOT KudosError (#B3261E), NOT KudosAccentRed (#D4271D)
- Font: `fontSize=10px`, `fontWeight=400`, `textAlign=center`
- Text: "Tiêu chuẩn cộng đồng"

**New token added (`Color.kt`):**
```kotlin
val KudosLinkRed = Color(0xFFE46060)  // node 6885:9933: rgba(228,96,96,1)
```

**Fix route:**
- `DanhHieuField.kt`: removed `onCommunityStandardsClick` parameter + the inline `buildAnnotatedString` link from helper text. Helper now only shows "Ví dụ: …" plain gray text.
- `RichTextToolbar.kt`: added `onCommunityStandardsClick` parameter. Link renders on the RIGHT of the toolbar row after the format buttons (`Spacer(weight=1f)` + red `Text` with underline + `fontSize=10.sp`).
- `MessageField.kt`: added `onCommunityStandardsClick` parameter, threaded to `RichTextToolbar`.
- `SendKudosFormContent.kt`: removed `onCommunityStandardsClick` from `DanhHieuField` call, added it to `MessageField` call.

**Emulator validation:** Link visible in red on the right of the toolbar row; tapping it navigates to Community Standards screen.

---

### Issue 3: Markdown toolbar — missing vertical dividers

**Authoritative design values (nodes 6885:9919..9929):**
Each toolbar button cell has `border: 0.447px solid #998C5F` on all sides. Adjacent cells share a border edge — this creates a visual 1dp KudosBorder vertical divider between buttons.

**Fix (`RichTextToolbar.kt`):**
After each format button `Box(size=24dp)`, inserted a 1dp×fillMaxHeight `Box(background=KudosBorder)` divider. This replicates the shared-border appearance. The last divider falls between the icon buttons and the text link. Total toolbar height stays at 24dp.

**Emulator validation:** Visible dividers between B/I/S/1./🔗/❝ buttons in screenshot `/tmp/04-send-form.png`.

---

### Issue 4: Community Standards screen — background missing + text layout off

**Authoritative design values:**
- Background: node 6885:10808 (`mm_media_MM_MEDIA_Keyvisual BG`) — same keyvisual as `SendKudosScreen` and `HomeScreen` → `bg_home_keyvisual` drawable with `ContentScale.Crop`
- Content container (6885:10832): `padding 20dp horizontal`, `gap 12dp`; `borderRadius 16dp` (top of content area)
- Divider (6885:10853): `rgba(46,57,64,1)` = KudosDivider, 1dp
- Section title (6885:10849, 6885:10855): `fontSize=18sp`, `fontWeight=700`, `lineHeight=24sp`, color=KudosGold
- Bold intro (6885:10851): `fontSize=14sp`, `fontWeight=700`, `lineHeight=20sp`, `letterSpacing=0.25sp`, color=KudosGold
- Body text (6885:10852, 6885:10857): `fontSize=14sp`, `fontWeight=400`, `lineHeight=20sp`, `letterSpacing=0.25sp`, color=KudosWhite
- List gap (node 6885:10850 Infor frame): `gap=16dp` between violation items (was 8dp)
- Security Infor gap (6885:10856): `gap=4dp` between sub-items
- Contact (6885:10859): `fontSize=14sp`, `fontWeight=700`, color=KudosGold

**Fix (`CommunityStandardsScreen.kt`):**
- Wrapped entire screen in `Box` with keyvisual `Image(bg_home_keyvisual, ContentScale.Crop, matchParentSize)` behind the content Column
- Removed `KudosBackground` plain color fill
- `LazyColumn` spacing uses 12dp gaps (matching design `gap=12dp`)
- Removed the `KudosContainer` dark placeholder box from `RootFurtherBanner` — `img_root_further` now renders on transparent over the keyvisual

**Fix (`CommunityStandardsContent.kt`):**
- All text nodes now use explicit `fontSize`, `fontWeight`, `lineHeight`, `letterSpacing` per design values (replaced `MaterialTheme.typography.*` style lookups which scaled incorrectly)
- Violation item gap changed from 8dp → 16dp
- Security sub-item gap changed from 8dp → 4dp (matches Infor frame gap=4dp)

**Emulator validation:** Keyvisual visible behind content, gold titles, white body text, correct spacing in screenshots `/tmp/07-community-standards.png` and `/tmp/08-community-standards-scrolled.png`.

**Note:** `RootFurtherBanner` (node 6885:10830) uses `img_root_further.png` which is a wordmark crop. The Figma asset has CSS background-position offset that requires the original bitmap export; the wordmark render is an acceptable placeholder as noted in the existing TODO comment.

---

## Files Modified

| File | Change |
|------|--------|
| `ui/theme/Color.kt` | Added `KudosLinkRed = Color(0xFFE46060)` |
| `feature/send/components/AnonymousSection.kt` | Replace Material3 TextField with BasicTextField+Box pattern |
| `feature/send/components/DanhHieuField.kt` | Remove `onCommunityStandardsClick` param + link from helper |
| `feature/send/components/RichTextToolbar.kt` | Add dividers between buttons + `onCommunityStandardsClick` + red link |
| `feature/send/components/MessageField.kt` | Thread `onCommunityStandardsClick` to RichTextToolbar |
| `feature/send/components/SendKudosFormContent.kt` | Reroute click from DanhHieuField → MessageField |
| `feature/send/CommunityStandardsScreen.kt` | Add keyvisual BG, fix layout spacing |
| `feature/send/components/CommunityStandardsContent.kt` | Fix typography sizes/weights/spacing per design |

## Tests

- `./gradlew assembleDebug`: BUILD SUCCESSFUL
- `./gradlew testDebugUnitTest`: BUILD SUCCESSFUL (186 tests green, no failures)
