#!/usr/bin/env python3
"""Wave 6.5 — feature spec structural validator.
Checks `spec.md` files against verification-checklist.md FeatureSpec rules.
Regex + fence-state tracking; stdlib only.
Exit codes: 0 (no critical), 1 (critical), 2 (internal).
"""
from __future__ import annotations
import argparse
import datetime as _dt
import json
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
from _slug_lib import assert_under, iter_spec_files, resolve_project_root  # noqa: E402
from _spec_parse import parse_headings_and_blocks, strip_html_comments  # noqa: E402
from _spec_block_lib import find_blocks_missing_linked_fr  # noqa: E402
from _summary_lib import (  # noqa: E402
    atomic_write, derive_overall_status, load_summary, merge_validator_result, recalculate_totals,
)

VALIDATOR = "feature_spec"

REQUIRED_H2 = ["## Overview", "## Why This Exists", "## Who Uses It", "## Business Workflow", "## Screen Flow", "## Polymorphic Behavior", "## Cross-Cutting Logic", "## User Stories", "## Key Entities",
               "## Artifact References", "## Assumptions", "## Source Code References", "## Unresolved Questions"]
LEGACY_H2 = {"## Related Artifacts", "## Spec Documents"}  # CRITICAL: legacy format no longer accepted
REQUIRED_CCL_H3 = ["### Requirements", "### Business Rules", "### Decision Logic", "### State Machines", "### Algorithms", "### External Integrations", "### Verification"]
DEPRECATED_H2 = {"## Requirements", "## Business Rules", "## State Machines", "## Algorithms", "## External Integrations", "## Success Criteria", "## How It Works"}
PLACEHOLDER_RE = re.compile(r"\{[A-Z][A-Z0-9_/|]*\}")
FCODE_HEADING_RE = re.compile(r"^#\s+F\d{3}_[A-Za-z0-9]+")
SCREEN_FLOW_OK_RE = re.compile(r"^\*\*See:\*\*\s+ScreenFlow\s+§\s+F\d{3}_\w+|^N/A —")
SM_BLOCK_RE = re.compile(r"^###\s+SM-\d{3}_")
NUMBERED_STEP_RE = re.compile(r"^\s*\d+\.\s+\S")
DEC_BLOCK_RE = re.compile(r"^####\s+DEC-\d{3}_")
VALID_SUBTYPES = {"render", "interaction", "flow"}
DISC_SUBSECTION_RE = re.compile(r"^### DISC-\d{3}")
BOOLEAN_VALUE_RE = re.compile(r"^\|\s*(true|false|yes|no|1|0)\s*\|", re.IGNORECASE)
_DISC_TABLE_HEADER_RE = re.compile(r"^\|\s*value\b", re.IGNORECASE)
_DISC_TABLE_SEP_RE = re.compile(r"^\|\s*[-:]+\s*\|")

# Lazy-N/A grep patterns — JSX-ternary and framework conditionals
LAZY_NA_PATTERNS = [
    re.compile(r"\{[^}]*\?[^:]*:[^}]*\}"),   # JSX ternary
    re.compile(r"v-if=|v-else=|v-show="),      # Vue conditionals
    re.compile(r"@if\s*\(|@else"),             # Blade
    re.compile(r"<%\s*if\b|<%\s*else\b"),      # ERB
]


def _bounds(h2, name, total):
    for i, (idx, h) in enumerate(h2):
        if h == name:
            return idx + 1, (h2[i + 1][0] if i + 1 < len(h2) else total)
    return None


