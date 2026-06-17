package com.sun.kudos_demo.feature.profile

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ProfileModels (Phase 07).
 *
 * Validates the domain models: ProfileKudosTab, ProfileStats, AwardBadge.
 */
class ProfileModelsTest {

    // ===================== ProfileKudosTab tests =====================

    @Test
    fun profileKudosTab_HasReceivedValue() {
        val tab = ProfileKudosTab.RECEIVED
        assertEquals(ProfileKudosTab.RECEIVED, tab)
    }

    @Test
    fun profileKudosTab_HasSentValue() {
        val tab = ProfileKudosTab.SENT
        assertEquals(ProfileKudosTab.SENT, tab)
    }

    @Test
    fun profileKudosTab_BothValuesDistinct() {
        assertNotEquals(ProfileKudosTab.RECEIVED, ProfileKudosTab.SENT)
    }

    @Test
    fun profileKudosTab_CanBeUsedInComparison() {
        val filterReceived = ProfileKudosTab.RECEIVED
        val filterSent = ProfileKudosTab.SENT
        assertTrue(filterReceived == ProfileKudosTab.RECEIVED)
        assertTrue(filterSent == ProfileKudosTab.SENT)
        assertFalse(filterReceived == filterSent)
    }

    // ===================== ProfileStats tests =====================

    @Test
    fun profileStats_ConstructorSetsAllFields() {
        val stats = ProfileStats(
            kudosReceived = 10,
            kudosSent = 20,
            heartsReceived = 30,
            secretBoxOpened = 40,
            secretBoxUnopened = 50
        )
        assertEquals(10, stats.kudosReceived)
        assertEquals(20, stats.kudosSent)
        assertEquals(30, stats.heartsReceived)
        assertEquals(40, stats.secretBoxOpened)
        assertEquals(50, stats.secretBoxUnopened)
    }

    @Test
    fun profileStats_EqualsAndHashCode() {
        val stats1 = ProfileStats(5, 10, 15, 20, 25)
        val stats2 = ProfileStats(5, 10, 15, 20, 25)
        assertEquals(stats1, stats2)
        assertEquals(stats1.hashCode(), stats2.hashCode())
    }

    @Test
    fun profileStats_NotEqualsWhenFieldsDiffer() {
        val stats1 = ProfileStats(5, 10, 15, 20, 25)
        val stats2 = ProfileStats(5, 10, 15, 20, 26)  // secretBoxUnopened differs
        assertNotEquals(stats1, stats2)
    }

    @Test
    fun profileStats_ZeroValues() {
        val stats = ProfileStats(0, 0, 0, 0, 0)
        assertEquals(0, stats.kudosReceived)
        assertEquals(0, stats.kudosSent)
        assertEquals(0, stats.heartsReceived)
        assertEquals(0, stats.secretBoxOpened)
        assertEquals(0, stats.secretBoxUnopened)
    }

    @Test
    fun profileStats_LargeValues() {
        val stats = ProfileStats(1000, 2000, 3000, 4000, 5000)
        assertEquals(1000, stats.kudosReceived)
        assertEquals(2000, stats.kudosSent)
        assertEquals(3000, stats.heartsReceived)
        assertEquals(4000, stats.secretBoxOpened)
        assertEquals(5000, stats.secretBoxUnopened)
    }

    @Test
    fun profileStats_Copy() {
        val original = ProfileStats(5, 10, 15, 20, 25)
        val modified = original.copy(kudosReceived = 6)
        assertEquals(5, original.kudosReceived)
        assertEquals(6, modified.kudosReceived)
        assertEquals(10, modified.kudosSent)  // Other fields unchanged
    }

    // ===================== AwardBadge tests =====================

    @Test
    fun awardBadge_ConstructorWithId_Label_Icon() {
        val badge = AwardBadge("badge_id", "Badge Label", 123)
        assertEquals("badge_id", badge.id)
        assertEquals("Badge Label", badge.label)
        assertEquals(123, badge.icon)
    }

    @Test
    fun awardBadge_ConstructorWithNullIcon() {
        val badge = AwardBadge("badge_id", "Badge Label", null)
        assertEquals("badge_id", badge.id)
        assertEquals("Badge Label", badge.label)
        assertNull(badge.icon)
    }

    @Test
    fun awardBadge_DefaultIconIsNull() {
        // Constructor with 2 args should have icon = null
        val badge = AwardBadge("badge_id", "Badge Label")
        assertEquals("badge_id", badge.id)
        assertEquals("Badge Label", badge.label)
        assertNull(badge.icon)
    }

    @Test
    fun awardBadge_EqualsAndHashCode() {
        val badge1 = AwardBadge("id1", "Label", 100)
        val badge2 = AwardBadge("id1", "Label", 100)
        assertEquals(badge1, badge2)
        assertEquals(badge1.hashCode(), badge2.hashCode())
    }

    @Test
    fun awardBadge_NotEqualsWhenIdDiffers() {
        val badge1 = AwardBadge("id1", "Label", 100)
        val badge2 = AwardBadge("id2", "Label", 100)
        assertNotEquals(badge1, badge2)
    }

    @Test
    fun awardBadge_NotEqualsWhenLabelDiffers() {
        val badge1 = AwardBadge("id1", "Label1", 100)
        val badge2 = AwardBadge("id1", "Label2", 100)
        assertNotEquals(badge1, badge2)
    }

    @Test
    fun awardBadge_NotEqualsWhenIconDiffers() {
        val badge1 = AwardBadge("id1", "Label", 100)
        val badge2 = AwardBadge("id1", "Label", 101)
        assertNotEquals(badge1, badge2)
    }

    @Test
    fun awardBadge_NullIconNotEqualToNonNull() {
        val badge1 = AwardBadge("id1", "Label", null)
        val badge2 = AwardBadge("id1", "Label", 100)
        assertNotEquals(badge1, badge2)
    }

    @Test
    fun awardBadge_Copy() {
        val original = AwardBadge("badge_id", "Badge Label", 123)
        val modified = original.copy(label = "New Label")
        assertEquals("badge_id", original.id)
        assertEquals("Badge Label", original.label)
        assertEquals(123, original.icon)

        assertEquals("badge_id", modified.id)
        assertEquals("New Label", modified.label)
        assertEquals(123, modified.icon)  // Icon unchanged
    }

    @Test
    fun awardBadge_CopyWithNullIcon() {
        val original = AwardBadge("badge_id", "Badge Label", 123)
        val modified = original.copy(icon = null)
        assertEquals(123, original.icon)
        assertNull(modified.icon)
    }

    @Test
    fun awardBadge_EmptyStrings() {
        val badge = AwardBadge("", "", null)
        assertEquals("", badge.id)
        assertEquals("", badge.label)
    }

    @Test
    fun awardBadge_UnicodeLabel() {
        val badge = AwardBadge("id", "REVIVAL - Sử Dụng Tính Năng LIÊN MINH", 100)
        assertEquals("REVIVAL - Sử Dụng Tính Năng LIÊN MINH", badge.label)
    }
}
