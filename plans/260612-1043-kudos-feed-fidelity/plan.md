---
title: Kudos Feed — Visual Fidelity Pass (per-section)
status: in_progress
created: 2026-06-12
screen: "[iOS] Sun*Kudos — fO0Kt19sZZ"
fileKey: 9ypp4enmFmdK3YAFJLIu6C
baseline_fidelity: ~62% (audited)
---

# Kudos Feed — Fidelity Pass

The feed screen is long (8+ stacked sections). A per-section audit (build vs design,
real emulator screenshots) confirmed ~60% fidelity. **Decision: split the work per section**
so each part can be matched precisely instead of one monolithic pass.

Detail: [`audit-fixes.md`](./audit-fixes.md) (findings grouped by file) · raw: `audit-result.json`
Screenshots: `screenshots/build-01-top.png`, `build-07/08/09.png` (emulator Pixel_8_Pro).

## Per-section status (audited fidelity → target ≥90%)

| # | Section | Fidelity | Owner files | Status |
|---|---------|----------|-------------|--------|
| 1 | Hero + TopBar + Send prompt | 74% | FeedHeroBanner, SendKudosPrompt, KudosTopBar | fixing |
| 2 | Highlight: card + filter + carousel | 68% | KudosCard, Hashtag/DepartmentFilterDropdown, HighlightCarousel | fixing |
| 3 | **Spotlight Board** | **30%** | SpotlightBoard, SpotlightMockData | fixing (biggest) |
| 4 | ALL KUDOS stats + Secret Box | 52% | StatsBlock, AllKudosSection | fixing |
| 5 | 10 Sunner + feed list + View all | 72% | GiftRecipientsSection, AllKudosSection | fixing |
| 6 | Bottom nav + section headers | 78% | KudosBottomNav (indicator) | partial |

## Top fixes (high severity)
- Card **title**: gold/left/large → dark/centered/bold small (repeats on every card).
- **Spotlight**: names overlap into a blob → fewer names + wider spread + smaller font; "388 KUDOS" top-center white; in-panel search pill; KV glow bg.
- Send prompt: pill → 4dp rect, centered white label, gold-10% bg + solid border.
- Filter dropdowns: capsule → 4dp rect, 40dp tall, solid gold border, white label.
- Stats: Secret Box button INSIDE panel + gold-fill; values gold; panel bg `KudosContainer2` + solid border; labels white.
- Gift panel: add bordered container; title centered; names gold.
- Bottom nav: remove Material active-pill indicator.

## Asset gaps (need Figma export — code can't fetch; MoMorph image API 403/500)
- Hero key-visual (feed-specific green/gold artwork) — currently reuses `bg_home_keyvisual`.
- "KUDOS" brand wordmark — currently gold text approximation.
- Real avatar photos — currently colored-initial placeholders.
- x2 "fire" badge icon on hearts stat.

## Deferred (global / risky — not in this pass)
- Montserrat font family (theme-wide; affects all screens) — recommend separate pass.
- SectionHeader divider (shared with Home; keep current to avoid Home regression).

## Verify loop
Build → install (`installDebug`) → emulator screenshot → re-audit changed sections → iterate until ≥90%.

## Result (fix pass 1 — 2026-06-12)
Applied via 6 file-partitioned implementer agents + 1 regression fix; build ✅; re-screenshotted on emulator.
Screenshots after: `screenshots/fix2-01-top.png`, `fix2-02-spotlight.png`, `fix-02.png`, `fix-03.png`.

| Section | Before | After (est.) | Notes |
|---------|--------|--------------|-------|
| Hero + TopBar + Send prompt | 74% | ~85% | tagline gold, send prompt 4dp rect/centered, bell white. KV+wordmark = asset gap |
| Highlight card + filter + carousel | 68% | ~90% | title dark/centered/bold, gold border, 24dp avatar, filter 2-up rect, plain pager chevrons |
| Spotlight Board | 30% | ~70% | readable cloud (32 names, spread), 388 KUDOS top-center, KV glow, in-panel search pill |
| ALL KUDOS stats + Secret Box | 52% | ~90% | values gold, Secret Box gold-fill INSIDE panel, labels white, dark panel + solid border |
| 10 Sunner + feed list | 72% | ~88% | bordered panel, centered gold title, gold names |
| Bottom nav | 78% | ~85% | Material active-pill removed (transparent indicator) |

**Avg ~62% → ~85%.** Regression fixed: filter dropdowns were full-width (only Hashtag showed) → gave each `Modifier.weight(1f)`.

## Fix pass 2 (2026-06-12) — user-supplied assets + carousel peek
User exported SVGs (temp/). Rasterized embedded base64 via extraction + sips; wordmark traced to vector.
- ✅ **KUDOS wordmark** → `ic_kudos_wordmark.xml` (vector, red mark + cream KUDOS) wired in hero (replaces gold text).
- ✅ **x2 fire** → `ic_fire.png` (extracted flame) + Compose "x2" overlay in StatsBlock.
- ✅ **Carousel peek**: removed side-arrow IconButtons that covered the peek; contentPadding 28→44dp, pageSpacing 12→8dp → 2 faded neighbour cards (~10%) now visible.
Screenshots: `screenshots/asset-01-top.png`, `asset-02-stats.png`.

### Still open (asset export needed)
- **Hero key-visual** (feed-specific green/gold artwork) — NOT among supplied SVGs; still using `bg_home_keyvisual`. MoMorph image API 403/500 → needs PNG export.
- Real avatar photos (placeholders kept). Legend/Rising badge images (text pills kept — supplied SVGs are gradient-textured rasters, rising lacked a distinct blob).
### Optional refinement
- Spotlight: reduce center overlap further. Montserrat font (theme-wide).
