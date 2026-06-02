# Pipeline — Wave Dependency Graph

Expresses rebuild-spec artifact generation order as `TaskCreate` chains.
Execution handled by TaskList — no custom orchestrator needed.

## Waves

| Wave | Artifact(s) | Depends On | Parallel |
|------|-------------|------------|---------|
| -1 | Hydrate (incremental only) | planner mode | — |
| 0 | Scout discovery | — | yes |
| 1 | SystemOverview, RouteList, DataModel | W0 | yes |
| 1.5 | DataModel structural gate | W1 data-model | — |
| 2 | ScreenList + ScreenFlow | W1.5 (RouteList + DataModel) | — |
|   | BehaviorLogic | W2 (ScreenList + ScreenFlow) | — |
| 3 | Permissions | W2b BehaviorLogic | — |
| 4 | UserStories | W3 | — |
| 4.5 | UserStories quality gate | W4 | — |
| 5 | FeatureList | W4.5 | — |
| 5.6 | FeatureList sanity review (fast gate) | W5 + W5.5 | — |
| 6 | FeatureSpec × F### | W5.6 | yes (fan-out) |
| 7a | Core artifact reviewer *(Review zone)* | W5 | yes (parallel with W6) |
| 7b | Feature spec reviewer (batched 5/reviewer) *(Review zone)* | W6 | yes (batches parallel) |
| 7-merge | Combine review reports *(Review zone)* | W7a + W7b | — |
| 7.5 | Structural fixer — edge-case safety net *(Pre-fix zone)* | W7-merge | — |
| 8 | Implementer + Re-reviewer (fix cycles, max 3, conditional) | W7-merge | — |
| 9 | Doc-writer (persist to docs/specs/) | W8 | — |
| 9.5 | Reverse-index refresh | W9 | — |

## Incremental orchestration (preamble)

When `docs/specs/.rebuild-state.json` exists, the orchestrator runs the incremental planner before any wave dispatch. **Note:** Wave -2 may pre-create the state file via bootstrap; if so, the planner invocation below sees a freshly-created state and proceeds with normal incremental flow.

### Wave -2 — Bootstrap detection (v2.x upgrade)

Fires BEFORE the planner invocation. Detects v2.x output without `.rebuild-state.json` and offers to bootstrap state from git history.

```js
// Wave -2 — Bootstrap detection (only fires when state is missing)
const stateFile = "docs/specs/.rebuild-state.json"
const v2EvidenceFiles = ["docs/specs/system-overview.md", "docs/specs/feature-list.md"]
const hasState = existsNonEmpty(stateFile)
const hasV2Evidence = v2EvidenceFiles.some(f => existsNonEmpty(f))

let bootstrapped = false
if (!hasState && hasV2Evidence) {
  const derivedSha = bashOutput("git log -1 --format=%H -- docs/specs/").trim()
  const headSha = bashOutput("git rev-parse HEAD").trim()

  if (!derivedSha) {
    console.log("[INFO] docs/specs/ not in git history — skipping bootstrap. Will fall back to full rebuild.")
  } else if (derivedSha === headSha) {
    console.log(`[INFO] derived baseline SHA = HEAD (${headSha.slice(0,7)}); bootstrap would produce empty diff. Falling back to full rebuild.`)
  } else {
    // Prompt user — Claude tool: AskUserQuestion
    const answer = await AskUserQuestion({
      header: "v2.x Upgrade",
      question: `Detected v2.x rebuild-spec output without state file. Baseline SHA derived from git log of docs/specs/ = ${derivedSha.slice(0,7)} (≠ HEAD). Bootstrap state from git and run incremental, OR force full rebuild?`,
      options: [
        {label: "Bootstrap from git (fast, ~2-3min)",
         description: "Treat docs/specs/ as canonical baseline at git SHA " + derivedSha.slice(0,7) + ". Diff source files from that SHA to HEAD. ⚠ If you hand-edited docs/specs/ since the last rebuild, those edits become the implicit baseline and pre-handedit source changes will be missed."},
        {label: "Full rebuild (slow, safe)",
         description: "Re-generate all artifacts + feature specs from scratch. 15-30 min for a 60-feature project but guaranteed correct."},
      ],
    })

    if (answer.startsWith("Bootstrap")) {
      bash(`.claude/skills/.venv/bin/python3 \
        claude/skills/rebuild-spec/scripts/build_source_to_fcode.py \
        --specs-root docs/specs/features \
        --docs-root docs/specs \
        --state-out docs/specs/.rebuild-state.json \
        --index-out docs/specs/_source-to-fcode.json \
        --mode full \
        --last-rebuild-sha "${derivedSha}"`)
      bootstrapped = true
      console.log(`[BOOTSTRAP] Emitted state file with last_rebuild_sha=${derivedSha.slice(0,7)}. Proceeding with incremental flow.`)
    } else {
      console.log("[INFO] User chose full rebuild — falling through to state_missing path.")
    }
  }
}
// If bootstrapped=true, state file now exists → planner below runs incremental as usual.
```

### Planner invocation

```
bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/incremental_planner.py \
  --plan-dir plans/<active-plan> \
  --docs-specs docs/specs \
  --scout-report plans/<active-plan>/artifacts/scout-report.md \
  [--full] [--since <sha>] [--dry-run] [--features F###,...] \
  [--threshold 0.30]
```

Exit 0 → `.incremental-plan.json` written; exit 1 → hard halt; exit 2 → arg error.

### Decision JSON shape

See `references/incremental-state-schema.md § .incremental-plan.json`.

### resolveWaveTaskId helper

Centralizes "create new task OR return prior-completed-task ID for blockedBy chains":

```
function resolveWaveTaskId(subject, dispatchFn) {
  if (mode === "full" || affected_waves.includes(subject)) {
    return dispatchFn()  // TaskCreate → returns new task ID
  }
  // Incremental: wave skipped → return sentinel for downstream blockedBy
  // Downstream waves see this as "already completed" and proceed.
  return `HYDRATED:${subject}`
}
```

### Branch on mode

```
const planJson = JSON.parse(readFile("plans/<active>/artifacts/.incremental-plan.json"))
const mode = planJson.mode
const affected_waves = planJson.affected_waves ?? []
const affected_fcodes = planJson.affected_fcodes ?? []
const w5_reran = planJson.w5_reran ?? false

if (mode === "incremental") {
  // Run hydrate
  bash: .claude/skills/.venv/bin/python3 \
    claude/skills/rebuild-spec/scripts/incremental_planner.py \
    --hydrate --plan-dir plans/<active-plan> --docs-specs docs/specs
  // Capture stderr → surface [OUT_OF_BAND_EDIT] warnings to user
}
// Then proceed to Wave 0+ dispatch (full or selective)
```

## Task chain pattern

```
// Wave 0
TaskCreate({ subject: "Scout: discovery scan",
  description: "Scan routing, data models, screens, bg logic, permissions. Detect project language from manifest (package.json → JS/TS; composer.json → PHP; Gemfile → Ruby; pyproject.toml → Python; pom.xml/build.gradle → Java; go.mod → Go; Cargo.toml → Rust). Scan all non-test, non-vendor source dirs relevant to the detected stack (e.g. pages/, views/, components/, features/, modules/ for JS/TS; app/Http/, resources/views/ for Laravel; app/controllers/, app/views/ for Rails; adapt accordingly). Multi-manifest rule: if multiple manifests coexist at root level (e.g. package.json + composer.json), the root-level manifest wins; priority order JS/TS > PHP > Ruby > Python > Java > Go > Rust if tied at same depth. Emit [MULTI_STACK] note in scout-report.md Notes section listing all detected stacks. Output MUST follow templates/scout-report-template.md. File Inventory is the contract for Wave 2 content-completeness and Wave 7 reviewer cross-validation — every source file must appear. After File Inventory: emit `## Background Logic Source Inventory` section. To do this: (1) read references/bl-source-patterns.md; (2) for Mode A stacks apply the folder-convention globs for the detected stack row; (3) for Mode B stacks apply the annotation/decorator grep markers from bl-source-patterns.md § Mode B Grep Markers; (4) emit one entry per file (Mode A) or per decorator hit (Mode B) sorted by category then path; (5) for any category with no matches emit `_(none found)_`; (6) for stacks/libraries not in the table use [SIGNAL_INFERRED] protocol (see bl-source-patterns.md § Signal Inference Fallback); (7) for [MULTI_STACK] projects emit one subsection per detected stack. Output: plans/<active-plan>/artifacts/scout-report.md" })

// Wave 1 — 3 tasks in parallel, each addBlockedBy: [scoutTaskId]
// In incremental mode, system-overview is NEVER in affected_waves — always use hydrated copy.
const sysOverviewTaskId = resolveWaveTaskId("Wave1: system-overview", () =>
  TaskCreate({ subject: "Wave1: system-overview",
    description: "Session context: read `plans/<active-plan>/artifacts/_session-context.md` FIRST.\nSynthesize SystemOverview. Template: system-overview-template.md. Schema: references/code-formats.md. Draft: plans/<active-plan>/artifacts/system-overview.md",
    addBlockedBy: [scoutTaskId] }))
const routeListTaskId = resolveWaveTaskId("Wave1: route-list", () =>
  TaskCreate({ subject: "Wave1: route-list",
    description: "Session context: read `plans/<active-plan>/artifacts/_session-context.md` FIRST.\nSynthesize RouteList. Template: route-list-template.md. Draft: plans/<active-plan>/artifacts/route-list.md",
    addBlockedBy: [scoutTaskId] }))
const dataModelTaskId = resolveWaveTaskId("Wave1: data-model", () =>
  TaskCreate({ subject: "Wave1: data-model",
    description: "Session context: read `plans/<active-plan>/artifacts/_session-context.md` FIRST.\nSynthesize DataModel. Template: data-model-template.md. Draft: plans/<active-plan>/artifacts/data-model.md",
    addBlockedBy: [scoutTaskId] }))

