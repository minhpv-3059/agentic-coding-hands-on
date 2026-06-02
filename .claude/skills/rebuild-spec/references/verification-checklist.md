# Verification Checklist

Consumed by `reviewer` subagent when validating rebuild-spec artifacts.

## How to use

Load this file + target artifact + cross-ref artifacts listed per section.
Output: per-issue list with severity (`critical`/`warning`) and `location.file:line`.
`passed` iff `failed === 0`.

## Validator Pre-Check Protocol

Before applying this checklist to feature specs (W7b), the reviewer MUST read `plans/<active-plan>/artifacts/validation/validation-summary.json`. The orchestrator injects per-fcode validator state into each W7b TaskCreate prompt. The reviewer behaves differently per state:

| Validator status (per fcode) | Reviewer behavior |
|------------------------------|-------------------|
| PASS (no validator issues) | Skip all `rule_id`s listed in `## Deterministic Validator Coverage`; mark them `[deterministic-pass]` in the review report. Focus on semantic depth (BR depth, FR/SC coverage, fabricated citations, cross-ref accuracy, edge case sufficiency). |
| WARN (warnings only) | Same as PASS, but cite warning `rule_id`s under "Validator notes" in the review report. Do not re-check those rules. |
| FAIL (critical present) | This should not occur at review time — orchestrator runs implementer fix cycle BEFORE dispatching W7b. If FAIL slips through, treat as critical and surface immediately. |
| no summary JSON (legacy plan) | Apply the full checklist (legacy mode). Note `[validator-summary-absent]` in the review report. |

`.pending` marker present in `artifacts/features/{slug}/` → reviewer emits `MISSING` for that fcode (see § Pending Marker Rule below). `MISSING` counts toward the review report's `failed` total.

## Universal rules

Applies to every artifact — do not repeat in per-artifact sections.

| Rule | Severity |
|------|----------|
| Artifact exists and is non-empty | critical |
| No placeholder text (`{PLACEHOLDER}`) | critical |
| Required sections present, in template order | critical |
| Orphaned code: exists in artifact but no F### in FeatureList references it | critical |
| Every reported issue includes `location.file` + `location.line` | critical |
| REG### must always appear as SCR###/REG### in cross-refs (bare REG### invalid) | critical |
| REG### parent SCR### must exist in same ScreenList | critical |
| REG### _NameSlug mandatory (no anonymous regions) | critical |
| REG### must not nest (no REG inside REG) | critical |
| Cross-ref tokenizer: split refs on `,` then on `/`. Left token = SCR### (must exist in ScreenList main index). Right token (if present) = REG### (must exist in the parent screen's Regions subsection). | critical |
| Content-completeness: every documented entity (route, model, screen, behavior-logic entry, permission) must be traceable to actual source code via scout-report.md inventory. Documented item with no verifiable source → critical. If scout-report.md absent → mark N/A, emit [WARN]. | critical |

Counting: `critical` → `failed`; `warning` → `warnings`; `MISSING` (see § Pending Marker Rule) → `missing` (counts toward `failed` for Wave 9 gate).

### Pending Marker Rule

`.pending` is a zero-byte sentinel written by Wave 5 and removed by Wave 6 researcher on successful `spec.md` write (see `references/canonical-fcode-schema.md § Folder Lifecycle`). When the W7b reviewer encounters `.pending` in `artifacts/features/{slug}/`:

| Marker state | spec.md state | Reviewer verdict | Frontmatter slot |
|--------------|---------------|------------------|------------------|
| `.pending` present | absent | `MISSING` | `missing += 1` |
| `.pending` present | present (W6 wrote but failed to remove marker) | `MISSING` | `missing += 1` (partial-write signal — researcher must verify or remove marker manually) |
| `.pending` absent | present | normal review | n/a |
| `.pending` absent | absent | critical (folder skeleton without content nor marker — orchestrator bug) | `failed += 1` |

`MISSING` blocks Wave 9 (review report frontmatter `missing > 0` → doc-writer HALT). Recovery: rerun Wave 6 for the affected fcode OR (after manual verification that `spec.md` is complete) remove `.pending` and rerun Wave 7b.

## Artifacts

### SystemOverview

**Cross-refs:** codebase package files (technology accuracy), FeatureList

**Required sections:** `# System Overview` (Project/Generated/Architecture Type) → `## Executive Summary` → `## System Architecture` → `### High-Level Architecture` (Mermaid graph TB) → `### Technology Stack` (Layer|Technology|Version) → `## Data Flow` (Mermaid sequenceDiagram) → `## Key Design Decisions` (min 2 × Context/Decision/Rationale) → `## Security Overview` → `## Scalability`

**Format checks:** both Mermaid diagrams present; Technology Stack has Layer/Technology/Version columns; heading hierarchy correct.

**Critical edge cases:**
- Missing Mermaid diagram → critical
- Technology documented but not in codebase → critical
- Technology in codebase but not documented → critical
- Mermaid syntax invalid → warning
- Key Design Decision missing Context/Decision/Rationale → warning

### RouteList

**Cross-refs:** codebase routes (`**/routes*.{php,js,ts,rb}`, `**/api*.{php,js,ts}`), FeatureList

**Required sections:** per `route-list-template.md`

**Format checks:** route path format valid (`METHOD /path`); handler references match actual code.

**Cross-refs:** every route in codebase must appear here; RouteList does NOT contain F### mapping (belongs to FeatureList only).

**Critical edge cases:**
- Route in codebase but not documented → critical
- Route documented but no F### in FeatureList references it → critical
- Route path/method doesn't match actual code → warning
- Route format invalid → warning

### DataModel

**Cross-refs:** codebase model files (`**/models/**/*.{js,ts,php,rb}`, `**/entities/**/*.{js,ts,php}`), FeatureList

**Required sections:** per `data-model-template.md`

**Format checks:** entity/attribute format valid; relationship types correct.

**Cross-refs:** every model in codebase must appear here; DataModel does NOT contain F### mapping.

**Critical edge cases:**
- Entity in codebase but not documented → critical
- Entity documented but no F### in FeatureList references it → critical
- Attribute format invalid → warning
- Relationship doesn't match actual model → warning
- Entity in codebase has enum/constant-type field but no `**Discriminator Fields**` block → critical
- `**Discriminator Fields**` block present but DISC-### IDs not unique within document → critical
- DISC-### values list does not match actual enum definition in source (missing or extra values) → warning (reviewer reads model source to verify)
- DISC-### entry for a boolean field (only `true`/`false` values) → warning (boolean flags belong in Business Rules, not DISC; validator also flags via `FeatureSpec.disc_boolean`)
- `**Discriminator Fields**: None.` written for entity that has enum/constant-type fields in source → critical

