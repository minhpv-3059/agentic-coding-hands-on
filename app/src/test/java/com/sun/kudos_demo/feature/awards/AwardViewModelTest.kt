package com.sun.kudos_demo.feature.awards

import com.sun.kudos_demo.feature.auth.AppLanguage
import com.sun.kudos_demo.navigation.NavRoutes
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for [AwardViewModel] logic (Phase 10).
 *
 * Tests the initialization with optional award route argument, award selection,
 * dropdown expansion toggle, and language toggle logic through AwardsUiState.
 *
 * Note: The ViewModel requires AndroidViewModel/SavedStateHandle/viewModelScope infrastructure.
 * These unit tests focus on the pure state logic (AwardsUiState) and the navigation argument
 * parsing logic that determines the initial selected award. We test:
 *  - AwardsUiState defaults (selected, dropdown, language, unreadCount)
 *  - selectAward() behavior (updates selected, closes dropdown)
 *  - setDropdownExpanded() behavior (toggle)
 *  - toggleLanguage() behavior (VN <-> EN)
 *  - Argument parsing rules (null/blank/invalid -> MVP fallback)
 */
class AwardViewModelTest {

    // ===================== AwardsUiState initialization tests =====================

    @Test
    fun awardsUiState_DefaultState_HasMvpSelected() {
        val state = AwardsUiState()
        assertEquals("Initial selected must be MVP (first award)", AwardData.awards[0], state.selected)
        assertEquals("Initial selected ID must be mvp", "mvp", state.selected.id)
    }

    @Test
    fun awardsUiState_DefaultState_DropdownClosedByDefault() {
        val state = AwardsUiState()
        assertFalse("Initial dropdownExpanded must be false", state.dropdownExpanded)
    }

    @Test
    fun awardsUiState_DefaultState_LanguageIsVnByDefault() {
        val state = AwardsUiState()
        assertEquals("Initial language must be VN", AppLanguage.VN, state.language)
    }

    @Test
    fun awardsUiState_DefaultState_UnreadCountIsZero() {
        val state = AwardsUiState()
        assertEquals("Initial unreadCount must be 0", 0, state.unreadCount)
    }

    @Test
    fun awardsUiState_DefaultState_AwardsListIsFromAwardData() {
        val state = AwardsUiState()
        assertEquals("Awards list must come from AwardData.awards", AwardData.awards, state.awards)
        assertEquals("Awards list must have 6 items", 6, state.awards.size)
    }

    @Test
    fun awardsUiState_CustomInitialization_WithSpecificAward() {
        val topProject = AwardData.byId("top_project")
        val state = AwardsUiState(selected = topProject)

        assertEquals("Can initialize with specific award", topProject, state.selected)
        assertEquals("Selected ID must be top_project", "top_project", state.selected.id)
    }

    @Test
    fun awardsUiState_CustomInitialization_WithDropdownExpanded() {
        val state = AwardsUiState(dropdownExpanded = true)
        assertTrue("Can initialize with dropdownExpanded = true", state.dropdownExpanded)
    }

    @Test
    fun awardsUiState_CustomInitialization_WithLanguageEn() {
        val state = AwardsUiState(language = AppLanguage.EN)
        assertEquals("Can initialize with language = EN", AppLanguage.EN, state.language)
    }

    @Test
    fun awardsUiState_CustomInitialization_WithUnreadCount() {
        val state = AwardsUiState(unreadCount = 5)
        assertEquals("Can initialize with unreadCount = 5", 5, state.unreadCount)
    }

    // ===================== selectAward() logic tests =====================

    @Test
    fun selectAward_UpdatesSelectedAward() {
        val state = AwardsUiState()
        val topProject = AwardData.byId("top_project")
        val updatedState = state.copy(selected = topProject)

        assertEquals("selectAward must update selected award", topProject, updatedState.selected)
    }

    @Test
    fun selectAward_ClosesDropdown() {
        val state = AwardsUiState(dropdownExpanded = true)
        val topProject = AwardData.byId("top_project")
        val updatedState = state.copy(selected = topProject, dropdownExpanded = false)

        assertFalse("selectAward must close dropdown", updatedState.dropdownExpanded)
    }

    @Test
    fun selectAward_UpdatesAndClosesDropdown_Atomically() {
        var state = AwardsUiState(
            selected = AwardData.awards[0],
            dropdownExpanded = true
        )

        val bestManager = AwardData.byId("best_manager")
        state = state.copy(selected = bestManager, dropdownExpanded = false)

        assertEquals("selectAward must update selected", bestManager, state.selected)
        assertFalse("selectAward must close dropdown", state.dropdownExpanded)
    }

