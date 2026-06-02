# Validation Verdict — Per-Item Output Template

<!-- One file per item under `plans/upsale/validation/item-<NN>-<slug>.md`.
     Written by the per-item validator subagent (Phase D, Step 6 — one subagent per item, batched ≤10) atomically via
     Bash + tempfile + rename. Consumed by the Step 7 apply subagent
     (`references/apply-validations.md`) which parses frontmatter `decision:` to
     apply KEEP/REVISE/DROP. -->

## File naming

`plans/upsale/validation/item-{NN}-{kebab-slug}.md`

- `{NN}` — zero-padded 1-based item index in document order (Technical first, then Business).
- `{kebab-slug}` — lower-case kebab slug derived from the item title.

## File body (REQUIRED structure)

```markdown
---
item_index: {N}
item_slug: {kebab-slug}
track: {technical | business}
decision: {KEEP | REVISE | DROP}
---

# Audits

- **Clause:** (need) {atomic claim verbatim or tightly paraphrased from the item's `**Need:**` bullet — one bullet per claim, covering every distinct assertion in Need}
  - **Evidence:** {what was checked to verify this specific claim; cite a **repo** `path:line` (source code, lockfile, manifest, config, in-repo spec, test, doc) — NEVER a `plans/upsale/**` path (those are this skill's own generated artifacts; see `references/validation.md` → "Evidence source rule"). When the claim is unsupportable, name the repo grep performed and its zero-hit result, e.g. `ripgrep on "rate.?limit" across src/api/ returned 0 hits`.}
  - **Verdict:** {correct | wrong}
- **Clause:** (need) {next Need claim}
  - **Evidence:** …
  - **Verdict:** {correct | wrong}
- **Clause:** (solution) {atomic claim from `**Proposed solution:**` — library/dep, file path, API symbol, version pin, pattern/refactor, or config/env key; one bullet per claim. If the solution is purely high-level, emit exactly ONE `(solution)` bullet stating the high-level pattern.}
  - **Evidence:** {forward-looking stack-fit check — lockfile / manifest / repo tree / existing-impl grep / env schema. Same evidence-source rule: **repo** `path:line` only, NEVER `plans/upsale/**`. For library/dep: `package.json:1-60` lists no `<name>` dep. For file path: `git ls-files <path>` empty + folder convention check. For API symbol: cited library version's docs or a repo grep showing usage. For pattern: repo grep showing absence (greenfield) or presence (duplicate).}
  - **Verdict:** {correct | wrong}
- **Clause:** (solution) {next Proposed solution claim}
  - **Evidence:** …
  - **Verdict:** {correct | wrong}

# Reason

{1-3 sentences citing check number(s) (1–6 from validation.md) and the specific claim that failed or was corrected.}

# Revised item

{Omit this entire `# Revised item` block when decision is KEEP or DROP.
 When decision is REVISE, emit the FULL revised item starting with `## <title>`,
 followed by the five bullets in order: Value, Need, Benefits, Proposed solution, Effort hint.
 `**Category:**` is NOT part of the schema — apply rejects bodies that include it (KEEP fallback).
 Schema MUST be preserved exactly — no extra headings, no extra bullets.}
```

## Frontmatter rules

- `item_index` — integer, matches the 1-based position assigned by the orchestrator.
- `item_slug` — kebab-case, must match the `{slug}` in the file name.
- `track` — `technical` or `business` (lower-case).
- `decision` — `KEEP` | `REVISE` | `DROP` (case-insensitive — the apply subagent accepts both `keep` and `KEEP`).

## Audits block rules

**Scope:** Audits cover the item's `**Need:**` AND `**Proposed solution:**` bullets. Each `**Clause:**` MUST start with `(need)` or `(solution)` — the tag identifies which bullet the claim came from. Other bullets (Value, Benefits, Effort hint) are NOT audited here — their verdicts are summarised in `# Reason` by citing the relevant check number from `references/validation.md`.

- Decompose BOTH the `**Need:**` and `**Proposed solution:**` bullets into atomic claims and emit ONE audit bullet per claim. Emit all `(need)` bullets first (in Need's document order), then all `(solution)` bullets (in Proposed solution's document order).
- A `(need)` claim is an independently verifiable backward-looking assertion: a `path:line` citation, a file path, a dependency `package@version`, a CVE/GHSA/RUSTSEC advisory ID, a route, a spec ID, a named metric/threshold ("p95 850 ms"), or a behavior assertion ("no rate limiter detected in middleware chain"). Split on `;`, `.`, and conjunctions when each fragment carries an independently checkable assertion.
- A `(solution)` claim is an independently verifiable forward-looking assertion: a named library/dep, a proposed file path, an API symbol the solution will call, a version pin, a pattern/refactor, or a config/env key. Verify against lockfile / manifest / repo tree / existing-impl grep / env schema. A purely high-level solution (no library / path / API named) emits exactly ONE `(solution)` claim stating the high-level pattern + stack-fit evidence — don't manufacture sub-claims.
- Each audit bullet has exactly three nested sub-bullets in this order: `**Clause:**`, `**Evidence:**`, `**Verdict:**`.
- `**Clause:**` — the atomic claim, verbatim from the source bullet where possible (prefixed with `(need)` or `(solution)`), lightly trimmed for readability (preserve every `path:line` / version / ID / metric exactly as written so the verdict's audit trail stays grep-able).
- `**Evidence:**` — what was inspected to verify the claim, with the supporting `path:line` citation from a **real repo file** (source, lockfile, manifest, config, in-repo spec, test, doc). **NEVER cite a `plans/upsale/**` path as evidence** — those are this skill's own generated artifacts and cannot serve as ground truth (see `references/validation.md` → "Evidence source rule"). If `item_evidence` references a `plans/upsale/**` discovery file, follow it to the underlying repo `path:line` and cite that. When the claim cannot be verified, name the repo grep / file lookup performed and its negative result (e.g., `ripgrep on "rate.?limit" across src/api/ returned 0 hits`). No prose-only justifications.
- `**Verdict:**` — exactly `correct` (claim verified against the cited evidence) or `wrong` (claim is unsupportable, the citation is stale/fabricated, the version/ID does not match the lockfile, the metric is not present in the discovery snapshot, etc.).
- If `**Need:**` is missing or empty: emit a single audit bullet with `**Clause:** (need) Need bullet absent or empty`, `**Evidence:** schema requires Need bullet; not found in `item_markdown``, `**Verdict:** wrong`.
- If `**Proposed solution:**` is missing or empty: emit a single audit bullet with `**Clause:** (solution) Proposed solution bullet absent or empty`, `**Evidence:** schema requires Proposed solution bullet; not found in `item_markdown``, `**Verdict:** wrong`.
- Always emit `# Audits` — even on `DROP`. Even when every claim is `correct`, emit the audits to document what was checked.
- `# Audits` is informational — the Step 7 apply subagent does not parse it; the `decision:` frontmatter remains the single source of truth for KEEP/REVISE/DROP. The block exists for human review of why each `(need)` / `(solution)` claim held up or failed.

## Decision semantics

The `decision:` frontmatter is determined by the full check 1–6 procedure in `references/validation.md` §Decision rules, NOT by the audits block alone. Audits reflect `(need)` + `(solution)` claim verification only; Value/Use-context/Benefits/Security/Formatting verdicts live in `# Reason`.

| decision | When to emit | `# Revised item` required? | apply-subagent effect |
|----------|--------------|----------------------------|------------------------------|
| `KEEP`   | Every check 1–6 passes (all `(need)` and `(solution)` audits `correct`, or wrong ones fixed inline at proposal-write time — rare) | NO | Item kept as-is in final proposal |
| `REVISE` | At least one check fired with a recoverable fix (typical: one or more `(need)` claims wrong but a closest supportable citation exists, OR a `(solution)` claim off-stack but a stack-native equivalent exists, OR Value/Benefits/Use-context issue) | YES — full revised item | Item replaced with revised body |
| `DROP`   | At least one check fired with no recoverable fix (typical: every `(need)` claim wrong with no supportable replacement, OR `(solution)` fundamentally off-stack with no stack-native equivalent, OR holistic-gate incoherence, OR use-context filter fired) | NO | Item removed; `drop:` log line emitted |

## Validation checks (1–6 from `references/validation.md`)

1. **Holistic proposal evaluation — GATE** (evaluate the whole item end-to-end against the prompt "Here is a product improvement proposal. Evaluate it: \<item\>"; decomposes BOTH `**Need:**` and `**Proposed solution:**` into atomic `(need)` / `(solution)` audit claims with repo-grounded evidence; DROP short-circuits checks 2–6)
2. Value rating defensibility (`high` / `medium` justified)
3. Use-context consistency (gating rules per `internal | hybrid | customer-facing`)
4. Benefits concreteness (real signal, not generic marketing copy)
5. Security & hallucination guard (no secrets, no invented citations)
6. Formatting preservation (schema intact)

## Revise body example

```markdown
---
item_index: 3
item_slug: introduce-edge-rate-limiter
track: technical
decision: REVISE
---

# Audits

- **Clause:** (need) p95 latency 850ms at the `/api/search` route under burst load
  - **Evidence:** `tests/perf/k6-search-2026-04.json:23` records `"p95_ms": 850` for the `/api/search` scenario; CI run reference in `.github/workflows/perf.yml:18`.
  - **Verdict:** correct
- **Clause:** (need) no rate limiter detected in middleware chain
  - **Evidence:** ripgrep on `rate.?limit|throttl|bucket` across `src/api/middleware/` returned 0 hits; `src/api/middleware/index.ts:1-44` registers only auth + logging.
  - **Verdict:** correct
- **Clause:** (need) stale path `src/api/old-middleware.ts:42`
  - **Evidence:** `src/api/old-middleware.ts` does not exist in the current tree (`git ls-files` returns nothing matching); closest live file is `src/api/middleware/index.ts`.
  - **Verdict:** wrong
- **Clause:** (solution) Redis-backed token-bucket rate limiter (introduces `ioredis` or `redis` dep)
  - **Evidence:** `package.json:1-60` declares no redis client dependency; `docker-compose.yml:1-30` declares no redis service; existing cache layer is in-memory `node-cache` (`package.json:42`).
  - **Verdict:** wrong
- **Clause:** (solution) introduce `src/api/middleware/rate-limit.ts`
  - **Evidence:** `src/api/middleware/` exists and accepts new modules per `src/api/middleware/index.ts:1-44` convention (each middleware exports a single Express handler); no name collision (`git ls-files src/api/middleware/rate-limit.ts` empty).
  - **Verdict:** correct

# Reason

Check 1 (holistic) — item is coherent; the `(need)` latency claim is evidenced from
`tests/perf/k6-search-2026-04.json:23`, but the `(need)` citation to
`src/api/old-middleware.ts:42` is stale (closest supportable file is
`src/api/middleware/index.ts`). The `(solution)` Redis claim fails its lockfile / infra
check — no redis client or service in the repo — so the proposed solution is rewritten
to use the existing in-memory `node-cache` token-bucket approach instead. The `(solution)`
file-path claim holds. Check 2 (Value rating defensibility) downgraded `high` → `medium`
per the repo's reliability-only signal (no revenue/incident anchor in the perf fixture or
any commit message).

# Revised item

## Introduce edge rate limiter

- **Value:** medium
- **Need:** p95 latency 850ms at the `/api/search` route under burst load (`tests/perf/k6-search-2026-04.json:23`); no rate limiter detected in middleware chain (`src/api/middleware/index.ts:1-44` registers only auth + logging).
- **Benefits:** Reliability — predictable latency under load; cost — fewer wasted compute cycles serving abusive clients.
- **Proposed solution:** Introduce a `node-cache`-backed token-bucket rate limiter in `src/api/middleware/rate-limit.ts` keyed on IP + route prefix; default 60 req/min/IP, configurable via env (extend `src/config/env.ts`).
- **Effort hint:** medium
```

## Atomic write recipe

The validator MUST write each verdict file atomically via Bash + tempfile + rename. The Write
tool is NOT atomic, and a half-written verdict whose frontmatter parses but body is truncated
would silently mis-apply on the next idempotent run. See `references/validation.md` → "Output
format" for the exact `cat > "$TMP" <<'__UPSALE_VERDICT_END__' … mv "$TMP" '<output_path>'`
recipe.
