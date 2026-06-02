<!-- Contract: references/screen-spec-researcher-contract.md -->

# {SCR###_Name} — Screen Spec

**Screen**: {SCR###_CODE}: {NAME}
**Type**: {atomic|composite}
**Route**: {URL}
**Generated**: {DATE}

## Purpose

{1 sentence: who uses this screen, what they accomplish, and when they encounter it. Plain language — no component names, no technical internals.}

## Screen Layout

{2–4 sentences naming major regions (header, sidebar, main content area, modals/drawers). Note fixed/sticky positioning and responsive breakpoints if present. Cite layout root file:line in trailing parenthetical.}

<!-- N/A is NOT allowed here. If layout is truly unreadable, escalate as Unresolved Question instead. -->

## UI States

> **Required rows:** loading + ≥1 error (per async call) + ≥1 empty (per data-displaying region) + saving/submitting + success/redirect. Write `N/A — no async ops` only if screen has zero API calls.

| State | Trigger | Visual Behavior | User Action Available | Source |
|-------|---------|----------------|-----------------------|--------|
| loading | API in-flight | skeleton/spinner | none | `{file}:{line}` |
| empty | 0 results | empty-state illustration + CTA | {CTA label} | `{file}:{line}` |
| error | API error / network | error message + retry | retry | `{file}:{line}` |
| saving | mutation in-flight | button spinner / disabled form | none | `{file}:{line}` |
| success | mutation complete | toast / inline confirmation | dismiss | `{file}:{line}` |
| {custom} | {trigger} | {behavior} | {action} | `{file}:{line}` |

## Validation & Error Feedback

### A) Client-side

| Field | Type | Required | Constraints | Async Check | Error Message |
|-------|------|----------|-------------|-------------|---------------|
| {field} | {type} | yes/no | {min/max/regex} | {endpoint if any} | {message} |

{`N/A — no client-side form validation detected.`}

### B) Server-side

<!-- One block per submit-style action. -->

#### {Action name}
- **Endpoint:** `{METHOD /path}`
- **Request:** `{field1, field2, ...}` *(fields the UI sends)*
- **Success:** `{HTTP code}` → {outcome: redirect / toast / state change}
- **Errors:** `{code}` {user-visible message or toast text} | `{code}` {message}
- **Trigger:** {gesture — button click / form submit / keyboard shortcut}
- **Source:** `{file}:{line}`

{`N/A — no submit-style action handlers detected.`}

## Interaction Patterns

<!-- Format: "**{User behavior — observable outcome}** — source: {file:line}" -->
<!-- BAD: "Root div has @click='resetTarget' which calls store.dispatch..." -->
<!-- GOOD: "Clicking outside any group deselects it — source: questions.vue:2" -->

- **{User behavior — observable outcome}** — source: `{file}:{line}`

{`N/A — no non-trivial interaction patterns detected (only standard form input bindings).`}

## Accessibility

| Aspect | Status | Notes |
|--------|--------|-------|
| ARIA roles/labels | {present\|absent\|partial} | {aria-label, role=, aria-labelledby usage} |
| Keyboard navigation | {supported\|not implemented\|unknown} | {tab order, shortcut keys} |
| Focus management | {managed\|unmanaged} | {modal/drawer focus trap, autofocus} |
| Screen reader compatibility | {unknown\|tested} | {label linkage, semantic landmarks} |

{When all status cells are absent/unmanaged/unknown: `[NO_A11Y_DETECTED] — accessibility audit needed before production release.`}

## Conditional Rendering

| Condition | Type | Renders | Hidden | Notes |
|-----------|------|---------|--------|-------|
| {role/flag/breakpoint/literal} | {auth\|feature-flag\|responsive\|legacy\|hardcoded-id} | {component} | {component} | {consequence of bypass for auth; [NEEDS_DOMAIN_CONFIRMATION] for hardcoded-id/legacy} |

{`N/A — no conditional rendering detected.`}

## Component Variants

<!-- Omit this section if no shared polymorphic component renders on this screen. -->

| Component | Discriminating field | Variants on this screen | Screen-specific props/slots | Cross-ref |
|-----------|---------------------|------------------------|----------------------------|-----------|
| {ComponentName} | {prop/field} | {variant-a, variant-b} | {props this screen passes} | DISC-### |

<!-- Cross-ref: prefer DISC-### from data-model.md when available. DO NOT restate variant business rules — reference only. -->

## Security Surface

| Guard | Type | Consequence if bypassed |
|-------|------|------------------------|
| {expression / route guard / middleware name} | {auth\|permission\|role} | {redirect / 403 from API / data exposure} |

{`N/A — no auth guards or permission checks detected on this screen.`}

<!-- When not triggered (no auth-type CR rows, no route guards found): use the N/A string above. When triggered: replace the placeholder row with real guard entries. -->
<!-- For each guard, note server enforcement: "[UNVERIFIED] server enforcement — static analysis cannot confirm API middleware" when unknown. -->

## Source References

- Page/View: `{file}:{line}`
- Form schema / validation: `{file}:{line}`
- State management: `{file}:{line}`

{List every file read to produce this spec. Minimum 1 entry (the page/view file). DO NOT fabricate paths.}
