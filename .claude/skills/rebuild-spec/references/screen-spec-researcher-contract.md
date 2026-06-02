# Screen Spec Researcher Contract (Wave 2.5 — rebuild-spec)

Consumed by W2.5 researcher subagents generating per-screen ScreenSpec artifacts.
Activated only when `--with-screen-specs` flag is set.

## Session Context

Read `plans/<active-plan>/artifacts/_session-context.md` FIRST. Then read the target SCR### section from `screen-list.md` and the relevant entity sections from `data-model.md`. Do NOT re-derive information already in the session context.

Note the project stack (frontend framework + backend language) from session-context.md. JS/TS (Vue/React) patterns are shown as primary examples throughout; inline `(stack: ...)` notes in each section provide alternatives for Python, Rails, PHP, and other stacks.

## Purpose Extraction Rule

Write 1 sentence in plain language: who uses this screen (role/persona), what they accomplish (goal), and when they encounter it (entry point or trigger).

**Voice:** User-narrative, not implementation-narrative.
- REJECT: "The Vue page renders a `FormContainer` with `useFormStore` composable..."
- ACCEPT: "Survey participants complete and submit assigned questionnaire forms from this screen."

**Source signals:** Page title, route name, primary CTA button label, primary form submit handler or main action event.
**N/A:** NOT allowed. If purpose is unclear from code, write `[UNVERIFIED] {best-effort description} — needs domain confirmation.`

## Scope Boundary (CRITICAL)

**ScreenSpec documents UI-LAYER behaviour only.**

