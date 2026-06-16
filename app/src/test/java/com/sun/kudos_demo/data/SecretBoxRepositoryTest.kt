package com.sun.kudos_demo.data

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SecretBoxRepository (Phase 09).
 *
 * Tests the in-memory singleton: openOne() transitions, count invariants, no-op behavior.
 * IMPORTANT: SecretBoxRepository is an app-process singleton with resetForTest() to restore seed.
 * Tests are ORDER-INDEPENDENT: each test calls resetForTest() in @Before for clean state.
 *
 * Test coverage:
 *  - TC_SB_FUN_002: openOne() decrements unopened by 1 and increments opened by 1
 *  - TC_SB_FUN_003: openOne() is a no-op when unopened == 0
 *  - Invariant: unopened + opened == 30 (5 + 25 seeded)
 */
class SecretBoxRepositoryTest {

    @Before
    fun setUp() {
        // Restore seeded counts: unopened = 5, opened = 25
        SecretBoxRepository.resetForTest()
    }

    // ===================== Seed initialization tests =====================

    @Test
    fun secretBoxRepository_SeedInitializes_UnopenedFiveOpenedTwentyFive() {
        val counts = SecretBoxRepository.counts.value
        assertEquals("Unopened count must be seeded to 5", 5, counts.unopened)
        assertEquals("Opened count must be seeded to 25", 25, counts.opened)
    }

    @Test
    fun secretBoxRepository_SeedInvariant_TotalCountIsThirty() {
        val counts = SecretBoxRepository.counts.value
        val total = counts.unopened + counts.opened
        assertEquals("Total unopened + opened must be 30", 30, total)
    }

    // ===================== openOne() transition tests (TC_SB_FUN_002) =====================

    @Test
    fun openOne_DecrementsUnopenedByOne() {
        val before = SecretBoxRepository.counts.value
        assertEquals("Initial unopened must be 5", 5, before.unopened)

        SecretBoxRepository.openOne()

        val after = SecretBoxRepository.counts.value
        assertEquals("After openOne(), unopened must decrement by 1", 4, after.unopened)
    }

    @Test
    fun openOne_IncrementsOpenedByOne() {
        val before = SecretBoxRepository.counts.value
        assertEquals("Initial opened must be 25", 25, before.opened)

        SecretBoxRepository.openOne()

        val after = SecretBoxRepository.counts.value
        assertEquals("After openOne(), opened must increment by 1", 26, after.opened)
    }

    @Test
    fun openOne_MaintainsInvariant_TotalCountIsStable() {
        val before = SecretBoxRepository.counts.value
        val totalBefore = before.unopened + before.opened

        SecretBoxRepository.openOne()

        val after = SecretBoxRepository.counts.value
        val totalAfter = after.unopened + after.opened

        assertEquals("Total unopened + opened must remain constant", totalBefore, totalAfter)
    }

    @Test
    fun openOne_MultipleConsecutiveCalls_EachDecrementsUnopenedByOne() {
        val initial = SecretBoxRepository.counts.value
        assertEquals("Start with 5 unopened", 5, initial.unopened)

        SecretBoxRepository.openOne()
        val after1 = SecretBoxRepository.counts.value
        assertEquals("After 1st openOne(), unopened = 4", 4, after1.unopened)

        SecretBoxRepository.openOne()
        val after2 = SecretBoxRepository.counts.value
        assertEquals("After 2nd openOne(), unopened = 3", 3, after2.unopened)

        SecretBoxRepository.openOne()
        val after3 = SecretBoxRepository.counts.value
        assertEquals("After 3rd openOne(), unopened = 2", 2, after3.unopened)
    }

    @Test
    fun openOne_MultipleConsecutiveCalls_EachIncrementsOpenedByOne() {
        val initial = SecretBoxRepository.counts.value
        assertEquals("Start with 25 opened", 25, initial.opened)

        SecretBoxRepository.openOne()
        val after1 = SecretBoxRepository.counts.value
        assertEquals("After 1st openOne(), opened = 26", 26, after1.opened)

        SecretBoxRepository.openOne()
        val after2 = SecretBoxRepository.counts.value
        assertEquals("After 2nd openOne(), opened = 27", 27, after2.opened)

        SecretBoxRepository.openOne()
        val after3 = SecretBoxRepository.counts.value
        assertEquals("After 3rd openOne(), opened = 28", 28, after3.opened)
    }

    // ===================== No-op when depleted (TC_SB_FUN_003) =====================

    @Test
    fun openOne_IsNoOp_WhenUnopenedIsZero() {
        // Drain all unopened boxes
        repeat(5) {
            SecretBoxRepository.openOne()
        }

        val drained = SecretBoxRepository.counts.value
        assertEquals("After 5 openOne() calls, unopened must be 0", 0, drained.unopened)
        assertEquals("After 5 openOne() calls, opened must be 30", 30, drained.opened)

        // Call openOne() again — should be no-op
        SecretBoxRepository.openOne()

        val afterNoOp = SecretBoxRepository.counts.value
        assertEquals("openOne() when unopened==0 must not change unopened", 0, afterNoOp.unopened)
        assertEquals("openOne() when unopened==0 must not change opened", 30, afterNoOp.opened)
    }

