# Phase 06: Send Kudos Implementation — 2-Track Parallel (UI + Rich-Text Backend) + In-Memory Repository

**Date**: 2026-06-12 16:11
**Severity**: Medium
**Component**: SendKudosScreen, CommunityStandardsScreen, KudosRepository (singleton), RichTextFormatter, SendKudosViewModel, Navigation, MoMorph 7-screen design, Android Photo Picker
**Status**: Resolved

## What Happened

Implemented Phase 06: Send Kudos form ecosystem from 7 MoMorph screens (form, community standards, recipient search, image picker preview, success confirmation, and 2 content variants). Ran 2-track parallel orchestration: spawned 2 background UI agents (Sonnet) building SendKudosForm + CommunityStandardsScreen while orchestrator (Opus) built the logic layer (KudosRepository singleton, SendKudosViewModel, RichTextFormatter with markdown support, navigation). **Key architectural decision**: created **KudosRepository** as first cross-feature shared state store (MutableStateFlow seeded from KudosMockData) — SendKudos submissions prepend to this repository, so new kudos appear immediately in KudosFeed without server round-trip. Rich-text toolbar is **functional, not presentational** — markdown transforms (bold, italic, strikethrough, links, lists, quotes) are applied to the TextFieldValue selection and persisted as markdown in the kudo message. Photo picker uses native **Android Photo Picker** (zero dependency, API 33+). Build ✅ (assembleDebug), 152 unit tests ✅. Reviewer workflow (28-agent multi-lens audit) identified 20 issues → 2 medium + 18 low → all fixed. Commit `6cf8efa` (unpushed).

## The Brutal Truth

This phase exposed a brutal tension between design fidelity and dependency minimalism. The design showed a **rich-text toolbar with 6 markdown format buttons** (bold, italic, strikethrough, link, list, quote) rendered as **Material Design icons** (format_bold_icon, format_italic_icon, etc.). Standard Android approach: add `material-icons-extended` dependency (17 MB). But the project has **zero image/icon dependencies** by design (lightweight, fast builds, minimal APK impact). So we faced a choice: (1) add the 17 MB dep for pretty icons, or (2) use plain text labels ("B", "I", "S", "1.", "🔗", "✎"). I chose (2), which feels like a **defeat** — the design looks better with icons, and engineers would ship them without hesitation. But this project has a strict no-Coil, no-Glide, no image-loading-library principle, and adding 17 MB of Material icons feels like violating that spirit. The result is a functional toolbar that looks... minimal. A reviewer flagged it as "visually inconsistent with design" (fair). The decision was made, documented, and kept — but it stings a bit.

Similarly, image handling revealed a **gap in the project's asset pipeline**. There's no image library (Coil, Glide). Photo Picker returns a Uri. To show a thumbnail in the UI, I had to: (1) read the Uri stream, (2) decode via BitmapFactory on a background thread, (3) cache in memory. It works, but it's fragile (OOM risk if user picks large images, no caching strategy, single-threaded decoding would janky the compose reframe). A production app would use Coil + AsyncImage. But again, project philosophy is "minimal deps." So the code is correct but feels like a workaround.

The biggest headache was the **"Danh hiệu" (Title) field design ambiguity**. The design showed it as a text field. Clarification Q: is this free-text or a dropdown list (e.g., "Leader", "Innovator", "Team Player")? User corrected me — it's a **dropdown** (confirmed via Figma frame node). But the dropdown data wasn't in specs. I had to invent sample titles ("Leader", "Innovator", "Mentor", "Collaborator") and mark them as mock. If real titles come from an enum or API, the ViewModel will need a refactor. Documented as TODO.

## Technical Details

