# AIDD Project Requirements & Definition of Done

> This repo is an **AIDD mock demo** for Sun*. Goal: generate complete source code from
> **Figma design + MoMorph screen specs** using the **Takumi** toolkit with Claude Code.
> **Every task/phase MUST satisfy the Definition of Done below** before it is considered complete.

## Primary Goal — Gen Code from Design & Spec
- **Input:** Figma design + Screen Specs on MoMorph.
- **Output:** Complete, working source code.
- **Method:** Start from the standard Takumi flow (plan → implement → test → review → ship),
  then customize the flow/agents to fit, and document the customization in the report/journal.

## Definition of Done (apply to EVERY phase & task)
1. **UI — pixel-exact to the Figma design.** MoMorph design data is authoritative; never guess visual
   values (see `momorph/*` rules + `clarifications.md`). Verify on a real device/emulator before "done".
2. **Logic — behaves exactly per the MoMorph screen specs** (states, validation, navigation, edge cases).
3. **Quality — full automated tests, TDD:**
   - **Unit Tests** for logic (ViewModels, mappers, pure functions).
   - **E2E / instrumented tests** (Compose UI test / Espresso) for each screen's user flow.
   - Never weaken, fake, or skip tests just to go green.
4. **Process — Takumi standard flow first, then customize;** share what was customized in the report.

## Encouraged extended use cases (beyond coding)
- **MoMorph support:** auto-generate UI specs and test cases.
- **Reverse engineering:** generate spec docs from an existing/legacy codebase.
- **Pre-project:** speed up Estimate and build a Prototype for bidding.
- **Customization:** design your own Agent / Flow for domain-specific problems.

## Current status / known gap
- Unit tests: in place (186 as of Phase 06).
- **E2E / instrumented tests: NOT yet added** — required by the DoD above; treat as an open gap to close.
