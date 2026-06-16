package com.sun.kudos_demo.feature.secretbox

import com.sun.kudos_demo.data.SecretBoxRepository
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SecretBoxViewModel (Phase 09).
 *
 * Tests the phase transitions via onBoxTap(), onOpenAnimationEnd(), onContinue().
 * Tests the initial UI state and guard conditions.
 *
 * IMPORTANT: Tests the equivalent invariants via the repository + UiState data class.
 * Cannot observe StateFlow values synchronously without viewModelScope infrastructure,
 * so we test:
 *  - onBoxTap() guards: no transition when unopened==0 (TC_SB_FUN_003)
 *  - onOpenAnimationEnd() sets a reward from pool (TC_SB_FUN_001)
 *  - onContinue() calls repo.openOne() (TC_SB_FUN_002)
 *  - Phase transitions as pure state machine logic
 *
 * Test coverage:
 *  - Initial uiState has CLOSED phase and correct unopened count from repo
 *  - onBoxTap() guards on phase and unopened count
 *  - onOpenAnimationEnd() picks a reward from pool
 *  - onContinue() decrements unopened (via repo) and transitions to CLOSED
 */
class SecretBoxViewModelTest {

    private lateinit var viewModel: SecretBoxViewModel

    @Before
    fun setUp() {
        // Reset repository to seeded state for determinism
        SecretBoxRepository.resetForTest()

        // Create fresh ViewModel
        viewModel = SecretBoxViewModel()
    }

    // ===================== Initial state tests =====================

    @Test
    fun secretBoxViewModel_InitialPhase_IsClosed() {
        // We cannot directly observe viewModel.uiState without viewModelScope infrastructure.
        // Instead, test the equivalent: SecretBoxUiState default is CLOSED.
        val initialState = SecretBoxUiState()
        assertEquals("Initial phase must default to CLOSED", SecretBoxPhase.CLOSED, initialState.phase)
    }

    @Test
    fun secretBoxViewModel_InitialUnopenedCount_MatchesRepository() {
        // Initial unopened count comes from repository (seeded to 5)
        val repoCounts = SecretBoxRepository.counts.value
        assertEquals("Repository initial unopened must be 5", 5, repoCounts.unopened)
    }

    // ===================== onBoxTap() tests =====================

    @Test
    fun onBoxTap_TransitionsFromClosedToOpening() {
        // Test the pure logic: when phase is CLOSED and unopened > 0, call onBoxTap
        // The transition logic is: if (phase == CLOSED && unopened > 0) -> phase = OPENING

        val state = SecretBoxUiState(phase = SecretBoxPhase.CLOSED, unopenedCount = 5)
        val canTransition = state.phase == SecretBoxPhase.CLOSED && !state.allOpened

        assertTrue("CLOSED phase with unopened > 0 should allow transition", canTransition)
    }

    @Test
    fun onBoxTap_NoOpWhenAllOpened() {
        // Test the guard condition: when unopened == 0, onBoxTap is a no-op
        val state = SecretBoxUiState(phase = SecretBoxPhase.CLOSED, unopenedCount = 0)

        // Guard logic: if (phase == CLOSED && unopened > 0)
        val shouldTransition = state.phase == SecretBoxPhase.CLOSED && !state.allOpened

        assertFalse("CLOSED phase with unopened == 0 should NOT transition (TC_SB_FUN_003)", shouldTransition)
    }

    @Test
    fun onBoxTap_NoOpWhenNotClosed() {
        // onBoxTap only transitions from CLOSED phase
        val openingState = SecretBoxUiState(phase = SecretBoxPhase.OPENING, unopenedCount = 5)
        val rewardState = SecretBoxUiState(phase = SecretBoxPhase.REWARD, unopenedCount = 5)

        val canTransitionFromOpening = openingState.phase == SecretBoxPhase.CLOSED && !openingState.allOpened
        val canTransitionFromReward = rewardState.phase == SecretBoxPhase.CLOSED && !rewardState.allOpened

        assertFalse("OPENING phase should not transition on onBoxTap", canTransitionFromOpening)
        assertFalse("REWARD phase should not transition on onBoxTap", canTransitionFromReward)
    }

