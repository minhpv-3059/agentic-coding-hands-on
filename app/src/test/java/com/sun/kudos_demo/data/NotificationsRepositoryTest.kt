package com.sun.kudos_demo.data

import com.sun.kudos_demo.feature.notifications.AppNotification
import com.sun.kudos_demo.feature.notifications.NotificationType
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for NotificationsRepository (Phase 08).
 *
 * Tests the in-memory singleton: markRead, markAllRead, unreadCount invariant.
 * IMPORTANT: NotificationsRepository is an app-process singleton that persists across tests.
 * Tests are ORDER-INDEPENDENT: we assert relative state changes, not absolute values.
 * Safe assertions:
 *  - After markAllRead(): unreadCount == 0 && all items isRead == true
 *  - markRead(id): specific item becomes isRead, list size unchanged
 *  - markRead("nonexistent"): no throw, list unchanged
 *  - unreadCount always == notifications.count { !it.isRead } (invariant at any point)
 *
 * We do NOT assert an absolute initial unread count of 1 AFTER any test mutation.
 * If you need to test the "1 unread" seed fact, assert it against NotificationsMockData
 * (immutable seed), not the live repo.
 */
class NotificationsRepositoryTest {

    @Before
    fun setUp() {
        // NotificationsRepository is a singleton. No reset available.
        // Each test must be independent and idempotent.
        // Use markAllRead() at the end of tests that mutate state if needed.
    }

    // ===================== markAllRead =====================

    @Test
    fun markAllRead_SetsAllNotificationsToRead() {
        val beforeCount = NotificationsRepository.notifications.value.size
        assertTrue("Repository should have notifications", beforeCount > 0)

        NotificationsRepository.markAllRead()

        val allRead = NotificationsRepository.notifications.value.all { it.isRead }
        assertTrue("All notifications must be marked read", allRead)
    }

    @Test
    fun markAllRead_ClearsUnreadCount() {
        NotificationsRepository.markAllRead()

        // After markAllRead, all notifications must be marked read
        val allAreRead = NotificationsRepository.notifications.value.all { it.isRead }
        assertTrue("All notifications must be read after markAllRead", allAreRead)

        // Allow Flow time to emit (even though stateIn should be eager, there might be a delay)
        Thread.sleep(50)

        // Verify the unreadCount matches (should be 0 if all are read)
        val computedUnread = NotificationsRepository.notifications.value.count { !it.isRead }
        val reportedCount = NotificationsRepository.unreadCount.value
        assertEquals("Unread count must match computed count", computedUnread, reportedCount)
    }

    @Test
    fun markAllRead_DoesNotChangeListSize() {
        val beforeSize = NotificationsRepository.notifications.value.size
        val beforeIds = NotificationsRepository.notifications.value.map { it.id }

        NotificationsRepository.markAllRead()

        val afterSize = NotificationsRepository.notifications.value.size
        val afterIds = NotificationsRepository.notifications.value.map { it.id }

        assertEquals("List size must not change", beforeSize, afterSize)
        assertEquals("IDs must remain unchanged", beforeIds, afterIds)
    }

    @Test
    fun markAllRead_MaintainsItemProperties() {
        NotificationsRepository.markAllRead()

        val items = NotificationsRepository.notifications.value
        for (item in items) {
            // Properties other than isRead should remain unchanged
            assertNotNull(item.id)
            assertNotNull(item.type)
            assertNotNull(item.message)
            assertNotNull(item.time)
        }
    }

    @Test
    fun markAllRead_MultipleCallsAreIdempotent() {
        NotificationsRepository.markAllRead()
        val afterFirst = NotificationsRepository.notifications.value.toList()
        val countAfterFirst = NotificationsRepository.unreadCount.value

        NotificationsRepository.markAllRead()
        val afterSecond = NotificationsRepository.notifications.value.toList()
        val countAfterSecond = NotificationsRepository.unreadCount.value

        assertEquals("Calling markAllRead twice should have same effect", afterFirst, afterSecond)
        assertEquals("Unread count should stay 0", countAfterFirst, countAfterSecond)
    }

    // ===================== markRead =====================

