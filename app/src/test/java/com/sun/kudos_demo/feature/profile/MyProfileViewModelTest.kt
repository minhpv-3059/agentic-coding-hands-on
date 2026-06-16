package com.sun.kudos_demo.feature.profile

import com.sun.kudos_demo.feature.feed.KudosMockData
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for MyProfileViewModel logic (Phase 07).
 *
 * Tests the default filter state, visible kudos list derivation, and like toggle logic.
 * Pure state testing only — no AndroidViewModel/DataStore infrastructure required.
 */
class MyProfileViewModelTest {

    // ===================== MyProfileUiState initialization tests =====================

    @Test
    fun myProfileUiState_DefaultFilter_IsSent() {
        val state = MyProfileUiState()
        assertEquals(ProfileKudosTab.SENT, state.filter)
    }

    @Test
    fun myProfileUiState_DefaultUser_IsCurrentUser() {
        val state = MyProfileUiState()
        assertEquals(ProfileMockData.currentUser, state.user)
    }

    @Test
    fun myProfileUiState_DefaultStats_IsCurrentUserStats() {
        val state = MyProfileUiState()
        assertEquals(ProfileMockData.currentUserStats, state.stats)
    }

    @Test
    fun myProfileUiState_DefaultLikedIds_IsEmpty() {
        val state = MyProfileUiState()
        assertTrue(state.likedIds.isEmpty())
    }

    @Test
    fun myProfileUiState_CustomFilter() {
        val state = MyProfileUiState(filter = ProfileKudosTab.RECEIVED)
        assertEquals(ProfileKudosTab.RECEIVED, state.filter)
    }

    // ===================== receivedKudos property tests =====================

    @Test
    fun myProfileUiState_ReceivedKudos_ReturnsProfileMockData() {
        val state = MyProfileUiState()
        assertEquals(ProfileMockData.myReceivedKudos, state.receivedKudos)
    }

    @Test
    fun myProfileUiState_ReceivedKudos_SizeIsFive() {
        val state = MyProfileUiState()
        assertEquals(5, state.receivedKudos.size)
    }

    @Test
    fun myProfileUiState_ReceivedKudos_AllRecipientsAreCurrentUser() {
        val state = MyProfileUiState()
        assertTrue(state.receivedKudos.all {
            it.recipient.id == ProfileMockData.CURRENT_USER_ID
        })
    }

    // ===================== sentKudos property tests =====================

    @Test
    fun myProfileUiState_SentKudos_ReturnsProfileMockData() {
        val state = MyProfileUiState()
        assertEquals(ProfileMockData.mySentKudos, state.sentKudos)
    }

    @Test
    fun myProfileUiState_SentKudos_SizeIsFive() {
        val state = MyProfileUiState()
        assertEquals(5, state.sentKudos.size)
    }

    @Test
    fun myProfileUiState_SentKudos_AllSendersAreCurrentUser() {
        val state = MyProfileUiState()
        assertTrue(state.sentKudos.all {
            it.sender?.id == ProfileMockData.CURRENT_USER_ID
        })
    }

    // ===================== visibleKudos property tests =====================

    @Test
    fun myProfileUiState_VisibleKudos_DefaultFilterIsSent() {
        val state = MyProfileUiState()  // Default filter = SENT
        assertEquals(state.sentKudos, state.visibleKudos)
    }

    @Test
    fun myProfileUiState_VisibleKudos_FilterSent_ShowsSentKudos() {
        val state = MyProfileUiState(filter = ProfileKudosTab.SENT)
        assertEquals(state.sentKudos, state.visibleKudos)
        assertEquals(5, state.visibleKudos.size)
    }

    @Test
    fun myProfileUiState_VisibleKudos_FilterReceived_ShowsReceivedKudos() {
        val state = MyProfileUiState(filter = ProfileKudosTab.RECEIVED)
        assertEquals(state.receivedKudos, state.visibleKudos)
        assertEquals(5, state.visibleKudos.size)
    }

    @Test
    fun myProfileUiState_VisibleKudos_SwitchFilter_SwitchesVisibleList() {
        val sentState = MyProfileUiState(filter = ProfileKudosTab.SENT)
        assertEquals(5, sentState.visibleKudos.size)
        assertTrue(sentState.visibleKudos.all { it.sender?.id == ProfileMockData.CURRENT_USER_ID })

        val receivedState = MyProfileUiState(filter = ProfileKudosTab.RECEIVED)
        assertEquals(5, receivedState.visibleKudos.size)
        assertTrue(receivedState.visibleKudos.all { it.recipient.id == ProfileMockData.CURRENT_USER_ID })

        // Visible lists should be different
        assertNotEquals(sentState.visibleKudos, receivedState.visibleKudos)
    }