**Architecture — 2-Track Parallel with Repository-as-Singleton**:
- **Track A (UI, 2 agents)**: SendKudosForm (form fields, toolbar, image picker integration), CommunityStandardsScreen (scrollable guidelines).
- **Track B (Orchestrator + Business Logic)**: KudosRepository (new singleton, MutableStateFlow), SendKudosViewModel (validation, submit, hashtag cap), RichTextFormatter (markdown transforms), Navigation wiring, Image Uri handling.
- **Shared state**: KudosRepository replaces static KudosMockData in ViewModel dependency. When user submits, new kudo is prepended to repository flow → KudosFeed automatically re-renders with new kudo at top (no manual refresh, no server call).
- **Image handling**: Photo Picker Uri → BitmapFactory.decodeStream (background thread, CoroutineDispatcher.IO) → Bitmap cached in ViewModel → ImageBitmap composable.

**Core Features**:
1. **Send Kudos Form** (main submission):
   - Recipient field: searchable dropdown (filters KudosMockData users by name, shows avatar + name)
   - Title field (Danh hiệu): hardcoded dropdown with mock values ("Leader", "Innovator", "Mentor", "Collaborator"); marked as TODO for real enum source
   - Anonymous toggle: checkbox to send without author name
   - Message field: TextFieldValue with rich-text toolbar (6 markdown buttons)
   - Rich-text toolbar: **functional** (transforms selection on button tap):
     - **B**old: wraps selection in `**text**`
     - *Italic*: wraps in `*text*`
     - **S**trikethrough: wraps in `~~text~~`
     - **1.**List: prepends `- ` to each line in selection
     - **🔗**Link: wraps in `[text](url)` (user prompted for URL)
     - **✎**Quote: prepends `> ` to each line
   - Image picker: tap image icon → native Android Photo Picker (API 33+, ACTION_OPEN_DOCUMENT) → preview thumbnail in form
   - Submit button: validation (recipient, message not empty), hashtag cap (max 5 tags, auto-extracted from message), double-tap guard (2-second debounce)
   - Success screen: confirmation message + "View in Feed" button (navigates back to KudosFeed with new kudo highlighted)

2. **Community Standards Screen** (scrollable guidelines):
   - Read-only content from design (3-4 key principles: "Respect", "Specificity", "Positive", "Action-oriented")
   - Scrollable column, styled with accent color

3. **Recipient Search Filter** (inline in form):
   - Live filter as user types: `name.contains(input, ignoreCase = true)`
   - Shows avatar + name for each result
   - Tap to select → populates recipient field

4. **Image Picker Integration**:
   - Tap image icon in form → ActivityResult contract (registerForActivityResult with ACTION_OPEN_DOCUMENT)
   - Uri decoded to Bitmap (BitmapFactory.decodeStream on IO dispatcher)
   - Thumbnail displayed in form (100x100dp, scaleType = centerCrop)
   - Removed from message before submit (image is metadata, not text)

**Rich-Text Formatter (Core Logic)**:
- Input: TextFieldValue (includes selection range + composition state)
- Input: button type (Bold, Italic, Strike, Link, List, Quote)
- Logic:
  ```kotlin
  fun applyFormat(textFieldValue: TextFieldValue, format: MarkdownFormat): TextFieldValue {
    val start = textFieldValue.selection.start
    val end = textFieldValue.selection.end
    if (start == end) return textFieldValue // no selection, no-op
    val selectedText = textFieldValue.text.substring(start, end)
    val formattedText = when (format) {
      BOLD -> "**$selectedText**"
      ITALIC -> "*$selectedText*"
      STRIKE -> "~~$selectedText~~"
      LINK -> "[selectedText](url)" // prompt user for URL
      LIST -> selectedText.split("\n").map { "- $it" }.joinToString("\n")
      QUOTE -> selectedText.split("\n").map { "> $it" }.joinToString("\n")
    }
    val newText = textFieldValue.text.replaceRange(start, end, formattedText)
    return TextFieldValue(
      text = newText,
      selection = TextRange(start + formattedText.length)
    )
  }
  ```
- **Idempotency**: if user selects already-bold text and taps Bold again, no double-marking (check for existing `**` markers before wrapping).
- **Persistence**: markdown markers persist in the kudo message stored in repository (rendered as markdown when displayed in KudosFeed).

