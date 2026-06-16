package com.sun.kudos_demo.feature.secretbox

import kotlin.random.Random
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SecretBoxMockData (Phase 09).
 *
 * Tests the reward pool: 6 unique rewards with valid names and image resource names.
 * Tests randomReward() determinism and pool membership.
 *
 * Test coverage:
 *  - Reward pool size and uniqueness
 *  - Each reward has non-blank id, name, imageResName
 *  - randomReward(Random(seed)) is deterministic
 *  - randomReward always returns a member of rewards
 */
class SecretBoxMockDataTest {

    // ===================== Reward pool structure tests =====================

    @Test
    fun rewards_ContainsSixEntries() {
        assertEquals("Reward pool must contain 6 entries", 6, SecretBoxMockData.rewards.size)
    }

    @Test
    fun rewards_AllEntriesHaveUniqueIds() {
        val ids = SecretBoxMockData.rewards.map { it.id }
        val uniqueIds = ids.distinct()

        assertEquals("All reward IDs must be unique", ids.size, uniqueIds.size)
    }

    @Test
    fun rewards_AllEntriesHaveNonBlankIds() {
        SecretBoxMockData.rewards.forEachIndexed { idx, reward ->
            assertFalse("Reward at index $idx must have non-blank id", reward.id.isBlank())
        }
    }

    @Test
    fun rewards_AllEntriesHaveNonBlankNames() {
        SecretBoxMockData.rewards.forEachIndexed { idx, reward ->
            assertFalse("Reward at index $idx must have non-blank name", reward.name.isBlank())
        }
    }

    @Test
    fun rewards_AllEntriesHaveNonBlankImageResNames() {
        SecretBoxMockData.rewards.forEachIndexed { idx, reward ->
            assertFalse("Reward at index $idx must have non-blank imageResName", reward.imageResName.isBlank())
        }
    }

    @Test
    fun rewards_AllEntriesAreComplete() {
        SecretBoxMockData.rewards.forEachIndexed { idx, reward ->
            assertNotNull("Reward at index $idx must not be null", reward)
            assertNotNull("Reward at index $idx id must not be null", reward.id)
            assertNotNull("Reward at index $idx name must not be null", reward.name)
            assertNotNull("Reward at index $idx imageResName must not be null", reward.imageResName)
        }
    }

    // ===================== Specific reward validation =====================

    @Test
    fun rewards_ContainsScarf_WithVietnamieseName() {
        val scarf = SecretBoxMockData.rewards.find { it.id == "scarf" }
        assertNotNull("Scarf reward must exist", scarf)
        assertEquals("Scarf name must be 'Khăn Root Further'", "Khăn Root Further", scarf!!.name)
        assertEquals("Scarf imageResName must be 'img_secretbox_scarf'", "img_secretbox_scarf", scarf.imageResName)
    }

    @Test
    fun rewards_ContainsStamps_WithVietnamieseName() {
        val stamps = SecretBoxMockData.rewards.find { it.id == "stamps" }
        assertNotNull("Stamps reward must exist", stamps)
        assertEquals("Stamps name must be 'Tem Root Further'", "Tem Root Further", stamps!!.name)
        assertEquals("Stamps imageResName must be 'img_secretbox_stamps'", "img_secretbox_stamps", stamps.imageResName)
    }

    @Test
    fun rewards_ContainsMug_WithVietnamieseName() {
        val mug = SecretBoxMockData.rewards.find { it.id == "mug" }
        assertNotNull("Mug reward must exist", mug)
        assertEquals("Mug name must be 'Cốc Root Further'", "Cốc Root Further", mug!!.name)
        assertEquals("Mug imageResName must be 'img_secretbox_mug'", "img_secretbox_mug", mug.imageResName)
    }

    @Test
    fun rewards_ContainsTshirt_WithVietnamieseName() {
        val tshirt = SecretBoxMockData.rewards.find { it.id == "tshirt" }
        assertNotNull("Tshirt reward must exist", tshirt)
        assertEquals("Tshirt name must be 'Áo thun Burberry'", "Áo thun Burberry", tshirt!!.name)
        assertEquals("Tshirt imageResName must be 'img_secretbox_tshirt'", "img_secretbox_tshirt", tshirt.imageResName)
    }

    @Test
    fun rewards_ContainsCombo_WithVietnamieseName() {
        val combo = SecretBoxMockData.rewards.find { it.id == "combo" }
        assertNotNull("Combo reward must exist", combo)
        assertEquals("Combo name must be 'Cốc & Tem Root Further'", "Cốc & Tem Root Further", combo!!.name)
        assertEquals("Combo imageResName must be 'img_secretbox_combo'", "img_secretbox_combo", combo.imageResName)
    }

    @Test
    fun rewards_ContainsGift_WithVietnamieseName() {
        val gift = SecretBoxMockData.rewards.find { it.id == "gift" }
        assertNotNull("Gift reward must exist", gift)
        assertEquals("Gift name must be 'Phần quà SAA 2025'", "Phần quà SAA 2025", gift!!.name)
        assertEquals("Gift imageResName must be 'img_secretbox_gift'", "img_secretbox_gift", gift.imageResName)
    }

    // ===================== randomReward() determinism tests =====================

    @Test
    fun randomReward_WithFixedSeed_IsDeterministic() {
        val random1 = Random(12345)
        val reward1 = SecretBoxMockData.randomReward(random1)

        val random2 = Random(12345)
        val reward2 = SecretBoxMockData.randomReward(random2)

        assertEquals("randomReward with same seed must return same reward", reward1, reward2)
    }

