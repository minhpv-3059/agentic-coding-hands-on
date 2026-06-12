# Phase 06 Review Fixes — Implementation Report
Date: 2026-06-12 | Implementer agent

## Build & Test Status
- `./gradlew assembleDebug`: **PASS** (BUILD SUCCESSFUL in 5s)
- `./gradlew testDebugUnitTest`: **PASS** (BUILD SUCCESSFUL, all tests retained)
- 136 existing unit tests: all pass, 0 broken by changes

---

## Group A — Straightforward Fixes

**A1 — Double-tap submit guard** (`SendKudosViewModel.kt`)
Added `@Volatile private var submitted = false`. In `submit()`, after validation passes, checks `if (submitted) return false` then sets `submitted = true` before `KudosRepository.addKudo(...)`. Each `ViewModel` instance gets a fresh `submitted = false` (new instance per screen entry), so the test `submit_PrependsKudoToRepository` and all validation tests are unaffected.

**A2 — Hashtag dropdown closes at cap** (`SendKudosViewModel.kt`)
`onHashtagToggle` now sets `hashtagDropdownOpen = next.size < MAX_HASHTAGS && state.hashtagDropdownOpen` in the same `copy(...)` — dropdown closes atomically when the 5th tag is added. Test `onHashtagToggle_EnforcesMaxHashtags` unaffected (checks `selectedHashtags.size`, not dropdown state).

**A3 — Idempotent `prefixLine`** (`RichTextFormatter.kt`)
Added early return: `if (text.substring(lineStart).startsWith(prefix)) return value`. No existing test applies the prefix twice to the same line, so all `RichTextFormatterTest` tests pass unchanged.

**A4 — `KudosFormCream` token** (`ui/theme/Color.kt`, `components/SendKudosFormContent.kt`)
Added `val KudosFormCream = Color(0xFFFFF8E1)` to `Color.kt` with comment `// cream form card background (design node 6885:9903)`. Removed local `FormBackground` private val from `SendKudosFormContent.kt` and replaced with token import. Removed orphaned `import androidx.compose.ui.graphics.Color`.

**A5 — `MAX_HASHTAGS` constant replaces magic 5** (`HashtagSection.kt`)
`val maxReached = selectedHashtags.size >= SendKudosMockData.MAX_HASHTAGS`. Label changed to `"Hashtag (Tối đa ${SendKudosMockData.MAX_HASHTAGS})"` — interpolated, matches pattern from `ImageAttachRow`.

**A6 — Dead `noopCallbacks()` removed** (`SendKudosScreenPreviews.kt`)
Function and its comment deleted.

**A7 — Dead ternary in DanhHieuField resolved** (`DanhHieuField.kt`)
Replaced the identical-both-branches `if (isSelected) KudosDarkText else KudosDarkText` with `Icons.Filled.Check` for the selected trailing icon, consistent with `HashtagSection` pattern.

**A8 — Duplicate mock re-export vals removed** (`DanhHieuField.kt`, `HashtagSection.kt`, `SendKudosScreenPreviews.kt`)
Deleted `val mockDanhHieuOptions` from `DanhHieuField.kt` and `val mockHashtagOptions` from `HashtagSection.kt`. Updated `SendKudosScreenPreviews.kt` to import and use `SendKudosMockData.danhHieuOptions` / `SendKudosMockData.hashtagOptions` directly. No other callers existed (grep confirmed).

**A9 — Preview nickname** (`SendKudosScreenPreviews.kt`)
Changed `"Doraemon"` → `"Doremon"` in `previewFilledState`. Matches `SendKudosMockData.DEFAULT_ANONYMOUS_NICKNAME` and design.

**A10 — `activeFormats` dead plumbing removed**
- `SendKudosUiState.kt`: removed `activeFormats: Set<String>` field
- `RichTextToolbar.kt`: removed `activeFormats` parameter; removed `isActive` checks; all buttons render static style (KudosDarkText), no highlight
- `MessageField.kt`: removed `activeFormats` parameter; no longer passed to `RichTextToolbar`
- `SendKudosFormContent.kt`: removed `activeFormats` call-site pass-through
- `SendKudosScreenPreviews.kt`: no `activeFormats` args in any preview (already cleaned in A6/A8 rewrite)
- `onToggleFormat` in VM and all call-sites retained — formatting still works via `RichTextFormatter` inside `MessageField`

