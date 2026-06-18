# Phase 12 — E2E / Instrumented Testing

## Overview

Implement Jetpack Compose instrumented (E2E) test suite covering representative authentication, navigation, and core Send Kudos flow. All tests pass on Pixel_8_Pro (API 34).

**Status:** ✅ Complete  
**Priority:** P0 (DoD requirement per `aidd-project-requirements.md`)

---

## Test Files & Coverage

### Test Files
- **E2eSupport.kt** — Shared markers, real-time wait helpers (`waitForText`, `loginToHome`)
- **LoginAndNavigationFlowTest.kt** — Flow 1 (login → Home), Flow 2 (bottom-nav Home ↔ Kudos ↔ Profile)
- **SendKudosFlowTest.kt** — Flow 3 (empty-form validation), Flow 4 (happy-path send + appears in feed)

### Support File (Production)
- **KudosTestTags.kt** (NEW) — Test tag constants
- **Production testTag additions** — `RecipientField.kt`, `MessageField.kt`, `SendKudosFormContent.kt` (additive, no behavior change)

### Flows Delivered (4/~20)
1. **Login + Navigate Home** — real auth flow, onboarding screen
2. **Bottom Navigation** — switch between Home, Kudos Feed, Profile tabs
3. **Form Validation** — empty recipient/message/hashtag triggers error banner
4. **Happy-Path Send** — fill form, submit, new kudo prepended in feed with sentinel marker

---

## Run Command

```bash
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.sun.kudos_demo.e2e
```

**Device tested:** Pixel_8_Pro (API 34, Android 14)  
**Result:** All 4 flows pass

---

## Success Criteria (DoD)

- ✅ All 4 test classes compile without errors
- ✅ Real-time poll strategy (no `waitUntil`; rationale documented in code)
- ✅ Unique sentinel marker (`E2EHAPPYMARKER`) prevents false-positives against seeded data
- ✅ Test tags are isolated in `KudosTestTags` object (no magic strings in tests)
- ✅ Production modifications are purely additive (testTag only, no behavior change)
- ✅ 100% pass rate on emulator
- ✅ Reviewer verdict: DONE_WITH_CONCERNS (all concerns actionable, no blockers)

---

## Reviewer Concerns & Status

**High Priority (actionable before next run):**
- H1: `SEND_ERROR` marker substring too short → extend to include full phrase
- H2: `Espresso.pressBack()` after hashtag pick may race with VM-driven dropdown close → remove or replace with state-driven toggle

**Medium Priority (document/improve, not blockers):**
- M1: Add `waitForIdle()` before final assert in `waitForText`
- M2: Document click behavior of `HOME_MARKER` button
- M3: Cross-reference `RECIPIENT_QUERY` test data to mock data source

**Low Priority (convention/polish):**
- L1: Move testTag to end of modifier chain in `MessageField.kt`
- L2: Add comment referencing string resource name for `HASHTAG_ADD` marker
- L3: Rename files to `E2ESupport`, `E2EMarkers` (idiomatic for acronym)

**Reviewer report:** `plans/reports/reviewer-260617-1641-e2e-tests.md`

---

## Known / Intentional Coverage Gaps

Per user decision (scope: representative subset, not full coverage):

- **NOT covered:** Notifications, Secret Box, Awards, Rules, 403/404 error screens
- **NOT covered:** Language switching to EN + string verification
- **NOT covered:** Send "Xem trước" (preview) dialog happy path
- **NOT covered:** Cross-tab communication, award unlock animations, search filters

These are documented as intentional gaps, not bugs. Expanding to 20+ flows is future work.

---

## Integration Notes

- E2E suite runs in isolation (parallel execution is safe; no shared state between tests)
- Mock data is seeded via `KudosMockData.searchableUsers` and `SendKudosMockData`; tests are data-coupled on specific names (documented in comments)
- Tests use `createAndroidComposeRule()` per test class → fresh Activity per test, no state leakage
- `Repository` singleton accumulates test submissions across runs; cross-test ordering tolerated per design

---

## Next Steps

1. Address H1 + H2 concerns (extend marker, remove/replace pressBack)
2. Expand E2E suite in future phase if needed (add Notifications, Secret Box, etc.)
3. Consider E2E for error screens and language switching if scope allows

---

**Status:** ✅ Complete  
**All 4 flows passing on emulator. Reviewer concerns documented. Known gaps intentional.**