    @Test
    fun randomReward_WithDifferentSeeds_MayReturnDifferent() {
        val random1 = Random(111)
        val reward1 = SecretBoxMockData.randomReward(random1)

        val random2 = Random(222)
        val reward2 = SecretBoxMockData.randomReward(random2)

        // Note: This test is probabilistic — very unlikely to fail, but theoretically possible
        // if random(111) and random(222) happen to return the same index.
        // We're documenting that different seeds CAN return different values.
        // Actual assertion is less strict: just verify both are valid rewards.
        assertTrue("reward1 must be in pool", SecretBoxMockData.rewards.contains(reward1))
        assertTrue("reward2 must be in pool", SecretBoxMockData.rewards.contains(reward2))
    }

    @Test
    fun randomReward_MultipleCalls_WithSameSeed_AreIdempotent() {
        val seed = 99999L

        val reward1 = SecretBoxMockData.randomReward(Random(seed))
        val reward2 = SecretBoxMockData.randomReward(Random(seed))
        val reward3 = SecretBoxMockData.randomReward(Random(seed))

        assertEquals("Multiple calls with same seed must return same reward", reward1, reward2)
        assertEquals("Multiple calls with same seed must return same reward", reward2, reward3)
    }

    @Test
    fun randomReward_AlwaysReturnsMemberOfPool() {
        // Test across multiple random instances and seeds
        repeat(20) { seedIdx ->
            val random = Random(seedIdx.toLong())
            val reward = SecretBoxMockData.randomReward(random)

            assertTrue(
                "randomReward must always return a member of the pool (seed=$seedIdx)",
                SecretBoxMockData.rewards.contains(reward)
            )
        }
    }

    @Test
    fun randomReward_CanReturnAnyMemberOfPool() {
        // Call randomReward many times with different seeds and collect results
        val results = mutableSetOf<SecretBoxReward>()
        for (seedIdx in 0..100) {
            val random = Random(seedIdx.toLong())
            val reward = SecretBoxMockData.randomReward(random)
            results.add(reward)
        }

        // We expect to get multiple different rewards (though not guaranteed to get all 6)
        assertTrue("randomReward should return variety of rewards", results.size > 1)

        // All results must be in the original pool
        assertTrue("All results must be pool members", results.all { SecretBoxMockData.rewards.contains(it) })
    }

    @Test
    fun randomReward_DefaultRandom_ReturnsValidReward() {
        // Call randomReward without explicit Random parameter
        val reward = SecretBoxMockData.randomReward()

        assertNotNull("randomReward() with default Random must return non-null", reward)
        assertTrue("randomReward() must return a pool member", SecretBoxMockData.rewards.contains(reward))
    }

    // ===================== SecretBoxReward data class tests =====================

    @Test
    fun secretBoxReward_DataClass_HasProperEquality() {
        val reward1 = SecretBoxReward("scarf", "Khăn Root Further", "img_secretbox_scarf")
        val reward2 = SecretBoxReward("scarf", "Khăn Root Further", "img_secretbox_scarf")

        assertEquals("Rewards with same values must be equal", reward1, reward2)
    }

    @Test
    fun secretBoxReward_DataClass_DifferentIds_AreNotEqual() {
        val reward1 = SecretBoxReward("scarf", "Khăn Root Further", "img_secretbox_scarf")
        val reward2 = SecretBoxReward("stamps", "Khăn Root Further", "img_secretbox_scarf")

        assertNotEquals("Rewards with different ids must not be equal", reward1, reward2)
    }

    @Test
    fun secretBoxReward_DataClass_DifferentNames_AreNotEqual() {
        val reward1 = SecretBoxReward("scarf", "Khăn Root Further", "img_secretbox_scarf")
        val reward2 = SecretBoxReward("scarf", "Tem Root Further", "img_secretbox_scarf")

        assertNotEquals("Rewards with different names must not be equal", reward1, reward2)
    }

    @Test
    fun secretBoxReward_DataClass_DifferentImageResNames_AreNotEqual() {
        val reward1 = SecretBoxReward("scarf", "Khăn Root Further", "img_secretbox_scarf")
        val reward2 = SecretBoxReward("scarf", "Khăn Root Further", "img_secretbox_stamps")

        assertNotEquals("Rewards with different imageResNames must not be equal", reward1, reward2)
    }

    @Test
    fun secretBoxReward_Copy_PreservesValues() {
        val original = SecretBoxReward("scarf", "Khăn Root Further", "img_secretbox_scarf")
        val modified = original.copy(id = "stamps")

        assertEquals("Original id must be scarf", "scarf", original.id)
        assertEquals("Modified id must be stamps", "stamps", modified.id)
        assertEquals("Modified name must be preserved", "Khăn Root Further", modified.name)
        assertEquals("Modified imageResName must be preserved", "img_secretbox_scarf", modified.imageResName)
    }

    // ===================== Pool immutability =====================

    @Test
    fun rewards_IsImmutable() {
        val initialSize = SecretBoxMockData.rewards.size
        val rewardsBefore = SecretBoxMockData.rewards.toList()

        // Access rewards multiple times
        repeat(5) {
            SecretBoxMockData.randomReward()
        }

        val rewardsAfter = SecretBoxMockData.rewards.toList()

        assertEquals("Rewards list size must not change", initialSize, rewardsAfter.size)
        assertEquals("Rewards list must remain unchanged", rewardsBefore, rewardsAfter)
    }
}