    @Test
    fun openOne_MultipleNoOpCalls_AreIdempotent() {
        // Drain all boxes
        repeat(5) {
            SecretBoxRepository.openOne()
        }

        val snapshot1 = SecretBoxRepository.counts.value

        // Call openOne() 5 more times — all should be no-ops
        repeat(5) {
            SecretBoxRepository.openOne()
        }

        val snapshot2 = SecretBoxRepository.counts.value

        assertEquals("After 5 no-op calls, state must not change", snapshot1, snapshot2)
    }

    @Test
    fun openOne_NoOpStateEqualsSnapshot_WhenDrained() {
        repeat(5) {
            SecretBoxRepository.openOne()
        }

        val beforeNoOp = SecretBoxRepository.counts.value

        SecretBoxRepository.openOne()
        SecretBoxRepository.openOne()

        val afterNoOp = SecretBoxRepository.counts.value

        assertEquals("No-op calls must produce identical state", beforeNoOp, afterNoOp)
    }

    // ===================== resetForTest() determinism =====================

    @Test
    fun resetForTest_RestoresSeedToUnopenedFiveOpenedTwentyFive() {
        // Mutate the repository
        SecretBoxRepository.openOne()
        SecretBoxRepository.openOne()

        val mutated = SecretBoxRepository.counts.value
        assertEquals("After mutation, unopened should be 3", 3, mutated.unopened)

        // Reset
        SecretBoxRepository.resetForTest()

        val reset = SecretBoxRepository.counts.value
        assertEquals("After resetForTest(), unopened must be 5", 5, reset.unopened)
        assertEquals("After resetForTest(), opened must be 25", 25, reset.opened)
    }

    @Test
    fun resetForTest_IsIdempotent() {
        SecretBoxRepository.resetForTest()
        val snapshot1 = SecretBoxRepository.counts.value

        SecretBoxRepository.resetForTest()
        val snapshot2 = SecretBoxRepository.counts.value

        assertEquals("Multiple resetForTest() calls must be idempotent", snapshot1, snapshot2)
    }

    // ===================== StateFlow emission tests =====================

    @Test
    fun counts_StateFlow_IsInitialized() {
        val counts = SecretBoxRepository.counts.value
        assertNotNull("counts StateFlow must be initialized", counts)
        assertTrue("counts must have unopened >= 0", counts.unopened >= 0)
        assertTrue("counts must have opened >= 0", counts.opened >= 0)
    }

    @Test
    fun counts_StateFlow_UpdatesAfterOpenOne() {
        val before = SecretBoxRepository.counts.value

        SecretBoxRepository.openOne()

        val after = SecretBoxRepository.counts.value

        assertNotEquals("State must change after openOne()", before, after)
        assertEquals("unopened must decrement", before.unopened - 1, after.unopened)
        assertEquals("opened must increment", before.opened + 1, after.opened)
    }

    // ===================== Data class equality tests =====================

    @Test
    fun secretBoxCounts_DataClass_HasProperEquality() {
        val counts1 = SecretBoxCounts(unopened = 5, opened = 25)
        val counts2 = SecretBoxCounts(unopened = 5, opened = 25)

        assertEquals("SecretBoxCounts with same values must be equal", counts1, counts2)
    }

    @Test
    fun secretBoxCounts_DataClass_DifferentValues_AreNotEqual() {
        val counts1 = SecretBoxCounts(unopened = 5, opened = 25)
        val counts2 = SecretBoxCounts(unopened = 4, opened = 26)

        assertNotEquals("SecretBoxCounts with different unopened must not be equal", counts1, counts2)
    }

    @Test
    fun secretBoxCounts_Copy_PreservesValues() {
        val original = SecretBoxCounts(unopened = 4, opened = 26)
        val modified = original.copy(unopened = 3)

        assertEquals("Original unopened must be 4", 4, original.unopened)
        assertEquals("Modified unopened must be 3", 3, modified.unopened)
        assertEquals("Modified opened must be preserved", 26, modified.opened)
    }

    // ===================== Invariant validation after draining =====================

    @Test
    fun drainAll_AllUnopenedBoxes_ThenAssertFinalState() {
        val initial = SecretBoxRepository.counts.value

        // Drain all unopened boxes
        for (i in 0 until initial.unopened) {
            SecretBoxRepository.openOne()
        }

        val drained = SecretBoxRepository.counts.value
        assertEquals("After draining, unopened must be 0", 0, drained.unopened)
        assertEquals("After draining, opened must equal initial total", initial.unopened + initial.opened, drained.opened)

        // Verify invariant holds
        val total = drained.unopened + drained.opened
        assertEquals("Total must still be 30", 30, total)
    }

    @Test
    fun drainAll_ThenResetForTest_ReseedsCorrectly() {
        // Drain all
        repeat(5) {
            SecretBoxRepository.openOne()
        }

        val drained = SecretBoxRepository.counts.value
        assertEquals("After draining: unopened = 0, opened = 30", 0, drained.unopened)
        assertEquals("After draining: opened = 30", 30, drained.opened)

        // Reset
        SecretBoxRepository.resetForTest()

        val reset = SecretBoxRepository.counts.value
        assertEquals("After reset: unopened = 5", 5, reset.unopened)
        assertEquals("After reset: opened = 25", 25, reset.opened)
    }
}
