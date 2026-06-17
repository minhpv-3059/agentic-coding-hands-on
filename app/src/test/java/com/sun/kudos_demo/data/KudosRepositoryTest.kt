package com.sun.kudos_demo.data

import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.KudosMockData
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for KudosRepository (Phase 06).
 *
 * Tests the in-memory store: adding kudos, lookup by id, and StateFlow emission.
 */
class KudosRepositoryTest {

    @Before
    fun setUp() {
        // KudosRepository is an app-process singleton that persists across tests.
        // Each test is order-independent: we assert based on relative list position
        // (new kudos prepend at index 0) rather than absolute size.
        // No reset needed — just ensure each test adds unique IDs to avoid conflicts.
    }

    // ===================== addKudo =====================

    @Test
    fun addKudo_PrependsNewKudo() {
        val beforeCount = KudosRepository.kudos.value.size
        val newKudo = createTestKudo(id = "new-prep-${System.nanoTime()}")

        KudosRepository.addKudo(newKudo)

        val kudos = KudosRepository.kudos.value
        assertEquals(beforeCount + 1, kudos.size)
        // New kudo should be at index 0 (prepended)
        assertEquals(newKudo, kudos[0])
    }

    @Test
    fun addKudo_PutsNewKudoAtIndex0() {
        val firstKudo = KudosRepository.kudos.value[0]

        val newKudo = createTestKudo(id = "new-idx0-${System.nanoTime()}")
        KudosRepository.addKudo(newKudo)

        val kudos = KudosRepository.kudos.value
        assertEquals(newKudo, kudos[0])
        assertEquals(firstKudo, kudos[1])
    }

    @Test
    fun addKudo_MultipleKudos_PrependsInOrder() {
        val nanoTime = System.nanoTime()
        val kudo1 = createTestKudo(id = "new-seq1-$nanoTime")
        val kudo2 = createTestKudo(id = "new-seq2-$nanoTime")
        val kudo3 = createTestKudo(id = "new-seq3-$nanoTime")

        KudosRepository.addKudo(kudo1)
        KudosRepository.addKudo(kudo2)
        KudosRepository.addKudo(kudo3)

        val kudos = KudosRepository.kudos.value
        // Most recent should be at index 0
        assertEquals("new-seq3-$nanoTime", kudos[0].id)
        assertEquals("new-seq2-$nanoTime", kudos[1].id)
        assertEquals("new-seq1-$nanoTime", kudos[2].id)
    }

    @Test
    fun addKudo_PreservesExistingKudos() {
        val initialKudos = KudosRepository.kudos.value.toList()
        val newKudo = createTestKudo(id = "new-preserv-${System.nanoTime()}")

        KudosRepository.addKudo(newKudo)

        val kudos = KudosRepository.kudos.value
        // All original kudos should still be present (but shifted by 1)
        for (i in initialKudos.indices) {
            assertEquals(initialKudos[i], kudos[i + 1])
        }
    }

    @Test
    fun addKudo_WithAnonymousKudo() {
        val anonymousKudo = createTestKudo(
            id = "new-anon-${System.nanoTime()}",
            sender = null,
            isAnonymous = true,
            anonymousAlias = "Mystery Person"
        )

        KudosRepository.addKudo(anonymousKudo)

        val stored = KudosRepository.kudos.value[0]
        assertNull(stored.sender)
        assertTrue(stored.isAnonymous)
        assertEquals("Mystery Person", stored.anonymousAlias)
    }

    @Test
    fun addKudo_WithMultipleHashtags() {
        val hashtags = listOf("BE OPTIMISTIC", "WASSHOI", "High-performing")
        val kudoWithTags = createTestKudo(id = "new-tags-${System.nanoTime()}", hashtags = hashtags)

        KudosRepository.addKudo(kudoWithTags)

        val stored = KudosRepository.kudos.value[0]
        assertEquals(hashtags, stored.hashtags)
    }

    @Test
    fun addKudo_WithImages() {
        val kudoWithImages = createTestKudo(id = "new-images-${System.nanoTime()}", imageCount = 3)

        KudosRepository.addKudo(kudoWithImages)

        val stored = KudosRepository.kudos.value[0]
        assertEquals(3, stored.imageCount)
    }

    // ===================== kudoById =====================

    @Test
    fun kudoById_FindsExistingKudoById() {
        val initialKudo = KudosRepository.kudos.value[0]

        val found = KudosRepository.kudoById(initialKudo.id)

        assertNotNull(found)
        assertEquals(initialKudo, found)
    }

    @Test
    fun kudoById_FindsNewlyAddedKudo() {
        val uniqueId = "unique-${System.nanoTime()}"
        val newKudo = createTestKudo(id = uniqueId)
        KudosRepository.addKudo(newKudo)

        val found = KudosRepository.kudoById(uniqueId)

        assertNotNull(found)
        assertEquals(newKudo, found)
    }

