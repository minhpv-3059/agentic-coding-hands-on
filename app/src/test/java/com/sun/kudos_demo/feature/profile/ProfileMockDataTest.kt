package com.sun.kudos_demo.feature.profile

import com.sun.kudos_demo.feature.feed.KudoUser
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ProfileMockData (Phase 07).
 *
 * Validates the mock data structure, user resolution, and kudos lists used by
 * MyProfileViewModel and UserProfileViewModel. All tests assert on deterministic
 * data—no time-dependent or random behavior.
 */
class ProfileMockDataTest {

    // ===================== currentUser tests =====================

    @Test
    fun currentUser_HasCorrectIdentity() {
        // Logged-in identity established at login (single source of truth: CurrentUser)
        val user = ProfileMockData.currentUser
        assertEquals("u1", user.id)
        assertEquals("Phan Văn Minh", user.name)
        assertEquals("CEVC1", user.code)
        assertEquals("Legend Hero", user.badge)
    }

    @Test
    fun currentUser_MatchesFeedUser() {
        // Profile's current user should align with the feed's current user
        // so received/sent kudo split stays consistent across the app
        assertEquals(ProfileMockData.CURRENT_USER_ID, "u1")
        assertEquals(ProfileMockData.currentUser.id, ProfileMockData.CURRENT_USER_ID)
    }

    // ===================== currentUserStats tests =====================

    @Test
    fun currentUserStats_HasCorrectValues() {
        val stats = ProfileMockData.currentUserStats
        assertEquals(5, stats.kudosReceived)
        assertEquals(25, stats.kudosSent)
        assertEquals(25, stats.heartsReceived)
        assertEquals(25, stats.secretBoxOpened)
        assertEquals(25, stats.secretBoxUnopened)
    }

    // ===================== awardBadges tests =====================

    @Test
    fun awardBadges_HasExactlySixBadges() {
        assertEquals(6, ProfileMockData.awardBadges.size)
    }

    @Test
    fun awardBadges_AllLabelsMatch() {
        val badgeLabels = ProfileMockData.awardBadges.map { it.label }
        assertEquals(listOf(
            "REVIVAL",
            "TOUCH OF LIGHT",
            "STAY GOLD",
            "FLOW TO HORIZON",
            "BEYOND THE BOUNDARY",
            "ROOT FUTHER"
        ), badgeLabels)
    }

    @Test
    fun awardBadges_AllIconsWired() {
        // Real Figma exports (img_badge_*) are wired — every badge has a drawable
        assertTrue(ProfileMockData.awardBadges.all { it.icon != null })
    }

    @Test
    fun awardBadges_IdAndLabelCorrect_Revival() {
        val badge = ProfileMockData.awardBadges[0]
        assertEquals("revival", badge.id)
        assertEquals("REVIVAL", badge.label)
    }

    @Test
    fun awardBadges_IdAndLabelCorrect_TouchOfLight() {
        val badge = ProfileMockData.awardBadges[1]
        assertEquals("touch_of_light", badge.id)
        assertEquals("TOUCH OF LIGHT", badge.label)
    }

    @Test
    fun awardBadges_IdAndLabelCorrect_StayGold() {
        val badge = ProfileMockData.awardBadges[2]
        assertEquals("stay_gold", badge.id)
        assertEquals("STAY GOLD", badge.label)
    }

    @Test
    fun awardBadges_IdAndLabelCorrect_FlowToHorizon() {
        val badge = ProfileMockData.awardBadges[3]
        assertEquals("flow_to_horizon", badge.id)
        assertEquals("FLOW TO HORIZON", badge.label)
    }

    @Test
    fun awardBadges_IdAndLabelCorrect_BeyondTheBoundary() {
        val badge = ProfileMockData.awardBadges[4]
        assertEquals("beyond_the_boundary", badge.id)
        assertEquals("BEYOND THE BOUNDARY", badge.label)
    }

    @Test
    fun awardBadges_IdAndLabelCorrect_RootFurther() {
        val badge = ProfileMockData.awardBadges[5]
        assertEquals("root_futher", badge.id)
        assertEquals("ROOT FUTHER", badge.label)
    }

    // ===================== userById tests =====================

    @Test
    fun userById_CurrentUser_ReturnsCurrentUser() {
        val user = ProfileMockData.userById("u1")
        assertEquals(ProfileMockData.currentUser, user)
        assertEquals("u1", user.id)
        assertEquals("Phan Văn Minh", user.name)
        assertEquals("Legend Hero", user.badge)
    }

    @Test
    fun resolveCurrentUser_NullSession_ReturnsCanonicalCurrentUser() {
        assertEquals(ProfileMockData.currentUser, ProfileMockData.resolveCurrentUser(null))
    }

    @Test
    fun resolveCurrentUser_PersistedCurrentUserId_ReturnsCurrentUser() {
        val user = ProfileMockData.resolveCurrentUser("u1")
        assertEquals("Phan Văn Minh", user.name)
        assertEquals("Legend Hero", user.badge)
    }