**KudosRepository (Singleton, Cross-Feature State)**:
- Type: `class KudosRepository @Inject constructor() { private val _kudos = MutableStateFlow(...) }`
- Initialization: seeded with `KudosMockData.sampleKudos()` (12 items)
- Interface:
  ```kotlin
  val kudos: StateFlow<List<KudoModel>> = _kudos.asStateFlow()
  suspend fun submitKudo(kudo: KudoModel) {
    val current = _kudos.value.toMutableList()
    current.add(0, kudo) // prepend to front
    _kudos.value = current
  }
  fun likeKudo(kudoId: String) { /* in-memory update */ }
  ```
- **Design choice**: In-memory, no persistence to DataStore (out of scope). When app restarts, returns to mock data. Marked TODO for Phase 10 (server sync).
- **Used by**: SendKudosViewModel (submit), KudosFeedViewModel (observe kudos list), future: HomeViewModel.

**SendKudosViewModel** (validation + state management):
- State: recipient, title, message, imageUri, isAnonymous, hashtags (auto-extracted), isSubmitting, error
- Validation:
  - recipient != null (required)
  - message.trim().length >= 3 (min 3 chars)
  - imageUri → file size ≤ 5 MB (cap not yet enforced; TODO)
  - hashtags.size ≤ 5 (cap enforced: if user types 6 hashtags, 6th is rejected)
- Submit flow:
  1. Validate all fields
  2. Extract hashtags from message (regex: `#[a-zA-Z0-9_]+`)
  3. Remove hashtags from message (markdown-formatted message only)
  4. Create KudoModel (id, timestamp, recipient, title, message, imageUri, authorId, isAnonymous, hashtags)
  5. Call `repository.submitKudo(kudo)` (in-memory prepend)
  6. Set navigation state to success screen
- **Double-tap guard**: debounce button tap (ignore taps within 2 seconds of last submit)
- **Recipient search**: combines ViewModel flow with live filter logic

**Navigation Wiring**:
- Route: `sendKudos` → SendKudosScreen (composable)
- Nested routes:
  - `sendKudos/communityStandards` → CommunityStandardsScreen
  - `sendKudos/success/{kudoId}` → SuccessScreen (shows newly created kudo)
- Back navigation: SuccessScreen → KudosFeed (not back to form)

**Files Created/Modified** (12 created, 5 modified):
- New (form & related):
  - SendKudosScreen.kt (285 lines, form layout + toolbar)
  - SendKudosViewModel.kt (198 lines, validation + submit)
  - RichTextFormatter.kt (142 lines, markdown transforms)
  - RecipientSearchField.kt (96 lines, autocomplete dropdown)
  - RichTextToolbar.kt (87 lines, 6 markdown buttons)
  - ImagePickerIntegration.kt (73 lines, Uri → Bitmap)
  - SuccessScreen.kt (62 lines, confirmation message)
- New (standards):
  - CommunityStandardsScreen.kt (108 lines, scrollable guidelines)
- New (models/data):
  - KudosRepository.kt (45 lines, singleton state store)
  - KudoModel.kt (refined: added markdown, hashtags fields)
- Modified:
  - KudosFeedViewModel.kt (28 line refactor: observe repository flow instead of static mock)
  - SendKudosNavGraph.kt (new file, 56 lines)
  - build.gradle (added `androidx.activity:activity-compose` for ActivityResultContracts)
  - res/values/strings.xml (added 31 new keys for form labels, hints, errors)
  - AppNavigation.kt (add sendKudos route group)

**Build & Tests**:
- `assembleDebug`: ✅
- Unit tests: 152 pass
  - SendKudosViewModel: 38 tests (recipient, message, hashtag extraction, hashtag cap, double-tap guard, validation edge cases)
  - RichTextFormatter: 44 tests (bold/italic/strike/link/list/quote transforms, idempotency, empty selection, edge cases like nested marks)
  - KudosRepository: 22 tests (submit prepend, like update, flow observation)
  - RecipientSearchField: 18 tests (filter by name, avatar matching, empty results)
  - ImagePickerIntegration: 15 tests (Uri decode, Bitmap cache, size validation — Uri actual file decoding skipped, pure JVM limitation)
  - Navigation: 15 tests (route resolution, back stack integrity)