| Belongs in ScreenSpec | Belongs in Feature Spec |
|----------------------|------------------------|
| UI states (loading/empty/error/success) | Server-side validation rules (BR/SM/ALG) |
| Client-side form validation (field constraints, async checks) | Business workflow (BR/SM/ALG) |
| Interaction patterns (optimistic update, infinite scroll) | Background logic (BL###) |
| ARIA roles, keyboard navigation, focus management | Decision logic (DEC-###) |
| Client-side conditional rendering (role gates, feature flags, breakpoints) | FR/BR/SM/ALG/INT/SC codes |
| Screen-specific UI state machines | Cross-feature navigation (→ screen-flow.md) |
| Server-side validation feedback visible to the user (error messages, toasts, banners) | Server-side validation logic itself |

**Never write FR/BR/SM/ALG/INT/SC codes in ScreenSpec.** Cross-reference `data-model.md` for server-side field constraints (do not re-state them — reference the source).

## Mandatory Source-Code Reading

- You MUST read the actual source code files (controllers, models, jobs, services,
  Vue/React pages) for every screen — NOT just summarize from upstream artifacts.
- Use Grep/Read tools to find the real page/view files, form schemas, state managers for this SCR###.
- Extract specific: file paths with line ranges, component names, prop names, API call sites.
- If you cannot read a file, note it under `## Unresolved Questions`.

**Import Discovery Rule:** After reading the page/view file, collect all non-vendor component imports (JS/TS: `./` or `@/`; Python templates: `{% include %}`/`{% extends %}`; Ruby: `render partial:`/`require_relative`; PHP/Blade: `@include`/`@component`/`@extends`; generic: include/import/require patterns). For each imported component file:
1. Grep for async/form signals — JS/TS: `useQuery|useMutation|axios|\$fetch|api\.|useForm`; htmx: `hx-get|hx-post`; generic: `\.get(\|\.post(\|fetch(\|validate\|rules` *(omit Vue-only `emit\(` and `v-model` — too broad)*
2. If matched AND file > 20 lines → read lines containing the matched pattern ±30 lines of context
3. Document behavior-relevant findings under the appropriate section (UI States, Validation, Interaction Patterns)
4. If file cannot be read: add to `## Unresolved Questions` as `(not read — referenced by import)`

**Depth:** 1 level only (page imports → components; do NOT follow component-to-component imports).

## Extraction Signatures

### 4.1 Screen Layout

Read the page/view file root template or JSX. Identify top-level container structure.

- Required: name all major regions (header, sidebar, main content, modals/drawers)
- Note fixed/sticky/scrollable positioning; note responsive breakpoint signals (CSS class names, `useBreakpoint`, media queries)
- Cite layout root file:line in trailing parenthetical
- **N/A: not allowed.** Layout is always documentable from source. If truly unreadable, escalate as Unresolved Question.

### 4.2 UI States

Scan page/view file and immediate dependencies for async signals:

- Loading: `isLoading`, `loading`, skeleton components, `Suspense`, spinner
- Empty: `isEmpty`, `data.length === 0`, empty-state components, zero-results branches
- Error: `isError`, `error !== null`, error boundary, catch blocks in data hooks
- Success: toast calls, `showSuccess`, confirmation components, mutation success handlers
- Custom: any named state not covered above (`isDraft`, `isPending`, etc.)

**Depth rule:** Scan for async calls by frontend paradigm — SPA (Vue/React/Angular): `axios|fetch|useQuery|useMutation|asyncData|\$fetch|API\.|api\.`; htmx-driven: `hx-get|hx-post|hx-patch|hx-delete|hx-trigger`; Turbo (Rails): `data-turbo-stream`; generic: `\.get(\|\.post(\|\.put(\|\.delete(` — produce ≥1 error row per distinct async endpoint + ≥1 empty row per data-displaying region. For each state: record trigger, visual component, user actions, source file:line.

**N/A:** `N/A — no async ops` — valid only when grep returns zero async matches AND screen renders no list/data block.

### 4.3 Validation & Error Feedback

**Sub-table A — Client-side:**
Scan for client-side validation (runs in browser — backend validation rules belong in Feature Spec, not here) — JS/TS: `useForm`, `Formik`, `react-hook-form`, `vee-validate`, `VueUseForm`, Zod/Yup/Joi; all stacks: HTML5 `required`/`pattern`/`min`/`max`; generic: `validate|rules|constraints`. For each field: name, type, required, constraints, async check endpoint, error message string.

**N/A (A):** `N/A — no client-side form validation detected.`

**Section B — Server-side (per-action blocks):**

Signature: action handler containing `await {api}.{method}(...)` combined with `.catch` / try-catch / `.then(err => ...)` that surfaces a user-visible error (toast, banner, inline message).

For each action, produce one block:
- **Endpoint:** HTTP method + path (from `axios`/`$fetch`/`api.` call site)
- **Request:** field names the UI sends (from call argument object; skip auth headers). If payload is assembled in a composable beyond Import Discovery depth, write `[UNVERIFIED] — payload assembled outside 1-level read depth; see Unresolved Questions`.
- **Success:** HTTP code + outcome visible to user (redirect URL, toast text, state change)
- **Errors:** HTTP code(s) + user-visible message text. Use `[UNVERIFIED]` when message is server-driven and not readable from source.
- **Trigger:** gesture that fires the action (button click, form submit, keyboard shortcut)
- **Source:** file:line of the action handler

**N/A (B):** `N/A — no submit-style action handlers detected.`

### 4.4 Interaction Patterns

Extract non-trivial patterns: optimistic update, infinite scroll, drag-drop, debounced search, keyboard shortcut.

**Format MUST be behavior-first:** `**{User behavior — observable outcome}** — source: {file:line}`

Implementation details (handler name, store action) may appear in trailing parenthetical — NEVER first.

- REJECT: "Root `div` has `@click='resetTarget'` which calls `store.dispatch(...)`..."
- ACCEPT: "Clicking outside any group deselects it — source: questions.vue:2"

Researcher MUST reformulate any handler-first extraction before writing.

**N/A:** `N/A — no non-trivial interaction patterns detected (only standard form input bindings).`

### 4.5 Accessibility

Always produce the 4-row table (Aspect | Status | Notes). Bare `N/A — no ARIA attributes detected` is BANNED at section level.

Per-row scan signatures:
- **ARIA roles/labels:** `aria-\w+|role=` across component files
- **Keyboard navigation:** explicit `keydown|keyup|keypress|tabindex` handlers
- **Focus management:** `\.focus\(\)|focus-trap|useFocus|autofocus`
- **Screen reader:** `<label>` linkage, semantic landmarks (`role="main"`, `role="dialog"`)

When all 4 status cells are absent/unmanaged/unknown, append:
`[NO_A11Y_DETECTED] — accessibility audit needed before production release.`

### 4.6 Conditional Rendering

Scan for runtime gates by type:

- **auth:** `hasRole`, `currentUser.role`, `can(`, `ability.can`, `isAdmin`, permission-based visibility
- **feature-flag:** `useFlag`, `useFeature`, `isEnabled`, `featureFlag(`, `checkFlag`
- **responsive:** CSS-in-JS breakpoint checks, `useMediaQuery`, `useBreakpoint`, Tailwind responsive classes controlling visibility
- **legacy:** condition that compares against a string constant name (e.g., `status === 'LEGACY_MODE'`) with no feature-flag API
- **hardcoded-id:** condition that compares a route param, entity ID, or field value against a numeric/string literal (e.g., `Number($route.params.form) === 458`)

**Notes column rules:**
- `auth` type → Notes MUST state: "Consequence if bypassed: {redirect / 403 / data exposure}". If consequence cannot be determined from source, write `[UNVERIFIED] consequence — needs security review`.
- `hardcoded-id` or `legacy` type → Notes MUST contain: `[NEEDS_DOMAIN_CONFIRMATION] — {description of what this gate does}; unknown whether legacy bug, feature flag, or intentional design`
- `feature-flag` / `responsive` → Notes optional

**N/A:** `N/A — no conditional rendering detected.`

### 4.7 Component Variants (optional)

**Trigger:** component imported on this screen also appears in ≥2 other screens in ScreenList AND renders ≥2 visual variants based on a discriminating field.

Scan: component file's render switch (`v-if`, switch statement, ternary chain) keyed off a prop value.

Output: screen-specific props/slots only. Reference DISC-### in data-model.md OR feature-spec § Polymorphic Behavior for variant business rules. DO NOT re-document the component's universal behavior.

**Omit section entirely when criteria not met** — this is the only optional section.

### 4.8 Source References

- Minimum 1 entry: the page/view file
- List every file actually read while extracting (not aspirational paths)
- If a file should exist but could not be found, log under Unresolved Questions — never fabricate

### 4.9 Security Surface

**Trigger:** Populate when Conditional Rendering has ≥1 `auth`-type row OR a route guard / navigation guard is found in the router config for this screen's route.

**Scan for:**
- Route-level guards (find router/routes config, check this screen's route entry): JS/TS: `src/router/index.{js,ts}` — grep `beforeEnter|meta\.requiresAuth|meta\.roles|router\.beforeEach`; Python: grep `@login_required|LoginRequiredMixin` in views/urls; Rails: grep `before_action :authenticate` in controllers; PHP: grep `->middleware('auth')` in route files; generic: grep `auth|guard|middleware` on route entries
- Component-level guards (auth conditional gating component render): JS/TS: `v-if="isAuthenticated"`, `can('action','resource')`; Python: `{% if user.is_authenticated %}`; Rails: `if current_user.admin?`; PHP: `@auth`/`@can` Blade directives; generic: conditional block keyed on auth/permission check
- Data-access guards: API calls that return 403 when unauthorized (document the permission boundary)

**For each guard, record:**
- Guard expression or middleware name
- Type: `auth` (login required) | `permission` (role/ability check) | `role` (specific role required)
- Consequence if bypassed: what a non-authorized user would see or access. If not determinable from static analysis, write `[UNVERIFIED] server enforcement — static analysis cannot confirm API middleware`.

**N/A:** `N/A — no auth guards or permission checks detected on this screen.`
Valid only when: Conditional Rendering has zero `auth`-type rows AND router config has no guard for this route.

## N/A Fallback Master Rule

Researcher MUST scan source file + immediate imports before writing any N/A. N/A is valid only after confirming absence — not as a default. At least one section MUST be populated (all-N/A = reviewer warning).

| Section | Exact N/A string |
|---------|-----------------|
| Screen Layout | N/A — not allowed; escalate as Unresolved Question |
| UI States | `N/A — no async ops` |
| Validation A (client) | `N/A — no client-side form validation detected.` |
| Validation B (server) | `N/A — no submit-style action handlers detected.` |
| Interaction Patterns | `N/A — no non-trivial interaction patterns detected (only standard form input bindings).` |
| Accessibility | No section-level N/A; always write 4-row table |
| Conditional Rendering | `N/A — no conditional rendering detected.` |
| Component Variants | Omit section entirely |
| Purpose | N/A — not allowed; write [UNVERIFIED] if unclear |
| Security Surface | `N/A — no auth guards or permission checks detected on this screen.` |

## [UNVERIFIED] Marker Protocol

Use `[UNVERIFIED]` when a value is observable only at runtime and cannot be confirmed from source alone.

**Format:** `[UNVERIFIED] {best-effort description} — needs runtime confirmation`

**Example:** `[UNVERIFIED] "Email already registered" toast — needs runtime confirmation`

- Distinct from **N/A** — N/A means "scanned, confirmed absent"; [UNVERIFIED] means "likely present, not confirmable from static analysis"
- Distinct from **fabrication** — fabrication is banned; [UNVERIFIED] is a tracked best-effort with explicit caveat
- Use for: exact server error message text, toast duration, animation timing, backend-driven content

**Canonical trailing phrase variants:**
- `— needs runtime confirmation` — use when value is observable at runtime (toast text, animation timing)
- `— needs domain confirmation` — use when value requires domain expert input (purpose, business intent)

## [NEEDS_DOMAIN_CONFIRMATION] Marker Protocol

Use `[NEEDS_DOMAIN_CONFIRMATION]` when a condition's intent cannot be determined from static analysis alone and requires domain expert input.

**When to use:** Conditional Rendering rows where `type` is `hardcoded-id` or `legacy`.

**Canonical format:** `[NEEDS_DOMAIN_CONFIRMATION] — {what this gate does}; unknown whether legacy bug, feature flag, or intentional design`

**Example:** `[NEEDS_DOMAIN_CONFIRMATION] — hides form for form ID 458; unknown whether legacy bug, feature flag, or intentional design`

- Distinct from `[UNVERIFIED]` — [UNVERIFIED] means "likely present but unconfirmable from source"; [NEEDS_DOMAIN_CONFIRMATION] means "present and confirmed, but purpose is unknown"
- Always include a description of what the gate does — never write bare `[NEEDS_DOMAIN_CONFIRMATION]`

## SHARED_COMPONENT Extraction Rule

A component is SHARED when it appears in ≥2 screens in ScreenList.

For shared components on this screen, document ONLY:
- Props this screen passes to the component
- Slot content this screen injects
- Events this screen handles from the component

DO NOT re-document the component's universal behavior. Defer to:
- DataModel DISC-### entry (for discriminator-driven variants)
- Feature spec § Polymorphic Behavior (for cross-feature variant logic)

## Output Path

Draft: `plans/<active-plan>/artifacts/screens/{SCR###_Name}/spec.md`

Final (promoted by Wave 9): `docs/specs/screens/{SCR###_Name}/spec.md`

## Task Closure

Call `TaskUpdate(status=completed)` on this task BEFORE returning.