def _check_dec_blocks(lines: list[str], headings: list, blocks: list, b_ccl: tuple | None) -> list[dict]:
    """Check DEC-### block structural validity within ## Cross-Cutting Logic."""
    issues: list[dict] = []
    if not b_ccl:
        return issues
    ccl_lines = lines[b_ccl[0]:b_ccl[1]]
    ccl_offset = b_ccl[0]

    # Find Decision Logic H3 bounds — use next H3 sibling (not H4 children) as dl_end
    ccl_headings = [(idx, h) for idx, h in headings if b_ccl[0] <= idx < b_ccl[1]]
    ccl_h3 = [(idx, h) for idx, h in ccl_headings if h.startswith("### ")]
    dl_start = dl_end = None
    for i, (idx, h) in enumerate(ccl_h3):
        if h == "### Decision Logic":
            dl_start = idx + 1
            dl_end = ccl_h3[i + 1][0] if i + 1 < len(ccl_h3) else b_ccl[1]
            break

    if dl_start is None:
        return issues  # CCL section check handles missing Decision Logic H3

    dl_content = "\n".join(lines[dl_start:dl_end]).strip()
    if dl_content.startswith("N/A"):
        # Warn: lazy N/A check — look for JSX ternary patterns in spec file (conservative)
        full_text = "\n".join(lines)
        hits = []
        for pat in LAZY_NA_PATTERNS:
            if pat.search(full_text):
                hits.append(pat.pattern)
        if hits:
            issues.append({
                "severity": "warning", "rule_id": "FeatureSpec.dec_lazy_na",
                "location": {"file": None, "line": dl_start + 1},
                "message": f"Decision Logic is N/A but spec contains conditional patterns matching DEC signatures ({hits[:2]}); reviewer should verify"
            })
        return issues

    # Check each DEC-### block
    dec_heads = [(idx, h) for idx, h in headings if DEC_BLOCK_RE.match(h) and dl_start <= idx < dl_end]
    for k, (idx, raw) in enumerate(dec_heads):
        nxt = dec_heads[k + 1][0] if k + 1 < len(dec_heads) else dl_end
        block_lines = lines[idx:nxt]
        block_text = "\n".join(block_lines)

        required_fields = ["**subtype:**", "**Triggers in:**", "**Involved entities:**",
                           "**user_visible_outcome:**", "**Source:**"]
        for field in required_fields:
            if field not in block_text:
                issues.append({
                    "severity": "critical", "rule_id": "FeatureSpec.dec_blocks_well_formed",
                    "location": {"file": None, "line": idx + 1},
                    "message": f"{raw} missing required field {field!r}"
                })

        # Check subtype values
        subtype_match = re.search(r"\*\*subtype:\*\*\s*(.+)", block_text)
        if subtype_match:
            declared = {s.strip() for s in subtype_match.group(1).split(",")}
            invalid = declared - VALID_SUBTYPES
            if invalid:
                issues.append({
                    "severity": "critical", "rule_id": "FeatureSpec.dec_blocks_well_formed",
                    "location": {"file": None, "line": idx + 1},
                    "message": f"{raw} invalid subtype(s): {invalid}; valid: render, interaction, flow"
                })

        # Check pseudocode ≤8 lines
        dec_blocks = [(s, e, l) for s, e, l in blocks if idx <= s < nxt]
        for start, end, lang in dec_blocks:
            if lang != "mermaid" and (end - start - 1) > 8:
                issues.append({
                    "severity": "warning", "rule_id": "FeatureSpec.dec_blocks_well_formed",
                    "location": {"file": None, "line": start + 1},
                    "message": f"{raw} pseudocode block {end - start - 1} lines > 8"
                })

    return issues


def _check_linked_fr(spec: Path, root: Path, lines: list[str]) -> list[dict]:
    """Check every BR/SM/ALG/INT block has **Linked FR:** line (rule: FeatureSpec.linked_fr_missing)."""
    text = "\n".join(lines)
    missing = find_blocks_missing_linked_fr(text)
    issues = []
    for blk in missing:
        try:
            loc = str(spec.relative_to(root))
        except ValueError:
            loc = str(spec)
        issues.append({
            "validator": VALIDATOR,
            "severity": "critical",
            "rule_id": "FeatureSpec.linked_fr_missing",
            "location": {"file": loc, "line": blk["heading_line"] + 1},
            "message": f"{blk['code']} block missing required '**Linked FR:**' line"
        })
    return issues


def _issue(sev, rid, spec, root, line, msg):
    try:
        loc = str(spec.relative_to(root))
    except ValueError:
        loc = str(spec)
    return {"validator": VALIDATOR, "severity": sev, "rule_id": rid,
            "location": {"file": loc, "line": line}, "message": msg}


