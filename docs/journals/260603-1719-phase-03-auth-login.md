# Phase 03: Authentication Login Screen Implementation

**Date**: 2026-06-03 17:19
**Severity**: Low
**Component**: LoginScreen (Compose), MoMorph asset pipeline
**Status**: Resolved

## What Happened

Implemented the Login screen from MoMorph Figma design with bilingual support (VN/EN). Phase executed cleanly: specs extracted, assets converted to Android drawables, Composable built and tested.

## The Brutal Truth

MoMorph API downtime at the start was annoying but brief — lost 15 minutes on re-authentication. The real friction came from design-to-implementation details: the Figma spec said button radius=4dp (rounded corner), which isn't "pill" shaped, but visually close enough that we had to verify twice. Small gap between design language and Android Material 3 conventions.

Also: the asset pipeline (download → convert SVG → place in drawable/) feels manual. We're doing this per-screen for now, but scaling to 10+ screens will be tedious.

## Technical Details

**MoMorph Access**: `get_frame()` returned 404 initially. Reconnect via FileKey refresh resolved it immediately.

**Assets Processed**:
- `bg_login_keyvisual.png`: 375×812, 861KB (background gradient from Figma)
- `logo_saa.png`: 48×44 (converted, placed in drawables)
- `logo_root_further.png`: 247×109 (SVG → Android VectorDrawable)
- Google icon: SVG → VectorDrawable

**LoginScreen.kt** (232 lines):
```
- Box(gradient overlay, 104dp header)
- Image(ROOT FURTHER, 247×109dp at y=252)
- Surface(card, description text)
- LanguageDropdown(2 items, min 48dp touch target)
- Button(gold #FFEA9E, radius=4dp, 246×40dp)
```

**LoginViewModel**: Mock auth delay (1s), `StateFlow<AuthState>`, `AppLanguage` enum for i18n.

## What We Tried

1. **Gradient + Image**: Initially layered in Column; switched to Box for overlay control.
2. **Touch target (M2 fix)**: Dropdown was 44dp — expanded minHeight to 48dp with internal padding.
3. **Edge-to-edge (H1 fix)**: Set `contentWindowInsets = WindowInsets(0)` on Scaffold + `navigationBarsPadding()` on bottom nav.

All fixes applied. Tests: 6/6 pass. Lint: clean.

## Root Cause Analysis

Nothing broke badly here. The friction points were:
- **MoMorph API reliability**: Unclear if re-auth is standard or a one-off issue.
- **Asset conversion workflow**: No automated pipeline; each asset is a manual download → convert → place step.
- **Design-to-code gap**: Button radius=4dp looks intentional but required extra verification against Material 3 spec.

## Lessons Learned

1. **Clarify design intent upfront**: Ask "is this rounded corner intentional or should it be Material default?" in Figma comments, not during implementation.
2. **Batch asset conversion**: Build a small script or CI step to extract all Figma images, convert SVGs in bulk, place in drawable/. Doing it per-screen is waste.
3. **MoMorph reliability**: If re-auth happens again, check session TTL or API status page upfront.

## Next Steps

1. Phase 04: Password reset screen — apply same gradient + image pattern (reuse template).
2. Document asset conversion workflow in `docs/` so next contributor isn't surprised by manual steps.
3. Monitor whether MoMorph API stability is a pattern; consider caching spec CSVs locally if rate-limited.
