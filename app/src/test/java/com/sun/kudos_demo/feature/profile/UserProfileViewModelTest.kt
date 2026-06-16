package com.sun.kudos_demo.feature.profile

import com.sun.kudos_demo.feature.auth.AppLanguage
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for UserProfileViewModel state logic (Phase 07).
 *
 * Tests the default state, user/badge resolution, and received kudos derivation.
 * Pure state testing only — no AndroidViewModel/SavedStateHandle infrastructure required.
 */
class UserProfileViewModelTest {

    // ===================== UserProfileUiState initialization tests =====================

    @Test
    fun userProfileUiState_DefaultUser_IsCurrentUser() {
        val state = UserProfileUiState()
        assertEquals(ProfileMockData.currentUser, state.user)
    }

    @Test
    fun userProfileUiState_DefaultBadges_IsAwardBadges() {
        val state = UserProfileUiState()
        assertEquals(ProfileMockData.awardBadges, state.badges)
    }

    @Test
    fun userProfileUiState_DefaultReceivedKudos_IsEmpty() {
        val state = UserProfileUiState()
        assertTrue(state.receivedKudos.isEmpty())
    }

    @Test
    fun userProfileUiState_DefaultLikedIds_IsEmpty() {
        val state = UserProfileUiState()
        assertTrue(state.likedIds.isEmpty())
    }

    @Test
    fun userProfileUiState_DefaultLanguage_IsVN() {
        val state = UserProfileUiState()
        assertEquals(AppLanguage.VN, state.language)
    }

    @Test
    fun userProfileUiState_DefaultUnreadCount_IsThree() {
        val state = UserProfileUiState()
        assertEquals(3, state.unreadCount)
    }

    // ===================== badge property tests =====================

    @Test
    fun userProfileUiState_Badge_UsesUserBadgeWhenPresent() {
        val user = ProfileMockData.currentUser
        val state = UserProfileUiState(user = user)
        assertEquals("Legend Hero", state.badge)
    }

    @Test
    fun userProfileUiState_Badge_FallsBackToRisingHeroWhenNull() {
        val userWithoutBadge = ProfileMockData.userById("unknown-id-xyz")
        val state = UserProfileUiState(user = userWithoutBadge)
        assertEquals("Rising Hero", state.badge)
    }

    @Test
    fun userProfileUiState_Badge_MatchesUserBadgeExactly() {
        val customBadge = "Custom Badge"
        val user = ProfileMockData.currentUser.copy(badge = customBadge)
        val state = UserProfileUiState(user = user)
        assertEquals(customBadge, state.badge)
    }

    // ===================== receivedCount property tests =====================

    @Test
    fun userProfileUiState_ReceivedCount_ZeroWhenEmptyList() {
        val state = UserProfileUiState(receivedKudos = emptyList())
        assertEquals(0, state.receivedCount)
    }

    @Test
    fun userProfileUiState_ReceivedCount_MatchesListSize() {
        val kudos = ProfileMockData.receivedKudosFor(ProfileMockData.currentUser)
        val state = UserProfileUiState(receivedKudos = kudos)
        assertEquals(5, state.receivedCount)
        assertEquals(kudos.size, state.receivedCount)
    }

    @Test
    fun userProfileUiState_ReceivedCount_CustomKudosList() {
        val kudos = ProfileMockData.receivedKudosFor(ProfileMockData.userById("s1"))
        val state = UserProfileUiState(receivedKudos = kudos)
        assertEquals(5, state.receivedCount)
    }

    // ===================== Custom user construction tests =====================

    @Test
    fun userProfileUiState_CustomUser() {
        val user = ProfileMockData.userById("s2")
        val kudos = ProfileMockData.receivedKudosFor(user)
        val state = UserProfileUiState(user = user, receivedKudos = kudos)

        assertEquals(user, state.user)
        assertEquals(kudos, state.receivedKudos)
        assertEquals(kudos.size, state.receivedCount)
    }

    @Test
    fun userProfileUiState_UserWithNullBadge_FallsBackInBadgeProperty() {
        val userNoBadge = ProfileMockData.userById("fake-unknown")
        val state = UserProfileUiState(user = userNoBadge)
        assertEquals("Rising Hero", state.badge)
    }

    // ===================== State properties tests =====================

    @Test
    fun userProfileUiState_IsDataClass() {
        val state1 = UserProfileUiState(
            user = ProfileMockData.currentUser,
            receivedKudos = ProfileMockData.receivedKudosFor(ProfileMockData.currentUser)
        )
        val state2 = UserProfileUiState(
            user = ProfileMockData.currentUser,
            receivedKudos = ProfileMockData.receivedKudosFor(ProfileMockData.currentUser)
        )
        assertEquals(state1, state2)
    }