### DataModel (W1.5 structural gate — scoped)

W1.5 reviewer checks ONLY these 5 items. Full DataModel review at W7a.

**Check 1 — Entity completeness (critical):**
- Each entity has name, description, at least one typed field
- Fail: entity with no fields, or entity with fields but no types documented

**Check 2 — DISC-### scope (critical):**
- Each DISC-### has ≥2 enum values with distinct behavioral outcomes
- Fail: DISC-### with `true`/`false` only (boolean flags belong in Business Rules)
- Fail: DISC-### with single value (not a discriminator — remove or expand)

**Check 3 — MODEL### uniqueness (critical):**
- No duplicate MODEL### codes across the document
- Fail: MODEL001 appears in two different entity blocks

**Check 4 — DISC-### anchor (critical):**
- Each DISC-### code is anchored to a specific entity's field
- Fail: DISC003 appears in Polymorphic section but no entity has a field mapped to DISC003

**Check 5 — Relationship completeness (warning):**
- Each relationship entry has source entity, target entity, cardinality
- Warning: missing cardinality

**Token budget:** data-model.md only. No cross-artifact loading.

### ScreenList

**Cross-refs:** screen/view/component source files inventoried in `scout-report.md` (reviewer uses W0 scout inventory — avoids framework-specific extension assumptions; exact dirs and extensions are project-language-dependent), FeatureList, RouteList

**Required sections:** per `screen-list-template.md`

**Format checks:** all SCR### codes follow `SCR###_NameSlug`; each screen has ≥1 US### mapped; each screen has a route in RouteList.

**Cross-refs:** ScreenList does NOT contain F### or US### mapping (feature → FeatureList; US → UserStories).

**Critical edge cases:**
- Screen in codebase but not documented → critical
- Screen documented but no F### in FeatureList references it → critical
- Screen has no US### mapped → critical
- Screen has no route in RouteList → critical
- Service coverage: a service/API hook/helper module used by the screen's page file or its immediate component/partial/helper dependencies (per scout-report.md inventory) has no matching ROUTE### in RouteList AND no matching BL### in BehaviorLogic → critical
- SCR### format invalid → warning
- Router-outlet grouping [H6-VIOLATION]: a screen documented as composite (with REG regions) where the source file's primary content is a router outlet (per-stack H6 outlet signals in `composite-screen-detection.md § H6`) AND child routes each have a distinct URL path segment — should be separate SCR entries, not REG regions → critical
- Parent shell without own API: a screen with zero independent API calls (only renders router outlet) documented as its own SCR entry, with no persistent layout content (sidebar, timeline nav) → warning; shell context belongs in each child SCR description
- Over-merged screen [OVER-MERGED]: a single SCR entry listing ≥3 URL patterns of structurally different depth (not just :id param variants — e.g. /batches, /compare, /batch-results under same parent path) is a candidate for H6-split; flag for researcher review → warning

### ScreenFlow