def _check_spec(spec: Path, root: Path) -> list[dict]:
    lines = spec.read_text(encoding="utf-8", errors="replace").splitlines()
    headings, blocks = parse_headings_and_blocks(lines)
    scrubbed = strip_html_comments(lines)
    h2 = [(i, h) for i, h in headings if h.startswith("## ") and not h.startswith("### ")]
    out: list[dict] = []
    add = lambda s, r, ln, m: out.append(_issue(s, r, spec, root, ln, m))  # noqa: E731

    if not any(FCODE_HEADING_RE.match(lines[i]) for i in range(min(5, len(lines)))):
        add("critical", "FeatureSpec.f_code_format", 1, "missing/invalid F### heading in preamble")

    for idx, raw in headings:
        if raw in DEPRECATED_H2:
            add("critical", "FeatureSpec.deprecated_headings", idx + 1, f"deprecated H2 {raw!r}")
        if raw in LEGACY_H2:
            add("critical", "FeatureSpec.legacy_artifact_sections", idx + 1,
                f"legacy section {raw!r} — replace both '## Related Artifacts' and '## Spec Documents' with single '## Artifact References' table (no transition window)")
        if raw == "## Appendix":
            add("critical", "FeatureSpec.no_appendix", idx + 1, "## Appendix must be removed")

    h2_names = [h for _, h in h2]
    present = [h for h in h2_names if h in REQUIRED_H2]
    expected = [h for h in REQUIRED_H2 if h in present]
    if present != expected or set(present) != set(REQUIRED_H2):
        missing = [h for h in REQUIRED_H2 if h not in present]
        add("critical", "FeatureSpec.required_sections", None,
            f"required H2 missing/out-of-order; missing: {missing}" if missing else "required H2 out of order")

    b_ccl = _bounds(h2, "## Cross-Cutting Logic", len(lines))
    if b_ccl:
        h3 = [(idx, h) for idx, h in headings if b_ccl[0] <= idx < b_ccl[1] and h.startswith("### ")]
        names = [h for _, h in h3]
        present_ccl = [h for h in names if h in REQUIRED_CCL_H3]
        if present_ccl != [h for h in REQUIRED_CCL_H3 if h in present_ccl] or set(present_ccl) != set(REQUIRED_CCL_H3):
            add("critical", "FeatureSpec.ccl_subsections", None,
                f"required CCL H3 missing/out-of-order; have {names}")
        for i, (idx, h) in enumerate(h3):
            end = h3[i + 1][0] if i + 1 < len(h3) else b_ccl[1]
            if not "\n".join(lines[idx + 1:end]).strip():
                add("critical", "FeatureSpec.ccl_blank", idx + 1, f"{h} is blank; write `None.` if empty")

    # Client Behavior Anchor — mandatory in ## Cross-Cutting Logic regardless of N/A content
    if b_ccl:
        ccl_text = "\n".join(lines[b_ccl[0]:b_ccl[1]])
        if "**Client behavior:** see" not in ccl_text:
            add("critical", "FeatureSpec.missing_client_behavior_anchor", None,
                "## Cross-Cutting Logic missing required '**Client behavior:** see' anchor block "
                "(links behavior-logic.md, permissions.md, screen-flow.md — mandatory even when all linked sections are N/A)")

    b_sf = _bounds(h2, "## Screen Flow", len(lines))
    if b_sf:
        for i in range(*b_sf):
            s = lines[i].strip()
            if s and not s.startswith("#") and not s.startswith("<!--"):
                if not SCREEN_FLOW_OK_RE.match(s):
                    add("critical", "FeatureSpec.screen_flow_crossref", i + 1,
                        "Screen Flow first content line must start with '**See:** ScreenFlow § F###_…' or 'N/A —'")
                break

    b_bw = _bounds(h2, "## Business Workflow", len(lines))
    if b_bw:
        steps = sum(1 for i in range(*b_bw) if NUMBERED_STEP_RE.match(lines[i]))
        if steps < 3:
            add("critical", "FeatureSpec.bw_steps", None,
                f"## Business Workflow needs ≥3 numbered steps; found {steps}")

    b_us = _bounds(h2, "## User Stories", len(lines))
    if b_us and not any(h == "### Edge Cases"
                        for idx, h in headings if b_us[0] <= idx < b_us[1] and h.startswith("### ")):
        add("critical", "FeatureSpec.edge_cases", None, "### Edge Cases required under ## User Stories")

    fenced = {i for s, e, _ in blocks for i in range(s, e + 1)}
    for i, raw in enumerate(lines):
        if i in fenced or not PLACEHOLDER_RE.search(scrubbed[i]):
            continue
        add("critical", "Universal.no_placeholder", i + 1, f"placeholder literal in line: {raw.strip()[:80]!r}")
        break

    sm_heads = [(idx, raw) for idx, raw in headings if SM_BLOCK_RE.match(raw)]
    for k, (idx, raw) in enumerate(sm_heads):
        nxt = sm_heads[k + 1][0] if k + 1 < len(sm_heads) else len(lines)
        if not any(idx < start < nxt and lang.startswith("mermaid") for start, _e, lang in blocks):
            add("critical", "FeatureSpec.sm_mermaid", idx + 1, f"{raw} block missing stateDiagram-v2 fence")

    # DISC boolean heuristic — warn if DISC subsection only lists true/false values
    b_poly = _bounds(h2, "## Polymorphic Behavior", len(lines))
    if b_poly:
        poly_headings = [(idx, h) for idx, h in headings
                         if b_poly[0] <= idx < b_poly[1] and DISC_SUBSECTION_RE.match(h)]
        for k, (idx, disc_h) in enumerate(poly_headings):
            end = poly_headings[k + 1][0] if k + 1 < len(poly_headings) else b_poly[1]
            disc_lines = lines[idx:end]
            bool_hits = [ln for ln in disc_lines if BOOLEAN_VALUE_RE.match(ln)]
            non_bool_table = [ln for ln in disc_lines if ln.startswith("|") and not BOOLEAN_VALUE_RE.match(ln)
                              and not _DISC_TABLE_HEADER_RE.match(ln) and not _DISC_TABLE_SEP_RE.match(ln)]
            if bool_hits and not non_bool_table:
                add("warning", "FeatureSpec.disc_boolean",
                    idx + 1,
                    f"{disc_h}: DISC subsection appears to document a boolean field (true/false values only); "
                    f"boolean flags belong in Business Rules, not DISC-### — consider removing this DISC entry")

    for start, end, lang in blocks:
        if lines[start].startswith("```{lang}"):
            add("warning", "FeatureSpec.pseudocode_fence", start + 1, "pseudocode fence uses literal {lang}")
        if end - start - 1 > 20 and lang and lang != "mermaid":
            add("warning", "FeatureSpec.pseudocode_length", start + 1,
                f"pseudocode block {end - start - 1} lines > 20")

    # DEC-### structural checks
    for dec_issue in _check_dec_blocks(lines, headings, blocks, b_ccl):
        dec_issue["validator"] = VALIDATOR
        try:
            dec_issue["location"]["file"] = str(spec.relative_to(root))
        except ValueError:
            dec_issue["location"]["file"] = str(spec)
        out.append(dec_issue)

    # Linked FR presence check — every BR/SM/ALG/INT block must declare Linked FR
    out.extend(_check_linked_fr(spec, root, lines))

    return out