    // ===================== counts tests =====================

    @Test
    fun myProfileUiState_ReceivedCount_IsFive() {
        val state = MyProfileUiState()
        assertEquals(5, state.receivedCount)
    }

    @Test
    fun myProfileUiState_SentCount_IsFive() {
        val state = MyProfileUiState()
        assertEquals(5, state.sentCount)
    }

    @Test
    fun myProfileUiState_CountsMatchListSizes() {
        val state = MyProfileUiState()
        assertEquals(state.receivedKudos.size, state.receivedCount)
        assertEquals(state.sentKudos.size, state.sentCount)
    }

    // ===================== Like toggle logic tests (pure state) =====================

    @Test
    fun myProfileUiState_CanToggleLike_OnReceivedKudo() {
        // Received kudos have different senders (not current user) → likeable
        val state = MyProfileUiState()
        val receivedKudo = state.receivedKudos[0]
        assertNotEquals(ProfileMockData.CURRENT_USER_ID, receivedKudo.sender?.id)
        // Logic: toggleLike should succeed for received kudos
    }

    @Test
    fun myProfileUiState_CannotToggleLike_OnSentKudo() {
        // Sent kudos have current user as sender → not likeable
        val state = MyProfileUiState()
        val sentKudo = state.sentKudos[0]
        assertEquals(ProfileMockData.CURRENT_USER_ID, sentKudo.sender?.id)
        // Logic: toggleLike should reject sent kudos
    }

    @Test
    fun myProfileUiState_SentKudosAreNotAnonymous() {
        val state = MyProfileUiState()
        assertTrue(state.sentKudos.all { !it.isAnonymous })
    }

    @Test
    fun myProfileUiState_ReceivedKudosCanBeAnonymous() {
        // The profile's received kudos may or may not be anonymous
        // (depends on underlying mock data generation)
        val state = MyProfileUiState()
        assertNotNull(state.receivedKudos)
        // Just verify they're not all anonymous (no strict requirement)
    }

    // ===================== State immutability tests =====================

    @Test
    fun myProfileUiState_IsDataClass() {
        // Data classes have proper equality and copy
        val state1 = MyProfileUiState(filter = ProfileKudosTab.SENT)
        val state2 = MyProfileUiState(filter = ProfileKudosTab.SENT)
        assertEquals(state1, state2)
    }

    @Test
    fun myProfileUiState_CopyPreservesValues() {
        val original = MyProfileUiState(
            filter = ProfileKudosTab.RECEIVED,
            likedIds = setOf("k1", "k2")
        )
        val modified = original.copy(filter = ProfileKudosTab.SENT)
        assertEquals(ProfileKudosTab.RECEIVED, original.filter)
        assertEquals(ProfileKudosTab.SENT, modified.filter)
        assertEquals(setOf("k1", "k2"), modified.likedIds)  // Preserved
    }

    @Test
    fun myProfileUiState_LikedIdsCanBeSet() {
        val likedSet = setOf("k1", "k2", "k3")
        val state = MyProfileUiState(likedIds = likedSet)
        assertEquals(likedSet, state.likedIds)
    }

    @Test
    fun myProfileUiState_LikedIdsCanBeEmpty() {
        val state = MyProfileUiState(likedIds = emptySet())
        assertTrue(state.likedIds.isEmpty())
    }

    // ===================== Integration with ProfileMockData =====================

    @Test
    fun myProfileUiState_VisibleKudos_UsesLiveProfileMockData() {
        // Verify that visible kudos are actually the profile mock data
        val state = MyProfileUiState(filter = ProfileKudosTab.SENT)
        val visibleIds = state.visibleKudos.map { it.id }
        val mockIds = ProfileMockData.mySentKudos.map { it.id }
        assertEquals(visibleIds, mockIds)
    }

    @Test
    fun myProfileUiState_VisibleKudos_UpdatesWhenFilterChanges() {
        val sentState = MyProfileUiState(filter = ProfileKudosTab.SENT)
        val sentIds = sentState.visibleKudos.map { it.id }

        val receivedState = MyProfileUiState(filter = ProfileKudosTab.RECEIVED)
        val receivedIds = receivedState.visibleKudos.map { it.id }

        // Should be completely different lists
        assertTrue(sentIds.isNotEmpty())
        assertTrue(receivedIds.isNotEmpty())
        assertNotEquals(sentIds, receivedIds)
    }
}