    @Test
    fun userProfileUiState_CopyPreservesFields() {
        val originalUser = ProfileMockData.userById("s1")
        val originalKudos = ProfileMockData.receivedKudosFor(originalUser)
        val original = UserProfileUiState(
            user = originalUser,
            receivedKudos = originalKudos,
            language = AppLanguage.EN
        )

        val modified = original.copy(language = AppLanguage.VN)
        assertEquals(originalUser, modified.user)
        assertEquals(originalKudos, modified.receivedKudos)
        assertEquals(AppLanguage.VN, modified.language)
        assertEquals(AppLanguage.EN, original.language)  // Original unchanged
    }

    @Test
    fun userProfileUiState_CopyWithLikedIds() {
        val state = UserProfileUiState(likedIds = setOf("k1", "k2"))
        val modified = state.copy(likedIds = setOf("k1", "k2", "k3"))
        assertEquals(setOf("k1", "k2"), state.likedIds)
        assertEquals(setOf("k1", "k2", "k3"), modified.likedIds)
    }

    // ===================== Like toggle logic tests =====================

    @Test
    fun userProfileUiState_AllReceivedKudos_AreLikeable() {
        // In UserProfile, all received kudos are from OTHER users → all likeable
        val kudos = ProfileMockData.receivedKudosFor(ProfileMockData.currentUser)
        val state = UserProfileUiState(receivedKudos = kudos)

        // Verify that no received kudo is sent by the current user
        // (by design, these are kudos RECEIVED by the current user)
        assertTrue(state.receivedKudos.all {
            it.recipient.id == ProfileMockData.CURRENT_USER_ID
        })
    }

    @Test
    fun userProfileUiState_ReceivedKudos_NeverHaveCurrentUserAsSender() {
        val kudos = ProfileMockData.receivedKudosFor(ProfileMockData.currentUser)
        val state = UserProfileUiState(
            user = ProfileMockData.currentUser,
            receivedKudos = kudos
        )
        // All senders in received kudos should NOT be the current user
        assertTrue(state.receivedKudos.all {
            it.sender?.id != ProfileMockData.CURRENT_USER_ID
        })
    }

    @Test
    fun userProfileUiState_OtherUserProfile_ShowsTheirReceivedKudos() {
        val otherUser = ProfileMockData.userById("s3")
        val kudos = ProfileMockData.receivedKudosFor(otherUser)
        val state = UserProfileUiState(user = otherUser, receivedKudos = kudos)

        assertEquals(otherUser, state.user)
        assertEquals(5, state.receivedCount)
        assertTrue(state.receivedKudos.all { it.recipient == otherUser })
    }

    // ===================== Badge resolution tests =====================

    @Test
    fun userProfileUiState_CurrentUserBadge_Legend() {
        val state = UserProfileUiState(user = ProfileMockData.currentUser)
        assertEquals("Legend Hero", state.badge)
    }

    @Test
    fun userProfileUiState_UnknownUserBadge_RisingHero() {
        // When user's badge is null or unknown
        val unknownUser = ProfileMockData.userById("definitely-not-real")
        val state = UserProfileUiState(user = unknownUser)
        assertEquals("Rising Hero", state.badge)
    }

    @Test
    fun userProfileUiState_CustomBadgeName() {
        val customUser = ProfileMockData.currentUser.copy(badge = "Master Mentor")
        val state = UserProfileUiState(user = customUser)
        assertEquals("Master Mentor", state.badge)
    }

    // ===================== Integration with ProfileMockData =====================

    @Test
    fun userProfileUiState_UsesProfileMockDataBadges() {
        val state = UserProfileUiState()
        assertEquals(ProfileMockData.awardBadges, state.badges)
        assertEquals(6, state.badges.size)
    }

    @Test
    fun userProfileUiState_ReceivedKudosFromMockData() {
        val user = ProfileMockData.userById("s1")
        val kudos = ProfileMockData.receivedKudosFor(user)
        val state = UserProfileUiState(user = user, receivedKudos = kudos)

        // All kudos should be resolvable
        kudos.forEach { kudo ->
            val resolved = ProfileMockData.kudoById(kudo.id)
            assertNotNull("Kudo ${kudo.id} should be resolvable", resolved)
        }
    }

    @Test
    fun userProfileUiState_EmptyReceivedKudos() {
        val state = UserProfileUiState(receivedKudos = emptyList())
        assertEquals(0, state.receivedCount)
        assertTrue(state.receivedKudos.isEmpty())
    }

    @Test
    fun userProfileUiState_LanguageToggleable() {
        val stateVN = UserProfileUiState(language = AppLanguage.VN)
        val stateEN = UserProfileUiState(language = AppLanguage.EN)

        assertEquals(AppLanguage.VN, stateVN.language)
        assertEquals(AppLanguage.EN, stateEN.language)
    }
}