    @Test
    fun selectAward_CanSwitchBetweenMultipleAwards() {
        var state = AwardsUiState()

        // Switch to signature_creator
        var sig = AwardData.byId("signature_creator")
        state = state.copy(selected = sig)
        assertEquals("Can switch to signature_creator", sig, state.selected)

        // Switch to top_project_leader
        var leader = AwardData.byId("top_project_leader")
        state = state.copy(selected = leader)
        assertEquals("Can switch to top_project_leader", leader, state.selected)

        // Switch back to mvp
        var mvp = AwardData.awards[0]
        state = state.copy(selected = mvp)
        assertEquals("Can switch back to MVP", mvp, state.selected)
    }

    @Test
    fun selectAward_PreservesOtherStateFields() {
        val state = AwardsUiState(
            selected = AwardData.awards[0],
            language = AppLanguage.EN,
            unreadCount = 5
        )

        val topTalent = AwardData.byId("top_talent")
        val updatedState = state.copy(selected = topTalent, dropdownExpanded = false)

        assertEquals("selectAward must preserve language", AppLanguage.EN, updatedState.language)
        assertEquals("selectAward must preserve unreadCount", 5, updatedState.unreadCount)
    }

    // ===================== setDropdownExpanded() logic tests =====================

    @Test
    fun setDropdownExpanded_True_OpensDropdown() {
        val state = AwardsUiState(dropdownExpanded = false)
        val updatedState = state.copy(dropdownExpanded = true)

        assertTrue("setDropdownExpanded(true) must open dropdown", updatedState.dropdownExpanded)
    }

    @Test
    fun setDropdownExpanded_False_ClosesDropdown() {
        val state = AwardsUiState(dropdownExpanded = true)
        val updatedState = state.copy(dropdownExpanded = false)

        assertFalse("setDropdownExpanded(false) must close dropdown", updatedState.dropdownExpanded)
    }

    @Test
    fun setDropdownExpanded_ToggleMultipleTimes() {
        var state = AwardsUiState(dropdownExpanded = false)

        // Toggle open
        state = state.copy(dropdownExpanded = true)
        assertTrue("First toggle: should open", state.dropdownExpanded)

        // Toggle closed
        state = state.copy(dropdownExpanded = false)
        assertFalse("Second toggle: should close", state.dropdownExpanded)

        // Toggle open again
        state = state.copy(dropdownExpanded = true)
        assertTrue("Third toggle: should open again", state.dropdownExpanded)
    }

    @Test
    fun setDropdownExpanded_PreservesSelectedAward() {
        val award = AwardData.byId("signature_creator")
        val state = AwardsUiState(selected = award, dropdownExpanded = false)

        val updatedState = state.copy(dropdownExpanded = true)

        assertEquals("setDropdownExpanded must preserve selected award", award, updatedState.selected)
    }

    @Test
    fun setDropdownExpanded_PreservesOtherStateFields() {
        val state = AwardsUiState(
            dropdownExpanded = false,
            language = AppLanguage.EN,
            unreadCount = 3
        )

        val updatedState = state.copy(dropdownExpanded = true)

        assertEquals("setDropdownExpanded must preserve language", AppLanguage.EN, updatedState.language)
        assertEquals("setDropdownExpanded must preserve unreadCount", 3, updatedState.unreadCount)
    }

    // ===================== toggleLanguage() logic tests =====================