def validate(plan_dir, root, single):
    paths = [single] if single else list(iter_spec_files(plan_dir))
    per_spec = {}
    for sp in paths:
        try:
            rel = str(sp.relative_to(root))
        except ValueError:
            rel = str(sp)
        per_spec[sp.parent.name] = {"spec_path": rel, "issues": _check_spec(sp, root)}
    return {"validator": VALIDATOR,
            "timestamp": _dt.datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%SZ"),
            "plan_dir": str(plan_dir), "specs": per_spec}


def main(argv: list[str]) -> int:
    p = argparse.ArgumentParser(description="rebuild-spec Wave 6.5 feature-spec validator")
    g = p.add_mutually_exclusive_group(required=True)
    g.add_argument("--plan-dir"); g.add_argument("--spec")
    p.add_argument("--project-root", default=None); p.add_argument("--summary-out", default=None)
    args = p.parse_args(argv)
    root = resolve_project_root(args.project_root)
    if args.plan_dir:
        plan_dir = Path(args.plan_dir).resolve(); single = None
        if not plan_dir.is_dir():
            print(f"[ERROR] --plan-dir is not a directory: {plan_dir}", file=sys.stderr); return 2
    else:
        single = Path(args.spec).resolve(); plan_dir = single.parent.parent.parent
        if not single.is_file():
            print(f"[ERROR] --spec is not a file: {single}", file=sys.stderr); return 2
    try:
        assert_under(plan_dir, root)
    except ValueError as exc:
        print(f"[ERROR] {exc}", file=sys.stderr); return 2
    try:
        result = validate(plan_dir, root, single)
    except Exception as exc:  # noqa: BLE001
        print(f"[ERROR] validator crashed: {exc}", file=sys.stderr); return 2
    print(json.dumps(result, indent=2, sort_keys=True))
    crit = sum(1 for s in result["specs"].values() for i in s["issues"] if i["severity"] == "critical")
    if args.summary_out:
        sp = Path(args.summary_out).resolve()
        try:
            assert_under(sp.parent, root)
            summary = load_summary(sp, plan_dir.name)
            merge_validator_result(summary, VALIDATOR, result)
            recalculate_totals(summary); summary["overall_status"] = derive_overall_status(summary)
            atomic_write(sp, summary)
        except Exception as exc:  # noqa: BLE001
            print(f"[ERROR] failed to merge summary: {exc}", file=sys.stderr); return 2
    return 1 if crit else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