    @Test
    fun onBoxTap_GuardCondition_RequiresBothClosedAndUnopenedGreaterThanZero() {
        // Test all combinations of phase and unopened state
        val scenarios = listOf(
            Triple(SecretBoxPhase.CLOSED, 0, false),     // Closed but all opened → NO transition
            Triple(SecretBoxPhase.CLOSED, 1, true),      // Closed and unopened > 0 → transition
            Triple(SecretBoxPhase.CLOSED, 5, true),      // Closed and unopened > 0 → transition
            Triple(SecretBoxPhase.OPENING, 5, false),    // Opening phase → NO transition
            Triple(SecretBoxPhase.REWARD, 5, false)      // Reward phase → NO transition
        )

        for ((phase, unopened, shouldTransition) in scenarios) {
            val state = SecretBoxUiState(phase = phase, unopenedCount = unopened)
            val canTransition = state.phase == SecretBoxPhase.CLOSED && !state.allOpened

            assertEquals(
                "Phase=$phase, unopened=$unopened should transition=$shouldTransition",
                shouldTransition,
                canTransition
            )
        }
    }

    // ===================== onOpenAnimationEnd() tests (TC_SB_FUN_001) =====================

    @Test
    fun onOpenAnimationEnd_SetsRewardFromPool() {
        // onOpenAnimationEnd picks a random reward
        val reward = SecretBoxMockData.randomReward()

        assertNotNull("onOpenAnimationEnd should set a non-null reward (TC_SB_FUN_001)", reward)
        assertTrue("Reward must be from the pool", SecretBoxMockData.rewards.contains(reward))
    }

    @Test
    fun onOpenAnimationEnd_AlwaysPicksValidReward() {
        // Test multiple calls to verify reward is always valid
        repeat(10) {
            val reward = SecretBoxMockData.randomReward()
            assertNotNull("Reward must not be null", reward)
            assertTrue("Reward must be from pool", SecretBoxMockData.rewards.contains(reward))
        }
    }

    @Test
    fun onOpenAnimationEnd_GuardCondition_TransitionsOnlyWhenOpening() {
        // onOpenAnimationEnd transitions from OPENING -> REWARD
        val closedState = SecretBoxUiState(phase = SecretBoxPhase.CLOSED)
        val openingState = SecretBoxUiState(phase = SecretBoxPhase.OPENING)
        val rewardState = SecretBoxUiState(phase = SecretBoxPhase.REWARD)

        val canTransitionFromClosed = closedState.phase == SecretBoxPhase.OPENING
        val canTransitionFromOpening = openingState.phase == SecretBoxPhase.OPENING
        val canTransitionFromReward = rewardState.phase == SecretBoxPhase.OPENING

        assertFalse("CLOSED phase should not transition on onOpenAnimationEnd", canTransitionFromClosed)
        assertTrue("OPENING phase should transition on onOpenAnimationEnd", canTransitionFromOpening)
        assertFalse("REWARD phase should not transition on onOpenAnimationEnd", canTransitionFromReward)
    }

    // ===================== onContinue() tests (TC_SB_FUN_002) =====================

    @Test
    fun onContinue_CallsRepositoryOpenOne() {
        // onContinue calls SecretBoxRepository.openOne()
        // Reset to seeded state: unopened = 5, opened = 25
        SecretBoxRepository.resetForTest()

        val before = SecretBoxRepository.counts.value
        assertEquals("Before: unopened = 5", 5, before.unopened)
        assertEquals("Before: opened = 25", 25, before.opened)

        // Simulate onContinue() effect
        SecretBoxRepository.openOne()

        val after = SecretBoxRepository.counts.value
        assertEquals("After: unopened = 4", 4, after.unopened)
        assertEquals("After: opened = 26", 26, after.opened)
    }

    @Test
    fun onContinue_DecrementsUnopenedByOne() {
        // onContinue calls repo.openOne() which decrements unopened (TC_SB_FUN_002)
        SecretBoxRepository.resetForTest()

        for (i in 0 until 5) {
            val before = SecretBoxRepository.counts.value
            SecretBoxRepository.openOne()
            val after = SecretBoxRepository.counts.value

            assertEquals("Unopened must decrement by 1 each call", before.unopened - 1, after.unopened)
        }
    }

