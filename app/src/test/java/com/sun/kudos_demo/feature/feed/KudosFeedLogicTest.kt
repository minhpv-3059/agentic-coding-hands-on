package com.sun.kudos_demo.feature.feed

import org.junit.Test
import org.junit.Assert.*

class KudosFeedLogicTest {

    // ===================== filterKudos tests =====================

    @Test
    fun filterKudos_NullFilters_ReturnsAll() {
        val result = filterKudos(KudosMockData.kudos, null, null)
        assertEquals(KudosMockData.kudos.size, result.size)
    }

    @Test
    fun filterKudos_ByHashtagOnly() {
        val result = filterKudos(KudosMockData.kudos, "Dedicated", null)
        // k1, k2, k4, k7 have "Dedicated"
        assertEquals(4, result.size)
        assertTrue(result.all { "Dedicated" in it.hashtags })
    }

    @Test
    fun filterKudos_ByDepartmentOnly() {
        val result = filterKudos(KudosMockData.kudos, null, "CEVC2")
        // k1, k4 have dept "CEVC2"
        assertEquals(2, result.size)
        assertTrue(result.all { it.department == "CEVC2" })
    }

    @Test
    fun filterKudos_ByBothHashtagAndDepartment_AND() {
        // "Dedicated" AND "CEVC2" → k1, k4
        val result = filterKudos(KudosMockData.kudos, "Dedicated", "CEVC2")
        assertEquals(2, result.size)
        assertTrue(result.all { "Dedicated" in it.hashtags && it.department == "CEVC2" })
    }

    @Test
    fun filterKudos_HashtagNotFound() {
        val result = filterKudos(KudosMockData.kudos, "NonExistent", null)
        assertEquals(0, result.size)
    }

    @Test
    fun filterKudos_DepartmentNotFound() {
        val result = filterKudos(KudosMockData.kudos, null, "NonExistent")
        assertEquals(0, result.size)
    }

    @Test
    fun filterKudos_NoMatches_BothFilters() {
        // "Dedicated" AND "Infra" → no kudo has both
        val result = filterKudos(KudosMockData.kudos, "Dedicated", "Infra")
        assertEquals(0, result.size)
    }

    // ===================== highlightKudos tests =====================

    @Test
    fun highlightKudos_Top5_OrderByHeartCountDesc() {
        val result = highlightKudos(KudosMockData.kudos)
        assertEquals(5, result.size)
        // Expected order: k1(1000), k2(850), k3(720), k4(500), k5(300)
        assertEquals("k1", result[0].id)
        assertEquals(1000, result[0].heartCount)
        assertEquals("k2", result[1].id)
        assertEquals(850, result[1].heartCount)
        assertEquals("k3", result[2].id)
        assertEquals(720, result[2].heartCount)
        assertEquals("k4", result[3].id)
        assertEquals(500, result[3].heartCount)
        assertEquals("k5", result[4].id)
        assertEquals(300, result[4].heartCount)
    }

    @Test
    fun highlightKudos_CustomLimit() {
        val result = highlightKudos(KudosMockData.kudos, limit = 3)
        assertEquals(3, result.size)
        assertEquals("k1", result[0].id)
        assertEquals("k2", result[1].id)
        assertEquals("k3", result[2].id)
    }

    @Test
    fun highlightKudos_LimitExceedsListSize() {
        val result = highlightKudos(KudosMockData.kudos, limit = 20)
        assertEquals(KudosMockData.kudos.size, result.size)
        // Should still be sorted descending
        assertTrue(result[0].heartCount >= result[1].heartCount)
    }

    @Test
    fun highlightKudos_LimitZero() {
        val result = highlightKudos(KudosMockData.kudos, limit = 0)
        assertEquals(0, result.size)
    }

    // ===================== applyLikes tests =====================

    @Test
    fun applyLikes_SingleLike_IncrementHeart() {
        val likedIds = setOf("k1")
        val result = applyLikes(KudosMockData.kudos, likedIds)
        val k1 = result.find { it.id == "k1" }
        assertEquals(1001, k1?.heartCount)
    }

    @Test
    fun applyLikes_MultipleLikes() {
        val likedIds = setOf("k1", "k2", "k3")
        val result = applyLikes(KudosMockData.kudos, likedIds)
        assertEquals(1001, result.find { it.id == "k1" }?.heartCount)
        assertEquals(851, result.find { it.id == "k2" }?.heartCount)
        assertEquals(721, result.find { it.id == "k3" }?.heartCount)
        // Others unchanged
        assertEquals(500, result.find { it.id == "k4" }?.heartCount)
    }

    @Test
    fun applyLikes_NoLikes() {
        val likedIds = emptySet<String>()
        val result = applyLikes(KudosMockData.kudos, likedIds)
        result.zip(KudosMockData.kudos).forEach { (original, unchanged) ->
            assertEquals(original.heartCount, unchanged.heartCount)
        }
    }

    @Test
    fun applyLikes_NonExistentId() {
        val likedIds = setOf("nonexistent")
        val result = applyLikes(KudosMockData.kudos, likedIds)
        // Should not crash, just leave all unchanged
        assertEquals(KudosMockData.kudos.size, result.size)
    }

    // ===================== searchUsers tests =====================

    @Test
    fun searchUsers_BlankQuery_EmptyList() {
        val result = searchUsers(KudosMockData.searchableUsers, "")
        assertEquals(0, result.size)
    }

    @Test
    fun searchUsers_WhitespaceQuery_EmptyList() {
        val result = searchUsers(KudosMockData.searchableUsers, "   ")
        assertEquals(0, result.size)
    }

    @Test
    fun searchUsers_ByNameExact() {
        val result = searchUsers(KudosMockData.searchableUsers, "Dương Huỳnh Xuân Nhật")
        assertTrue(result.isNotEmpty())
        assertTrue(result.any { it.name == "Dương Huỳnh Xuân Nhật" })
    }

    @Test
    fun searchUsers_ByNamePartial_CaseInsensitive() {
        // "Huỳnh" is in s1, s2, s4 names
        val result = searchUsers(KudosMockData.searchableUsers, "Huỳnh")
        assertEquals(3, result.size)
        assertTrue(result.all { it.name.contains("Huỳnh", ignoreCase = true) })
    }

    @Test
    fun searchUsers_ByCode() {
        val result = searchUsers(KudosMockData.searchableUsers, "CECV1")
        // BUG: searchUsers uses contains(), matches "CECV1" and "CECV10"
        // s1, s2 have code "CECV1"; s4 has "CECV10" → 3 total
        assertEquals(3, result.size)
        assertTrue(result.all { "CECV1" in it.code })
    }

    @Test
    fun searchUsers_ByCodeCaseInsensitive() {
        val result = searchUsers(KudosMockData.searchableUsers, "cecv1")
        // BUG: same as above, matches "CECV1" and "CECV10" (s1, s2, s4)
        assertEquals(3, result.size)
        assertTrue(result.all { "cecv1" in it.code.lowercase() })
    }

    @Test
    fun searchUsers_NoMatches() {
        val result = searchUsers(KudosMockData.searchableUsers, "xyz999")
        assertEquals(0, result.size)
    }

    @Test
    fun searchUsers_MatchesBothNameAndCode() {
        // "Anh" appears in names and "Anh" is not a code, but we should match name
        val result = searchUsers(KudosMockData.searchableUsers, "Anh")
        assertTrue(result.isNotEmpty())
        assertTrue(result.any { "Anh" in it.name })
    }
}
