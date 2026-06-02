# Researcher Contract (Wave 6 — rebuild-spec)

## Session Context

Read `plans/<active-plan>/artifacts/_session-context.md` FIRST before any other artifact read. This file contains the detected stack, pointer paths to all shared artifacts, templates, and contracts, plus reminders that avoid common token-wasting patterns. Do NOT re-derive information already present in the session context file.

## Mandatory Source-Code Reading

- You MUST read the actual source code files (controllers, models, jobs, services,
  Vue/React pages) for every feature — NOT just summarize from upstream artifacts.
- Use Grep/Read tools to find the real controllers, models, jobs, routes for this F###.
- Extract specific: file paths with line ranges, method names, table/column names,
  HTTP status codes for error cases, job class names, event names.
- If you cannot read a file, note it under `## Unresolved Questions`.

## Discriminator Coverage (CRITICAL)

After identifying Key Entities, you MUST check data-model.md for discriminator fields:

1. For each entity in `## Key Entities`, read its `**Discriminator Fields**` block in data-model.md.
2. If ANY entity has DISC-### entries → you MUST write `## Polymorphic Behavior` with one subsection per DISC-###.
3. Cover ALL values listed in data-model.md. Missing a known value = CRITICAL reviewer rejection.
4. Include ≥1 edge case per variant in the `### Edge Cases` table (e.g., what happens when a record arrives with an unexpected/deprecated value).
5. Each behavior cell must be grounded in source code. Write `unverified` if you cannot confirm from code — do NOT leave blank or fabricate.

**N/A fallback** — only valid when Key Entities have zero DISC-### in data-model.md:
`N/A — no discriminator fields in Key Entities.`

**Section is ALWAYS present.** Omitting `## Polymorphic Behavior` entirely = CRITICAL.

#### SM `kind` Classification

For every extracted SM-###, set `**kind:**` as the first metadata line after the heading:
- **`entity`** — state field is persisted (DB column, ORM attribute, model field, type defined in data-model.md). Look for: database enum columns, ActiveRecord state machine, ORM status fields, type aliases in data-model.md.
- **`ui`** — state is component-local (useState, ref, computed, signal, local var — NOT persisted beyond the render cycle). Look for: form submission status, modal open/close, panel collapsed, loading/error/success cycles.
- If the same concept has both (entity status + UI loading mirror), document as 2 separate SM-### blocks.
- **Threshold:** only classify `kind: ui` for state machines with ≥3 states OR ≥2 transitions. Smaller cases stay implicit in BR-### rules.

### Client-Side Logic Extraction (→ behavior-logic.md § Client-Side Logic)

Scan for these 5 patterns in client/frontend source files. For each found, add an entry to the `## Client-Side Logic` section of `behavior-logic.md`:

**Debounce / Throttle**
Signature: `setTimeout`/`clearTimeout` wrapping a handler, `debounce(fn, ms)`, `throttle(fn, ms)`, `useDebounce`, `useDebouncedCallback`
Capture: trigger location (file:line), delay value, what action is debounced.

**Optimistic UI**
Signature: state mutation applied before `await`, with a catch/rollback block; `useOptimistic`, `optimisticUpdate`
Capture: trigger (user action), optimistic state change, rollback target, API endpoint.

**Polling**
Signature: `setInterval` calling an API, recursive `setTimeout` + API call, `refetchInterval`, `usePolling`
Capture: interval duration, condition to stop polling, API called.

**Upload Progress**
Signature: `XHR.upload.onprogress`, `axios onUploadProgress`, `fetch` with streaming body, `useUpload`, `onProgress`
Capture: trigger action, progress state field name, error path.

**Realtime**
Signature: `new WebSocket(...)`, `new EventSource(...)`, `useWebSocket`, ActionCable subscribe, Pusher subscribe, SSE listener
Capture: channel/URL pattern, trigger (mount/user action), reconnect strategy, teardown (unmount handler).