**Cross-refs:** ScreenList (SCR### completeness), FeatureList

**Required sections:** per `screen-flow-template.md` (includes `## Feature Entry Points` H2)

**Format checks:** all SCR### in flow exist in ScreenList; navigation transitions documented.

**Cross-refs:** every SCR### in ScreenList must appear in ScreenFlow.

**Critical edge cases:**
- SCR### in ScreenList but not in ScreenFlow → critical
- SCR### in ScreenFlow but no F### in FeatureList references it → critical
- Circular navigation dependency → critical
- Screen transition doesn't match actual navigation → warning
- Auth flow not documented → warning
- `## Feature Entry Points` section absent from screen-flow.md → warning (expected after W6 fan-out completes)
- `## Feature Entry Points` has `{POPULATED_BY_W6}` placeholder still present post-W6 → warning (fragment consolidation may not have run)

### BehaviorLogic

**Cross-refs:** RouteList (ROUTE### refs), DataModel (MODEL### refs), FeatureList, scout-report.md `## Background Logic Source Inventory`

**Required sections:** `# Background Logic` → `## Background Logic Index` (Code|Name|Type|Trigger) → `## Background Logic Details` → `## Summary` → `## Cross-Reference Validation`

**Format checks:** all BL### follow `BL###_NameSlug`; codes unique; valid Type values (canonical 10): `custom-command`, `event-listener`, `integration`, `mail`, `middleware`, `notification`, `observer`, `queue-worker`, `scheduled-job`, `webhook`; each item has Type + Trigger + Description + Related Modules + Source File + Source Symbol.

**Cross-refs:** referenced ROUTE### must exist in RouteList; referenced MODEL### must exist in DataModel; overlap check: same name+different BL### or >50% keyword overlap = critical.

**Critical edge cases:**
- Duplicate BL### codes → critical
- BL### format invalid → critical
- Missing Type, Trigger, or Description → critical
- Invalid Type value (not in canonical 10) → critical
- BL item missing `Source File` field → critical
- BL item missing `Source Symbol` field → critical
- BL item `Source Symbol` containing multi-symbol delimiter — `,`, `;`, or whitespace-bounded ` and ` / ` & ` / ` + ` → critical (aggregation; split into separate BL items). `/` is excluded (overlaps composite refs / paths); `+`/`&` only count when surrounded by whitespace to avoid false-positives on Swift `MyClass+Extension` and similar single-symbol forms.
- Referenced ROUTE### not in RouteList → warning
- Referenced MODEL### not in DataModel → warning
- BL Source File not found in scout BL inventory → warning (researcher must justify in Description)

**Cardinality Cross-Check** (load scout `## Background Logic Source Inventory` before running):

Run per-stack, then take MAX gap across stacks (do NOT OR-merge — see bl-source-patterns.md § Multi-Stack Handling).

1. **Total count gap** — for each stack subsection: `gap = abs(inventory_count − bl_count) / max(inventory_count, 1) × 100`. Both undershoot and overshoot count. Bounds use strict-inequality semantics — boundary values fall in the lower band.
   - `gap ≤ 5%`: PASS
   - `5% < gap ≤ 15%`: warning + list uncovered entries (or BL items not backed by inventory)
   - `gap > 15%`: critical
   - Small-project floor: if `max(inventory_count, bl_count) < 20`, switch to absolute thresholds — `abs gap ≤ 2`: PASS; `2 < abs gap ≤ 4`: warning; `abs gap > 4`: critical.
   - **Overshoot diagnosis** (bl_count > inventory_count): surplus is either (a) duplicate BLs sharing a Source File with no distinct Source Symbol → critical (Rule C1 violation), or (b) BL items without inventory backing → check Rule 3.
2. **Category drop** — for each category present in inventory with ≥ 1 entry: artifact must have ≥ 1 BL of matching type. Category present in inventory but 0 BL of that type → critical.
3. **Source File check** — BL item with no `Source File` field → critical (already covered above). BL Source File not in any inventory entry → warning.
4. **Orphan file** — inventory entry with no matching BL Source File → critical (one finding per orphaned file).
5. **Inferred ratio (per stack)** — applies ONLY to stacks listed in `references/bl-source-patterns.md` table AND NOT the `### Unknown` no-manifest subsection. Stacks outside the table (e.g., Phoenix Elixir) and `### Unknown` are exempt — 100% inferred is expected and Rule 5 does not fire. For applicable stacks: `inferred_ratio = signal_inferred_count / stack_inventory_count` (both counts read from scout report § Background Logic Source Inventory, same stack subsection). Strict-inequality semantics — boundary values fall in the lower band. Thresholds preliminary; calibrate after smoke test.
   - **Guard (applied AFTER exemption check):** if `stack_inventory_count == 0`, skip ratio check (division undefined). If also `signal_inferred_count > 0`, that is a scout self-contradiction → critical.
   - `ratio ≤ 20%`: PASS
   - `20% < ratio ≤ 50%`: warning ("verify Mode A globs / Mode B grep coverage; non-standard libraries may be legitimate")
   - `ratio > 50%`: critical ("scout likely skipped per-stack patterns; re-scan required")
6. **Exclusion-pattern leak** — BL Source File matching scout filename-level exclusions → critical (scout-side filter failure; researcher cannot fix — re-run scout). Patterns:
   - **Test files (per language):** `*Test.{php,java,kt}`, `*Tests.cs`, `*_spec.rb`, `test_*.py`, `*_test.py`, `*_test.go`, `*.test.{ts,tsx,js,jsx}`, `*.spec.{ts,tsx,js,jsx}`, `tests/*.rs`
   - **Abstract bases:** `Abstract*.{php,java,kt,ts,cs}`, `*Base.{php,java,kt,ts,cs}`
   - **Vendor paths:** `vendor/`, `node_modules/`, `Pods/`, `.venv/`, `target/`
   LOC and auth-classification checks remain scout-side; if a leaked file passes filename heuristics, surface in next re-scan.

Reviewer output format for Cardinality Cross-Check:

```markdown
### BehaviorLogic Cardinality
- Inventory total: {N}
- Artifact BL count: {N}
- Gap: {X}% ({PASS|WARNING|CRITICAL})
- Missing categories: {type1, type2, ...} or none
- Orphan files: {path1}, {path2}, ... or none
```

Multi-stack example: "Laravel gap 2%, NestJS gap 67% (CRITICAL); max=67% → CRITICAL"

### Permissions

**Cross-refs:** RouteList (ROUTE### refs), ScreenList (SCR### refs), FeatureList

**Required sections:** `# Permissions` → `## Authorization System Type` → `## Permissions Index` (Code|Name|Type|Enforced At) → `### PERM###: Name` subsections → `## Summary` → `## Cross-Reference Validation`

**Format checks:** all PERM### follow `PERM###_NameSlug`; codes unique; valid Auth System Type: `rbac`, `abac`, `acl`, `ownership`, `hybrid`, `other`; valid Permission Type: `route-guard`, `screen-permission`, `action-permission`, `data-permission`, `role-based`, `resource-ownership`, `field-permission`, `api-scope`, `feature-flag`, `experiment`, `env-gate`, `locale-gate`; each item has Type + Enforced At + Description + Related Modules; for traditional types also Permission Rules matrix; for client-side gate types (`feature-flag`, `experiment`, `env-gate`, `locale-gate`) use `source:` field instead of Permission Rules matrix.

**Cross-refs:** referenced ROUTE### must exist in RouteList; referenced SCR### must exist in ScreenList; overlap check same as BehaviorLogic.

**Critical edge cases:**
- Missing Authorization System Type section → critical
- Invalid Authorization System Type value → critical
- Duplicate PERM### codes → critical
- PERM### format invalid → critical
- Missing Type, Enforced At, Description, or Permission Rules → critical
- Same route/screen with conflicting permissions → warning
- Referenced ROUTE### not in RouteList → warning
- Referenced SCR### not in ScreenList → warning

### UserStories

**Cross-refs:** ScreenList (SCR### count + refs), BehaviorLogic (BL### refs), Permissions (actor split), FeatureList

**Required sections:** per `user-stories-template.md`

**Format checks:** all US### follow `US###_NameSlug`; `ui` type US has ≥1 SCR### in Screens section; `system` type US has ≥1 BL### in Background Logic section; UI US count ≥ SCR### count in ScreenList.

**Cross-refs:** actor split — if Permissions shows different roles have different access → verify US split by actor.

**Critical edge cases:**
- UI US count < UI Screen count → critical
- US### not referenced by any F### in FeatureList → critical
- UI US### has no SCR### mapped → critical
- System US### has no BL### mapped → critical
- Referenced SCR### not in ScreenList → warning
- Referenced BL### not in BehaviorLogic → warning
- US combines multiple user actions (compound title with "and"/"or", multiple verbs, or CRUD grouping like "manage"/"CRUD"/"create/edit/delete") → critical
- US title uses compound/vague action verbs ("manage", "handle", "CRUD", "create/edit", "create or edit") → critical; split into separate US per verb
- US title contains "/" separating two actions (e.g., "Create/Edit User") → critical
- Destructive action (Delete/Remove/Revoke/Deactivate) visible on a screen with no dedicated US → critical [IPE_MISSING_DESTRUCTIVE]
- Screen with ≥3 distinct buttons/actions in source but only 1 US mapped → warning [IPE_SPARSE]; note screen for researcher re-pass
- Interaction Inventory table absent or empty when UI screen count > 0 → warning
- Two US sharing identical HTTP endpoint AND actor → warning [IPE_MERGE_CANDIDATE]; verify merge exception applies (same data flow required)
- Multiple distinct actors combined in single US (e.g., "Admin and User") → warning
- Acceptance criteria vague/non-testable → warning
- US missing priority → warning

### UserStories (W4.5 quality gate — scoped)

W4.5 reviewer checks ONLY these 5 items. Full UserStories review at W7a.

**Check 1 — Single intent (critical):**
- Each US### has exactly one user action in goal
- Fail: "create, edit, and delete" in one story; "as well as" joining distinct actions
- Only flag critical when verbs describe CLEARLY DISTINCT independent actions

**Check 2 — Human actor (critical):**
- Actor is a named human role (user, admin, manager, guest)
- Fail: actor is "system", "app", "platform", or missing

**Check 3 — Outcome present (warning):**
- "so that..." or equivalent user-visible value statement
- Warning: story missing outcome

**Check 4 — Overly broad scope (warning):**
- Goal uses generic management verb without specific action
- Warning: "manage all user data", "administer the system"
- Acceptable: "manage my account settings" (specific resource, clear scope)

**Check 5 — US### uniqueness (critical):**
- No duplicate codes
- Fail: US005 appears twice

**Token budget:** user-stories.md only.

### FeatureList

**Cross-refs:** UserStories, ScreenList, RouteList, DataModel, BehaviorLogic, Permissions (all codes cross-validated)

**Required sections:** `# Feature List` → `## Feature Hierarchy` (Code|Name|Type|Language|Workspace|Priority) → `## Feature Details` → `## Summary` → `## Cross-Reference Validation`

**Format checks:** all F### follow `F###_NameSlug`; codes unique; valid Type: `ui`, `background`, `mixed`; feature names specific (not "Management", "CRUD").

**Feature type rules:** `ui` → SCR### required; `background` → BL### required, no SCR###; `mixed` → SCR### + BL### both required. PERM### optional for all types.

**Valid feature criteria (all 4):** Single Intent, Clear Flow (input→process→output), Independently Testable, Agent Implementable. Same name+different F### or >50% keyword overlap = critical.

**Cross-refs:**
- All US###/SCR###/ROUTE###/MODEL###/BL###/PERM### in feature must exist in their source artifact
- Every BL### in BehaviorLogic must be referenced by ≥1 F### (orphan BL### → missing-feature-log)
- Every PERM### in Permissions must be referenced by ≥1 F### (orphan PERM### → missing-feature-log)
- Read missing-feature-log at start → report each item as critical → clear log

**Critical edge cases:**
- Duplicate F### codes → critical
- Feature with multiple intents or no clear flow → critical
- Orphaned US###/SCR###/ROUTE###/MODEL### in feature → critical
- BL### in BehaviorLogic not mapped to any F### → critical
- PERM### in Permissions not mapped to any F### → critical
- Feature name vague → warning

### FeatureList (W5.6 fast gate — scoped)

W5.6 reviewer checks these 8 items across 3 groups. Full FeatureList review happens at W7a.

**Group A — Structural integrity (critical on fail):**

**Check 1 — US### coverage:**
- Every US### in user-stories.md appears in at least one F###
- Fail: US005 exists in user-stories.md but no F### references it

**Check 2 — SCR### coverage:**
- Every SCR### main entry in screen-list.md owned by at least one F###
- Fail: SCR008 in screen-list.md, no F### has SCR008 in Related Screens

**Check 3 — Orphan codes:**
- No US### or SCR### in FeatureList that don't exist in their source artifact
- Fail: F003 references US099 but user-stories.md has no US099

**Check 4 — F-code uniqueness:**
- No duplicate F-code numbers across Feature Details
- Fail: F003_Auth and F003_Profile both exist

**Group B — Quality criteria per F### (critical or warning):**

**Check 5 — Single Intent (critical):**
- Each F### describes exactly one user-facing intent
- Fail: F003_UserManagement covers login + profile + admin (3 intents)

**Check 6 — Clear Flow (warning):**
- Identifiable input→process→output for each F###
- Fail: F007_System — no discernible user trigger or outcome

**Check 7 — Vague naming (warning):**
- F### name is not a standalone generic noun: "Management", "System", "Handler", "CRUD"
- Acceptable if project has ≤5 features total

**Check 8 — Scope overlap (warning):**
- Two F### do not share >50% of description keywords indicating duplicate scope

**Group C — Grouping coherence (critical):**

**Check 9 — Grouping coherence:**
- Each F###'s US###/SCR### set is thematically coherent
- Fail: F001_Auth owns payment-related US### codes

**Output format:** `feature-list-review.md` with YAML frontmatter `passed: bool, issues: int, warnings: int`.
**Token budget:** full feature-list.md (needs Feature Details); user-stories.md headers + US### list; screen-list.md SCR### index only.

### FeatureSpec

**Cross-refs:** all 9 document artifacts (FeatureList, UserStories, ScreenList, ScreenFlow, RouteList, DataModel, BehaviorLogic, Permissions, SystemOverview)

**Required sections (in order):** `## Overview` → `## Why This Exists` → `## Who Uses It` → `## Business Workflow` → `## Screen Flow` → `## Polymorphic Behavior` → `## Cross-Cutting Logic` → `## User Stories` → `## Key Entities` → `## Artifact References` → `## Assumptions` → `## Source Code References` → `## Unresolved Questions`

**Preamble structure:** All 4 preamble sections (`## Why This Exists`, `## Who Uses It`, `## Business Workflow`, `## Screen Flow`) MUST be present and non-empty. Valid content: real prose OR literal `N/A — {justification}.` Specifically: `## Why This Exists` accepts `N/A — inferred from code; domain confirmation needed.`; `## Screen Flow` MUST begin with `**See:** ScreenFlow § {F###_entry}` cross-ref OR `N/A — background feature; no user-facing screen flow.`; `## Who Uses It` PERM### refs MUST resolve to Permissions artifact.

**Cross-Cutting Logic structure:** 7 required H3 subsections in order: `### Requirements`, `### Business Rules`, `### Decision Logic`, `### State Machines`, `### Algorithms`, `### External Integrations`, `### Verification`. Empty subsection MUST contain `None.`.

**User Stories structure:** ≥1 `### {US###_CODE} — {Title} (Priority: Pn)` block. Each block: `**What happens:**`, `**Why this priority:**`, `**Independent Test:**`, `**Acceptance Scenarios:**` (Given/When/Then), `**Requirements fulfilled:**` bullets are REQUIRED. `**Rules enforced:**`, `**State transitions:**`, `**Algorithms:**`, `**External integrations:**`, `**Verification:**` are OPTIONAL (omit when not applicable). `### Edge Cases` H3 MUST appear after last US block.

**Format checks:**
- F### code follows `F###_NameSlug`, matches FeatureList entry exactly (code + name + priority).
- US### / SCR### / REG### formats valid (reference code-formats.md).
- BR / SM / ALG / INT codes use `{PREFIX}-###_NameSlug`; per-spec unique; code appears with full `**Source:**` block exactly once.
- Each BR/SM/ALG/INT full block has `**Source:** path:start-end` citation (line range mandatory).
- Each BR/SM/ALG/INT full block has ≥1 `**Linked FR:** FR-###` referencing an FR in the same spec. (Mechanical insertion handled by `scripts/structural_fixer.py` at Wave 7.5; reviewer flags only when placeholder `FR-???` remains.)
- SM full block MUST contain a Mermaid `stateDiagram-v2` fenced block.
- Pseudocode blocks ≤20 lines; no literal `{lang}` in fence; no secrets/credentials.
- Each FR-### appears in exactly one of: a US's `**Requirements fulfilled:**` list OR Cross-Cutting `### Requirements` table.
- Every FR-### declared (whether under a US or in `## Cross-Cutting Logic > ### Requirements`) MUST be covered by ≥1 SC-### via the `(covers …)` back-ref. Uncovered FR = critical (verification missing).
- Each SC-### appears inline in a US's `**Verification:**` list OR in Cross-Cutting `### Verification`; has `(covers FR-### / BR-### / …)` back-ref to ≥1 code in the same spec.
- Cross-US reference format: `BR-### (see US###)` — reference-only, no Source block.
- `## Screen Flow` first non-blank line matches regex `^\*\*See:\*\* ScreenFlow § F\d{3}_\w+$` OR equals `N/A — background feature; no user-facing screen flow.`.
- No H2 heading named `## Requirements`, `## Business Rules`, `## State Machines*`, `## Algorithms*`, `## External Integrations*`, `## Success Criteria`, or `## How It Works`.
- No `## Appendix` heading in submitted draft.

**Cross-refs (all 9 artifacts mandatory):**

| Field in spec | Must match |
|---------------|-----------|
| F### code, feature name, priority | FeatureList (exact match) |
| All SCR###/US###/ROUTE###/MODEL###/BL### listed in FeatureList for this F### | Present in spec |
| SCR### codes | Exist in ScreenList |
| US### codes | Exist in UserStories |
| Screen flow references | Match ScreenFlow |
| Routes referenced | Exist in RouteList |
| Entities referenced | Exist in DataModel |
| BL### codes in Artifact References | Exist in BehaviorLogic |
| PERM### codes in Artifact References | Exist in Permissions |
| `**Source:** file:line` cited in BR/SM/ALG/INT blocks | File exists and cited range contains the described logic (reviewer reads to verify) |

Content grounded in actual source code (no fabricated details).

**Critical edge cases:**
- Feature spec missing / empty → critical
- F### not in FeatureList → critical
- Feature name or priority mismatch with FeatureList → critical
- Required section absent or out of order → critical (includes missing `## Why This Exists` / `## Who Uses It` / `## Business Workflow` / `## Screen Flow` / `## Polymorphic Behavior` / `## Key Entities` / `## Artifact References` / `## Assumptions` / `## Source Code References` / `## Unresolved Questions`)
- Legacy two-section format detected (`## Related Artifacts` + `## Spec Documents` both present) → **CRITICAL** immediately (no transition window — replace with `## Artifact References` table)
- `## Artifact References` present but missing required rows (System Overview, Feature List, Route List, Data Model, Screen List, Screen Flow, Behavior Logic, Permissions, User Stories) → critical
- `## Artifact References` Codes Used column contains bare REG### without parent SCR### prefix → critical
- Preamble section present but empty / placeholder-only → critical
- `## Screen Flow` missing `**See:** ScreenFlow § {F###_entry}` first-line cross-ref AND not the N/A fallback → critical
- `## Who Uses It` references PERM### not in Permissions artifact → critical
- Top-level deprecated heading present (`## Requirements` / `## Business Rules` / `## State Machines*` / `## Algorithms*` / `## External Integrations*` / `## Success Criteria` / `## How It Works`) → critical
- FR-### defined but not appearing under any US's `**Requirements fulfilled:**` list or under Cross-Cutting `### Requirements` → critical (orphan FR)
- FR-### declared in `## Cross-Cutting Logic > ### Requirements` but not referenced from any US's `**Requirements fulfilled:**` list AND not covered by any SC-### `(covers …)` back-ref → critical (zombie cross-cutting FR — declared but never consumed).
- FR-### declared anywhere (US or Cross-Cutting) but no SC-### `(covers …)` references it → critical (uncovered FR — no verification path).
- FR-### appearing BOTH under a US AND under Cross-Cutting → critical (ambiguous home)
- SC-### appearing in a dedicated top-level section → critical (must be inline under US or Cross-Cutting)
- SC-### without `(covers …)` back-ref → critical
- SC-### `(covers …)` references a code not defined in this spec → critical
- `## Polymorphic Behavior` section absent entirely → critical
- `## Polymorphic Behavior` present but Key Entities include entity with DISC-### in data-model.md AND section body is `N/A` → critical (false N/A)
- `## Polymorphic Behavior` has DISC-### subsection but one or more known values from data-model.md are missing → critical (incomplete coverage)
- `## Polymorphic Behavior` DISC-### subsection present but not all Key Entities' DISC-### are covered (subsection for DISC-001 present, DISC-002 from same entity missing) → critical
- `## Polymorphic Behavior` behavior cell is blank (empty string, not `unverified`) → warning
- `## Polymorphic Behavior` DISC-### subsection references a DISC-### not present in data-model.md for any entity in Key Entities → critical (phantom discriminator)

**Content depth checks (CRITICAL — these catch shallow/generic specs):**
- `## Business Workflow` without numbered steps (≥3 for non-trivial features) → critical
- `## Business Workflow` steps that lack specific entity/table/job/field references → critical (generic prose)
- `## Screen Flow` for UI/mixed features missing bullet list of owned screens with route + atomic/composite annotation → critical
- `## Screen Flow` contains a 3-column Screen Route Table (Screen|Route|Purpose) → **CRITICAL** (deprecated format — replace with bullet list per current template)
- `## Screen Flow` forward-ref anchor `F###_{name}` does not match any `### F###_{name}` subsection under `## Feature Entry Points` in screen-flow.md → warning (anchor unresolved)
- `## Cross-Cutting Logic > ### Business Rules` with <3 BRs for UI features → warning (shallow extraction)
- BR/SM/ALG/INT block missing `**Source:** file:line-range` citation → critical
- `**Source:**` cites a non-existent file or invalid/unverified range → critical
- User Story missing `**What happens:**` / `**Why this priority:**` / `**Independent Test:**` → critical
- User Story acceptance scenarios without Given/When/Then structure → warning (vague criteria)
- User Story missing `**Endpoints**: METHOD /path` for its routes → warning
- `### Edge Cases` section missing → critical (must appear under `## User Stories`)
- `### Edge Cases` with <3 rows for UI features or <1 for background features → critical (shallow)
- Edge case rows missing HTTP status code or specific error message → warning
- `## Key Entities` missing or has 0 entity rows → critical
- `## Key Entities` without database table names (just model codes) → warning
- `## Key Entities` with <3 entities for non-trivial features → warning (likely incomplete)
- `## Source Code References` missing or has 0 entries → critical
- `## Source Code References` with <3 entries → warning (likely incomplete)
- `## Source Code References` entries without file path line ranges → warning
- `## Assumptions` missing or has 0 entries → warning
- `## Assumptions` with <2 entries for non-trivial features → warning
- `## Unresolved Questions` missing → warning (expected for complex features)
- `## Artifact References` missing or has 0 rows → critical
- `## Artifact References` missing System Overview or Feature List rows → critical (both are always-required, always-reviewed)
- `## Artifact References` Codes Used column empty for non-overview rows when feature uses that artifact → warning (should list specific codes)
- Same BR/SM/ALG/INT code with `**Source:**` line appearing in 2+ places → critical (duplicate full block; secondary occurrences must be reference-only)
- Cross-Cutting subsection blank (no `None.` under empty H3) → critical
- Any SCR###/US###/ROUTE###/MODEL###/BL### from FeatureList absent in spec → critical
- BR/SM/ALG/INT referencing FR-### not in same spec → critical
- Cross-spec BR/SM/ALG/INT ref (e.g., "see BR-001 in F002") → critical
- SM full block missing Mermaid `stateDiagram-v2` → critical
- Secret/credential leaked in pseudocode → critical
- `## Appendix` heading present in submitted draft → critical
- `### Edge Cases` promoted to H2 or missing from `## User Stories` → critical
- Cross-US reference using a format other than `BR-### (see US###)` (e.g., `BR-### from US###`, `see BR-001`) → warning
- Pseudocode block > 20 lines → warning
- Pseudocode fence contains literal `{lang}` placeholder → warning
- US without priority → warning
- `## Why This Exists` reads as invented product rationale without `**Source:**` citation or N/A disclaimer (reviewer judgment call; when in doubt, flag) → warning
- `## Screen Flow` inline prose duplicates > 50% of `## Business Workflow` prose (reviewer judgment; substring overlap heuristic) → warning
- Legacy `## Related Artifacts` section present (deprecated) → **CRITICAL** (no transition window — use `## Artifact References` table)
- Legacy `## Spec Documents` section present (deprecated) → **CRITICAL** (no transition window — use `## Artifact References` table)

### ScreenSpec

**Applies when:** `--screen-specs` standalone pass. ScreenSpec files at `docs/specs/screens/SCR###_Name/spec.md`.

**Required sections:** `# {SCR###_Name} — Screen Spec` header → `## Purpose` → `## Screen Layout` → `## UI States` → `## Validation & Error Feedback` → `## Interaction Patterns` → `## Accessibility` → `## Conditional Rendering` → `## Component Variants` (optional; when omitted, `## Security Surface` follows directly after `## Conditional Rendering`) → `## Security Surface` → `## Source References`

**Scope boundary (enforced):** ScreenSpec = UI-layer only. No FR/BR/SM/ALG/INT/SC codes. No business logic. No server-side validation rules. Reviewer flags any business logic content as critical.

**Severity policy (rows 1–7: `warning` during initial release, promotes to `critical` at stable; rows 8–14: gap-fix rules Round 1 — CRITICAL immediately; rows 15–18: `warning` during initial release; rows 19–24: gap-fix rules Round 2 — `warning` immediately, promotes to `critical` at stable):**

| Rule | Severity |
|------|----------|
| SCR### code in header does not exist in ScreenList | warning |
| All sections are N/A (no content populated at all) | warning (flag for re-examination) |
| Business logic or FR/BR/SM/ALG/INT/SC codes present | warning (scope violation — promoted to critical after stable release) |
| `## Source References` missing or empty | warning |
| Required section absent | warning |
| N/A written without source file scan evidence | warning |
| Validation rules documented that are server-side only (no client-side enforcement found in code) | warning |
| `## Screen Layout` section absent | critical |
| `## Screen Layout` is bare N/A (layout always documentable) | critical |
| `## UI States` lacks ≥1 error row when screen has async calls | critical |
| `## UI States` lacks ≥1 empty row when screen renders data lists/blocks | critical |
| Legacy section name `## Form Validation Rules` present | critical |
| `## Validation & Error Feedback` missing Section B per-action block(s) when screen has submit-style actions | critical |
| `## Interaction Patterns` entry uses implementation-first format | critical |
| `## Accessibility` uses bare `N/A` instead of 4-row audit table | warning |
| `## Component Variants` appears for screen with no shared polymorphic components | warning |
| `## Component Variants` restates DEC/DISC business rules instead of cross-referencing | warning |
| `[UNVERIFIED]` marker used without best-effort description | warning |
| `## Purpose` section absent | warning |
| `## Purpose` content is technical (references component names, store names, or code internals) | warning |
| Conditional Rendering `auth`-type row has no Notes entry stating consequence of bypass | warning |
| Conditional Rendering row with hardcoded numeric/string literal lacks `[NEEDS_DOMAIN_CONFIRMATION]` in Notes | warning |
| `## Security Surface` absent when Conditional Rendering has ≥1 `auth`-type row OR router config guard confirmed for this screen's route | warning |
| `(not read — referenced by import)` entry in Source References with no corresponding Unresolved Question | warning |

**Cross-refs:**
- SCR### in header MUST exist in ScreenList main index
- Source citations MUST reference real files (reviewer spot-checks 1–2 per spec)
- DataModel `## Discriminator Fields` for Component Variants cross-refs; FeatureSpec § Polymorphic Behavior when present.
- `## Security Surface` guard types cross-reference Conditional Rendering `auth`-type rows — must be consistent.
- `## Purpose` must not contradict screen's SCR### entry in ScreenList (same user persona / goal).

#### W7c — SM `kind` field present and valid
**Check:** Every SM-### block has `**kind:**` as the first metadata line.
**Pass:** `kind` value is `entity` or `ui` for all SM blocks.
**Fail evidence:** SM-### block missing `**kind:**` line, OR `kind` value is anything other than `entity`/`ui`.
**N/A:** Feature has no state machines (SM section is empty or absent).

#### W7d — behavior-logic.md Client-Side Logic section populated
**Check:** `behavior-logic.md` contains a `## Client-Side Logic` section.
**Pass:** At least one subsection has content (not all N/A) — OR — all subsections are N/A with confirmation note that codebase has no client-side code.
**Fail evidence:** `## Client-Side Logic` section absent; OR subsection shows N/A but code evidence of the pattern exists (reviewer must cite file:line).
**N/A:** Feature is backend/server-only with no client-side code (reviewer must state this explicitly).

#### W7e — permissions.md captures client-side gates
**Check:** `permissions.md` includes entries for any feature-flag / experiment / env-gate / locale-gate found in code for this feature.
**Pass:** All gates found in code appear in permissions.md — OR — `N/A — no {type} gates detected.` with confirmation.
**Fail evidence:** Gate function call found in code (cite file:line) but not listed in permissions.md.
**N/A:** Reviewer confirms no gate patterns present in feature's code surface.

#### W7f — screen-flow.md has Guard Logic / Deep-Link / Unsaved-Changes sections
**Check:** `screen-flow.md` contains the 3 new sections.
**Pass:** Each section either has entries OR an explicit N/A statement confirmed by code review.
**Fail evidence:** Section absent entirely; OR N/A written without reviewer confirming code absence (lazy N/A).
**N/A:** Not applicable for this feature (e.g., feature has no screens).

#### W7g — Client behavior anchor present in every feature-spec
**Check:** `## Cross-Cutting Logic` in `spec.md` contains the `**Client behavior:**` anchor block.
**Pass:** Anchor block present with all 3 relative links.
**Fail evidence:** Anchor absent, or only 1–2 of the 3 links present.
**N/A:** Never — this anchor is mandatory regardless of N/A content in the linked files.

#### W7h — DEC-### Coverage (Semantic)

**Applies to:** each feature spec (run after `validate_feature_spec.py` passes structural check).

**Pass criteria:**
- `## Cross-Cutting Logic > ### Decision Logic` section present (structural — pre-checked by validator)
- For each DEC-###:
  - `subtype:` declaration matches outcome (e.g. `flow` subtype requires navigation/step in `user_visible_outcome`)
  - `user_visible_outcome` is genuine business outcome (not "spinner shows" / "loading toggle" / dispatch wrapper)
  - Pseudocode matches Source at cited lines (reviewer cross-checks by reading source)
  - No anti-example patterns captured (loading toggle, generic dispatch, debounce/throttle, i18n routing, cache mechanic)
- For N/A: validator already confirmed no JSX-ternary hits in involved files; reviewer scans for non-JSX branches if applicable

**Fail evidence:**
- subtype `flow` declared but pseudocode contains no navigation → mismatch
- `user_visible_outcome` is "spinner appears" → plumbing leakage, fail
- Pseudocode references variables not in cited Source range → hallucination
- Anti-example pattern detected (e.g. `if isLoading → show <Spinner/>`) → fail

**Subtype routing for evidence-check:**
- `render` → reviewer reads component render tree at Source
- `interaction` → reviewer reads event handler body at Source
- `flow` → reviewer reads navigation/step-advance code at Source (can be saga, controller, route, anywhere — Source location agnostic)

**Fail evidence:**
- DEC pseudocode with single predicate (no AND/OR/multi-condition) and subtype `render` → warning: "consider expressing as DISC or Business Rule"

**Cross-link:** When validator catches single-field condition in a DEC, reviewer suggests moving to DISC. See W7a (DISC boolean — data-model.md).

## Composite Detection Rules

Rules fire unconditionally on every pipeline invocation. No opt-in flag.

- [ ] **H4 short-circuit respected (tabs only):** mutually-exclusive tab content (per-stack tab signals in `composite-screen-detection.md § H4`) → SCR variants (SCR###a/b), not REG. Hard rule; overrides all other heuristics for tab-style screens. Reviewer selects signals from the row matching the task's `Detected stack:` value.
- [ ] **H5 wizard/stepper classification:** screens with wizard/stepper signals (per-stack signals in `composite-screen-detection.md § H5`) MUST cite classification evidence in spec. Case A (SCR variants) requires explicit citation of distinct validation rules AND distinct API endpoints AND distinct user action per step. Case A without cited evidence → warning (researcher should re-evaluate as Case B). 2-step wizards defaulting to Case A → critical (must be Case B). ≥3-step wizards default to Case B (composite SCR + step REGs).
- [ ] **H3 region count excludes H4/H5 signals:** tab signals (H4) and wizard/stepper signals (H5) — per per-stack tables in `composite-screen-detection.md § H4` and `§ H5` — MUST NOT count toward H3 (any stack).
- [ ] **H2 module count uses per-stack include/exclude tables:** only domain/feature module imports count toward H2; UI-library and framework-primitive imports are excluded. Reviewer applies the include/exclude row matching the task's `Detected stack:` value — see per-stack tables in `composite-screen-detection.md § H2`.
- [ ] **2-of-3 signal gate applied:** composites cite which 2 signals passed (H1∧H2, H1∧H3, or H2∧H3). Screens not meeting gate → emit bare SCR###.
- [ ] **Raw-div fallback noted when H3=0:** if H3 yields 0, gate uses H1+H2 only. If both H1 and H2 also fail → emit atomic + warning. Known Detection Limitation documented in spec output.
- [ ] **FeatureList composite-ref tokenizer (C3):** FeatureList Related Screens tokenizer splits on `,` then on `/`. Left token = SCR### (must exist in ScreenList main index). Right token (if present) = REG### (must exist in that screen's Regions subsection). Both tokens validated independently. Missing REG### under valid SCR### → critical.
- [ ] **Malformed composite ref (M3):** malformed composite refs (`SCR001REG001` missing `/`, `SCR001/` missing REG, `SCR/REG001` missing digits, `SCR001/REG` missing digits) → critical. Regex: `^SCR\d{3}_\w+(/REG\d{3}_\w+)?$` must match.
- [ ] **W1 no-REG rule (M5):** W1 artifacts (SystemOverview, RouteList, DataModel) MUST NOT contain REG### codes. REG### first appears in W2 ScreenList. Orphan REG### in W1 artifact → critical.
- [ ] **Partial-screen ownership (CE3):** each SCR### must have ≥1 F### owning the screen shell (bare SCR### ref in FeatureList Related Screens); each REG### must have ≥1 F### owning it (SCR###/REG### ref). An F### with only SCR###/REG### refs does NOT own the parent SCR.
- [ ] **SIGNAL_INFERRED cap and justification:** `[SIGNAL_INFERRED]` tag in ScreenList Notes signals researcher used `composite-screen-detection.md § Signal Inference Fallback`. Tag MUST cite an H-rule in H2–H6 (H1 has no signal table — inference invalid for H1). Each occurrence MUST carry a 3-part justification (Intent matched / No-row reason / Observed pattern). Missing any part → critical. Count `[SIGNAL_INFERRED]` occurrences across the entire ScreenList document; threshold = `max(5, ceil(0.10 × SCR_count))` — exceeding triggers warning (over-reliance on inference; per-stack tables likely outdated or stack uncovered).

## Deterministic Validator Coverage

These `rule_id`s are pre-checked by `scripts/validate_*.py` BEFORE the W7b reviewer runs. When `validation-summary.json` reports a rule as `PASS`, the reviewer marks that rule `[deterministic-pass]` and focuses on semantic depth instead. When a rule is `FAIL`, the orchestrator dispatches an `implementer` fix cycle before W7b runs (see `pipeline.md § Wave 6.5`).

| rule_id | validator script | severity |
|---------|------------------|----------|
| existence.folder_missing | validate_feature_existence.py | critical |
| existence.folder_incomplete | validate_feature_existence.py | critical |
| existence.slug_format | validate_feature_existence.py | critical/warning |
| existence.orphan_folder | validate_feature_existence.py | warning |
| existence.canonical_missing | validate_feature_existence.py | warning |
| FeatureSpec.required_sections | validate_feature_spec.py | critical |
| FeatureSpec.ccl_subsections | validate_feature_spec.py | critical |
| FeatureSpec.ccl_blank | validate_feature_spec.py | critical |
| FeatureSpec.screen_flow_crossref | validate_feature_spec.py | critical |
| FeatureSpec.bw_steps | validate_feature_spec.py | critical |
| FeatureSpec.deprecated_headings | validate_feature_spec.py | critical |
| FeatureSpec.no_appendix | validate_feature_spec.py | critical |
| FeatureSpec.edge_cases | validate_feature_spec.py | critical |
| FeatureSpec.f_code_format | validate_feature_spec.py | critical |
| FeatureSpec.sm_mermaid | validate_feature_spec.py | critical |
| FeatureSpec.pseudocode_length | validate_feature_spec.py | warning |
| FeatureSpec.pseudocode_fence | validate_feature_spec.py | warning |
| Universal.no_placeholder | validate_feature_spec.py | critical |
| citation.file_missing | validate_source_citations.py | critical |
| FeatureSpec.linked_fr_missing | validate_feature_spec.py | critical |
| FeatureSpec.disc_boolean | validate_feature_spec.py | warning |
| citation.range_invalid | validate_source_citations.py | critical |
| citation.range_inverted | validate_source_citations.py | critical |
| citation.path_traversal | validate_source_citations.py | critical |
| citation.unreadable | validate_source_citations.py | warning |
| FeatureSpec.br_linked_fr_present | structural_fixer.py | critical |
| FeatureSpec.polymorphic_behavior_present | validate_feature_spec.py | critical |
| FeatureSpec.decision_logic_section_present | validate_feature_spec.py | critical |
| FeatureSpec.dec_blocks_well_formed | validate_feature_spec.py | critical/warning |
| FeatureSpec.dec_lazy_na | validate_feature_spec.py | warning |
| FeatureSpec.missing_client_behavior_anchor | validate_feature_spec.py | critical |

> **Note:** `FeatureSpec.polymorphic_behavior_present` only checks section presence (not N/A validity or value coverage — those require semantic reading). The validator checks: `## Polymorphic Behavior` heading exists in the spec file.

> **Note:** `FeatureSpec.decision_logic_section_present` checks `### Decision Logic` H3 presence in CCL (covered by `FeatureSpec.ccl_subsections`). `FeatureSpec.dec_blocks_well_formed` checks structural fields per DEC block (subtype, Triggers in, user_visible_outcome, Source, pseudocode ≤8 lines). `FeatureSpec.dec_lazy_na` uses grep to flag JSX-ternary patterns when section says N/A — raises warning for reviewer attention (not auto-fail). Source-file location is NOT validated per scope-agnostic rule. `FeatureSpec.missing_client_behavior_anchor` checks for `**Client behavior:** see` anchor block in `## Cross-Cutting Logic` — mandatory even when all linked sections (behavior-logic.md, permissions.md, screen-flow.md) are N/A.

Rules NOT in this table remain reviewer-only (e.g., FR/SC coverage cross-refs, BR depth heuristics, cardinality cross-check, composite detection — these need semantic judgement).

## Failure Trap Assertions

- **Trap 1 (proliferation):** every REG has ≥1 independence signal, drawn from: distinct API endpoint (read or write), independent loading state, independent scroll container, independent auth / permission gate, distinct business workflow, distinct mutation surface / API cluster (distinct write endpoints or POST/PUT/DELETE namespace — even if the initial GET payload is shared), distinct validation / action path. Missing signal → critical. Visual separation alone is NOT sufficient.
- **Trap 2 (tab/stepper misclassification):** mutually-exclusive tab content declared as REG (not SCR variants) → critical. Wizard/stepper content: Case A emitted without cited distinct-validation + distinct-endpoint evidence → warning (prompt researcher to re-evaluate as Case B). 2-step wizard emitted as Case A → critical.
- **Trap 3 (shared data):** collapse two REG candidates into one Feature ONLY when they share ALL of: read surface (same GET endpoint/store) AND write surface (same mutations) AND business workflow. Shared initial payload alone does NOT disqualify a split — if regions diverge on write endpoints, validation rules, action paths, or business workflow, they remain separate. Researcher self-check advisory (NOT reviewer-enforceable critical — reviewer cannot inspect codebase at review time).
- **Trap 4 (LOC-based over-split):** LOC is NOT a composite signal. H3 uses named wrapper components only. Advisory note: flag any ScreenList entry where the researcher's justification cites line count rather than named wrappers or import count.
- **Trap 5 (spec orphan):** every REG### in ScreenList must have an owner annotation (can be `TBD`) → critical if owner annotation missing entirely.
- **Trap 6 (inferred-signal abuse):** `[SIGNAL_INFERRED]` tag without all 3 justification parts (Intent matched, No-row reason, Observed pattern) → critical. Tag citing H1 (which has no signal table) → critical; H2–H6 only. Tag used to bypass a per-stack row that DOES match the screen's stack/library naming → critical (researcher must use explicit row first). Tag count exceeding `max(5, ceil(0.10 × SCR_count))` across the entire ScreenList document → warning (suggests per-stack tables need updating or stack/library not covered).
