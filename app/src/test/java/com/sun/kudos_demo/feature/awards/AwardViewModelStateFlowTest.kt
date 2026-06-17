package com.sun.kudos_demo.feature.awards

import androidx.lifecycle.SavedStateHandle
import com.sun.kudos_demo.feature.auth.AppLanguage
import com.sun.kudos_demo.feature.secretbox.MainDispatcherRule
import com.sun.kudos_demo.navigation.NavRoutes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Real [AwardViewModel] tests — construct the VM with a [SavedStateHandle] so the
 * `NavRoutes.ARG_AWARD` pre-select path (entry from a Home award card) is actually exercised,
 * not just the [AwardsUiState] data class (closes reviewer gap "Important #1").
 *
 * The VM exposes a plain `MutableStateFlow.asStateFlow()`, so `.value` reflects the initial
 * selection synchronously after construction. [MainDispatcherRule] provides a Main dispatcher
 * for the `observeUnreadNotifications()` coroutine launched in `init`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AwardViewModelStateFlowTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun vmWith(arg: String?): AwardViewModel =
        AwardViewModel(
            SavedStateHandle(if (arg == null) emptyMap() else mapOf(NavRoutes.ARG_AWARD to arg))
        )

    @Test
    fun default_noArg_selectsMvp() {
        assertEquals("mvp", vmWith(null).uiState.value.selected.id)
    }

    @Test
    fun blankArg_selectsMvp() {
        assertEquals("mvp", vmWith("").uiState.value.selected.id)
    }

    @Test
    fun validArg_preSelectsThatAward() {
        assertEquals("top_project", vmWith("top_project").uiState.value.selected.id)
        assertEquals("signature_creator", vmWith("signature_creator").uiState.value.selected.id)
    }

    @Test
    fun unknownArg_fallsBackToMvp() {
        assertEquals("mvp", vmWith("does_not_exist").uiState.value.selected.id)
    }

    @Test
    fun selectAward_updatesSelectionAndClosesDropdown() {
        val vm = vmWith(null)
        vm.setDropdownExpanded(true)
        vm.selectAward(AwardData.awards.first { it.id == "top_talent" })

        assertEquals("top_talent", vm.uiState.value.selected.id)
        assertFalse("selecting an award closes the dropdown", vm.uiState.value.dropdownExpanded)
    }

    @Test
    fun setDropdownExpanded_togglesFlag() {
        val vm = vmWith(null)
        vm.setDropdownExpanded(true)
        assertTrue(vm.uiState.value.dropdownExpanded)
        vm.setDropdownExpanded(false)
        assertFalse(vm.uiState.value.dropdownExpanded)
    }

    @Test
    fun toggleLanguage_flipsVnEn() {
        val vm = vmWith(null)
        assertEquals(AppLanguage.VN, vm.uiState.value.language)
        vm.toggleLanguage()
        assertEquals(AppLanguage.EN, vm.uiState.value.language)
        vm.toggleLanguage()
        assertEquals(AppLanguage.VN, vm.uiState.value.language)
    }
}