    @Test
    fun kudoById_ReturnsNullForUnknownId() {
        val found = KudosRepository.kudoById("nonexistent-id-xyz")

        assertNull(found)
    }

    @Test
    fun kudoById_FindsFromMiddleOfList() {
        val nanoTime = System.nanoTime()
        val kudo1 = createTestKudo(id = "mid1-$nanoTime")
        val kudo2 = createTestKudo(id = "mid2-$nanoTime")
        val kudo3 = createTestKudo(id = "mid3-$nanoTime")

        KudosRepository.addKudo(kudo1)
        KudosRepository.addKudo(kudo2)
        KudosRepository.addKudo(kudo3)

        // kudo2 should be in the middle
        val found = KudosRepository.kudoById("mid2-$nanoTime")

        assertNotNull(found)
        assertEquals(kudo2, found)
    }

    @Test
    fun kudoById_FindsMultipleTimesConsistently() {
        val consistentId = "consistent-${System.nanoTime()}"
        val newKudo = createTestKudo(id = consistentId)
        KudosRepository.addKudo(newKudo)

        val first = KudosRepository.kudoById(consistentId)
        val second = KudosRepository.kudoById(consistentId)

        assertEquals(first, second)
        assertEquals(newKudo, first)
    }

    @Test
    fun kudoById_FindsAnonymousKudo() {
        val anonId = "anon-${System.nanoTime()}"
        val anonymousKudo = createTestKudo(
            id = anonId,
            sender = null,
            isAnonymous = true
        )
        KudosRepository.addKudo(anonymousKudo)

        val found = KudosRepository.kudoById(anonId)

        assertNotNull(found)
        assertTrue(found!!.isAnonymous)
        assertNull(found.sender)
    }

    @Test
    fun kudoById_IdSearchIsCaseSensitive() {
        val nanoTime = System.nanoTime()
        val newKudo = createTestKudo(id = "case-ABC-$nanoTime")
        KudosRepository.addKudo(newKudo)

        val foundExact = KudosRepository.kudoById("case-ABC-$nanoTime")
        val foundWrong = KudosRepository.kudoById("case-abc-$nanoTime")

        assertNotNull(foundExact)
        assertNull(foundWrong)
    }

    // ===================== StateFlow Emission =====================

    @Test
    fun kudos_StateFlow_InitializedWithMockData() {
        val initialKudos = KudosRepository.kudos.value
        assertNotNull(initialKudos)
        assertTrue(initialKudos.isNotEmpty())
        // KudosMockData.kudos should have at least 8 items
        assertTrue(initialKudos.size >= 8)
    }

    @Test
    fun kudos_StateFlow_UpdatesAfterAddKudo() {
        val initialValue = KudosRepository.kudos.value
        val newKudo = createTestKudo(id = "flow-test-${System.nanoTime()}")

        KudosRepository.addKudo(newKudo)

        val updatedValue = KudosRepository.kudos.value
        assertNotEquals(initialValue, updatedValue)
        assertEquals(initialValue.size + 1, updatedValue.size)
        assertEquals(newKudo, updatedValue[0])
    }

    @Test
    fun kudos_StateFlow_SequentialUpdates() {
        val initial = KudosRepository.kudos.value.size
        val nanoTime = System.nanoTime()

        KudosRepository.addKudo(createTestKudo(id = "seq-1-$nanoTime"))
        assertEquals(initial + 1, KudosRepository.kudos.value.size)

        KudosRepository.addKudo(createTestKudo(id = "seq-2-$nanoTime"))
        assertEquals(initial + 2, KudosRepository.kudos.value.size)

        KudosRepository.addKudo(createTestKudo(id = "seq-3-$nanoTime"))
        assertEquals(initial + 3, KudosRepository.kudos.value.size)
    }

    // ===================== Helper =====================

    private fun createTestKudo(
        id: String = "test-kudo-${System.currentTimeMillis()}",
        sender: KudoUser? = KudoUser(id = "u1", name = "Test Sender", code = "TEST", badge = null),
        recipient: KudoUser = KudoUser(id = "u2", name = "Test Recipient", code = "TEST2", badge = null),
        message: String = "Test message",
        hashtags: List<String> = listOf("BE OPTIMISTIC"),
        imageCount: Int = 0,
        isAnonymous: Boolean = false,
        anonymousAlias: String = "Test Alias"
    ): Kudo = Kudo(
        id = id,
        sender = sender,
        recipient = recipient,
        department = recipient.code,
        timeRange = "10:00 - 10/30/2025",
        title = "TEST TITLE",
        message = message,
        hashtags = hashtags,
        heartCount = 0,
        imageCount = imageCount,
        recipientKudosCount = 1,
        isAnonymous = isAnonymous,
        anonymousAlias = anonymousAlias
    )
}