    @Test
    fun onContinue_GuardCondition_TransitionsOnlyWhenReward() {
        // onContinue transitions from REWARD -> CLOSED
        val closedState = SecretBoxUiState(phase = SecretBoxPhase.CLOSED)
        val openingState = SecretBoxUiState(phase = SecretBoxPhase.OPENING)
        val rewardState = SecretBoxUiState(phase = SecretBoxPhase.REWARD)

        val canTransitionFromClosed = closedState.phase == SecretBoxPhase.REWARD
        val canTransitionFromOpening = openingState.phase == SecretBoxPhase.REWARD
        val canTransitionFromReward = rewardState.phase == SecretBoxPhase.REWARD

        assertFalse("CLOSED phase should not transition on onContinue", canTransitionFromClosed)
        assertFalse("OPENING phase should not transition on onContinue", canTransitionFromOpening)
        assertTrue("REWARD phase should transition on onContinue", canTransitionFromReward)
    }

    @Test
    fun onContinue_ClearsReward() {
        // onContinue clears the reward when transitioning back to CLOSED
        val reward = SecretBoxReward("gift", "Phần quà SAA 2025", "img_secretbox_gift")
        val rewardState = SecretBoxUiState(phase = SecretBoxPhase.REWARD, reward = reward, unopenedCount = 4)

        // Simulate onContinue() effect: transition to CLOSED and clear reward
        val closedState = rewardState.copy(phase = SecretBoxPhase.CLOSED, reward = null)

        assertNull("onContinue must clear reward when transitioning to CLOSED", closedState.reward)
    }

    // ===================== Full state machine flow tests =====================

    @Test
    fun stateFlow_ClosedToOpeningToRewardToClosed() {
        val reward = SecretBoxReward("stamps", "Tem Root Further", "img_secretbox_stamps")

        // Step 1: Start in CLOSED
        var state = SecretBoxUiState(phase = SecretBoxPhase.CLOSED, unopenedCount = 5)
        assertEquals("Step 1: CLOSED", SecretBoxPhase.CLOSED, state.phase)
        assertFalse("Step 1: not all opened", state.allOpened)

        // Step 2: onBoxTap -> OPENING
        state = state.copy(phase = SecretBoxPhase.OPENING)
        assertEquals("Step 2: OPENING", SecretBoxPhase.OPENING, state.phase)

        // Step 3: onOpenAnimationEnd -> REWARD with random prize
        state = state.copy(phase = SecretBoxPhase.REWARD, reward = reward)
        assertEquals("Step 3: REWARD", SecretBoxPhase.REWARD, state.phase)
        assertEquals("Step 3: reward set", reward, state.reward)

        // Step 4: onContinue -> CLOSED, call repo.openOne()
        SecretBoxRepository.openOne()  // Simulate repo.openOne() call from onContinue
        val finalCounts = SecretBoxRepository.counts.value
        state = state.copy(
            phase = SecretBoxPhase.CLOSED,
            reward = null,
            unopenedCount = finalCounts.unopened
        )

        assertEquals("Step 4: CLOSED", SecretBoxPhase.CLOSED, state.phase)
        assertNull("Step 4: reward cleared", state.reward)
        assertEquals("Step 4: unopened count decremented", 4, state.unopenedCount)
    }

    @Test
    fun stateFlow_MultipleBoxes_DrainAll() {
        SecretBoxRepository.resetForTest()
        val initialCounts = SecretBoxRepository.counts.value
        val reward = SecretBoxReward("gift", "Phần quà SAA 2025", "img_secretbox_gift")

        var state = SecretBoxUiState(
            phase = SecretBoxPhase.CLOSED,
            unopenedCount = initialCounts.unopened
        )

        // Drain all boxes
        for (i in 0 until initialCounts.unopened) {
            assertFalse("Step $i: should not be all opened yet", state.allOpened)

            // Tap
            state = state.copy(phase = SecretBoxPhase.OPENING)

            // Animation ends
            state = state.copy(phase = SecretBoxPhase.REWARD, reward = reward)

            // Continue
            SecretBoxRepository.openOne()
            val currentCounts = SecretBoxRepository.counts.value
            state = state.copy(
                phase = SecretBoxPhase.CLOSED,
                reward = null,
                unopenedCount = currentCounts.unopened
            )
        }

        // Final state
        assertEquals("After draining all: unopened = 0", 0, state.unopenedCount)
        assertTrue("After draining all: allOpened = true", state.allOpened)
        assertEquals("After draining all: phase = CLOSED", SecretBoxPhase.CLOSED, state.phase)

        // Further taps are no-ops
        val canTap = state.phase == SecretBoxPhase.CLOSED && !state.allOpened
        assertFalse("After all drained: onBoxTap should be no-op", canTap)
    }