    @Test
    fun resolveCurrentUser_OtherPersistedId_ResolvesThatUser() {
        // A different persisted id resolves to that Sunner, not the current user
        val user = ProfileMockData.resolveCurrentUser("u6")
        assertEquals("u6", user.id)
        assertEquals("Huỳnh Dương Xuân Nhật", user.name)
    }

    @Test
    fun userById_UnknownId_ReturnsFallbackUser() {
        val user = ProfileMockData.userById("unknown-id-xyz")
        assertNotNull(user)
        assertEquals("unknown-id-xyz", user.id)
        assertEquals("Sunner", user.name)
        assertEquals("SAA", user.code)
        assertEquals("Rising Hero", user.badge)
    }

    @Test
    fun userById_GiftRecipientIds_ResolveToTheirOwnDistinctUsers() {
        // "10 Sunner nhận quà mới nhất" uses ids g0..g9 — each must open its own profile,
        // not a shared fallback (regression guard for the gift-recipient lookup bug).
        val g0 = ProfileMockData.userById("g0")
        val g1 = ProfileMockData.userById("g1")
        assertEquals("g0", g0.id)
        assertEquals("g1", g1.id)
        assertNotEquals(g0.name, g1.name)          // different Sunners, not all the same person
        assertNotEquals("Sunner", g0.name)         // resolved, not the unknown fallback
        assertNotEquals("Sunner", g1.name)
    }

    @Test
    fun userById_UnknownId_NeverReturnsNull() {
        // The design guarantees a fallback — screen should never render null
        val ids = listOf("totally-unknown", "", "zzz", "123", "fake-user-999")
        ids.forEach { id ->
            val user = ProfileMockData.userById(id)
            assertNotNull("userById($id) should never return null", user)
            assertEquals(id, user.id)
        }
    }

    @Test
    fun userById_KnownSearchableUser() {
        // Test against a known user from KudosMockData.searchableUsers
        // We'll resolve s1's id and confirm we get back a real user (not fallback)
        val searchableUserId = "s1"
        val user = ProfileMockData.userById(searchableUserId)
        // Should resolve to a real user (not fallback) if the id exists in the index
        // If it doesn't exist, we get a fallback with "Rising Hero" badge
        assertNotNull(user)
        assertEquals(searchableUserId, user.id)
    }

    @Test
    fun userById_FallbackBadgeIsRisingHero() {
        // Unknown IDs should return fallback with "Rising Hero" badge
        val user = ProfileMockData.userById("definitely-not-a-user")
        assertEquals("Rising Hero", user.badge)
    }

    // ===================== myReceivedKudos tests =====================

    @Test
    fun myReceivedKudos_SizeIsExactlyFive() {
        assertEquals(5, ProfileMockData.myReceivedKudos.size)
    }

    @Test
    fun myReceivedKudos_AllRecipientsAreCurrentUser() {
        assertTrue(ProfileMockData.myReceivedKudos.all {
            it.recipient.id == ProfileMockData.CURRENT_USER_ID
        })
    }

    @Test
    fun myReceivedKudos_IdsPrefixedWithMe() {
        assertTrue(ProfileMockData.myReceivedKudos.all {
            it.id.startsWith("me-recv-")
        })
    }

    @Test
    fun myReceivedKudos_AllDifferentIds() {
        val ids = ProfileMockData.myReceivedKudos.map { it.id }
        assertEquals(ids.size, ids.toSet().size)  // All unique
    }

    @Test
    fun myReceivedKudos_SendersAreFromOthers() {
        // Every kudo should have a non-current-user sender
        val kudos = ProfileMockData.myReceivedKudos
        kudos.forEach { kudo ->
            assertNotNull("Received kudo must have a sender", kudo.sender)
            assertNotEquals(
                "Received kudo sender must not be current user",
                ProfileMockData.CURRENT_USER_ID,
                kudo.sender?.id
            )
        }
    }

    @Test
    fun myReceivedKudos_IsDeterministic() {
        // Same call twice should produce identical list
        val first = ProfileMockData.myReceivedKudos
        val second = ProfileMockData.myReceivedKudos
        assertEquals(first.map { it.id }, second.map { it.id })
        assertEquals(first.map { it.sender?.id }, second.map { it.sender?.id })
    }

    // ===================== mySentKudos tests =====================

    @Test
    fun mySentKudos_SizeIsExactlyFive() {
        assertEquals(5, ProfileMockData.mySentKudos.size)
    }

    @Test
    fun mySentKudos_AllSendersAreCurrentUser() {
        assertTrue(ProfileMockData.mySentKudos.all {
            it.sender?.id == ProfileMockData.CURRENT_USER_ID
        })
    }

    @Test
    fun mySentKudos_AllNonAnonymous() {
        assertTrue(ProfileMockData.mySentKudos.all { !it.isAnonymous })
    }