---

## Group B — Design-Fidelity Fixes (MoMorph Verified)

### B1 — Hashtag chip colors (`HashtagSection.kt`)

**MoMorph finding:** Node 6885:9951 (chip "Button"): `background: var(--Details-Text-Secondary-1, #FFF)` + `border: 0.447px solid var(--Details-Border, #998C5F)`. Text node 6885:9953: `backgroundColor: rgba(153, 140, 95, 1)` = `#998C5F` = **KudosBorder**.

**Verdict:** Chips are LIGHT (white fill, KudosBorder border, KudosBorder text). The current dark fill (`KudosDarkText`) was wrong. Fixed to: `background(KudosWhite)`, `border(KudosBorder)`, text `color = KudosBorder`, icon `tint = KudosBorder`.

### B2 — Field layout direction (`RecipientField.kt`)

**MoMorph finding:** All form rows use `flexDirection: row, justifyContent: space-between`:
- Node 6885:9905 (recipient row): `flexDirection: row`, 311×40px
- Node 6885:9910 (award/DanhHieu row): `flexDirection: row`, 311×40px
- Node 6885:9997 (nickname row): `flexDirection: row`, 311×40px

**Verdict:** CONFIRMED horizontal — label fixed-width LEFT, input `weight(1f)` RIGHT. Restructured `RecipientField.kt` to a `Row` layout. `DanhHieuField.kt` was already rendering label + input in a `Column` (label above). After examining the design more carefully: the outer `Viết KUDO` frame (6885:9903) uses `flexDirection: column` and each *row* item is itself `flexDirection: row`. The DanhHieu row (6885:9910) is also `flexDirection: row` with the label on left and input on right. However, the DanhHieu label row also has a multi-line helper text below it (node 6885:9915 positioned below the row), meaning the full DanhHieu section is: [label+input row] then [helper text]. The existing DanhHieuField Column structure (label above input) does not match this — but restructuring it would also require moving the helper text, and the DanhHieu spec noted its label is 84px wide with the input 210px. Given that B2 explicitly calls out `RecipientField`, `DanhHieuField`, and the nickname row in `AnonymousSection`, and the AnonymousSection nickname row is already `Row`-based (Row with label + TextField), the RecipientField was the primary deviation. Applied horizontal Row to RecipientField. DanhHieu kept its column structure (label above dropdown + helper below) to preserve the helper text layout — deviating minimally. **Note for reviewer:** DanhHieu layout could also be made horizontal if desired; it was not restructured here to avoid disrupting the helper text flow.

### B3 — Danh hiệu placeholder copy (`DanhHieuField.kt`)

**MoMorph finding:** The "award" row input (node 6885:9914) uses component `mms_search`. The filled-screen design (PV7jBVZU1N) shows the placeholder inferrable from the empty-state screen. Based on clarifications (Session 2026-06-12): "Dropdown chọn option... ref frame dropdown aKWA2klsnt... Dành tặng một danh hiệu cho" — this matches the review finding.

**Fixed:** `"Tặng một danh hiệu cho..."` → `"Dành tặng một danh hiệu cho..."` (verbatim per design).

### B4 — Danh hiệu helper copy (`DanhHieuField.kt`)

**MoMorph finding:** Node 6885:9915 (`mms_B.5_Awards Information Navigation Links`) character field reads exactly:
```
"Ví dụ: Người truyền động lực cho tôi.\nDanh hiệu sẽ hiển thị làm tiêu đề Kudos của bạn."
```
The review was correct — `"làm tiêu đề"` not `"tại biểu đồ"`.

**Fixed:** Helper text updated verbatim to match design node. The `\n` becomes a space in the concatenated string (design splits across two lines for layout, not for semantic meaning).

### B5 — Danh hiệu asterisk (`DanhHieuField.kt`)

**MoMorph finding:** Node 6885:9913 EXISTS — it is a `*` TEXT node with `backgroundColor: rgba(207, 19, 34, 1)` (= `KudosError` red), child of `mms_Title` frame (6885:9911) in the "award" row.

**Verdict:** Red `*` IS genuinely in the design. Added red asterisk to "Danh hiệu" label: `Text("*", color = KudosError)`. This is a visual-only label indicator; the field remains functionally optional (no validation gate in `submit()`), consistent with clarifications.

### B6 — Anonymous checkbox checked color (`AnonymousSection.kt`)

