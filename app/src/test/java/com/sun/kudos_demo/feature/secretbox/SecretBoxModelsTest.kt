package com.sun.kudos_demo.feature.secretbox

import com.sun.kudos_demo.R
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SecretBoxUiState (Phase 09).
 *
 * Tests the UI state data class: phase transitions, unopened count, reward tracking,
 * allOpened computed property, equality, and copy semantics.
 */
class SecretBoxModelsTest {

    // ===================== Default initialization tests =====================

    @Test
    fun secretBoxUiState_DefaultPhase_IsClosed() {
        val state = SecretBoxUiState()
        assertEquals("Default phase must be CLOSED", SecretBoxPhase.CLOSED, state.phase)
    }

    @Test
    fun secretBoxUiState_DefaultUnopenedCount_IsZero() {
        val state = SecretBoxUiState()
        assertEquals("Default unopened count must be 0", 0, state.unopenedCount)
    }

    @Test
    fun secretBoxUiState_DefaultReward_IsNull() {
        val state = SecretBoxUiState()
        assertNull("Default reward must be null", state.reward)
    }

    // ===================== allOpened computed property tests (TC_SB_FUN_003) =====================

    @Test
    fun secretBoxUiState_AllOpened_IsTrueWhenUnopenedCountIsZero() {
        val state = SecretBoxUiState(unopenedCount = 0)
        assertTrue("allOpened must be true when unopenedCount == 0", state.allOpened)
    }

    @Test
    fun secretBoxUiState_AllOpened_IsFalseWhenUnopenedCountIsOne() {
        val state = SecretBoxUiState(unopenedCount = 1)
        assertFalse("allOpened must be false when unopenedCount == 1", state.allOpened)
    }

    @Test
    fun secretBoxUiState_AllOpened_IsFalseWhenUnopenedCountIsGreaterThanOne() {
        val state = SecretBoxUiState(unopenedCount = 5)
        assertFalse("allOpened must be false when unopenedCount == 5", state.allOpened)
    }

    @Test
    fun secretBoxUiState_AllOpened_IsTrueByDefault() {
        val state = SecretBoxUiState()  // Default unopenedCount = 0
        assertTrue("allOpened must be true by default (unopenedCount = 0)", state.allOpened)
    }

    @Test
    fun secretBoxUiState_AllOpened_ReflectsCurrentUnopenedCount() {
        var state = SecretBoxUiState(unopenedCount = 3)
        assertFalse("allOpened is false when unopenedCount = 3", state.allOpened)

        state = state.copy(unopenedCount = 1)
        assertFalse("allOpened is false when unopenedCount = 1", state.allOpened)

        state = state.copy(unopenedCount = 0)
        assertTrue("allOpened is true when unopenedCount = 0", state.allOpened)
    }

    // ===================== Custom initialization tests =====================

    @Test
    fun secretBoxUiState_CustomPhase() {
        val state = SecretBoxUiState(phase = SecretBoxPhase.OPENING)
        assertEquals("Phase must be set to OPENING", SecretBoxPhase.OPENING, state.phase)
    }

    @Test
    fun secretBoxUiState_CustomUnopenedCount() {
        val state = SecretBoxUiState(unopenedCount = 5)
        assertEquals("Unopened count must be 5", 5, state.unopenedCount)
    }

    @Test
    fun secretBoxUiState_CustomReward() {
        val reward = SecretBoxReward("scarf", R.string.sb_gift_scarf, "img_secretbox_scarf")
        val state = SecretBoxUiState(reward = reward)
        assertEquals("Reward must be set correctly", reward, state.reward)
    }

    @Test
    fun secretBoxUiState_AllFieldsCustom() {
        val reward = SecretBoxReward("mug", R.string.sb_gift_mug, "img_secretbox_mug")
        val state = SecretBoxUiState(
            phase = SecretBoxPhase.REWARD,
            unopenedCount = 2,
            reward = reward
        )

        assertEquals("Phase must be REWARD", SecretBoxPhase.REWARD, state.phase)
        assertEquals("Unopened count must be 2", 2, state.unopenedCount)
        assertEquals("Reward must be set correctly", reward, state.reward)
        assertFalse("allOpened must be false when unopenedCount = 2", state.allOpened)
    }

    // ===================== Data class equality tests =====================

    @Test
    fun secretBoxUiState_Equality_WithSameValues() {
        val state1 = SecretBoxUiState(
            phase = SecretBoxPhase.OPENING,
            unopenedCount = 3,
            reward = null
        )
        val state2 = SecretBoxUiState(
            phase = SecretBoxPhase.OPENING,
            unopenedCount = 3,
            reward = null
        )

        assertEquals("States with same values must be equal", state1, state2)
    }

    @Test
    fun secretBoxUiState_Inequality_WithDifferentPhase() {
        val state1 = SecretBoxUiState(phase = SecretBoxPhase.CLOSED)
        val state2 = SecretBoxUiState(phase = SecretBoxPhase.OPENING)

        assertNotEquals("States with different phases must not be equal", state1, state2)
    }