// Extract detected stack from scout report (Wave 0 output, available at this point)
const scoutReportPath = `plans/<active-plan>/artifacts/scout-report.md`
const scoutContent = existsNonEmpty(scoutReportPath) ? readFile(scoutReportPath) : ''
const detectedLangMatch = scoutContent.match(/^## Detected Language\s*\n\s*(\S[^\n]*)/m)
const detectedStack = detectedLangMatch ? detectedLangMatch[1].trim() : 'JS/TS'
const isMultiStack = scoutContent.includes('[MULTI_STACK]')

// Enumerate every root-level manifest present so reviewer/researcher know exactly
// which stacks' signal rows to OR-merge in H-rule tables.
// Bare filenames are intentional — these manifests live at the repo root (CWD).
const manifestMap = {
  'package.json': 'JS/TS',
  'composer.json': 'PHP',
  'Gemfile': 'Ruby',
  'pyproject.toml': 'Python',
  'pom.xml': 'Java',
  'build.gradle': 'Java',
  'go.mod': 'Go',
  'Cargo.toml': 'Rust'
}
const uniqueFoundStacks = [...new Set(
  Object.entries(manifestMap)
    .filter(([file]) => existsNonEmpty(file))
    .map(([, stack]) => stack)
)]
let stackNote = detectedStack
if (isMultiStack) {
  stackNote = uniqueFoundStacks.length > 1
    ? `${detectedStack} [MULTI_STACK — all stacks: ${uniqueFoundStacks.join(', ')}; apply union of signals for all listed stacks in H-rule tables (consult each stack's row in every H-rule table; OR-merge signals before counting)]`
    : `${detectedStack} [MULTI_STACK — scout flagged multi-stack but root manifest scan found ${uniqueFoundStacks.length} stack(s); cannot enumerate union — emit [STACK_LIST_MISSING] advisory in classification justification per composite-screen-detection.md § Stack Probe]`
}

// Wave 0.5 — Emit shared session-context file (read by all W1-W9 subagents)
bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/build_session_context.py \
  --plan-dir plans/<active-plan> \
  --scout-report plans/<active-plan>/artifacts/scout-report.md \
  --stack-note "${stackNote}"

// Also extract BL inventory fragment for W7a (avoids loading full scout-report)
bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/extract_scout_section.py \
  --scout-report plans/<active-plan>/artifacts/scout-report.md \
  --section "Background Logic Source Inventory" \
  --out plans/<active-plan>/artifacts/_scout-bl-inventory.md

// --- Wave 1.5: DataModel structural gate (before W2 dispatch) ---
// Runs after W1 data-model task completes. Single reviewer, DataModel-scoped.
// W2 dispatches ONLY after W1.5 reports passed: true.
// Incremental: skip W1.5 if data-model.md not re-generated this run.

let w15TaskId = null
const w15_reran = mode === "full" || affected_waves.includes("Wave1: data-model")
if (w15_reran) {
  w15TaskId = TaskCreate({
    subject: "Wave1.5: data-model-review",
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.

Fast structural review of DataModel ONLY — do NOT review other artifacts (that is Wave 7a's scope).

INPUT: plans/<active-plan>/artifacts/data-model.md

CHECKS (run all, report each separately):

**Check 1 — Entity completeness:**
- Each entity block has: name, description, at least one field with name+type
- Fail: entity with no fields, or entity with fields but no types documented

**Check 2 — DISC-### scope:**
- Each DISC-### entry in a discriminator table should have ≥2 enum values with DISTINCT behavioral outcomes
- DISC-### with only \`true\`/\`false\` values → critical (boolean flags belong in Business Rules)
- DISC-### with only 1 value → critical (not a discriminator, remove or expand)
- Cross-check: each DISC-### code references an actual entity field in the same entity block

**Check 3 — MODEL### uniqueness:**
- No duplicate MODEL### codes across the document
- Fail: MODEL001 appears in two different entity blocks

**Check 4 — DISC-### orphan check:**
- Each DISC-### code in the document is anchored to a specific entity's field
- Fail: DISC003 appears in Polymorphic section but no entity has a field mapped to DISC003

**Check 5 — Relationship completeness:**
- Each relationship entry has: source entity, target entity, cardinality
- Missing cardinality → warning

OUTPUT: plans/<active-plan>/artifacts/data-model-review.md

Use this exact frontmatter:
\`\`\`yaml
---
passed: true   # true = W2 can proceed; false = halt
issues: 0
warnings: 0
---
\`\`\`

Passed Checks: ONE LINE per check (\`✓ <check_name>\`). NO prose.

TOKEN BUDGET: Load data-model.md only — self-contained, no cross-artifact loading needed.`,
    addBlockedBy: [dataModelTaskId]
  })
} else {
  console.log("[INFO] W1.5 skipped — data-model not re-generated (incremental: prior DataModel carries forward)")
  w15TaskId = `SKIPPED:W1.5`
}

// Halt on W1.5 failure before W2 dispatch
if (w15TaskId && !w15TaskId.startsWith("SKIPPED:")) {
  if (!existsNonEmpty("plans/<active-plan>/artifacts/data-model-review.md")) {
    throw new Error("W1.5 HALT — gate output missing: data-model-review.md was not written. Check W1.5 task for errors.")
  }
  const w15Content = readFile("plans/<active-plan>/artifacts/data-model-review.md")
  const w15fm = parseFrontmatter(w15Content)
  if (w15fm.passed === "false" || w15fm.passed === false) {
    throw new Error(
      `W1.5 HALT — DataModel has ${w15fm.issues} critical issue(s). ` +
      `Fix data-model.md, then re-run Wave 1 (--artifact data-model) or restart from W1. ` +
      `Review: plans/<active-plan>/artifacts/data-model-review.md`
    )
  }
  console.log(`[INFO] W1.5 passed (issues: ${w15fm.issues ?? 0}, warnings: ${w15fm.warnings ?? 0})`)
}

const w15Blocker = (w15TaskId && !w15TaskId.startsWith("SKIPPED:")) ? w15TaskId : dataModelTaskId

// Wave 2 — threshold gate: merge W2a+W2b for small projects
bash: screen_count=$(.claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/count_screen_files.py \
  --scout-report plans/<active-plan>/artifacts/scout-report.md)
const W2_MERGE_THRESHOLD = parseInt(process.env.REBUILD_W2_MERGE_THRESHOLD ?? '30')

if (screen_count < W2_MERGE_THRESHOLD) {
  // Small project: merge W2a+W2b into a single researcher task.
  // Check BOTH individual W2 subjects (planner never emits "combined-screen-bg").
  const needsW2 = mode === "full"
    || affected_waves.includes("Wave2: screen-list + screen-flow")
    || affected_waves.includes("Wave2: behavior-logic")
  const combinedW2TaskId = needsW2
    ? TaskCreate({ subject: "Wave2: combined-screen-bg",
      description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST (single source for shared session inputs).

Generate ScreenList + ScreenFlow + BehaviorLogic in a SINGLE session.
Templates: screen-list-template.md, screen-flow-template.md, behavior-logic-template.md.
Drafts: plans/<active-plan>/artifacts/.
Context: references/composite-screen-detection.md (H1-H6 rules), references/verification-checklist.md (Composite Detection Rules + Failure Trap Assertions).
Detected stack: \${stackNote}.

EMIT ORDER: ScreenList FIRST (BL needs its service-call inventory), ScreenFlow second, BehaviorLogic last.
Route-first enumeration: cross-reference RouteList artifact. Apply composite-screen detection unconditionally.
Import chain rule: follow imports one level deep using language-specific mechanism.
CARDINALITY CONTRACT: read behavior-logic-template.md § Cardinality Contract. Read scout-report.md § Background Logic Source Inventory for authoritative file list.
Schema: references/code-formats.md (canonical 10 BL types + Source File/Source Symbol field schema).`,
      addBlockedBy: [routeListTaskId, w15Blocker] })
    : `HYDRATED:Wave2: combined-screen-bg`
  var w2TaskIds = [combinedW2TaskId]
} else {
  // Large project: existing W2a + W2b split path

// Wave 2a — ScreenList runs first (its service-call inventory feeds BehaviorLogic)
const screenListAndFlowTaskId = resolveWaveTaskId("Wave2: screen-list + screen-flow", () =>
  TaskCreate({ subject: "Wave2: screen-list + screen-flow",
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Generate ScreenList + ScreenFlow. Templates: screen-list-template.md, screen-flow-template.md. Drafts: plans/<active-plan>/artifacts/. Context: references/composite-screen-detection.md (H1-H6 rules — read this file before classifying any screen), references/verification-checklist.md (Composite Detection Rules + Failure Trap Assertions). Detected stack: ${stackNote} — pre-extracted from scout-report.md § ## Detected Language by the orchestrator; use this for per-stack signal selection in H-rule tables, do not re-read scout-report.md for stack detection. Route-first enumeration: Before applying H-rules, cross-reference the RouteList artifact (Wave 1, already complete). Map each distinct URL pattern to its page/component file. Each file serving a distinct URL path = one SCR candidate. Multiple URL patterns mapping to the SAME file = same SCR. Different files → separate SCR candidates. Then apply H1-H6 (full execution order: H6 → H4 → H5 → H2 → H3 → H1 → 2-of-3 gate) within each candidate. Apply composite-screen detection unconditionally to every screen file per references/composite-screen-detection.md. Import chain rule: if scout-report.md exists, use its flat file inventory to identify screen files (do not re-glob); if absent (e.g. --artifact entry point), emit [WARN] scout-report.md not found — skip content-completeness check and mark service coverage N/A. When scout-report exists: (1) resolve path aliases first (read tsconfig.json paths or package.json workspaces — unresolvable → treat as compliant, log [UNRESOLVED_ALIAS]); (2) follow imports one level deep using language-specific mechanism (ES6 import for JS/TS; use/require for PHP; require for Ruby; import for Python; import for Java/Kotlin/Go; use for Rust); (3) extract all service calls, API hooks, and helper functions in those immediate imports; (4) Known Limitation: barrel/re-export files (e.g. index.ts) re-exporting service modules at depth 2 are not followed — flag screens importing ONLY barrel files as [BARREL_IMPORT] advisory. The extracted service-call inventory is consumed by BehaviorLogic (Wave 2b) and confirms RouteList coverage.`,
    addBlockedBy: [routeListTaskId, w15Blocker] }))

// Wave 2b — BehaviorLogic waits on ScreenList to consume its service-call inventory
const behaviorLogicTaskId = resolveWaveTaskId("Wave2: behavior-logic", () =>
  TaskCreate({ subject: "Wave2: behavior-logic",
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Generate BehaviorLogic. Template: behavior-logic-template.md. Schema: references/code-formats.md (canonical 10 BL types + Source File/Source Symbol field schema). Context: screen-list.md (service-call references extracted by Wave 2a). CARDINALITY CONTRACT (read template § Cardinality Contract before writing any BL item): (1) Read scout-report.md § Background Logic Source Inventory — this is the authoritative file list; (2) For each inventory entry emit exactly 1 BL item (Mode A: 1 file = 1 BL; Mode B: 1 decorator hit = 1 BL; multiple hits in same file = multiple BL items); (2a) Sentinel handling — entries shaped \`- {category}: _(none found)_\` (value field is the sentinel) are scout markers for empty categories; SKIP them, never emit a BL; (3) Set Source File = inventory entry path and Source Symbol = class or method name (single symbol only — see template Rule C2 for forbidden multi-symbol delimiters); (4) Aggregation is a critical violation — do NOT combine multiple source files into one BL item; (5) For per-stack signal context (what counts as each BL type per stack) read references/bl-source-patterns.md. Draft: plans/<active-plan>/artifacts/behavior-logic.md`,
    addBlockedBy: [screenListAndFlowTaskId] }))

  var w2TaskIds = [screenListAndFlowTaskId, behaviorLogicTaskId]
} // end W2 threshold gate

// W2.5 (ScreenSpec fan-out) removed from main pipeline (2026-05-25).
// Use /rebuild-spec --screen-specs for standalone ScreenSpec pass after main rebuild.
// See ## Screen-Specs Pass section at end of this file.

// Wave 3 — blocks on W2b BehaviorLogic only
const w3BlockedBy = w2TaskIds

const permissionsTaskId = resolveWaveTaskId("Wave3: permissions", () =>
  TaskCreate({ subject: "Wave3: permissions",
    description: "Session context: read `plans/<active-plan>/artifacts/_session-context.md` FIRST.\nGenerate Permissions. Template: permissions-template.md. Draft: plans/<active-plan>/artifacts/permissions.md",
    addBlockedBy: w3BlockedBy }))

// Wave 4
const userStoriesTaskId = resolveWaveTaskId("Wave4: user-stories", () =>
  TaskCreate({ subject: "Wave4: user-stories",
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Generate UserStories. Template: user-stories-template.md. Draft: plans/<active-plan>/artifacts/user-stories.md.
Load: references/user-stories-ipe-protocol.md — run ALL IPE steps BEFORE writing any US.
Inputs: ScreenList (SCR### + source file paths), Permissions (actor split).`,
    addBlockedBy: [permissionsTaskId] }))

// --- Wave 4.5: UserStories quality gate (before W5 FeatureList dispatch) ---
// Runs after W4 user-stories task. W5 dispatches ONLY after W4.5 passes.
// Incremental: skip W4.5 if user-stories.md not re-generated this run.

let w45TaskId = null
const w45_reran = mode === "full" || affected_waves.includes("Wave4: user-stories")
if (w45_reran) {
  w45TaskId = TaskCreate({
    subject: "Wave4.5: user-stories-review",
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.

Fast quality review of UserStories ONLY — do NOT review other artifacts (that is Wave 7a's scope).

INPUT: plans/<active-plan>/artifacts/user-stories.md

CHECKS (run all, report each separately):

**Check 1 — Single intent per story (critical):**
- Each US### goal describes exactly ONE user action
- Fail indicators: goal contains "and" joining two distinct actions, "as well as", list of verbs (create + edit + delete), or covers full CRUD in one story
- Example fail: US003 "As a user, I can create, edit, and delete my profile" → 3 intents, split into 3 stories
- Flag as critical if ≥2 clearly distinct user intents in one story

**Check 2 — Actor clarity (critical):**
- Each US### has a named human actor (user, admin, manager, etc.)
- Fail: actor is "system", "application", "platform", or missing entirely
- "The system sends a notification" → not a user story, belongs in BehaviorLogic
- Flag as critical if actor is non-human or absent

**Check 3 — Outcome present (warning):**
- Each US### has a user-visible outcome ("so that..." or equivalent)
- No outcome = unclear why the feature matters → W5 cannot assess feature value
- Flag as warning if outcome absent

**Check 4 — Overly broad scope (warning):**
- US### covering an entire resource domain ("manage all X", "administer Y system") → too broad for W5 grouping
- Heuristic: goal contains "manage", "administer", "handle" as the ONLY verb without a specific action
- Acceptable: "manage my account settings" (specific resource, clear scope)
- Flag as warning, not critical (W5 can still attempt grouping)

**Check 5 — US### code uniqueness (critical):**
- No duplicate US### codes in the document
- Fail: US005 appears in two separate story blocks

OUTPUT: plans/<active-plan>/artifacts/user-stories-review.md

Use this exact frontmatter:
\`\`\`yaml
---
passed: true   # true = W5 can proceed; false = halt
issues: 0
warnings: 0
---
\`\`\`

Severity: Checks 1, 2, 5 are critical (issues count). Checks 3, 4 are warnings.
Halt threshold: passed=false only when issues > 0.

Passed Checks: ONE LINE per check (\`✓ <check_name>\`). NO prose.

TOKEN BUDGET: Load user-stories.md only. No cross-artifact loading needed.`,
    addBlockedBy: [userStoriesTaskId]
  })
} else {
  console.log("[INFO] W4.5 skipped — user-stories not re-generated (incremental: prior UserStories carries forward)")
  w45TaskId = `SKIPPED:W4.5`
}

// Halt on W4.5 failure before W5 dispatch
if (w45TaskId && !w45TaskId.startsWith("SKIPPED:")) {
  if (!existsNonEmpty("plans/<active-plan>/artifacts/user-stories-review.md")) {
    throw new Error("W4.5 HALT — gate output missing: user-stories-review.md was not written. Check W4.5 task for errors.")
  }
  const w45Content = readFile("plans/<active-plan>/artifacts/user-stories-review.md")
  const w45fm = parseFrontmatter(w45Content)
  if (w45fm.passed === "false" || w45fm.passed === false) {
    throw new Error(
      `W4.5 HALT — UserStories has ${w45fm.issues} critical issue(s). ` +
      `Fix user-stories.md, then re-run Wave 4 (--artifact user-stories) or restart from W4. ` +
      `Review: plans/<active-plan>/artifacts/user-stories-review.md`
    )
  }
  console.log(`[INFO] W4.5 passed (issues: ${w45fm.issues ?? 0}, warnings: ${w45fm.warnings ?? 0})`)
}

const w45Blocker = (w45TaskId && !w45TaskId.startsWith("SKIPPED:")) ? w45TaskId : userStoriesTaskId

// Wave 5
const featureListTaskId = resolveWaveTaskId("Wave5: feature-list", () =>
  TaskCreate({ subject: "Wave5: feature-list",
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Generate FeatureList from all prior drafts. Template: feature-list-template.md. Draft: plans/<active-plan>/artifacts/feature-list.md.

After writing feature-list.md, the W5 researcher MUST ALSO emit a canonical fcode/slug JSON and pre-create per-feature folders. Authority: references/canonical-fcode-schema.md.

Steps (run AFTER feature-list.md is finalized):
1. Parse every '### F###: Name' heading under '## Feature Details'.
2. Derive slug per references/canonical-fcode-schema.md § Slug Grammar (CamelCase, alnum-only, ≤36 chars).
3. Collision check: if two features derive the same slug → print '[ERROR] SLUG_COLLISION: F### "<a>" and F### "<b>" both derive slug "<slug>"' to stdout, ABORT (no JSON, no folders). User resolves by renaming a feature, then reruns Wave 5.
4. Write plans/<active-plan>/artifacts/_canonical-fcodes.json per the schema doc (sorted by fcode).
5. For each feature: mkdir -p plans/<active-plan>/artifacts/features/{slug}/ AND touch plans/<active-plan>/artifacts/features/{slug}/.pending (zero-byte marker).

Both outputs (canonical JSON + .pending folders) are consumed by Wave 5.5 existence validator and Wave 6 fan-out (slug source).`,
    addBlockedBy: [w45Blocker] }))
```

### Wave 5.5 — Feature existence gate

After Wave 5 completes, the orchestrator runs the existence validator BEFORE Wave 6 dispatch. Validator is unconditional — no env var, no opt-in (skipped in incremental mode if `w5_reran === false`).

```
// Wave 5.5 — conditional on w5_reran in incremental mode
if (mode === "full" || w5_reran) {
  bash: .claude/skills/.venv/bin/python3 \
    claude/skills/rebuild-spec/scripts/validate_feature_existence.py \
    --plan-dir plans/<active-plan> \
    --summary-out plans/<active-plan>/artifacts/validation/validation-summary.json

  exit 0 → status PASS or WARN → proceed to Wave 6 fan-out
  exit 1 → status FAIL → HALT pipeline; surface JSON issues to user; prompt fix; NO Wave 6 dispatch
  exit 2 → internal validator error → surface stderr; halt
} else {
  console.log("[INFO] W5.5 skipped — W5 not re-run (incremental: prior validation carries forward)")
}
```

Validator authority: `references/canonical-fcode-schema.md` (schema + slug grammar). Output schema: `validation-summary.json` (see § Wave 6.5 below).

### Wave 5 post-write: update session-context feature_count

After Wave 5 completes (and W5.5 passes), update the shared session-context file with the actual feature count:

```
const featureCount = JSON.parse(readFile("plans/<active-plan>/artifacts/_canonical-fcodes.json")).features.length
bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/build_session_context.py \
  --plan-dir plans/<active-plan> \
  --scout-report plans/<active-plan>/artifacts/scout-report.md \
  --stack-note "${stackNote}" \
  --feature-count ${featureCount}
```

// --- Wave 5.6: FeatureList sanity review (fast gate before W6 dispatch) ---
// Runs after W5.5 passes. Single reviewer task, FeatureList-scoped.
// W6 fan-out dispatches ONLY after W5.6 reports passed: true.
// Incremental: skip W5.6 if w5_reran === false (FeatureList not re-generated this run).

let w56TaskId = null
if (mode === "full" || w5_reran) {
  w56TaskId = TaskCreate({
    subject: "Wave5.6: feature-list-review",
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.

Fast sanity review of FeatureList ONLY — do NOT review other core artifacts (that is Wave 7a's scope).

INPUT: plans/<active-plan>/artifacts/feature-list.md

CHECKS (run all, report each separately):

**Group A — Structural integrity (cross-reference checks):**
1. **Coverage completeness**: are all US### from user-stories.md referenced by at least one F###?
   - Missing US### → critical
   - Missing SCR### (from screen-list.md main index) → critical
2. **Orphan codes**: codes in FeatureList not found in their source artifact
   - US### in F### but not in user-stories.md → critical
   - SCR### in F### but not in screen-list.md → critical
3. **F-code uniqueness**: no duplicate F-code numbers across Feature Details

**Group B — Quality criteria (per-F### semantic checks; read Feature Details section):**
4. **Single Intent**: each F### describes exactly one user-facing intent
   - Fail: F003_UserManagement describes login + profile edit + admin dashboard (3 intents)
   - → critical if multiple intents detected
5. **Clear Flow**: each F### has an identifiable input→process→output
   - Fail: F007_System — no clear what triggers it, what it processes, or what user gets
   - → warning (flag for human review)
6. **Vague naming**: F### name is too broad to scope a sprint
   - Patterns: "Management", "System", "Handler", "Admin", "CRUD" as the ONLY noun
   - → warning
7. **Scope overlap**: two F### share >50% of the same US### or SCR### keywords in description
   - → warning

**Group C — Grouping coherence:**
8. **Grouping coherence**: do F-code assignments reflect logical feature boundaries?
   - No F### should aggregate unrelated concerns
   - → critical if clearly mixed concerns

OUTPUT: plans/<active-plan>/artifacts/feature-list-review.md

Use this exact frontmatter:
\`\`\`yaml
---
passed: true   # true = W6 can proceed; false = halt, surface issues to user
issues: 0      # count of critical findings
warnings: 0    # count of non-blocking findings
---
\`\`\`
Then list issues in markdown body.

Passed Checks: ONE LINE per check (\`✓ <check_name>\`). NO prose.

TOKEN BUDGET: Load full feature-list.md (Feature Details needed for quality checks). Load user-stories.md (headers + US### list only). Load screen-list.md (SCR### index only). DO NOT load full upstream artifacts beyond these.`,
    addBlockedBy: [featureListTaskId]
  })
} else {
  console.log("[INFO] W5.6 skipped — W5 not re-run (incremental: prior FeatureList carries forward)")
  w56TaskId = `SKIPPED:W5.6`
}

// Halt on W5.6 failure before W6 dispatch
if (w56TaskId && !w56TaskId.startsWith("SKIPPED:")) {
  if (!existsNonEmpty("plans/<active-plan>/artifacts/feature-list-review.md")) {
    throw new Error("W5.6 HALT — gate output missing: feature-list-review.md was not written. Check W5.6 task for errors.")
  }
  const w56Content = readFile("plans/<active-plan>/artifacts/feature-list-review.md")
  const w56fm = parseFrontmatter(w56Content)
  if (w56fm.passed === "false" || w56fm.passed === false) {
    throw new Error(
      `W5.6 HALT — FeatureList has ${w56fm.issues} critical issue(s). ` +
      `Fix feature-list.md, then re-run Wave 5 (--artifact feature-list) or restart from W5. ` +
      `Review: plans/<active-plan>/artifacts/feature-list-review.md`
    )
  }
  console.log(`[INFO] W5.6 passed (issues: ${w56fm.issues ?? 0}, warnings: ${w56fm.warnings ?? 0})`)
}

const w56Blocker = (w56TaskId && !w56TaskId.startsWith("SKIPPED:")) ? w56TaskId : featureListTaskId

## Fan-out pattern (Wave 6)

Slug source: read `plans/<active-plan>/artifacts/_canonical-fcodes.json` (emitted by Wave 5). Each `features[].slug` is authoritative. Fallback for legacy plans without canonical JSON: parse `feature-list.md` `## Feature Hierarchy` rows with `^\|\s*(F\d{3}_\w+)\s*\|` (orchestrator emits `[WARN] canonical_missing` once).

**Incremental mode target selection:**

```
// Determine which F### to process in Wave 6
const wave6Targets = (mode === "incremental" && !w5_reran)
    ? affected_fcodes   // subset fan-out
    : allCanonicalFcodes // all F### (full mode or w5 re-ran)

if (wave6Targets.length === 0 && mode === "incremental") {
  console.log("[INFO] incremental: 0 affected F### — W6 skipped")
  // skip W6, W6.5, W7b — proceed directly to W7-merge (carry-forward only)
}
```

**Small codebases (F### count ≤ 20):** flat fan-out — one task per feature, all blocked on FeatureList.

```
// Collect task IDs for Wave 7 blocking
const allFeatureTaskIds = []

// Repeat for every F### in wave6Targets (full: all from canonical; incremental: affected only)
const wave6TaskId = TaskCreate({ subject: "Wave6: feature-spec F001_Auth",
  description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Generate DETAILED spec for F001_Auth.

PRE-WRITE GUARD (idempotent — runs even if Wave 5 already created the folder):
bash: mkdir -p plans/<active-plan>/artifacts/features/F001_Auth/

CONTRACT: Read references/feature-spec-researcher-contract.md for mandatory rules (includes § Decision Logic Extraction (DEC-###) for user-facing decision capture).
TEMPLATE: feature-spec-template.md.
DRAFT: plans/<active-plan>/artifacts/features/F001_Auth/spec.md
POST-WRITE: On successful spec.md write, run: rm plans/<active-plan>/artifacts/features/F001_Auth/.pending

CONTEXT LOADING (token-efficient — DO NOT load full artifacts):
1. Grep plans/<active-plan>/artifacts/feature-list.md for the F001 entry.
   Extract all referenced codes: US###, SCR###, ROUTE###, MODEL###, BL###, PERM###.
2. For each referenced code, Grep ONLY that code's section from its artifact:
   - US### → user-stories.md (heading ### US### … read until next ### US)
   - SCR### → screen-list.md (heading ### SCR### … read section)
   - BL### → behavior-logic.md (heading ### BL### … read section)
   - PERM### → permissions.md (heading ### PERM### … read section)
   - ROUTE### → route-list.md (grep the route entry row)
   - MODEL### → data-model.md (grep the entity entry)
3. Always read in full: system-overview.md (small, provides global context).
4. DO NOT read entire upstream artifacts — only the sections for codes in step 1.

SOURCE CODE: Use Grep/Read to find and read real controller(s), model(s), job(s),
service(s), policy(ies), and page files. Extract file paths, line ranges, method names,
table names, HTTP status codes, job class names, event names from actual code.

ALL TEMPLATE SECTIONS MANDATORY. DEPTH BAR enforced — see contract.

FEATURE ENTRY POINTS (fragment file pattern — mandatory):
After writing spec.md, emit a fragment file for the Feature Entry Points consolidation:
bash: mkdir -p plans/<active-plan>/artifacts/_feature-entry-points/
Write to: plans/<active-plan>/artifacts/_feature-entry-points/F001.md
Content:
### F001_{name}

- **Entry screen**: {SCR###_Name} — \`{/initial/route}\`
- **Owned screens**:
  - {SCR###_Name} — \`{/route/path}\` (atomic | composite)
- **Exit screens**: {SCR###_Name} (on {action/completion})
DO NOT append directly to screen-flow.md — parallel W6 tasks would race-write the same file.
The orchestrator consolidates all _feature-entry-points/F###.md fragments into screen-flow.md after W6 completes.`,
  addBlockedBy: [w56Blocker] })
allFeatureTaskIds.push(wave6TaskId)
```

**Large codebases (F### count > 20):** batch of N features per dispatcher task (default 5; env: `REBUILD_W6_BATCH_SIZE`) — bounds peak context and rate-limit pressure.

```
// Chunk F### list into groups of N (default 5; env override for rate-limit tuning)
const W6_BATCH_SIZE = parseInt(process.env.REBUILD_W6_BATCH_SIZE ?? '5')
const batches = chunk(fCodes, W6_BATCH_SIZE)

// Each batch gets a dispatcher task that spawns researcher per feature within it.
// Batches run sequentially to cap parallel subagent count; features within a batch run in parallel.
let prevBatchId = w56Blocker
for (const [i, batch] of batches.entries()) {
  const batchTaskId = TaskCreate({
    subject: `Wave6.batch-${pad2(i+1)}: feature-specs (${batch[0]}..${batch.at(-1)})`,
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Generate DETAILED specs for ${batch.length} features in parallel: ${batch.join(', ')}.
Apply the same rules as the small-codebase Wave 6 block above to EACH feature in this batch:
- PRE-WRITE GUARD: for each F### in this batch, run \`mkdir -p plans/<active-plan>/artifacts/features/{slug}/\` (idempotent — Wave 5 already created folders + .pending markers; this is belt-and-suspenders).
- Read researcher contract, use scoped context loading (Grep per-feature codes only), read real source code.
- POST-WRITE: on each successful spec.md write, run \`rm plans/<active-plan>/artifacts/features/{slug}/.pending\`. Failure to remove the marker → Wave 7b reviewer marks the feature as MISSING.

Slug source: plans/<active-plan>/artifacts/_canonical-fcodes.json (canonical). Fallback if absent: parse feature-list.md hierarchy rows.

Contract: references/feature-spec-researcher-contract.md.
Template: feature-spec-template.md. Drafts: plans/<active-plan>/artifacts/features/<F###>/spec.md.

FEATURE ENTRY POINTS (fragment file pattern — mandatory for each F### in this batch):
After writing each spec.md, emit a fragment file:
  mkdir -p plans/<active-plan>/artifacts/_feature-entry-points/
  Write to: plans/<active-plan>/artifacts/_feature-entry-points/{fcode}.md
  Content: ### F###_{name} block (entry/owned/exit screens). See W6 small-codebase block above for format.
DO NOT append directly to screen-flow.md — parallel tasks would race-write the same file.
On batch completion, write plans/<active-plan>/artifacts/wave6-batch-${pad2(i+1)}.flag listing completed F###.`,
    addBlockedBy: [prevBatchId]
  })
  prevBatchId = batchTaskId
}
// Wave 7 reviewer blocks on the final batch (large codebase)
```

**Rationale:** orchestrator only holds batch-level task state in context at any time, not all N features.

### Post-W6 — Feature Entry Points consolidation

After ALL Wave 6 tasks complete (before Wave 6.5), consolidate fragment files into screen-flow.md:

```
// Consolidate _feature-entry-points/ fragments into screen-flow.md § Feature Entry Points
const fragmentDir = `plans/<active-plan>/artifacts/_feature-entry-points/`
const fragmentFiles = bash(`ls ${fragmentDir}*.md 2>/dev/null | sort`).split('\n').filter(Boolean)

if (fragmentFiles.length > 0) {
  const consolidated = fragmentFiles.map(f => readFile(f)).join('\n\n')
  // Replace {POPULATED_BY_W6} placeholder in screen-flow.md Feature Entry Points section
  const screenFlowPath = `plans/<active-plan>/artifacts/screen-flow.md`
  const screenFlow = readFile(screenFlowPath)
  const updated = screenFlow.replace('{POPULATED_BY_W6}', consolidated)
  writeFile(screenFlowPath, updated)
  console.log(`[INFO] Feature Entry Points consolidated: ${fragmentFiles.length} features`)
  // Clean up ephemeral fragment files
  bash(`rm -rf ${fragmentDir}`)
} else {
  console.log("[WARN] No _feature-entry-points/ fragments found — Feature Entry Points section not populated")
}
```

This is the ONLY step that writes to the `## Feature Entry Points` section of screen-flow.md. W6 researchers MUST NOT append to screen-flow.md directly.

### Wave 6.5 — Spec + citation gate

After ALL Wave 6 researcher tasks (or batches) return, the orchestrator runs two deterministic validators that share a single aggregator file `artifacts/validation/validation-summary.json`. Unconditional — no env var.

```
// Wave 6.5 — feature-spec structural + source-citation gate
// In incremental mode with wave6Targets: pass --spec flags for touched F### only.
// Validators merge results into existing validation-summary.json (carry forward untouched entries).
const specFlags = (mode === "incremental" && !w5_reran)
    ? wave6Targets.map(f => `--spec ${f}`).join(' ')
    : ''

bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/validate_feature_spec.py \
  --plan-dir plans/<active-plan> \
  --summary-out plans/<active-plan>/artifacts/validation/validation-summary.json \
  ${specFlags}

bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/validate_source_citations.py \
  --plan-dir plans/<active-plan> \
  --summary-out plans/<active-plan>/artifacts/validation/validation-summary.json \
  ${specFlags}

read plans/<active-plan>/artifacts/validation/validation-summary.json:
  if overall_status === "FAIL":
    for each fcode in validators.specs where status === "FAIL":
      spawn implementer task: "Fix validator issues in {spec_path} per validation-summary.json validators.specs.{fcode}.issues"
      on implementer return: re-run BOTH validators for that fcode only (--spec arg)
      if still FAIL after 2 implementer cycles: spawn fresh researcher re-draft for that fcode
  else (PASS or WARN):
    dispatch W7b reviewer batches; inject per-fcode `specs.{fcode}` slot into reviewer prompt
```

Validator output schema (single file under `artifacts/validation/`):

```json
{
  "schema_version": 1,
  "generated_at": "ISO-8601",
  "plan": "<active-plan>",
  "overall_status": "PASS|WARN|FAIL",
  "totals": {"critical": 0, "warning": 0, "passed_specs": 0, "failed_specs": 0},
  "validators": {
    "feature_existence": {"status": "...", "summary": {...}, "issues": [...]},
    "specs": {
      "F001_Auth": {
        "spec_path": "plans/.../features/F001_Auth/spec.md",
        "status": "PASS|WARN|FAIL",
        "summary": {"critical": 0, "warning": 0},
        "issues": [{"validator": "feature_spec|citation", "severity": "...", "rule_id": "...", "location": {...}, "message": "..."}]
      }
    }
  }
}
```

Validator authority: `references/canonical-fcode-schema.md`. Coverage table: `references/verification-checklist.md § Deterministic Validator Coverage`.

## Review + fix cycle (Waves 7-9)

```
// review-report.md format — markdown body with YAML frontmatter:
//
// ---
// failed: <number>      # count of critical-severity issues (0 = all pass)
// warnings: <number>    # count of warning-severity issues
// result: PASS | FAIL   # PASS iff failed === 0
// ---
// (markdown body follows — use templates/review-report-template.md as base)
//
// Reviewer MUST start from templates/review-report-template.md and fill in all fields.
// Orchestrator reads ONLY the frontmatter to branch on failed count.

/**
 * Parse review-report.md body and group critical issues by affected file path.
 * Returns: Map<filePath, issueList[]>
 *
 * Assumes review-report.md body format from review-report-template.md:
 *   ## Critical Issues
 *   ### {ArtifactName or F###_Slug}
 *   - **File:** `plans/.../artifacts/...`
 *   - **Issue:** [description]
 */
function extractIssuesByFile(reportContent) {
  const fileMap = new Map()  // filePath → [{issue, location, rule_id}]
  const lines = reportContent.split('\n')

  let currentFile = null
  let inCritical = false

  for (const line of lines) {
    if (line.startsWith('## Critical Issues')) { inCritical = true; continue }
    if (line.startsWith('## ') && line !== '## Critical Issues') { inCritical = false; continue }
    if (!inCritical) continue

    if (line.startsWith('### ')) {
      currentFile = null
      continue
    }
    const fileMatch = line.match(/\*\*File:\*\*\s*`([^`]+)`/)
    if (fileMatch) {
      currentFile = fileMatch[1].trim()
      if (!fileMap.has(currentFile)) fileMap.set(currentFile, [])
      continue
    }
    const issueMatch = line.match(/^\s*-\s+(.+)/)
    if (issueMatch && currentFile && (line.includes('Issue') || line.includes('CRITICAL') || line.includes('[critical]'))) {
      fileMap.get(currentFile).push(issueMatch[1].trim())
    }
  }

  // Fallback: if no structured file refs found, return single entry with full report path
  if (fileMap.size === 0) {
    fileMap.set('review-report.md', ['See full review-report.md for all issues'])
  }
  return fileMap
}

function parseFrontmatter(content) {
  const match = content.match(/^---\n([\s\S]*?)\n---/)
  if (!match) return {}
  return Object.fromEntries(
    match[1].split('\n')
      .filter(line => line.includes(':'))
      .map(line => { const [k, ...v] = line.split(':'); return [k.trim(), v.join(':').split('#')[0].trim()] })
  )
}

// ═══════════════════════════════════════════════════════
// REVIEW ZONE (W7a | W7b | W7-merge)
// LLM reviewer passes — semantic quality checks.
// W7a runs in parallel with W6; W7b runs after W6.
// W7-merge combines all batch reports into review-report.md.
// ═══════════════════════════════════════════════════════

// --- Wave 7a: Core artifact review (runs in PARALLEL with Wave 6) ---
// Blocks only on W5 (FeatureList), NOT on W6. Reviews the 9 core doc artifacts.
// Incremental mode: skip W7a if no core wave re-ran (only W6 subset dispatched).
const re_generated_artifacts = (mode === "incremental")
    ? affected_waves.map(w => {
        // Reverse-map wave subjects to filenames
        const mapping = {
          "Wave1: route-list": ["route-list.md"],
          "Wave1: data-model": ["data-model.md"],
          "Wave2: screen-list + screen-flow": ["screen-list.md", "screen-flow.md"],
          "Wave2: behavior-logic": ["behavior-logic.md"],
          "Wave3: permissions": ["permissions.md"],
          "Wave4: user-stories": ["user-stories.md"],
          "Wave5: feature-list": ["feature-list.md"],
        }
        return mapping[w] ?? []
      }).flat()
    : ["all"]

const w7aTaskId = (mode === "incremental" && re_generated_artifacts.length === 0)
  ? `SKIPPED:W7a`
  : TaskCreate({ subject: "Wave7a: core-artifact-review",
  description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Review ${mode === "incremental" ? "re-generated" : "9"} core artifacts: ${mode === "incremental" ? re_generated_artifacts.join(', ') : "system-overview, route-list, data-model, screen-list, screen-flow, behavior-logic, permissions, user-stories, feature-list"}.
${mode === "incremental" ? `\nINCREMENTAL MODE: Only the above artifacts were re-generated this run. Remaining artifacts are unchanged from prior rebuild — read them as upstream context (read-only) but do not review them for compliance.\n` : ''}
Detected stack: ${stackNote} — when applying composite detection rules from verification-checklist.md, select per-stack signals from the row matching this stack value (see composite-screen-detection.md § Stack Probe).

CHECKLIST SECTION TARGETING — load ONLY these sections from references/verification-checklist.md:
- "## Universal rules"
- "### SystemOverview" through "### FeatureList" (all core artifact sections)
- "## Composite Detection Rules"
- "## Failure Trap Assertions"
SKIP the "### FeatureSpec" section entirely (handled by Wave 7b).

BEHAVIOR LOGIC CARDINALITY CHECK — for the behavior-logic artifact: load plans/<active-plan>/artifacts/_scout-bl-inventory.md (pre-extracted BL fragment), then apply all 4 rules from verification-checklist.md § BehaviorLogic "Cardinality Cross-Check" (total count gap per stack + MAX, category drop, Source File check, orphan file check). Emit the "### BehaviorLogic Cardinality" output block in the review report.

Passed Checks: ONE LINE per rule (\`✓ <rule_id> @ <fcode>\`). NO prose. NO grouping under headings.

Use templates/review-report-template.md as base. Output: plans/<active-plan>/artifacts/core-review-report.md`,
  addBlockedBy: [featureListTaskId] })

// --- Wave 7b: Feature spec review (batched, runs after Wave 6) ---
// Blocks on ALL Wave 6 outputs, same as the old single-reviewer W7.
// Incremental mode: review only wave6Targets (affected fcodes). Empty targets → skip W7b entirely.
const wave7bBlockedBy = isLargeCodebase
  ? [prevBatchId]       // prevBatchId = ID of the last batch in the loop above
  : allFeatureTaskIds   // array populated during small-codebase fan-out

// Chunk feature specs into groups of 5 for bounded reviewer context
const reviewTargets = (mode === "incremental" && !w5_reran) ? wave6Targets : fCodes
const reviewBatches = chunk(reviewTargets, 5)
const allW7bTaskIds = []

if (reviewTargets.length === 0 && mode === "incremental") {
  console.log("[INFO] incremental: 0 affected F### — W7b skipped")
  // allW7bTaskIds stays empty; W7-merge uses only W7a output
}

// Read validator summary (emitted by Wave 6.5) for per-batch prompt injection.
// Absent JSON = legacy plan → reviewer falls back to full checklist (see verification-checklist § Validator Pre-Check Protocol).
const validatorSummaryPath = `plans/<active-plan>/artifacts/validation/validation-summary.json`
const validatorSummary = existsNonEmpty(validatorSummaryPath)
  ? JSON.parse(readFile(validatorSummaryPath))
  : null

function buildValidatorPreamble(batchFcodes) {
  if (!validatorSummary) return `## Validator pre-check\n[validator-summary-absent] — apply full checklist (legacy plan).\n`
  const overall = validatorSummary.overall_status ?? 'PASS'
  const lines = [`## Validator pre-check (auto-injected)`, `overall_status: ${overall}`]
  for (const fcode of batchFcodes) {
    const slot = validatorSummary.validators?.specs?.[fcode]
    if (!slot) { lines.push(`- ${fcode}: no validator data`); continue }
    const { status, summary, issues } = slot
    lines.push(`- ${fcode}: ${status} (${summary.critical} critical, ${summary.warning} warning)`)
    for (const i of issues || []) {
      lines.push(`  - [${i.severity}] ${i.rule_id} @ ${i.location.file}:${i.location.line ?? '?'} — ${i.message}`)
    }
  }
  lines.push(`Skip rules listed in verification-checklist.md § Deterministic Validator Coverage that report PASS; mark them [deterministic-pass] in the review report.`)
  lines.push(`.pending marker rule: if plans/<active-plan>/artifacts/features/{slug}/.pending exists, emit MISSING for that fcode in review-report frontmatter (counts toward failed for Wave 9 gate).`)
  lines.push(`Focus: semantic depth (BR/FR coverage, fabricated citations, cross-ref accuracy, edge case sufficiency).`)
  return lines.join('\n') + '\n'
}

for (const [i, batch] of reviewBatches.entries()) {
  const w7bTaskId = TaskCreate({
    subject: `Wave7b.batch-${pad2(i+1)}: feature-spec-review (${batch[0]}..${batch.at(-1)})`,
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Review ${batch.length} feature specs: ${batch.join(', ')}.

Detected stack: ${stackNote} — when validating composite-screen tags (REG/SCR variants) in feature specs, select per-stack signals from the row matching this stack value (see composite-screen-detection.md § Stack Probe).

${buildValidatorPreamble(batch)}
CHECKLIST SECTION TARGETING — load ONLY these sections from references/verification-checklist.md:
- "## Validator Pre-Check Protocol" (read FIRST — apply per-fcode behavior above)
- "## Universal rules" (including § Pending Marker Rule)
- "### FeatureSpec" (feature spec rules only)
- "## Composite Detection Rules"
- "## Failure Trap Assertions"
- "## Deterministic Validator Coverage" (rule_ids to mark [deterministic-pass] on PASS)
SKIP all core artifact sections (SystemOverview through FeatureList — handled by Wave 7a).

For each spec, cross-ref against its F### entry in feature-list.md. For each features/{slug}/ folder: if .pending present, emit MISSING in frontmatter for that fcode.

Passed Checks: ONE LINE per rule (\`✓ <rule_id> @ <fcode>\`). NO prose. NO grouping under headings.
Scout-report not needed for this task — do NOT read scout-report.md.

Use templates/review-report-template.md as base. Output: plans/<active-plan>/artifacts/feature-review-batch-${pad2(i+1)}.md`,
    addBlockedBy: wave7bBlockedBy
  })
  allW7bTaskIds.push(w7bTaskId)
}

// W7c (ScreenSpec reviewer) removed from main pipeline (2026-05-25).
// ScreenSpec review runs in standalone --screen-specs pass (Wave SS.2).

// --- Wave 7-merge: Combine all review reports ---
// Incremental mode: if prior review reports exist in docs/specs/.review-archive/<latest>/,
// merge prior sections for untouched artifacts with new sections from W7a/W7b.
const w7MergeBlockedBy = [w7aTaskId, ...allW7bTaskIds].filter(id => !id.startsWith("SKIPPED:"))
const w7MergeTaskId = TaskCreate({ subject: "Wave7-merge: combine review reports",
  description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Merge core-review-report.md + all feature-review-batch-*.md into a single review-report.md.
Sum failed/warnings counts across all reports. Set result = PASS iff combined failed === 0.
${mode === "incremental" ? `\nINCREMENTAL CARRY-FORWARD: If docs/specs/.review-archive/ exists, read the latest archived review reports. Carry forward review sections for untouched artifacts (those NOT in the current run's re-generated set). Merge with new W7a/W7b sections. Only new + carry-forward sections count toward failed/warnings totals.\n` : ''}
When merging Passed Checks: if same rule_id appears for ≥3 consecutive F### codes, collapse to \`✓ <rule_id> @ F###..F### (<N>/<N>)\`. Drop any line under Passed Checks not matching \`^✓ \` pattern (post-filter for compliance).

Use templates/review-report-template.md format with combined YAML frontmatter.
Output: plans/<active-plan>/artifacts/review-report.md`,
  addBlockedBy: w7MergeBlockedBy })

// ═══════════════════════════════════════════════════════
// PRE-FIX ZONE (W7.5)
// Deterministic structural fixer — stdlib only, no LLM.
// Edge-case safety net: expected near-no-op when W6.5
// FeatureSpec.linked_fr_missing validator is active.
// W7.6 removed 2026-05-25 (root cause fixed at W6.5).
// ═══════════════════════════════════════════════════════

// --- Wave 7.5: Structural fixer (regex-based, no LLM) ---
// Inserts placeholder `**Linked FR:** FR-???` into BR/SM/ALG/INT blocks missing it.
// Decrements `failed` count in review-report.md by resolved structural-only issues.
bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/structural_fixer.py \
  --plan-dir plans/<active-plan> \
  --incremental-plan-json plans/<active-plan>/artifacts/.incremental-plan.json

// Wave 7.6 removed (2026-05-25): FeatureSpec.linked_fr_missing validator in W6.5
// catches missing Linked FR before W7. W7.5 is retained as edge-case safety net
// for legacy plans and resume scenarios where W6.5 gate was not present.

// After Wave 7.5, re-read frontmatter to determine branch.
const reportContent = readFile("plans/<active-plan>/artifacts/review-report.md")
const report = parseFrontmatter(reportContent)
if (!report.result) throw new Error("review-report.md missing YAML frontmatter — reviewer must use review-report-template.md")
let currentFailed = parseInt(report.failed ?? 0)

const MAX_FIX_CYCLES = 3
const REBUILD_W8_MAX_PARALLEL = parseInt(process.env.REBUILD_W8_MAX_PARALLEL ?? '5')
let fixCycle = 0
let lastReviewTaskId = w7MergeTaskId

// If W7.5 resolved all issues, this loop is naturally skipped (currentFailed === 0)
while (currentFailed > 0 && fixCycle < MAX_FIX_CYCLES) {
  fixCycle++

  // Extract issues grouped by file from review-report.md
  const reportContent = readFile("plans/<active-plan>/artifacts/review-report.md")
  const issuesByFile = extractIssuesByFile(reportContent)
  const affectedFiles = [...issuesByFile.keys()]

  console.log(`[INFO] W8 cycle ${fixCycle}: ${currentFailed} critical issues across ${affectedFiles.length} file(s)`)

  // Chunk files into parallel batches (cap: REBUILD_W8_MAX_PARALLEL)
  const fileBatches = chunk(affectedFiles, REBUILD_W8_MAX_PARALLEL)
  const allFixTaskIds = []

  for (const [bi, batch] of fileBatches.entries()) {
    for (const filePath of batch) {
      const fileIssues = issuesByFile.get(filePath)
      const fixTaskId = TaskCreate({
        subject: `Wave8.cycle-${fixCycle}.fix-${filePath.split('/').pop().replace('.md', '')}`,
        description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.

Fix cycle ${fixCycle}/${MAX_FIX_CYCLES} — targeted fix for ONE file.

**File to fix:** \`${filePath}\`

**Issues to resolve (fix ALL of these):**
${fileIssues.map((issue, i) => `${i + 1}. ${issue}`).join('\n')}

Rules:
- Fix ONLY the listed issues in ONLY this file
- Do NOT alter other files or passing sections
- Re-read the file before editing to get current state
- After fixing, verify: each listed issue is addressed

Detected stack: ${stackNote}`,
        addBlockedBy: [lastReviewTaskId]
      })
      allFixTaskIds.push(fixTaskId)
    }
  }

  // Single re-reviewer after ALL per-file fixes complete
  const reReviewTaskId = TaskCreate({
    subject: `Wave8.cycle-${fixCycle}: re-reviewer`,
    description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Re-verify all drafts after fix cycle ${fixCycle}. Load only verification-checklist.md sections for artifact types with OPEN critical issues.

Detected stack: ${stackNote}

Overwrite plans/<active-plan>/artifacts/review-report.md with fresh content (frontmatter + markdown body using review-report-template.md).`,
    addBlockedBy: allFixTaskIds
  })

  const freshContent = readFile("plans/<active-plan>/artifacts/review-report.md")
  currentFailed = parseInt(parseFrontmatter(freshContent).failed ?? 0)
  lastReviewTaskId = reReviewTaskId
}

if (currentFailed > 0) {
  // Escalate — do NOT create Wave 9; leave drafts in artifacts/ for manual inspection
  throw new Error(`ESCALATE: ${currentFailed} artifacts still failing after ${MAX_FIX_CYCLES} fix cycles. Manual review required. Drafts preserved in plans/<active-plan>/artifacts/.`)
}

// --- Wave 9 pre-flight gate ---
// Runs UNCONDITIONALLY before W9 dispatch. Reads validation-summary.json (if present)
// AND review-report frontmatter; HALTS pipeline if either signals failure.
//
// Halt conditions (any one of these halts; orchestrator surfaces issues, no docs/ writes):
//   - validation-summary.json overall_status === "FAIL"
//   - review-report.md frontmatter `failed > 0`
//   - review-report.md frontmatter `missing > 0`  (from .pending markers, see verification-checklist § Pending Marker Rule)
//
// Legacy plans without validation-summary.json: gate on review frontmatter alone (`failed`/`missing`); log [INFO] no validator summary.
const summaryPath = `plans/<active-plan>/artifacts/validation/validation-summary.json`
let validatorOverall = "PASS"
if (existsNonEmpty(summaryPath)) {
  const s = JSON.parse(readFile(summaryPath))
  validatorOverall = s.overall_status ?? "PASS"
} else {
  console.log("[INFO] no validation-summary.json — Wave 9 gating on review frontmatter only (legacy plan).")
}
const fm = parseFrontmatter(readFile("plans/<active-plan>/artifacts/review-report.md"))
const reviewFailed = parseInt(fm.failed ?? 0)
const reviewMissing = parseInt(fm.missing ?? 0)
if (validatorOverall === "FAIL" || reviewFailed > 0 || reviewMissing > 0) {
  throw new Error(`Wave 9 gate HALT — validator=${validatorOverall}, review failed=${reviewFailed}, missing=${reviewMissing}. No docs/ writes. Resolve before re-running. Drafts preserved in plans/<active-plan>/artifacts/.`)
}

// Wave 9 — only reached when gate passes (validator !== FAIL AND failed === 0 AND missing === 0)
// Incremental mode: promote ONLY affected artifacts + affected feature specs. Leave others byte-identical.
const affected_artifacts = (mode === "incremental") ? re_generated_artifacts : ["all"]

// Wave 9 pre-promote: pure file-ops via script (cp, stub, archive, GC, sha256 manifest)
bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/promote_drafts.py \
  --plan-dir plans/<active-plan> \
  --mode ${mode} \
  --affected-artifacts ${affected_artifacts.join(',') || 'all'} \
  --affected-fcodes ${(mode === "incremental" ? affected_fcodes : ["all"]).join(',') || 'all'} \
  ${mode === "incremental" && flags.with_screen_specs ? `--affected-screens ${(planJson.affected_screens || []).join(',')}` : ""}

// Wave 9.5 — reverse-index refresh (run here, in orchestrator context, while it is still alive)
// Reads from docs/specs/features/ which promote_drafts.py just populated — no dependency on the flag.
// Running before the flag means flag existence ↔ both W9 + W9.5 completed.
bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/build_source_to_fcode.py \
  --specs-root docs/specs/features \
  --docs-root docs/specs \
  --state-out docs/specs/.rebuild-state.json \
  --index-out docs/specs/_source-to-fcode.json \
  --mode ${mode} \
  --incremental-plan-json plans/<active-plan>/artifacts/.incremental-plan.json

// Doc-writer agent: only writes completion flag + self-closes (all file-ops already done, W9.5 already ran)
TaskCreate({ subject: "Wave9: doc-writer — finalize",
  description: `Session context: read \`plans/<active-plan>/artifacts/_session-context.md\` FIRST.
Read plans/<active-plan>/artifacts/_promoted-sha256.txt.
Write plans/<active-plan>/artifacts/wave9-complete.flag with this body:
  # Wave 9 complete — <ISO-8601 UTC>
  # Wave 9.5 complete (reverse-index refreshed by orchestrator)
  # Mode: ${mode}
  # Affected artifacts: ${affected_artifacts.join(', ') || 'all'}
  # Affected F###: ${(mode === "incremental" ? affected_fcodes : ["all"]).join(', ') || 'all'}
  <contents of _promoted-sha256.txt, one line per file>
Call TaskUpdate(status=completed) on this task id BEFORE returning.`,
  addBlockedBy: [lastReviewTaskId] })
```

## Reconcile pattern (run on every invocation)

Before dispatching any new wave, sync TaskList with disk. Closes tasks whose previous session died after the subagent wrote its output but before `TaskUpdate` fired. On resume, also re-read `plans/<active-plan>/artifacts/validation/validation-summary.json` if present — it is the source of truth for W5.5 / W6.5 state and feeds the Wave 9 pre-flight gate.

Note: `.incremental-plan.json` (if present) lists intentionally-skipped waves — reconcile naturally ignores them because no TaskCreate was issued for skipped waves.

```
// Mapping: task subject pattern → expected output path (relative to plans/<active>/artifacts/)
// null = not reconcilable (no single output file); skip in reconcile loop
const expectedOutput = {
  "Scout: discovery scan":            "scout-report.md",
  "Wave1: system-overview":           "system-overview.md",
  "Wave1: route-list":                "route-list.md",
  "Wave1: data-model":                "data-model.md",
  "Wave1.5: data-model-review":       "data-model-review.md",
  "Wave2: screen-list + screen-flow": ["screen-list.md", "screen-flow.md"],
  "Wave2: behavior-logic":          "behavior-logic.md",
  "Wave2: combined-screen-bg":        ["screen-list.md", "screen-flow.md", "behavior-logic.md"],
  "Wave3: permissions":               "permissions.md",
  "Wave4: user-stories":              "user-stories.md",
  "Wave4.5: user-stories-review":     "user-stories-review.md",
  "Wave5: feature-list":              "feature-list.md",
  "Wave5.6: feature-list-review":     "feature-list-review.md",
  // Wave6 per-feature: "Wave6: feature-spec F###_Name" → features/F###_Name/spec.md
  // Wave6 batch:       "Wave6.batch-NN: ..." → wave6-batch-NN.flag
  "Wave7a: core-artifact-review":     "core-review-report.md",
  // Wave7b batch: "Wave7b.batch-NN: ..." → feature-review-batch-NN.md
  "Wave7-merge: combine review reports": "review-report.md",
  "Wave8.*":                          null,  // fix cycles: no single output file — skip auto-close
  "Wave8.review-*":                   null,  // re-reviewer tasks: same — skip auto-close
  "Wave9: doc-writer — persist":      "wave9-complete.flag",
  "Wave9: doc-writer — finalize":     "wave9-complete.flag"
}

// Explicit list of core doc artifacts for Wave 9 fallback check
// Update this list if new doc artifacts are added to the pipeline
const coreDocArtifacts = [
  "system-overview.md", "route-list.md", "data-model.md",
  "screen-list.md", "screen-flow.md", "behavior-logic.md",
  "permissions.md", "user-stories.md", "feature-list.md"
]

const rebuildTasks = TaskList({ filter: /^(Scout:|Wave\d|Wave6\.batch|Wave7[ab\-])/ })
for (const task of rebuildTasks.filter(t => t.status === "in_progress")) {
  const paths = resolveExpected(task.subject, expectedOutput)
  if (paths === null) continue  // Wave 8 tasks: not auto-closeable — require manual resolution

  const allExist = paths.every(p => existsNonEmpty(`plans/<active>/artifacts/${p}`)
                               && !containsPlaceholder(`plans/<active>/artifacts/${p}`))

  // Wave 9 stricter check: flag present OR all core docs promoted to docs/specs/
  const allCoreDocsPromoted = coreDocArtifacts.every(f => existsNonEmpty(`docs/specs/${f}`))
  const anyFeatureSpecPromoted = glob("docs/specs/features/*/spec.md").length > 0
  const wave9Ok = task.subject.startsWith("Wave9") &&
                  (existsNonEmpty("plans/<active>/artifacts/wave9-complete.flag")
                   || (allCoreDocsPromoted && anyFeatureSpecPromoted))

  if (allExist || wave9Ok) {
    TaskUpdate({ id: task.id, status: "completed",
                 note: "auto-reconciled from disk — output present, session likely died before TaskUpdate" })
  }
}
```

**With `--resume`:** run the block above, then STOP.
- Tasks closed: report subject + output path
- Tasks still `in_progress` AND output not yet written (STUCK): report as STUCK — user must manually reset to `pending` or `cancelled` before re-running without `--resume`
- Wave 8 tasks (`null` in map): always reported as STUCK if `in_progress` — cannot auto-close
- No new `TaskCreate` calls in resume mode

**Without `--resume` (default no-args):** run reconcile, then dispatch next pending wave.
- STUCK tasks (`in_progress`, no output) are left as-is — orchestrator does NOT re-create them to avoid duplicates
- If a task is STUCK, use `--resume` first to diagnose, then manually reset before re-running

## Artifact paths

| Stage | Draft path | Persistent path |
|-------|-----------|-----------------|
| Document artifacts | `plans/<active-plan>/artifacts/<name>.md` | `docs/specs/<name>.md` |
| Feature specs | `plans/<active-plan>/artifacts/features/<F###>/spec.md` | `docs/specs/features/<F###>/spec.md` |
| Core review report | `plans/<active-plan>/artifacts/core-review-report.md` | ephemeral (W7a output) |
| Feature review batches | `plans/<active-plan>/artifacts/feature-review-batch-NN.md` | ephemeral (W7b output) |
| Merged review report | `plans/<active-plan>/artifacts/review-report.md` | ephemeral (W7-merge output) |
| Wave 6 batch flags | `plans/<active-plan>/artifacts/wave6-batch-NN.flag` | ephemeral (reconcile signal) |
| Wave 9 completion flag | `plans/<active-plan>/artifacts/wave9-complete.flag` | ephemeral (reconcile signal + audit) |

Doc-writer (Wave 9) promotes drafts only after reviewer passes (`failed === 0`).

## Wave 9 completion flag format

`plans/<active>/artifacts/wave9-complete.flag` is the single source of truth that the pipeline finished. Plain text, one line per promoted file. Header lines start with `#` (backward-compatible — existing parsers ignore them).

**Full mode flag:**

```
# Wave 9 complete — 2026-05-20T08:34:00Z
# Mode: full
# Affected artifacts: all
# Affected F###: all
# OOB warnings: 0
docs/specs/system-overview.md  <sha256>
docs/specs/route-list.md       <sha256>
docs/specs/data-model.md       <sha256>
docs/specs/screen-list.md      <sha256>
docs/specs/screen-flow.md      <sha256>
docs/specs/behavior-logic.md <sha256>
docs/specs/permissions.md      <sha256>
docs/specs/user-stories.md     <sha256>
docs/specs/feature-list.md     <sha256>
docs/specs/features/F001_Auth/spec.md <sha256>
...
```

**Incremental mode flag (lists ONLY promoted files — untouched files omitted):**

```
# Wave 9 complete — 2026-05-20T08:34:00Z
# Mode: incremental
# Affected artifacts: route-list.md, screen-list.md, screen-flow.md, behavior-logic.md, permissions.md, user-stories.md, feature-list.md
# Affected F###: F001_Auth, F005_Payments
# OOB warnings: 0
# Archive GC: kept latest 5, removed 0 older
docs/specs/route-list.md       <sha256>
docs/specs/screen-list.md      <sha256>
docs/specs/screen-flow.md      <sha256>
docs/specs/behavior-logic.md <sha256>
docs/specs/permissions.md      <sha256>
docs/specs/user-stories.md     <sha256>
docs/specs/feature-list.md     <sha256>
docs/specs/features/F001_Auth/spec.md     <sha256>
docs/specs/features/F005_Payments/spec.md <sha256>
```

SHA is recorded for audit trail only — reconcile does NOT verify SHA programmatically.
To manually verify integrity: `sha256sum -c wave9-complete.flag` (strip comment lines first).

### Review archive (incremental carry-forward)

W9 doc-writer also archives review reports to `docs/specs/.review-archive/<ISO-timestamp>/` containing `core-review-report.md`, `feature-review-batch-*.md`, `review-report.md`. Next incremental run's W7-merge reads the latest archive to carry forward review sections for untouched artifacts. Archive directory is committed (lightweight markdown).

Archive GC: after writing the new archive subdir, W9 enumerates `docs/specs/.review-archive/*` sorted by name (ISO-8601, lexicographically sortable). If count > 5, delete the oldest (`count - 5`) subdirs. Policy noted in flag header `# Archive GC: kept latest 5, removed N older`.

## Wave 9.5 — reverse-index refresh

Embedded in the Wave 9 orchestrator dispatch block, **between `promote_drafts.py` and the doc-writer `TaskCreate`** (see Wave 9 block above). This placement guarantees it runs while the orchestrator context is still alive — after the long pipeline, the orchestrator's context window may be exhausted by the time the doc-writer task self-closes, so a post-task bash step would never execute.

Real dependency: `docs/specs/features/` populated by `promote_drafts.py`. Does not read `wave9-complete.flag`. Running before the flag is written means flag existence ↔ both W9 + W9.5 completed.

Stdlib-only; no LLM call. Updates `last_rebuild_sha`, `fcode_index_sha`, `doc_shas`, `screen_spec_shas` (when `--screen-specs` standalone pass was run).

## Screen-Specs Pass (`--screen-specs`)

Standalone pass — runs independently of the main pipeline.
**Requires:** `docs/specs/screen-list.md` present (main rebuild must complete first).

Invocation: `/rebuild-spec --screen-specs`

### Preflight

1. Verify `docs/specs/screen-list.md` exists and is non-empty. ABORT if missing with: "Run `/rebuild-spec` first to generate screen-list.md"
2. Read SCR### codes: `grep -oP 'SCR\d{3,4}' docs/specs/screen-list.md | sort -u`
3. Incremental check: if `docs/specs/.rebuild-state.json` exists and `screen_spec_shas` present, emit `affected_screens` list (screens whose SCR### section hash changed since last pass). If state absent → treat all screens as affected.

### Wave SS.1 — ScreenSpec fan-out (same logic as former W2.5)

```js
const scrCodes = bash(`grep -oP 'SCR\\d{3,4}' docs/specs/screen-list.md | sort -u`).split('\n').filter(Boolean)
// Incremental filter: use affected_screens from state if available
const scrCodesToProcess = /* incremental filter applied here, same logic as former W2.5 incremental block */

const SCREEN_SPEC_BATCH_SIZE = parseInt(process.env.REBUILD_SCREEN_SPEC_BATCH_SIZE ?? '5')
const scrBatches = chunk(scrCodesToProcess, SCREEN_SPEC_BATCH_SIZE)
const screenSpecTaskIds = []

for (const [i, batch] of scrBatches.entries()) {
  const batchId = TaskCreate({
    subject: `WaveSS1.batch-${pad2(i+1)}: screen-specs (${batch[0]}..${batch.at(-1)})`,
    description: `Session context: read docs/specs/_session-context.md if present; otherwise load docs/specs/screen-list.md + docs/specs/data-model.md directly.
Generate ScreenSpec for ${batch.length} screens: ${batch.join(', ')}.
Template: templates/screen-spec-template.md. Contract: references/screen-spec-researcher-contract.md.
Write drafts to: plans/<active-plan>/artifacts/screens/{SCR###_Name}/spec.md`,
  })
  screenSpecTaskIds.push(batchId)
}
```

### Wave SS.2 — ScreenSpec reviewer (same logic as former W7c)

```js
const SCREEN_SPEC_REVIEW_BATCH_SIZE = parseInt(process.env.REBUILD_SCREEN_SPEC_REVIEW_BATCH_SIZE || "5")
const reviewBatches = chunk(scrCodesToProcess, SCREEN_SPEC_REVIEW_BATCH_SIZE)
for (const [i, batch] of reviewBatches.entries()) {
  TaskCreate({
    subject: `WaveSS2.batch-${pad2(i+1)}: screen-spec-review (${batch[0]}..${batch.at(-1)})`,
    description: `Review ScreenSpec files for [${batch.join(', ')}] using verification-checklist.md § ScreenSpec.
Input: plans/<active-plan>/artifacts/screens/{SCR###}/spec.md. Output: plans/<active-plan>/artifacts/screen-review-batch-${pad2(i+1)}.md`,
    addBlockedBy: screenSpecTaskIds
  })
}
```

### Wave SS.3 — Promote

```js
// After all SS.2 reviewers pass:
bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/promote_drafts.py \
  --plan-dir plans/<active-plan> \
  --mode ${mode} \
  --affected-screens ${scrCodesToProcess.join(',')}
// Then update screen_spec_shas in .rebuild-state.json
bash: .claude/skills/.venv/bin/python3 \
  claude/skills/rebuild-spec/scripts/build_source_to_fcode.py \
  --specs-root docs/specs/features --docs-root docs/specs \
  --state-out docs/specs/.rebuild-state.json \
  --index-out docs/specs/_source-to-fcode.json \
  --mode ${mode}
```
