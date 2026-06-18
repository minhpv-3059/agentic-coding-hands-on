package com.sun.kudos_demo.feature.feed

import org.junit.Test
import org.junit.Assert.*

class KudoModelsTest {

    // ===================== starLevel tests =====================

    @Test
    fun starLevel_Zero() {
        assertEquals(0, starLevel(0))
    }

    @Test
    fun starLevel_Below10() {
        assertEquals(0, starLevel(5))
        assertEquals(0, starLevel(9))
    }

    @Test
    fun starLevel_Exactly10() {
        assertEquals(1, starLevel(10))
    }

    @Test
    fun starLevel_Between10And20() {
        assertEquals(1, starLevel(15))
        assertEquals(1, starLevel(19))
    }

    @Test
    fun starLevel_Exactly20() {
        assertEquals(2, starLevel(20))
    }

    @Test
    fun starLevel_Between20And50() {
        assertEquals(2, starLevel(30))
        assertEquals(2, starLevel(49))
    }

    @Test
    fun starLevel_Exactly50() {
        assertEquals(3, starLevel(50))
    }

    @Test
    fun starLevel_Above50() {
        assertEquals(3, starLevel(100))
        assertEquals(3, starLevel(1000))
    }

    @Test
    fun starLevel_MirrorKudosMockData() {
        // Verify against actual mock data
        assertEquals(3, starLevel(52))  // k1 recipient: 52 kudos → star 3
        assertEquals(2, starLevel(25))  // k2 recipient: 25 kudos → star 2
        assertEquals(1, starLevel(12))  // k3 recipient: 12 kudos → star 1
        assertEquals(2, starLevel(30))  // k4 recipient: 30 kudos → star 2
        assertEquals(0, starLevel(8))   // k5 recipient: 8 kudos → star 0
    }

    // ===================== senderDisplayName tests =====================

    @Test
    fun senderDisplayName_Regular_ShowsSenderName() {
        val sender = KudoUser(id = "u1", name = "John Doe", code = "EMP001", badge = null)
        val kudo = Kudo(
            id = "k1",
            sender = sender,
            recipient = KudoUser(id = "u2", name = "Jane Doe", code = "EMP002", badge = null),
            department = "CEVC2",
            timeRange = "10:00 - 10/30/2025",
            title = "Great Work",
            message = "Well done!",
            isAnonymous = false
        )
        assertEquals("John Doe", kudo.senderDisplayName)
    }

    @Test
    fun senderDisplayName_Anonymous_ShowsAlias() {
        val kudo = Kudo(
            id = "k1",
            sender = null,
            recipient = KudoUser(id = "u2", name = "Jane Doe", code = "EMP002", badge = null),
            department = "CEVC2",
            timeRange = "10:00 - 10/30/2025",
            title = "Great Work",
            message = "Well done!",
            isAnonymous = true,
            anonymousAlias = "Anh Hùng Xạ Điêu"
        )
        assertEquals("Anh Hùng Xạ Điêu", kudo.senderDisplayName)
    }

    @Test
    fun senderDisplayName_NullSender_EmptyString() {
        val kudo = Kudo(
            id = "k1",
            sender = null,
            recipient = KudoUser(id = "u2", name = "Jane Doe", code = "EMP002", badge = null),
            department = "CEVC2",
            timeRange = "10:00 - 10/30/2025",
            title = "Great Work",
            message = "Well done!",
            isAnonymous = false
        )
        assertEquals("", kudo.senderDisplayName)
    }

    @Test
    fun senderDisplayName_MirrorKudosMockData() {
        // k1: regular sender (nhat) → show name
        val k1 = KudosMockData.kudos[0]
        assertEquals("Huỳnh Dương Xuân Nhật", k1.senderDisplayName)

        // k4: anonymous (null sender) → show alias
        val k4 = KudosMockData.kudos[3]
        assertTrue(k4.isAnonymous)
        assertEquals("Anh Hùng Xạ Điêu", k4.senderDisplayName)
    }

    @Test
    fun senderDisplayName_CustomAlias() {
        val kudo = Kudo(
            id = "k1",
            sender = null,
            recipient = KudoUser(id = "u2", name = "Jane Doe", code = "EMP002", badge = null),
            department = "CEVC2",
            timeRange = "10:00 - 10/30/2025",
            title = "Great Work",
            message = "Well done!",
            isAnonymous = true,
            anonymousAlias = "Người Bí Ẩn"
        )
        assertEquals("Người Bí Ẩn", kudo.senderDisplayName)
    }
}