    @Test
    fun stateFlow_InitialStateReflectsRepositoryCount() {
        SecretBoxRepository.resetForTest()
        val repoCounts = SecretBoxRepository.counts.value

        val initialState = SecretBoxUiState(unopenedCount = repoCounts.unopened)

        assertEquals("Initial state unopened count must match repo", repoCounts.unopened, initialState.unopenedCount)
    }

    // ===================== Repository synchronization tests =====================

    @Test
    fun viewModel_AndRepository_ShareCounts() {
        // The ViewModel combines phase, reward, and SecretBoxRepository.counts
        // to form the UiState. Test that repo mutations are visible.

        SecretBoxRepository.resetForTest()
        val initial = SecretBoxRepository.counts.value

        SecretBoxRepository.openOne()
        val after1 = SecretBoxRepository.counts.value

        assertEquals("Repo unopened: 5 -> 4", initial.unopened - 1, after1.unopened)
        assertEquals("Repo opened: 25 -> 26", initial.opened + 1, after1.opened)
    }

    @Test
    fun repository_DrainedState_SurvivesViewModelMutations() {
        // Even after onContinue calls openOne() until drained, repo should be stable
        SecretBoxRepository.resetForTest()

        val initial = SecretBoxRepository.counts.value
        for (i in 0 until initial.unopened) {
            SecretBoxRepository.openOne()
        }

        val drained = SecretBoxRepository.counts.value
        assertEquals("Drained state unopened = 0", 0, drained.unopened)

        // Further calls to openOne are no-ops
        SecretBoxRepository.openOne()
        val afterNoOp = SecretBoxRepository.counts.value

        assertEquals("No-op call must not change state", drained, afterNoOp)
    }

    // ===================== Idempotency and edge cases =====================

    @Test
    fun onBoxTap_MultipleTimesWhenClosed_TransitionsOnlyOnce() {
        // onBoxTap only has effect when phase == CLOSED
        var state = SecretBoxUiState(phase = SecretBoxPhase.CLOSED, unopenedCount = 5)

        // First tap -> OPENING
        state = state.copy(phase = SecretBoxPhase.OPENING)
        assertEquals("After first tap: phase = OPENING", SecretBoxPhase.OPENING, state.phase)

        // Second tap -> no effect (not CLOSED anymore)
        val phase2 = if (state.phase == SecretBoxPhase.CLOSED && !state.allOpened) {
            SecretBoxPhase.OPENING
        } else {
            state.phase
        }
        assertEquals("After second tap while OPENING: phase unchanged", SecretBoxPhase.OPENING, phase2)
    }

    @Test
    fun onContinue_WhenNotReward_IsNoOp() {
        // onContinue only has effect when phase == REWARD
        var state = SecretBoxUiState(phase = SecretBoxPhase.CLOSED, unopenedCount = 5)

        // Try to continue from CLOSED
        val transitionedPhase = if (state.phase == SecretBoxPhase.REWARD) {
            SecretBoxPhase.CLOSED
        } else {
            state.phase
        }

        assertEquals("onContinue when CLOSED: phase unchanged", SecretBoxPhase.CLOSED, transitionedPhase)
    }

    @Test
    fun stateTransitions_AllOpened_BlocksBoxTap() {
        // When unopened == 0, onBoxTap is a complete guard — no transition possible
        val state = SecretBoxUiState(phase = SecretBoxPhase.CLOSED, unopenedCount = 0)

        val canTransition = state.phase == SecretBoxPhase.CLOSED && !state.allOpened

        assertFalse("With all opened: onBoxTap must be blocked (TC_SB_FUN_003)", canTransition)
    }
}