    @Test
    fun secretBoxUiState_Inequality_WithDifferentUnopenedCount() {
        val state1 = SecretBoxUiState(unopenedCount = 3)
        val state2 = SecretBoxUiState(unopenedCount = 4)

        assertNotEquals("States with different unopened counts must not be equal", state1, state2)
    }

    @Test
    fun secretBoxUiState_Inequality_WithDifferentReward() {
        val reward1 = SecretBoxReward("scarf", R.string.sb_gift_scarf, "img_secretbox_scarf")
        val reward2 = SecretBoxReward("mug", R.string.sb_gift_mug, "img_secretbox_mug")

        val state1 = SecretBoxUiState(reward = reward1)
        val state2 = SecretBoxUiState(reward = reward2)

        assertNotEquals("States with different rewards must not be equal", state1, state2)
    }

    @Test
    fun secretBoxUiState_Inequality_NullVsNonNullReward() {
        val reward = SecretBoxReward("scarf", R.string.sb_gift_scarf, "img_secretbox_scarf")

        val state1 = SecretBoxUiState(reward = null)
        val state2 = SecretBoxUiState(reward = reward)

        assertNotEquals("State with null reward vs non-null reward must not be equal", state1, state2)
    }

    // ===================== Copy semantics tests =====================

    @Test
    fun secretBoxUiState_Copy_PreservesAllFieldsByDefault() {
        val original = SecretBoxUiState(
            phase = SecretBoxPhase.OPENING,
            unopenedCount = 3,
            reward = null
        )

        val copied = original.copy()

        assertEquals("Copied state must equal original", original, copied)
        assertEquals("Phase must be preserved", original.phase, copied.phase)
        assertEquals("Unopened count must be preserved", original.unopenedCount, copied.unopenedCount)
        assertEquals("Reward must be preserved", original.reward, copied.reward)
    }

    @Test
    fun secretBoxUiState_Copy_CanChangePhase() {
        val original = SecretBoxUiState(phase = SecretBoxPhase.CLOSED)
        val modified = original.copy(phase = SecretBoxPhase.OPENING)

        assertEquals("Original phase must be CLOSED", SecretBoxPhase.CLOSED, original.phase)
        assertEquals("Modified phase must be OPENING", SecretBoxPhase.OPENING, modified.phase)
    }

    @Test
    fun secretBoxUiState_Copy_CanChangeUnopenedCount() {
        val original = SecretBoxUiState(unopenedCount = 5)
        val modified = original.copy(unopenedCount = 4)

        assertEquals("Original unopened count must be 5", 5, original.unopenedCount)
        assertEquals("Modified unopened count must be 4", 4, modified.unopenedCount)
    }

    @Test
    fun secretBoxUiState_Copy_CanChangeReward() {
        val reward1 = SecretBoxReward("scarf", R.string.sb_gift_scarf, "img_secretbox_scarf")
        val reward2 = SecretBoxReward("mug", R.string.sb_gift_mug, "img_secretbox_mug")

        val original = SecretBoxUiState(reward = reward1)
        val modified = original.copy(reward = reward2)

        assertEquals("Original reward must be reward1", reward1, original.reward)
        assertEquals("Modified reward must be reward2", reward2, modified.reward)
    }

    @Test
    fun secretBoxUiState_Copy_CanClearReward() {
        val reward = SecretBoxReward("scarf", R.string.sb_gift_scarf, "img_secretbox_scarf")

        val original = SecretBoxUiState(reward = reward)
        val modified = original.copy(reward = null)

        assertEquals("Original reward must be set", reward, original.reward)
        assertNull("Modified reward must be null", modified.reward)
    }

    @Test
    fun secretBoxUiState_Copy_MultipleFieldsCanChange() {
        val reward1 = SecretBoxReward("scarf", R.string.sb_gift_scarf, "img_secretbox_scarf")
        val reward2 = SecretBoxReward("mug", R.string.sb_gift_mug, "img_secretbox_mug")

        val original = SecretBoxUiState(
            phase = SecretBoxPhase.CLOSED,
            unopenedCount = 5,
            reward = reward1
        )

        val modified = original.copy(
            phase = SecretBoxPhase.REWARD,
            unopenedCount = 4,
            reward = reward2
        )

        assertEquals("Original state must be unchanged", SecretBoxPhase.CLOSED, original.phase)
        assertEquals("Modified phase must be REWARD", SecretBoxPhase.REWARD, modified.phase)
        assertEquals("Modified unopened count must be 4", 4, modified.unopenedCount)
        assertEquals("Modified reward must be reward2", reward2, modified.reward)
    }

    // ===================== Phase enum tests =====================

    @Test
    fun secretBoxPhase_HasThreeValues() {
        val phases = SecretBoxPhase.values()
        assertEquals("SecretBoxPhase must have 3 values", 3, phases.size)
    }

    @Test
    fun secretBoxPhase_ContainsClosed() {
        assertTrue("SecretBoxPhase must contain CLOSED", SecretBoxPhase.values().contains(SecretBoxPhase.CLOSED))
    }