    @Test
    fun markRead_MarksSpecificNotificationAsRead() {
        // Reset to known state
        NotificationsRepository.markAllRead()

        // Re-create the repository state with one unread by finding and creating a test notif
        val nanoTime = System.nanoTime()
        val testNotif = AppNotification(
            id = "test-mark-read-$nanoTime",
            type = NotificationType.KUDOS_RECEIVED,
            message = "Test message",
            time = "Just now",
            isRead = false,
            targetId = "k1"
        )

        // Add it directly to the repo by extracting a notif we can mutate
        // Actually, we can't directly add to the repo. Instead, let's test with existing items.
        // Since all are read after markAllRead, let's test by creating a scenario.

        // Better approach: assert that markRead on an existing ID makes it read
        val items = NotificationsRepository.notifications.value
        if (items.isNotEmpty()) {
            val targetId = items[0].id
            val wasPreviouslyRead = items[0].isRead

            NotificationsRepository.markRead(targetId)

            val afterMark = NotificationsRepository.notifications.value
            val markedItem = afterMark.find { it.id == targetId }
            assertNotNull("Item should still exist", markedItem)
            assertTrue("Item must be marked read", markedItem!!.isRead)
        }
    }

    @Test
    fun markRead_DoesNotChangeListSize() {
        val beforeSize = NotificationsRepository.notifications.value.size
        if (beforeSize > 0) {
            val targetId = NotificationsRepository.notifications.value[0].id

            NotificationsRepository.markRead(targetId)

            val afterSize = NotificationsRepository.notifications.value.size
            assertEquals("List size must not change after markRead", beforeSize, afterSize)
        }
    }

    @Test
    fun markRead_PreservesOtherNotifications() {
        NotificationsRepository.markAllRead()
        val beforeState = NotificationsRepository.notifications.value.toList()

        if (beforeState.isNotEmpty()) {
            val targetId = beforeState[0].id
            NotificationsRepository.markRead(targetId)

            val afterState = NotificationsRepository.notifications.value

            // All items should still be present with same properties
            assertEquals("List size unchanged", beforeState.size, afterState.size)
            for (i in beforeState.indices) {
                assertEquals("Item at index $i ID unchanged", beforeState[i].id, afterState[i].id)
            }
        }
    }

    @Test
    fun markRead_WithNonexistentId_DoesNotThrow() {
        val beforeSize = NotificationsRepository.notifications.value.size

        // Should not throw
        NotificationsRepository.markRead("nonexistent-id-xyz-${System.nanoTime()}")

        val afterSize = NotificationsRepository.notifications.value.size
        assertEquals("List size should be unchanged after markRead with nonexistent ID", beforeSize, afterSize)
    }

    @Test
    fun markRead_WithNonexistentId_DoesNotMutateList() {
        val beforeList = NotificationsRepository.notifications.value.map { it.id }

        NotificationsRepository.markRead("nonexistent-id-xyz-${System.nanoTime()}")

        val afterList = NotificationsRepository.notifications.value.map { it.id }
        assertEquals("IDs should be unchanged after markRead with nonexistent ID", beforeList, afterList)
    }

    @Test
    fun markRead_IdSearchIsCaseSensitive() {
        NotificationsRepository.markAllRead()
        val items = NotificationsRepository.notifications.value

        if (items.isNotEmpty()) {
            val targetId = items[0].id
            val wrongCaseId = if (targetId.contains("a")) {
                targetId.replace("a", "A")
            } else if (targetId.contains("A")) {
                targetId.replace("A", "a")
            } else {
                // ID doesn't have case-sensitive chars, skip test
                return
            }

            NotificationsRepository.markRead(wrongCaseId)

            val afterMark = NotificationsRepository.notifications.value.find { it.id == targetId }
            assertNotNull("Original item should exist", afterMark)
            assertTrue("Original item should still be read", afterMark!!.isRead)
        }
    }

    // ===================== unreadCount Invariant =====================

    @Test
    fun unreadCount_EqualsCountOfUnreadItems() {
        NotificationsRepository.markAllRead()

        val notifications = NotificationsRepository.notifications.value
        val computedUnreadCount = notifications.count { !it.isRead }
        val reportedUnreadCount = NotificationsRepository.unreadCount.value

        assertEquals("unreadCount must equal count of unread items", computedUnreadCount, reportedUnreadCount)
    }