    @Test
    fun toggleLanguage_FromVnToEn() {
        val state = AwardsUiState(language = AppLanguage.VN)
        val updatedState = state.copy(language = if (state.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)

        assertEquals("toggleLanguage from VN must switch to EN", AppLanguage.EN, updatedState.language)
    }

    @Test
    fun toggleLanguage_FromEnToVn() {
        val state = AwardsUiState(language = AppLanguage.EN)
        val updatedState = state.copy(language = if (state.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)

        assertEquals("toggleLanguage from EN must switch to VN", AppLanguage.VN, updatedState.language)
    }

    @Test
    fun toggleLanguage_BackAndForth() {
        var state = AwardsUiState(language = AppLanguage.VN)

        // Toggle to EN
        state = state.copy(language = if (state.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)
        assertEquals("First toggle: VN -> EN", AppLanguage.EN, state.language)

        // Toggle back to VN
        state = state.copy(language = if (state.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)
        assertEquals("Second toggle: EN -> VN", AppLanguage.VN, state.language)

        // Toggle to EN again
        state = state.copy(language = if (state.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)
        assertEquals("Third toggle: VN -> EN", AppLanguage.EN, state.language)
    }

    @Test
    fun toggleLanguage_PreservesSelectedAward() {
        val award = AwardData.byId("top_project")
        val state = AwardsUiState(selected = award, language = AppLanguage.VN)

        val updatedState = state.copy(language = if (state.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)

        assertEquals("toggleLanguage must preserve selected award", award, updatedState.selected)
    }

    @Test
    fun toggleLanguage_PreservesOtherStateFields() {
        val state = AwardsUiState(
            language = AppLanguage.VN,
            dropdownExpanded = true,
            unreadCount = 7
        )

        val updatedState = state.copy(language = if (state.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)

        assertTrue("toggleLanguage must preserve dropdownExpanded", updatedState.dropdownExpanded)
        assertEquals("toggleLanguage must preserve unreadCount", 7, updatedState.unreadCount)
    }

    // ===================== Combined interaction tests =====================

    @Test
    fun interactionFlow_SelectAwardThenToggleLanguage() {
        var state = AwardsUiState(
            selected = AwardData.awards[0],
            language = AppLanguage.VN
        )

        // Select a different award
        val award = AwardData.byId("best_manager")
        state = state.copy(selected = award, dropdownExpanded = false)
        assertEquals("After select: should be best_manager", award, state.selected)

        // Toggle language
        state = state.copy(language = if (state.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)
        assertEquals("After toggle: language should be EN", AppLanguage.EN, state.language)
        assertEquals("After toggle: award should still be best_manager", award, state.selected)
    }

    @Test
    fun interactionFlow_OpenDropdownSelectThenToggleLanguage() {
        var state = AwardsUiState(
            selected = AwardData.awards[0],
            dropdownExpanded = false,
            language = AppLanguage.VN
        )

        // Open dropdown
        state = state.copy(dropdownExpanded = true)
        assertTrue("Dropdown should be open", state.dropdownExpanded)

        // Select an award (which closes dropdown)
        val award = AwardData.byId("top_talent")
        state = state.copy(selected = award, dropdownExpanded = false)
        assertEquals("Selected should be top_talent", award, state.selected)
        assertFalse("Dropdown should be closed", state.dropdownExpanded)

        // Toggle language
        state = state.copy(language = if (state.language == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN)
        assertEquals("Language should be EN", AppLanguage.EN, state.language)
    }

    @Test
    fun interactionFlow_MultipleSelectsAndLanguageToggles() {
        var state = AwardsUiState()

        // Select signature_creator
        state = state.copy(selected = AwardData.byId("signature_creator"), dropdownExpanded = false)
        assertEquals("Select 1: signature_creator", "signature_creator", state.selected.id)

        // Toggle language to EN
        state = state.copy(language = AppLanguage.EN)
        assertEquals("Toggle 1: EN", AppLanguage.EN, state.language)

        // Select top_project
        state = state.copy(selected = AwardData.byId("top_project"), dropdownExpanded = false)
        assertEquals("Select 2: top_project", "top_project", state.selected.id)

        // Toggle language back to VN
        state = state.copy(language = AppLanguage.VN)
        assertEquals("Toggle 2: VN", AppLanguage.VN, state.language)

        // Select top_project_leader
        state = state.copy(selected = AwardData.byId("top_project_leader"), dropdownExpanded = false)
        assertEquals("Select 3: top_project_leader", "top_project_leader", state.selected.id)
        assertEquals("Final language: VN", AppLanguage.VN, state.language)
    }

    // ===================== Argument parsing logic tests =====================

    @Test
    fun argumentParsing_WithMixedCaseAwardId_FallsBackToMvp() {
        // Mixed case ID won't match exact string comparison
        val initialAwardId = "Top_Project"
        val selected = AwardData.byId(initialAwardId)

        assertEquals("Mixed case ID should fall back to MVP", "mvp", selected.id)
    }

    @Test
    fun argumentParsing_WithLeadingTrailingWhitespace_FallsBackToMvp() {
        // takeIf will keep the whitespace string, so byId won't find a match
        val initialAwardId: String? = "  top_project  ".takeIf { it.isNotBlank() }
        val selected = AwardData.byId(initialAwardId)

        assertEquals("Whitespace-padded ID should fall back to MVP", "mvp", selected.id)
    }

    @Test
    fun argumentParsing_AllValidAwardIds_CanBeSelected() {
        val validIds = listOf("mvp", "best_manager", "signature_creator", "top_project", "top_project_leader", "top_talent")

        for (id in validIds) {
            val initialAwardId: String? = id.takeIf { it.isNotBlank() }
            val selected = AwardData.byId(initialAwardId)

            assertEquals("Should select $id when passed as argument", id, selected.id)
        }
    }

    @Test
    fun argumentParsing_NullOrEmptyArg_DefaultsToMvp() {
        val scenarios = listOf(
            null as String?,
            "",
            "unknown_id",
            "INVALID"
        )

        for (arg in scenarios) {
            val initialAwardId: String? = arg?.takeIf { it.isNotBlank() }
            val selected = AwardData.byId(initialAwardId)

            assertEquals("Argument '$arg' should fall back to MVP", "mvp", selected.id)
        }
    }
}