If a pattern is absent in the entire codebase, write `N/A — no {pattern} patterns detected.` in the corresponding subsection.

### Client-Side Gate Extraction (→ permissions.md)

Scan for runtime gates that affect UI rendering. Use these function-name signatures — do NOT hard-code library names:

**feature-flag:** `useFlag|useFeature|isEnabled|featureFlag\(|checkFlag` — capture first string argument (flag name), file:line, effect (what branch differs).

**experiment:** `useExperiment|getVariant|abTest\(|experiment\.variant|useAbTest` — capture experiment name, variant identifiers found in code.

**env-gate:** comparison against `process\.env\.|import\.meta\.env\.|ENV\[|os\.environ\[` followed by `===`/`==`/`in (...)` — capture env var name, compared value, effect.

**locale-gate:** comparison against `i18n\.locale|currentLocale|getLocale\(\)|locale\s*===|lang\s*===` — capture locale value, effect.

Name-only rule: capture the flag/experiment name found in source. Do NOT look up the flag's configuration in LaunchDarkly, Statsig, or any external service.
If none found: write `N/A — no {type} gates detected.` for each absent type.

### Screen-Flow Client-Side Extraction (→ screen-flow.md)

**Guard Logic**
Signature: function/method definitions tied to route entry: `beforeEnter|canActivate|middleware|loader|before_action|authenticate|authorize` — check if registered in router config or route annotation.
For each guard: record GUARD-### id, trigger hook name, source file:line, pseudocode logic (if/redirect chains), failure path.
N/A rule: only write `N/A — no route guards detected.` after confirming no route-level interception exists (check router config files, not just component code).

**Deep-Link State Restoration**
Signature: URL param reads at component mount → state sync: `useSearchParams|useQuery|router\.query|URLSearchParams|params\[|$route\.query`
For each screen: record URL pattern, table of param → UI state, default-if-missing, failure mode.
N/A rule: write `N/A — no URL-driven state restoration detected.` only if no URL params are read into component state.

**Unsaved-Changes Protection**
Signature: `beforeunload|onbeforeunload|usePrompt|useBeforeUnload|leaveGuard|isDirty|formState\.isDirty|data-turbo-confirm`
For each form/screen: record trigger, source, dirty detection method, exact prompt text.
N/A rule: write `N/A — no unsaved-changes guards detected.` only after checking all forms with user input.

### Client Behavior Anchor (→ feature-spec.md § Cross-Cutting Logic)

Every feature spec MUST include the client behavior anchor block in `## Cross-Cutting Logic`, even if all 3 linked artifacts have mostly N/A content. The anchor links are relative from `docs/specs/features/F###/spec.md`:

```markdown
---

**Client behavior:** see
[`behavior-logic.md`](../../behavior-logic.md) (client-side patterns — debounce, optimistic UI, polling, upload, realtime),
[`permissions.md`](../../permissions.md) (feature flags / experiments / env / locale gates),
[`screen-flow.md`](../../screen-flow.md) (guards / deep-link state restoration / unsaved-changes protection).
```

This anchor is always present. The linked files may have N/A sections — that is fine and correct.

## Decision Logic Extraction (DEC-###)

DEC-### captures decisions with **user-visible business outcome** — regardless of which file the code lives in. Component files, saga files, controller files, route guards — all valid Sources if the decision changes what the user sees, does, or where they go.

### Scope statement

Scope is **outcome-based**, **source-location-agnostic**. The question is: "Does this decision change what the user sees, interacts with, or where they navigate?" If yes → DEC candidate. If no → skip.

### Subtype signatures (extraction patterns)

**render — multi-predicate render branches:**
- JSX/template trees with conditional rendering involving ≥2 predicates OR non-single-field predicates
- Signature: `{(condA && condB) ? <A/> : <B/>}`, `v-if="condA && condB"`, computed render props using ≥2 entity fields, switch statement rendering different components
- SKIP single-field conditions — those go to DISC (enum or boolean)
- SKIP cosmetic toggles (class names, padding, color only)