    @Test
    fun mySentKudos_IdsPrefixedWithMeSent() {
        assertTrue(ProfileMockData.mySentKudos.all {
            it.id.startsWith("me-sent-")
        })
    }

    @Test
    fun mySentKudos_AllDifferentIds() {
        val ids = ProfileMockData.mySentKudos.map { it.id }
        assertEquals(ids.size, ids.toSet().size)  // All unique
    }

    @Test
    fun mySentKudos_RecipientsAreFromOthers() {
        // Every sent kudo should have a non-current-user recipient
        val kudos = ProfileMockData.mySentKudos
        kudos.forEach { kudo ->
            assertNotEquals(
                "Sent kudo recipient must not be current user",
                ProfileMockData.CURRENT_USER_ID,
                kudo.recipient.id
            )
        }
    }

    @Test
    fun mySentKudos_IsDeterministic() {
        // Same call twice should produce identical list
        val first = ProfileMockData.mySentKudos
        val second = ProfileMockData.mySentKudos
        assertEquals(first.map { it.id }, second.map { it.id })
        assertEquals(first.map { it.recipient.id }, second.map { it.recipient.id })
    }

    // ===================== receivedKudosFor tests =====================

    @Test
    fun receivedKudosFor_CurrentUserDefaultPrefix_SizeIsExactlyFive() {
        val kudos = ProfileMockData.receivedKudosFor(ProfileMockData.currentUser)
        assertEquals(5, kudos.size)
    }

    @Test
    fun receivedKudosFor_AllRecipientsMatchUser() {
        val user = ProfileMockData.userById("s1")
        val kudos = ProfileMockData.receivedKudosFor(user)
        assertTrue(kudos.all { it.recipient == user })
    }

    @Test
    fun receivedKudosFor_CustomIdPrefix() {
        val user = KudoUser(id = "test-user", name = "Test", code = "TEST", badge = null)
        val kudos = ProfileMockData.receivedKudosFor(user, "custom")
        assertTrue(kudos.all { it.id.startsWith("custom-recv-") })
    }

    @Test
    fun receivedKudosFor_DefaultIdPrefixUsesUserId() {
        val user = KudoUser(id = "user-123", name = "Test", code = "ABC", badge = null)
        val kudos = ProfileMockData.receivedKudosFor(user)
        assertTrue(kudos.all { it.id.startsWith("user-123-recv-") })
    }

    @Test
    fun receivedKudosFor_IsDeterministic() {
        val user = ProfileMockData.userById("s2")
        val first = ProfileMockData.receivedKudosFor(user)
        val second = ProfileMockData.receivedKudosFor(user)
        assertEquals(first.map { it.id }, second.map { it.id })
    }

    // ===================== kudoById tests =====================

    @Test
    fun kudoById_ResolvesMyReceivedKudo() {
        val kudo = ProfileMockData.myReceivedKudos[0]
        val resolved = ProfileMockData.kudoById(kudo.id)
        assertNotNull(resolved)
        assertEquals(kudo.id, resolved?.id)
    }

    @Test
    fun kudoById_ResolvesMySentKudo() {
        val kudo = ProfileMockData.mySentKudos[0]
        val resolved = ProfileMockData.kudoById(kudo.id)
        assertNotNull(resolved)
        assertEquals(kudo.id, resolved?.id)
    }

    @Test
    fun kudoById_UnknownId_ReturnsNull() {
        val resolved = ProfileMockData.kudoById("totally-unknown-kudo-id")
        assertNull(resolved)
    }

    @Test
    fun kudoById_AllMyReceivedKudos_Resolvable() {
        ProfileMockData.myReceivedKudos.forEach { kudo ->
            val resolved = ProfileMockData.kudoById(kudo.id)
            assertNotNull("Should resolve kudo ${kudo.id}", resolved)
            assertEquals(kudo.id, resolved?.id)
        }
    }

    @Test
    fun kudoById_AllMySentKudos_Resolvable() {
        ProfileMockData.mySentKudos.forEach { kudo ->
            val resolved = ProfileMockData.kudoById(kudo.id)
            assertNotNull("Should resolve kudo ${kudo.id}", resolved)
            assertEquals(kudo.id, resolved?.id)
        }
    }

    @Test
    fun kudoById_OtherUserReceivedKudos_Resolvable() {
        // Test that other user's received kudos are also resolvable
        val user = ProfileMockData.userById("s3")
        val kudos = ProfileMockData.receivedKudosFor(user)
        kudos.forEach { kudo ->
            val resolved = ProfileMockData.kudoById(kudo.id)
            assertNotNull("Should resolve other user's kudo ${kudo.id}", resolved)
            assertEquals(kudo.id, resolved?.id)
        }
    }

    @Test
    fun kudoById_IsDeterministic() {
        val id = ProfileMockData.myReceivedKudos[0].id
        val first = ProfileMockData.kudoById(id)
        val second = ProfileMockData.kudoById(id)
        assertEquals(first?.id, second?.id)
    }
}
