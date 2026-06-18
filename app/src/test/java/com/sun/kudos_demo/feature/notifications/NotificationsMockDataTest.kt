package com.sun.kudos_demo.feature.notifications

import com.sun.kudos_demo.R
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for NotificationsMockData (Phase 08).
 *
 * Verifies the seed notifications list matches the design spec:
 * - Exactly 7 items, one per NotificationType in declared order
 * - Only the first item (n1, type KUDOS_RECEIVED) is unread
 * - Kudo-opening types have non-null targetId; others may be null
 * - No blank messages or times
 */
class NotificationsMockDataTest {

    @Test
    fun seed_HasExactly7Notifications() {
        val notifications = NotificationsMockData.notifications
        assertEquals(7, notifications.size)
    }

    @Test
    fun seed_ContainsAllNotificationTypes() {
        val notifications = NotificationsMockData.notifications
        val types = notifications.map { it.type }

        // All 7 types must be present
        assertTrue(types.contains(NotificationType.KUDOS_RECEIVED))
        assertTrue(types.contains(NotificationType.HEART_RECEIVED))
        assertTrue(types.contains(NotificationType.SECRET_BOX))
        assertTrue(types.contains(NotificationType.LEVEL_UP))
        assertTrue(types.contains(NotificationType.CONTENT_HIDDEN))
        assertTrue(types.contains(NotificationType.BADGE_COLLECTED))
        assertTrue(types.contains(NotificationType.REVIEW_REQUEST))
    }

    @Test
    fun seed_TypesAreInDeclaredOrder() {
        val notifications = NotificationsMockData.notifications
        val types = notifications.map { it.type }

        // Order matches the enum declaration
        assertEquals(NotificationType.KUDOS_RECEIVED, types[0])
        assertEquals(NotificationType.HEART_RECEIVED, types[1])
        assertEquals(NotificationType.SECRET_BOX, types[2])
        assertEquals(NotificationType.LEVEL_UP, types[3])
        assertEquals(NotificationType.CONTENT_HIDDEN, types[4])
        assertEquals(NotificationType.BADGE_COLLECTED, types[5])
        assertEquals(NotificationType.REVIEW_REQUEST, types[6])
    }

    @Test
    fun seed_OnlyFirstItemIsUnread() {
        val notifications = NotificationsMockData.notifications

        // n1 (index 0) must be unread
        assertFalse(notifications[0].isRead)

        // All others must be read
        for (i in 1 until notifications.size) {
            assertTrue("Item at index $i should be read", notifications[i].isRead)
        }
    }

    @Test
    fun seed_FirstUnreadItemIsKudosReceived() {
        val notifications = NotificationsMockData.notifications
        val firstItem = notifications[0]

        assertEquals("n1", firstItem.id)
        assertEquals(NotificationType.KUDOS_RECEIVED, firstItem.type)
        assertFalse(firstItem.isRead)
    }

    @Test
    fun seed_KudoOpeningTypesHaveTargetId() {
        val notifications = NotificationsMockData.notifications

        // Types that open a kudo: KUDOS_RECEIVED, HEART_RECEIVED, CONTENT_HIDDEN
        val kudoOpeningTypes = setOf(
            NotificationType.KUDOS_RECEIVED,
            NotificationType.HEART_RECEIVED,
            NotificationType.CONTENT_HIDDEN
        )

        for (notif in notifications) {
            if (notif.type in kudoOpeningTypes) {
                assertNotNull("Notification type ${notif.type} (id=${notif.id}) must have targetId", notif.targetId)
            }
        }
    }

    @Test
    fun seed_OtherTypesCanHaveNullTargetId() {
        val notifications = NotificationsMockData.notifications

        // Types that do NOT open a kudo
        val nonKudoTypes = setOf(
            NotificationType.SECRET_BOX,
            NotificationType.LEVEL_UP,
            NotificationType.BADGE_COLLECTED,
            NotificationType.REVIEW_REQUEST
        )

        // These items should NOT have targetId (or if they do, it doesn't matter for this test)
        for (notif in notifications) {
            if (notif.type in nonKudoTypes) {
                assertNull("Notification type ${notif.type} (id=${notif.id}) should not have targetId", notif.targetId)
            }
        }
    }

    @Test
    fun seed_NoBlankMessages() {
        val notifications = NotificationsMockData.notifications

        for (notif in notifications) {
            assertFalse("Message for id=${notif.id} must not be blank", notif.message.isBlank())
            assertTrue("Message for id=${notif.id} must have non-zero length", notif.message.length > 0)
        }
    }

    @Test
    fun seed_AllTimeResourcesAreSet() {
        val notifications = NotificationsMockData.notifications

        for (notif in notifications) {
            assertNotEquals("timeRes for id=${notif.id} must be set", 0, notif.timeRes)
        }
    }

    @Test
    fun seed_TimeResourcesAreDistinct() {
        val notifications = NotificationsMockData.notifications
        val timeResources = notifications.map { it.timeRes }

        // Verify that we have distinct resources (at least 2+ distinct values across 7 notifications)
        assertTrue("Time resources should have multiple distinct values", timeResources.distinct().size >= 2)
    }

    @Test
    fun seed_AllIdsAreUnique() {
        val notifications = NotificationsMockData.notifications
        val ids = notifications.map { it.id }

        // Convert to set to remove duplicates, compare size
        assertEquals("All notification IDs must be unique", ids.size, ids.toSet().size)
    }

    @Test
    fun seed_FirstItemHasTargetId() {
        val firstNotif = NotificationsMockData.notifications[0]

        // n1 is KUDOS_RECEIVED, so it must have targetId
        assertNotNull(firstNotif.targetId)
        assertEquals("k7", firstNotif.targetId)
    }

    @Test
    fun seed_CountUnreadItemsMatchesDesign() {
        val notifications = NotificationsMockData.notifications
        val unreadCount = notifications.count { !it.isRead }

        // Design specifies exactly 1 unread (the red dot on n1)
        assertEquals(1, unreadCount)
    }
}