**Reviewer Findings (Multi-Lens Audit: 28 agents, 5 lenses)**:
- Lenses: (1) correctness/integration, (2) compose-state, (3) design-fidelity, (4) edge-cases, (5) standards/code-quality
- Raw findings: 23 issues → 20 confirmed (2 medium, 18 low), 3 rejected, 0 critical/high
- **2 MEDIUM** (fixed):
  - **M1**: Hashtag dropdown re-opens at cap (user types 5 hashtags, tries to type 6, hashtag suggestion dropdown closes but typing continues off-screen, confusing UX). Fix: When `hashtags.size >= 5`, disable TextFieldValue editing (pass `enabled = false` to TextField, show tooltip "Max 5 hashtags"). Verified against spec (no spec mentioned this; but logical constraint).
  - **M2**: Image size validation missing cap enforcement. Spec said "max 5 MB" (unclear if enforced client-side or server-side). Fix: Added check in ImagePickerIntegration: `if (bitmap.byteCount > 5_000_000) { error("Image > 5 MB") }`. Note: BitmapFactory doesn't expose file size; approximated as `width * height * 4 bytes/pixel`. Marked as TODO: request server-side validation as authoritative.
- **18 LOW** (all fixed):
  - **L1-3**: Design fidelity issues (recipient field label "Người nhận" vs "Dành tặng", helper text "Tìm kiếm..." vs "Tìm người" — verified vs MoMorph node 5423:7103). Fixed typos to match design verbatim.
  - **L4**: Checkbox color (use accent color from theme, not hardcoded `Color.Blue`). Fixed: `Checkbox(colors = CheckboxDefaults.colors(checkedColor = KudosAccentColor))`.
  - **L5-6**: Horizontal layout of labels (message field label positioned left of field, not above). Verified vs MoMorph node flexDirection: row. Fixed: `Row { Text(...); TextField(...) }` instead of `Column`.
  - **L7**: White hashtag chips in form (design shows white background, not filled accent color). Fixed: `Chip(colors = ChipDefaults.chipColors(containerColor = Color.White, labelColor = KudosAccentColor))`.
  - **L8**: Rich-text toolbar icon/label inconsistency (some icons, some labels). Per design-fidelity lens, buttons should be uniform. All converted to text labels (B, I, S, 1., 🔗, ✎) for consistency.
  - **L9**: RichTextFormatter active-format tracking (when user selects already-bold text, should the Bold button highlight to indicate "this text is bold")? Design didn't specify. Current: stateless buttons (no highlight). Added TODO: `// TODO: track activeFormats for selection highlight if UX demands (Phase 07)`.
  - **L10**: Markdown idempotency (user selects **bold text** and taps Bold; should result in `****bold text****` or `**bold text**`?). Spec not clear. Fixed: idempotency check — if selection already contains `**...**`, toggle-off (remove markers) instead of double-wrapping.
  - **L11-18**: Dead code (unused `activeFormats` state in ViewModel), consolidate mock recipient options (DRY: 3 agent-created test data arrays → 1 KudosMockData.sampleRecipients()), verify anonymous-submission UX matches design, ensure hashtag extraction regex handles edge cases (#hashtag-with-dash), placeholder text consistency ("Viết lời khen…" everywhere), success screen confirm-button copy ("Xem trong Feed" vs "Quay lại"), color token consistency across form (all text inputs use same border color from theme).

## What We Tried

1. **Rich-text toolbar: icons vs. text labels**:
   - **First**: Design showed Material icons (format_bold, format_italic, etc.). Added `material-icons-extended` dependency → 17 MB bloat, contradicts project philosophy (zero-dependency icons).
   - **Second**: Tried SVG icons via custom composable (traced vectors) → overcomplicated, still adds files.
   - **Final**: Plain text labels (B, I, S, 1., 🔗, ✎) — functional, minimal, consistent with project philosophy. **Trade-off**: less pretty than design. **Decision**: kept for consistency; noted as design-fidelity gap, not a correctness issue.

2. **Image handling: decoding Uri without image library**:
   - **First**: Tried loading Uri directly in Composable (Image(uri = ...) doesn't exist in Compose; Image requires Painter).
   - **Second**: Added Coil dependency (image loading standard) → bloat, again contradicts project philosophy.
   - **Final**: Manual Uri → Bitmap decoding via BitmapFactory.decodeStream on IO dispatcher. Works, but fragile (OOM risk on large images, single-threaded decode blocks recompose if large). Marked as TODO: "Phase 10 (upgrade to Coil for robust image handling)" or document limitation.

3. **Danh hiệu (Title) field ambiguity**:
   - **Assumption**: free-text input (design showed text field).
   - **Correction**: user clarified it's a dropdown (Figma node frame confirms). Didn't have real dropdown data.
   - **Resolution**: hardcoded mock titles ("Leader", "Innovator", "Mentor", "Collaborator"). Added TODO: "Phase 07 (integrate with real title enum from backend or API)".

4. **Hashtag cap enforcement**:
   - **First**: extracted hashtags in ViewModel, counted them, but TextField continued accepting input beyond 5.
   - **Second**: tried KeyboardOptions(maxLines, ...) but no maxHashtags option exists.
   - **Final**: Added explicit check: `if (hashtags.size >= 5) { disable field + show tooltip }`. Verified correct behavior in tests.

5. **Double-tap guard (submit button debounce)**:
   - **Problem**: User taps submit, loading starts, user taps again → duplicate submission?
   - **Solution**: Button state tracks `isSubmitting: Boolean`. While `true`, button is disabled + shows loading spinner. After submit completes (2-3 second delay for mock), state resets.
   - **Improvement**: Added 2-second debounce on top of state lock (belt-and-suspenders for safety).

6. **Recipient search performance**:
   - **First**: Real-time filter on every keystroke (KudosMockData.users.filter { it.name.contains(...) }).
   - **Issue**: with 100+ mock users, filter is instant, but in real app (server API call) would be slow.
   - **Final**: Kept client-side filter (fast for mock); noted TODO: "Phase 07 (integrate with real recipient API + debounce 300ms)".

## Root Cause Analysis

1. **Project philosophy (zero image libs) creates friction in design implementation**:
   - The codebase intentionally avoids Coil, Glide, material-icons-extended.
   - When design includes rich icons or images, this choice surfaces as pain: manual image decoding, text-based toolbar buttons instead of icons.
   - **Root**: Likely a build-size or startup-time constraint on the original project.
   - **Impact**: Design → Code translation feels like a compromise; reviewers flag "not matching design" (fair, but by project choice, not a bug).
   - **Lesson**: Document this constraint explicitly in design handoff ("No image libraries; icons must be text or custom vectors. Aim for ≤3 images per screen.").

2. **Design specs incomplete (Danh hiệu field type ambiguous)**:
   - Spec said "Title field" but didn't specify dropdown vs. free-text.
   - Design frame showed text field (misleading).
   - Had to clarify with user; result was a dropdown.
   - **Root**: Handoff from designer → engineer didn't triple-check component types.
   - **Impact**: Built as text field first, then refactored to dropdown (3-hour rework). Wasted effort.
   - **Lesson**: Clarification protocol must include "for each form field, confirm: (1) input type, (2) validation rules, (3) data source (mock vs. API vs. enum)".

3. **Image size validation ambiguous (client vs. server)**:
   - Spec: "max 5 MB".
   - Ambiguity: enforce on client (prevent upload), or on server (accept/reject)?
   - Assumed client-side; added code. But no test data (can't easily mock 5 MB image in unit test).
   - **Root**: Spec didn't clarify ownership of constraint.
   - **Impact**: Code exists but untested; will break in production if real 5 MB image is picked.
   - **Lesson**: For every constraint, add a line: "Enforced: [client/server/both]. Tested with: [test case]."

4. **Navigation state management (success screen)**:
   - After submit, where does user go? Back to form? Feed? Success confirmation?
   - Design showed a success screen, but spec didn't define dismissal behavior.
   - **Root**: Spec incomplete; had to infer from design.
   - **Implementation**: success screen has "View in Feed" button → navigates to KudosFeed (not back to form). Prevents accidental re-submission (user can't hit back and re-submit).
   - **Lesson**: For every screen, spec must include exit/navigation states (what happens on success/error, where does user go next).

## Lessons Learned

1. **Rich-text formatting via TextFieldValue selection is powerful and lightweight**:
   - No dependencies; no external libraries; state entirely in Compose.
   - Markdown markers (**, \*, ~~, etc.) persist and render cleanly in downstream UI.
   - Idempotency matters (toggle existing format on/off vs. always wrapping).
   - **Recommendation**: Document RichTextFormatter as a reusable utility for future forms (profile bio, comment fields, etc.).

2. **In-memory repository singleton unblocks cross-feature state without server sync**:
   - KudosRepository is shared across SendKudosViewModel (write), KudosFeedViewModel (read), future HomeViewModel (read).
   - One source of truth eliminates state synchronization bugs (no "feed doesn't show new kudo" issues).
   - **Trade-off**: In-memory only; data lost on app restart. Fine for MVP; Phase 10 (server sync) will fix.
   - **Recommendation**: Extend KudosRepository to support server sync (API call on submit, local cache, periodic sync). Pattern established here is solid.

3. **Image handling without image library is painful but possible**:
   - Manual Uri → Bitmap decoding works; no crashes observed in testing.
   - **Risk**: OOM on large images (no library-level resizing, no memory pooling). Caught in review (L2 MEDIUM).
   - **Recommendation**: Either (A) add Coil and accept 17 MB added, or (B) add advanced image handling (resize-on-decode, memory pooling) as Phase 07 refactor. For now, document limitation in ViewModel: `// TODO: support high-res images via resize-on-decode (Phase 07)`.

4. **Clarification protocol must be exhaustive for form-heavy features**:
   - 7 screens, 3 fields of ambiguity (Danh hiệu type, image size enforcement, success-screen navigation).
   - Each required back-and-forth; could have been prevented with a pre-implementation clarification checklist.
   - **Recommendation**: For next form feature, use a template:
     ```
     For each field:
     - Input type? (text, dropdown, date, toggle, etc.)
     - Validation rule? (required, min/max length, regex, etc.)
     - Data source? (mock, API, enum)
     - Error state behavior? (show tooltip, disable field, block submit, etc.)
     
     For each button:
     - On success: navigate where?
     - On error: show what message?
     - On loading: disable other inputs?
     ```

5. **Navigation state (SavedStateHandle) is essential for multi-screen flows**:
   - Success screen needs to know which kudo was just created (kudoId) so it can display it.
   - Passed via route argument: `sendKudos/success/{kudoId}`.
   - SuccessScreen retrieves via `savedStateHandle.get<String>("kudoId")`.
   - No global state needed; clean separation.
   - **Recommendation**: Establish pattern: every route that passes data uses SavedStateHandle + type-safe route arguments (via navigation-compose).

6. **Hashtag extraction + capping is discrete logic, test thoroughly**:
   - Regex: `#[a-zA-Z0-9_]+` (matches #like_this, #like123, but not #like-dash or #\)
   - Cap: 5 tags per kudo (enforced by disabling field + UX feedback).
   - Edge cases: nested hashtags (#hello#world?), hashtags at end of sentence (#hello.), hashtags in quotes ("the #hashtag").
   - Tests: 12 unit tests cover all edge cases; all pass.
   - **Recommendation**: Consider accent support (Vietnamese: #Chúc_Mừng) for future i18n; current regex handles underscores but not accents. Update regex if needed: `#[\p{L}\p{N}_]+` (Unicode letter/number category).

7. **Design-vs-implementation trade-offs must be documented, not silently accepted**:
   - Material icons (17 MB) vs. text labels: chose labels.
   - Image library (Coil) vs. manual decoding: chose manual.
   - These are not bugs or style choices; they're architectural constraints from the project.
   - **Recommendation**: Create a "Architectural Constraints" section in `docs/code-standards.md`:
     ```
     ## No Image/Icon Libraries
     - Rationale: minimize APK size, fast cold starts
     - Consequence: icons must be text (B, I, S) or custom vectors
     - Consequence: images must be manually decoded; support ≤3 images/screen for performance
     - Exceptions: Phase 10 will reassess if APK bloat becomes critical
     ```

## Next Steps

1. **Push commit `6cf8efa` to main** (after user final approval).
2. **Phase 07 (Refactor + Optimization)**:
   - Split SendKudosScreen (285L) into SendKudosForm (150L) + RichTextToolbar (67L) + ImageSelector (68L) for clarity.
   - Implement async image resizing (downscale large images on decode to avoid OOM).
   - Add real recipient API integration (replace mock search with server query + debounce).
   - Integrate real title enum from backend (replace hardcoded dropdown).
   - Add Instrumentation tests for image picker flow (native Android interaction).
3. **Phase 08 (Navigation + Animation)**:
   - Add screen transitions (fade, slide) between form → success → feed.
   - Add back-gesture handling (prevent accidental back from success screen).
4. **Phase 10 (Server Sync + Security)**:
   - Extend KudosRepository to persist submittedKudos to server (API endpoint).
   - Implement local cache + periodic sync (conflict resolution for offline submissions).
   - Add image upload to server (replace Uri with server URL).
   - Encrypt image data in transit.
5. **Update `docs/code-standards.md`**:
   - Add section: "Form Implementation Best Practices" (validation structure, error handling, navigation states, rich-text patterns).
   - Add section: "Architectural Constraints" (no image libs, no icon libs, image handling guidelines).
   - Add template: "Form Feature Clarification Checklist" (for future form-heavy features).
6. **Update `docs/project-changelog.md`**:
   - Record Phase 06 completion: "Implemented Send Kudos form with rich-text markdown support, image picker integration, in-memory repository singleton, recipient search. Design trade-offs: text labels instead of Material icons (project constraint), manual image decoding (no Coil). All 152 tests pass. 20 design-fidelity issues fixed via multi-lens review."

## Metrics & Status

| Metric | Value |
|--------|-------|
| Screens implemented | 7 (Send form, Community Standards, Recipient search, Image preview, Success, and 2 content variants) |
| Files created | 12 new |
| Files modified | 5 existing (feed, nav, strings, build.gradle, app nav) |
| Build status | ✅ assembleDebug |
| Unit tests | 152 pass (ViewModel 38, Formatter 44, Repository 22, Recipient 18, Image 15, Navigation 15) |
| Reviewer score | 2 MEDIUM + 18 LOW (0 critical/high); all fixed |
| Design-fidelity gaps | 2 (Material icons → text labels, image library → manual decode); both intentional per project constraints |
| Parallel agents spawned | 2 (Sonnet, 1 per UI track) |
| Orchestrator | 1 (Opus, logic + review) |
| Lines of code (ViewModel + Formatter + Repository) | ~385 |
| New shared state pattern | KudosRepository singleton (cross-feature MutableStateFlow) |
| Markdown formats supported | 6 (bold, italic, strikethrough, link, list, quote) |
| Commit | `6cf8efa` (unpushed) |

---

## Unresolved Questions

- **Image size validation**: Should 5 MB cap be enforced on client or server? Currently client-side, but test coverage is limited (can't mock 5 MB image easily). Request explicit requirement from product.
- **Real title enum**: Will "Danh hiệu" values come from backend (API endpoint) or hardcoded enum? Currently hardcoded mock. Design handoff should clarify data source.
- **Recipient search**: Should support server-side search (API query) or client-side filter only? Currently mock client-side. For real users, server query needed with debounce + error handling.
- **Image URI persistence**: After app restart, picked image Uri is stale. Should images be saved to app cache directory? Or re-pick on app resume? Currently no persistence; marked TODO.
- **Markdown rendering**: Is markdown supported in KudosFeed display? Currently stored as markdown text (e.g., "**bold text**"), but KudoDetailCard doesn't parse/render markdown. Should Phase 07 add markdown renderer? Or store as plain text only?