    @Test
    fun secretBoxPhase_ContainsOpening() {
        assertTrue("SecretBoxPhase must contain OPENING", SecretBoxPhase.values().contains(SecretBoxPhase.OPENING))
    }

    @Test
    fun secretBoxPhase_ContainsReward() {
        assertTrue("SecretBoxPhase must contain REWARD", SecretBoxPhase.values().contains(SecretBoxPhase.REWARD))
    }

    // ===================== Transition scenarios =====================

    @Test
    fun secretBoxUiState_TransitionClosed_ToOpening() {
        val closed = SecretBoxUiState(phase = SecretBoxPhase.CLOSED, unopenedCount = 5)
        val opening = closed.copy(phase = SecretBoxPhase.OPENING)

        assertEquals("Initial state phase must be CLOSED", SecretBoxPhase.CLOSED, closed.phase)
        assertEquals("Transitioned state phase must be OPENING", SecretBoxPhase.OPENING, opening.phase)
        assertEquals("Unopened count must be preserved", 5, opening.unopenedCount)
    }

    @Test
    fun secretBoxUiState_TransitionOpening_ToReward() {
        val reward = SecretBoxReward("gift", R.string.sb_gift_gift, "img_secretbox_gift")
        val opening = SecretBoxUiState(phase = SecretBoxPhase.OPENING, unopenedCount = 5, reward = null)
        val reward_state = opening.copy(phase = SecretBoxPhase.REWARD, reward = reward)

        assertEquals("Opening phase must be OPENING", SecretBoxPhase.OPENING, opening.phase)
        assertEquals("Reward state phase must be REWARD", SecretBoxPhase.REWARD, reward_state.phase)
        assertEquals("Reward state must have the reward", reward, reward_state.reward)
    }

    @Test
    fun secretBoxUiState_TransitionReward_ToClosed() {
        val reward = SecretBoxReward("gift", R.string.sb_gift_gift, "img_secretbox_gift")
        val reward_state = SecretBoxUiState(phase = SecretBoxPhase.REWARD, unopenedCount = 4, reward = reward)
        val closed = reward_state.copy(phase = SecretBoxPhase.CLOSED, reward = null, unopenedCount = 4)

        assertEquals("Reward state phase must be REWARD", SecretBoxPhase.REWARD, reward_state.phase)
        assertEquals("Closed state phase must be CLOSED", SecretBoxPhase.CLOSED, closed.phase)
        assertNull("Closed state reward must be null", closed.reward)
        assertEquals("Unopened count must be 4 (after opening one)", 4, closed.unopenedCount)
    }

    @Test
    fun secretBoxUiState_FullFlow_ClosedToOpeningToRewardToClosed() {
        val reward = SecretBoxReward("stamps", R.string.sb_gift_stamps, "img_secretbox_stamps")

        // Start: CLOSED, 5 unopened
        val step1 = SecretBoxUiState(phase = SecretBoxPhase.CLOSED, unopenedCount = 5)
        assertTrue("Step 1: allOpened must be false", !step1.allOpened)

        // Tap box: OPENING
        val step2 = step1.copy(phase = SecretBoxPhase.OPENING)
        assertEquals("Step 2: phase must be OPENING", SecretBoxPhase.OPENING, step2.phase)

        // Animation ends: REWARD with prize
        val step3 = step2.copy(phase = SecretBoxPhase.REWARD, reward = reward)
        assertEquals("Step 3: phase must be REWARD", SecretBoxPhase.REWARD, step3.phase)
        assertEquals("Step 3: reward must be set", reward, step3.reward)

        // Continue: CLOSED, 4 unopened
        val step4 = step3.copy(phase = SecretBoxPhase.CLOSED, reward = null, unopenedCount = 4)
        assertEquals("Step 4: phase must be CLOSED", SecretBoxPhase.CLOSED, step4.phase)
        assertNull("Step 4: reward must be null", step4.reward)
        assertEquals("Step 4: unopened count must be 4", 4, step4.unopenedCount)
        assertFalse("Step 4: allOpened must be false", step4.allOpened)
    }

    @Test
    fun secretBoxUiState_AllOpenedDrain_FromFiveToZero() {
        var state = SecretBoxUiState(phase = SecretBoxPhase.CLOSED, unopenedCount = 5)
        assertFalse("unopenedCount = 5: allOpened must be false", state.allOpened)

        state = state.copy(unopenedCount = 4)
        assertFalse("unopenedCount = 4: allOpened must be false", state.allOpened)

        state = state.copy(unopenedCount = 3)
        assertFalse("unopenedCount = 3: allOpened must be false", state.allOpened)

        state = state.copy(unopenedCount = 2)
        assertFalse("unopenedCount = 2: allOpened must be false", state.allOpened)

        state = state.copy(unopenedCount = 1)
        assertFalse("unopenedCount = 1: allOpened must be false", state.allOpened)

        state = state.copy(unopenedCount = 0)
        assertTrue("unopenedCount = 0: allOpened must be true", state.allOpened)
    }
}