**interaction — event handlers with visible business meaning:**
- Event handlers (`onClick`, `onChange`, `onSelect`, `onKeyDown`) where body reveals/hides substantive UI, focuses a new input, or shows/hides meaningful sections
- SKIP form-field two-way binding (input value mirrors state only)
- SKIP loading spinner reveals (`isLoading ? <Spinner/> : ...`)
- SKIP error toast displays (those are error-handling, separate section)

**flow — multi-step / navigation / step routing:**
- Functions/handlers advancing user position in a flow: `setStep(...)`, `next()`, `history.push(...)`, `router.push(...)`, `redirect(...)`, conditional rendering of next/previous wizard page
- In-feature scope: stays within feature (multi-step wizard advance, sub-screen routing, post-action navigation)
- SKIP cross-feature navigation — that is screen-flow.md territory
- SKIP error redirects to global error pages — that is error-handling territory

### Anti-examples (skip these — they are plumbing)

```pseudo
❌ if isLoading → show spinner          (loading toggle — not decision)
❌ if api.success → dispatch SUCCESS    (dispatch wrapper — not decision)
❌ if !error → show data                (presence check — not branching)
❌ if token expired → refresh token     (fetch mechanic — not user-facing)
❌ if response.status === 200 → ...     (HTTP plumbing)
❌ if user.locale === 'en' → load 'en'  (i18n routing — not business)
❌ debounce(handler, 300)               (timing wrapper)
❌ if cache hit → return cached         (cache mechanic)
```

If uncertain, ask: **"Does this affect what the user SEES, DOES, or WHERE they go?"** If no → skip.

### DISC vs DEC boundary

- Single-field condition with **enum type** (≥2 named values with distinct behavioral outcomes) → **DISC**
- Boolean flag (`is_published: boolean`, `is_active: boolean`) → **Business Rule** or FR note, NOT DISC
- ≥2 predicates OR interaction-driven OR flow-step routing → **DEC**

Examples:
- `if question.type === 'multiple choice' → render MultipleChoice` → DISC (single enum field)
- `if survey.published === true → show banner` → Business Rule (single boolean, no DISC code needed)
- `order.status` enum with values `pending / shipped / cancelled` each affecting UI → DISC (enum ≥2 values)
- `user.is_admin === true → show admin panel` → DEC-interaction or Business Rule depending on complexity; NOT DISC
- `if user.role === 'admin' AND survey.published_at !== null → show MetricsPanel` → DEC (multi-predicate render)
- `on option select: if option.is_other_option → reveal CommentInput` → DEC (interaction reveal)
- `on submit success: if survey.type === 'mbti' → push('/result')` → this is single-field but it's a navigation flow decision — DEC-flow (navigation post-action with meaningful routing)

`N/A — no discriminator fields in Key Entities.` is valid when Key Entities have zero DISC-### entries in data-model.md. Boolean fields are NOT DISC-### — their absence from Polymorphic Behavior is correct.

### Multi-subtype guidance

If a decision spans ≥2 dimensions (e.g. a Submit button that both validates form AND advances wizard), declare `subtype: render, flow`. The pseudocode block captures both dimensions together.

### Required output fields per DEC