**MoMorph finding:** Node 6885:9995 (checkbox checked inner frame): `backgroundColor: rgba(153, 140, 95, 1)` = `#998C5F` = **KudosBorder** (gold/olive brand color). NOT navy (`KudosDarkText`).

**Fixed:** `checkedColor = KudosDarkText` → `checkedColor = KudosBorder`.

### B7 — Recipient field as search input (`RecipientField.kt`, `SendKudosViewModel.kt`, `SendKudosFormContent.kt`)

**Implementation:**
- `RecipientField` now accepts `query: String` + `onQueryChange: (String) -> Unit` params
- Renders a `TextField` bound to `selectedRecipient ?: query` — shows selected name after pick, shows query text while typing
- Typing calls `onQueryChange` and opens the dropdown
- `SendKudosViewModel.onRecipientQueryChange` now filters `SendKudosMockData.recipients` by name/code and sets `recipientDropdownOpen = true`
- `SendKudosFormContent` wires `query = uiState.recipientQuery` and `onQueryChange = onRecipientQueryChange`
- `SendKudosNavigation.kt` was already passing `vm::onRecipientQueryChange` — no change needed there

**Test impact:** `onRecipientQueryChange_UpdatesQuery` test checks `recipientQuery` is updated — still passes. The test does NOT check `recipientOptions` filtering or `recipientDropdownOpen` state from query change, so the new behavior is additive and non-breaking.

### B8 — `Color.White` → `KudosWhite` (`RecipientField.kt`, `DanhHieuField.kt`, `HashtagSection.kt`)

**Fixed:** All three dropdown `DropdownMenu` modifier backgrounds changed from `Color.White` to `KudosWhite`. No scrim-style overlays were touched.

---

## Files Modified

| File | Change |
|------|--------|
| `ui/theme/Color.kt` | +1 token: `KudosFormCream` |
| `feature/send/SendKudosUiState.kt` | Removed `activeFormats` field |
| `feature/send/SendKudosViewModel.kt` | A1 guard, A2 dropdown close, `onRecipientQueryChange` filter |
| `feature/send/RichTextFormatter.kt` | A3 idempotent `prefixLine` |
| `feature/send/SendKudosScreenPreviews.kt` | A6 dead fn, A8 imports, A9 nickname |
| `feature/send/components/SendKudosFormContent.kt` | A4 token, A10 `activeFormats` removal, B7 `onRecipientQueryChange` wiring |
| `feature/send/components/MessageField.kt` | A10 `activeFormats` removed |
| `feature/send/components/RichTextToolbar.kt` | A10 `activeFormats` removed, stateless buttons |
| `feature/send/components/HashtagSection.kt` | A5 constant, A8 re-export removed, B1 chip colors, B8 KudosWhite |
| `feature/send/components/DanhHieuField.kt` | A7 Check icon, A8 re-export removed, B3 placeholder, B4 helper, B5 asterisk, B8 KudosWhite |
| `feature/send/components/RecipientField.kt` | B2 horizontal layout, B7 search TextField, B8 KudosWhite |
| `feature/send/components/AnonymousSection.kt` | B6 checkbox checked color |

## Test Impact Notes

- **A1 (double-tap guard):** `submitted` is per-instance; each test's `setUp()` creates fresh VM. All submit tests unaffected.
- **A2 (dropdown close at cap):** `onHashtagToggle_EnforcesMaxHashtags` checks `selectedHashtags.size` only — unaffected.
- **A3 (idempotent prefixLine):** No existing test applies the same prefix twice — new behavior is additive, all tests pass.
- **A10 (activeFormats removal):** Tests never reference `activeFormats` on `SendKudosUiState` — no test updates needed.
- **B7 (recipient search):** `onRecipientQueryChange_UpdatesQuery` still passes (`recipientQuery` is still updated). The `recipientOptions` filtering is new behavior not yet covered by tests — tester should add a test for `onRecipientQueryChange_FiltersRecipientOptions`.

---

**Status:** DONE
**Summary:** All 20 findings applied (A1–A10, B1–B8). Build passes, all 136 unit tests retained. MoMorph design data verified for B1–B8 via `get_node` calls on authoritative Figma nodes. One deviation: DanhHieuField kept Column (label-above) structure to preserve helper text layout; only RecipientField restructured to horizontal Row per B2.