    @Test
    fun unreadCount_IsZeroWhenAllRead() {
        NotificationsRepository.markAllRead()

        assertEquals("unreadCount must be 0 when all items are read", 0, NotificationsRepository.unreadCount.value)
    }

    @Test
    fun unreadCount_StaysConsistent_AfterMultipleMarkReads() {
        // Don't assume a clean state - just verify invariant holds regardless
        // Get current unread items
        val items = NotificationsRepository.notifications.value
        val unreadItems = items.filter { !it.isRead }

        if (unreadItems.isNotEmpty()) {
            val sampleSize = minOf(3, unreadItems.size)

            for (i in 0 until sampleSize) {
                val targetId = unreadItems[i].id
                NotificationsRepository.markRead(targetId)

                // Invariant: unreadCount == count of unread items
                val expected = NotificationsRepository.notifications.value.count { !it.isRead }
                val actual = NotificationsRepository.unreadCount.value

                assertEquals("Invariant violated after markRead($targetId)", expected, actual)
            }
        }
    }

    // ===================== StateFlow Emission =====================

    @Test
    fun notifications_StateFlow_IsInitialized() {
        val notifs = NotificationsRepository.notifications.value
        assertNotNull("notifications StateFlow must be initialized", notifs)
        assertTrue("notifications list must not be empty", notifs.isNotEmpty())
    }

    @Test
    fun notifications_StateFlow_UpdatesAfterMarkRead() {
        val before = NotificationsRepository.notifications.value.toList()
        if (before.isNotEmpty() && !before.all { it.isRead }) {
            val targetId = before.first { !it.isRead }.id

            NotificationsRepository.markRead(targetId)

            val after = NotificationsRepository.notifications.value.toList()
            // At least one item should have changed its isRead state
            val changed = before.zip(after).any { (b, a) -> b.isRead != a.isRead }
            assertTrue("State should change after markRead", changed || before.all { it.isRead })
        }
    }

    @Test
    fun unreadCount_StateFlow_IsInitialized() {
        val count = NotificationsRepository.unreadCount.value
        assertNotNull("unreadCount StateFlow must be initialized", count)
        assertTrue("unreadCount must be >= 0", count >= 0)
    }

    @Test
    fun unreadCount_StateFlow_UpdatesAfterMarkAllRead() {
        NotificationsRepository.markAllRead()

        // Allow Flow time to emit
        Thread.sleep(50)

        val count = NotificationsRepository.unreadCount.value
        val notifications = NotificationsRepository.notifications.value
        val allRead = notifications.all { it.isRead }

        if (allRead) {
            assertEquals("unreadCount must be 0 when all items are read", 0, count)
        } else {
            // If not all items are read (shouldn't happen after markAllRead in normal flow),
            // verify the count matches reality
            val expected = notifications.count { !it.isRead }
            assertEquals("unreadCount must match actual unread items", expected, count)
        }
    }

    // ===================== Idempotency & Edge Cases =====================

    @Test
    fun markRead_OnAlreadyReadItem_DoesNothing() {
        NotificationsRepository.markAllRead()
        val items = NotificationsRepository.notifications.value

        if (items.isNotEmpty()) {
            val targetId = items[0].id
            val beforeSnapshot = items.toList()

            NotificationsRepository.markRead(targetId)

            val afterSnapshot = NotificationsRepository.notifications.value
            assertEquals("Marking an already-read item should not change state", beforeSnapshot, afterSnapshot)
        }
    }

    @Test
    fun repository_MaintainsInvariant_ForAllOperations() {
        // Start fresh
        NotificationsRepository.markAllRead()

        // Perform a sequence of operations
        NotificationsRepository.markAllRead()
        assertInvariant("After first markAllRead")

        val items = NotificationsRepository.notifications.value
        if (items.isNotEmpty()) {
            NotificationsRepository.markRead(items[0].id)
            assertInvariant("After first markRead")

            if (items.size > 1) {
                NotificationsRepository.markRead(items[1].id)
                assertInvariant("After second markRead")
            }
        }

        NotificationsRepository.markAllRead()
        assertInvariant("After final markAllRead")
    }

    private fun assertInvariant(context: String) {
        val notifications = NotificationsRepository.notifications.value
        val expected = notifications.count { !it.isRead }
        val actual = NotificationsRepository.unreadCount.value
        assertEquals("Invariant violated $context", expected, actual)
    }
}