Each DEC-### block MUST include:
- `**subtype:**` — list (≥1, comma-separated: `render`, `interaction`, `flow`)
- `**Triggers in:**` — screen code (SCR###) + triggering event or lifecycle hook
- `**Involved entities:**` — `Entity.field` names that drive the branching
- `**user_visible_outcome:**` — 1-sentence justification (what changes for the user)
- `**Source:**` — `file/path.js:start-end` (read actual line range; source location NOT validated — saga ok)
- Pseudocode block ≤8 lines (`pseudo` or `js`/`ts`/`py` hint)

### N/A fallback rule

`N/A — no user-facing decision logic beyond DISC-### Polymorphic Behavior.` is ONLY valid if the feature has **zero** non-plumbing user-facing branches AND no multi-predicate / interaction / flow decisions. Researcher MUST scan all source files implicated by the feature before writing N/A. If component files contain JSX ternaries with ≥2 predicates, N/A is invalid.

## Scope & Codes

- All FR/BR/SM/ALG/INT/SC codes are LOCAL to this spec. Cross-spec refs (e.g., "see BR-001 in F002") are INVALID.
- Every BR/SM/ALG/INT block MUST cite `**Source:** path/to/file.ext:start-end`.
  NEVER fabricate paths or line ranges. Confirm each citation by reading the range.
- BR/SM/ALG/INT heading MUST use `### {PREFIX}-###_NameSlug` (e.g., `### BR-001_OrderMinItems`).
- Pseudocode ≤20 lines per block. Use a concrete language hint (ts/py/php/go) or `text`. NEVER leave `{lang}` literally in the fence.
- Pseudocode MUST NOT embed secrets or credentials.

## Placement Rules

- FR-### MUST appear under EXACTLY ONE of:
    (a) a US's `**Requirements fulfilled:**` list, OR
    (b) `## Cross-Cutting Logic > ### Requirements` (for FRs spanning ≥2 USs equally).
  An FR-### appearing in both locations, or in neither, is CRITICAL.
- BR/SM/ALG/INT defined under a US apply PRIMARILY to that US.
  Another US MAY reference by code only: `BR-001 (see US001)`. No duplicate Source block.
  A BR/SM/ALG/INT that applies to ≥2 USs equally → move to `## Cross-Cutting Logic`.
- SC-### appears inline under the US it validates (`**Verification:**`),
  OR under `## Cross-Cutting Logic > ### Verification` for global SCs.
  No standalone `## Success Criteria` heading in submitted specs.

## Preamble Rules

- `## Why This Exists`, `## Who Uses It`, `## Business Workflow`, `## Screen Flow` MUST each be populated
  with prose OR the literal fallback `N/A — {justification}.` Leaving placeholder text is CRITICAL.
- `## Why This Exists` — derive rationale from route names, comments, or domain context.
  If no signal, write EXACTLY: `N/A — inferred from code; domain confirmation needed.`
  DO NOT fabricate product rationale.
- `## Screen Flow` MUST start with `**See:** ScreenFlow § {F###_entry}`.
  For UI/mixed features, MUST include a bullet list of owned screens with route path and
  atomic/composite annotation. For composite screens, list REG### inline:
  `- SCR###_Name — `/route` (composite: REG001 {label}, REG002 {label})`.
  The forward-ref anchor `F###_{name}` resolves to `## Feature Entry Points § F###_{name}`
  in screen-flow.md (populated by W6 fragment consolidation — not W2 ScreenFlow researcher).
  Background-only features: write `N/A — background feature; no user-facing screen flow.`
  Screen Route Table (3-column: Screen|Route|Purpose) is DEPRECATED — do NOT write it.
  Use bullet list format only.
- Preamble sections contain NO FR/BR/SM/ALG/INT/SC codes.

## Depth Requirements (CRITICAL — incomplete sections = reviewer rejection)

- `## Business Workflow`: MUST use numbered steps (≥3 for non-trivial features).
  Each step references specific entities, table names, job classes, or field names
  found in source code. Generic prose like "user does X" without specifics is REJECTED.
- `## Cross-Cutting Logic > ### Business Rules`: extract ALL guards, validations,
  and constraints from controllers/services/policies. Each BR needs:
  (a) exact behavior description with HTTP status codes for error cases
  (b) `**Source:** file.ext:start-end` with verified line range
  Aim for ≥3 BRs per UI feature; ≥1 per background feature.
- `## User Stories`: each US MUST have `**What happens:**`, `**Why this priority:**`,
  `**Independent Test:**`, and Given/When/Then acceptance scenarios.
  Vague scenarios like "works correctly" are REJECTED.
  Each US MUST list `**Endpoints**: METHOD /path` for the routes it touches.
- `### Edge Cases`: MUST contain ≥3 rows for UI features. Each row specifies
  the scenario, the system behavior, and the HTTP status code / error message.
- `## Key Entities`: table MUST list ALL database tables this feature reads/writes.
  Include table name, key columns used, and purpose. ≥3 entities for non-trivial features.
- `## Source Code References`: MUST list the primary controller(s), model(s), job(s),
  service(s), and page/component file(s) with their paths and line ranges.
  ≥3 entries required.
- `## Assumptions`: ≥2 entries for any non-trivial feature. Document implicit
  behaviors, missing DB constraints, eval assumptions, etc.
- `## Unresolved Questions`: list anything you could NOT verify from source code
  or that has ambiguous behavior. ≥1 entry expected for complex features.

## Empty-Section Rule

- `## Cross-Cutting Logic` subsections: if feature has none of a kind, write `None.` under that H3.

## Artifact References Rules

- `## Artifact References` is a single merged table replacing the legacy `## Related Artifacts` + `## Spec Documents` two-section format.
- The table has 4 columns: `Artifact | File | Codes Used | Reviewed`.
- Always include System Overview and Feature List (they apply to every feature; mark Reviewed `[x]`).
- For all other artifacts: list the specific codes referenced in Codes Used (e.g. `SCR001, SCR001/REG001`).
- Codes Used column lists the same code set previously in `## Related Artifacts`. The orphan-ref rule applies: every code listed MUST exist in its source artifact.
- For region ownership: use `SCR###/REG###` format in Codes Used. Parent SCR### must exist in ScreenList.
- Unchecked rows (`[ ]`) stay in the table so consuming agents know these docs exist.
- **LEGACY FORMAT CRITICAL**: specs using the old two-section format (`## Related Artifacts` + `## Spec Documents`) are flagged CRITICAL immediately — no transition window. Researcher MUST use the merged `## Artifact References` table.

## All Sections Mandatory

- Every H2 section in the template body MUST appear in submitted specs:
  Overview, Why This Exists, Who Uses It, Business Workflow, Screen Flow,
  Polymorphic Behavior, Cross-Cutting Logic, User Stories (with Edge Cases), Key Entities,
  Artifact References, Assumptions, Source Code References,
  Unresolved Questions.
- Missing ANY of these sections is a CRITICAL reviewer failure.

## Appendix

- The `## Appendix — Worked Example` at the bottom of the template is HTML-commented so it does NOT render in specs.
  DELETE the entire HTML-comment block before submitting a real spec.
  A spec with `## Appendix` in its heading tree is CRITICAL.

## Task Closure

On successful spec.md write (and after `rm .pending` succeeds), call `TaskUpdate(status=completed)` on this task id BEFORE returning. Belt-and-suspenders with reconcile preflight — reduces orphan tasks visible in TaskList between waves.

## Folder Lifecycle (Wave 5 / Wave 6 contract)

- Wave 5 pre-creates `plans/<active-plan>/artifacts/features/{slug}/` and writes a `.pending` marker (zero-byte file) per feature.
- On SUCCESSFUL `spec.md` write, the W6 researcher MUST run: `rm plans/<active-plan>/artifacts/features/{slug}/.pending`.
- Failure to remove `.pending` causes Wave 7b reviewer to mark this feature as `MISSING` (counts toward the review report's `failed` total and blocks Wave 9).
- If the spec write itself fails, leave `.pending` intact — the marker signals a partial write to downstream stages.
- See `references/canonical-fcode-schema.md` § Folder Lifecycle and `references/verification-checklist.md` § Pending Marker Rule.
