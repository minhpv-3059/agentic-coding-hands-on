# New Kudo Screen — Fidelity Fix + Preview Feature

**Date:** 2026-06-15
**Status:** DONE

---

## Issues Fixed

### Issue 1 — Dropdown backgrounds were WHITE; design is DARK

**Design values (MoMorph nodes 6891:17450, 6891:17706):**
- Background: `#00070C` = `KudosContainer2` (already defined token)
- Border: `1px solid #998C5F` = `KudosBorder`
- Border-radius: `8dp`
- Selected item highlight: `rgba(255,234,158,0.20)` → added new token `KudosDropdownHighlight = Color(0x33FFEA9E)`
- Item text: white (`KudosWhite`) on dark background
- Check icon for selected: gold (`KudosGold`)

**Fix:** Updated `RecipientField.kt`, `DanhHieuField.kt`, `HashtagSection.kt` — replaced `KudosWhite` background on `DropdownMenu` with `KudosContainer2 + KudosBorder border`. Item text changed from `KudosDarkText` → `KudosWhite`. Check icons changed from `KudosDarkText` → `KudosGold`.

**Verified:** Tapped recipient field + typed "a" → dark dropdown appeared with white text and avatar. Tapped hashtag add button → dark dropdown with all hashtags listed.

---

### Issue 2 — "Người nhận" TextField clips text at 40dp height

**Root cause:** Material3 `TextField` has a built-in min-height of 56dp with internal padding that overflows or clips when constrained to 40dp.

**Fix:** Replaced `TextField` with `BasicTextField` inside a `Box` that is explicitly `40dp` tall with `contentAlignment = Alignment.CenterStart` and `padding(horizontal = 11dp)`. Placeholder rendered via `decorationBox` lambda. Text is now fully visible at the 40dp field height matching design node 6885:9909 (`height: 40px`).

---

### Issue 3 — Buttons were pills; design is 4dp rounded-rect

**Design values (nodes 6885:10003, 6885:10004):**
- `border-radius: 4px` on both Huỷ and Gửi đi
- Height: `40dp` (design: `height: 40px`)
- Huỷ: `rgba(255,234,158,0.10)` background = `KudosSecondaryButtonNormal`, `#998C5F` border = `KudosBorder`
- Gửi đi: `rgba(255,234,158,1)` fill = `KudosGold`, dark text

**Fix:** Replaced `KudosPrimaryButton` / `KudosSecondaryButton` (which use `PillShape=RoundedCornerShape(50)`) with local `OutlinedButton` / `Button` instances inside `SendKudosFormContent.kt` using `ActionButtonShape = RoundedCornerShape(4.dp)` and `height(40.dp)`. The shared button components were NOT changed — their pill defaults are preserved for other screens.

---

### Issue 4 — Screen background was flat dark; design has keyvisual artwork

**Design (node 6885:9884):** `MM_MEDIA_Keyvisual BG` image positioned at the right side of the screen, plus `Shadow Left` (linear gradient `#00101A → transparent`) and `Shadow Bottom` (rotated gradient) overlays.

**Fix:** Wrapped the `Scaffold` in a `Box` with `Image(R.drawable.bg_home_keyvisual, ContentScale.Crop)` filling `matchParentSize`. Changed `Scaffold.containerColor` to `Color.Transparent` so the keyvisual shows through. The `bg_home_keyvisual` asset is the same keyvisual used on the Home/Feed screens — reused per task instructions.

**Verified:** Screenshot shows colorful keyvisual artwork in the top-right area matching the design.

---

### Issue 5 — Title alignment and color

**Design (node 6885:9894):** `"New Kudo"` — `textAlign: center`, `color: rgba(255,255,255,1)` = white, `fontSize: 17px`, `fontWeight: 500` (Medium).

**Previous code:** Title was left-aligned, gold color (`KudosGold`).

**Fix:** Updated `TopAppBar` title in `SendKudosScreen.kt` to use `KudosWhite`, `textAlign = TextAlign.Center`, `style = MaterialTheme.typography.titleMedium`, `modifier = Modifier.fillMaxWidth()`.

---

### Issue 6 — Full screen re-verification

Compared the emulator screenshot against MoMorph design `7fFAb-K35a` (default form) and `PV7jBVZU1N` (filled form):

- Cream card: `rgba(255,248,225,1)` = `KudosFormCream`, `radius ≈ 11dp` — correct
- Heading "Gửi lời cám ơn...": gold (`KudosGold`), above the card — correct
- Field layout: label LEFT + input RIGHT (row layout) — correct
- Rich text toolbar: B/I/S/list/link/quote buttons — correct
- Hashtag chips: white fill + KudosBorder border + KudosBorder text (design node 6885:9951) — unchanged, correct
- Anonymous checkbox section — unchanged, correct
- Error banner — unchanged, correct
- Bottom nav tab active on Kudos — correct

---

## Part B — Preview Feature

**Implementation:** Added `onPreviewKudo: () -> Kudo` parameter to `SendKudosScreen`. A local `var previewKudo` state holds the currently-previewed kudo (null = dialog hidden).

**Preview trigger:** "👁" (eye emoji text) `OutlinedButton` placed between Huỷ and Gửi đi in `SendKudosFormContent.kt`. Uses same `ActionButtonShape` (4dp) and gold outlined style.

**Dialog:** New `KudoPreviewDialog.kt` uses `BasicAlertDialog` + `Surface(color=KudosBackground)`. Renders the kudo card via the existing `KudosCard(canLike=false)` composable — identical to feed rendering, with `MarkdownText` for message.

**Wire-up:** `SendKudosNavigation.kt` passes `vm::previewKudo` as `onPreviewKudo`. `SendKudosScreenPreviews.kt` passes `{ KudosMockData.kudos.first() }`.

**Verified:** Tapped 👁 button → dialog appeared titled "Xem trước" showing the kudo card with sender, recipient, time, title, action row. Tapped backdrop → dismissed.

---

## New Token Added

`Color.kt`:
```kotlin
val KudosDropdownHighlight = Color(0x33FFEA9E)  // rgba(255,234,158,0.20) — selected row in dark dropdown
```

---

## Files Modified

| File | Change |
|------|--------|
| `feature/send/SendKudosScreen.kt` | Keyvisual BG, transparent scaffold, centered white title, `onPreviewKudo` param + dialog state |
| `feature/send/components/SendKudosFormContent.kt` | 4dp action buttons (local), `onPreview` param, preview button |
| `feature/send/components/RecipientField.kt` | Dark dropdown, BasicTextField (no clip) |
| `feature/send/components/DanhHieuField.kt` | Dark dropdown, white/gold item text |
| `feature/send/components/HashtagSection.kt` | Dark dropdown, white/gold item text |
| `feature/send/components/KudoPreviewDialog.kt` | NEW — preview dialog (55 lines) |
| `feature/send/SendKudosScreenPreviews.kt` | Added `onPreviewKudo` param to all previews |
| `navigation/SendKudosNavigation.kt` | Added `onPreviewKudo = vm::previewKudo` |
| `ui/theme/Color.kt` | Added `KudosDropdownHighlight` token |

## Test Results

- `./gradlew assembleDebug` → **BUILD SUCCESSFUL**
- `./gradlew testDebugUnitTest` → **BUILD SUCCESSFUL** (152 tests green, unaffected)
- Visual validation on emulator-5554 — all issues confirmed fixed
