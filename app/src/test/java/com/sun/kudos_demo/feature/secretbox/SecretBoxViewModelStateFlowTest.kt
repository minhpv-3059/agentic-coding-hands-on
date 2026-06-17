package com.sun.kudos_demo.feature.secretbox

import com.sun.kudos_demo.data.SecretBoxRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Real StateFlow tests for [SecretBoxViewModel] — observe the live `uiState` produced by the
 * `combine(...) + stateIn(...)` wiring (closes review gap M-02). A collector is launched so the
 * `WhileSubscribed` flow activates and reflects upstream changes synchronously under the
 * UnconfinedTestDispatcher. The shared [SecretBoxRepository] is reset around each test.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SecretBoxViewModelStateFlowTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before fun resetBefore() = SecretBoxRepository.resetForTest()

    @After fun resetAfter() = SecretBoxRepository.resetForTest()

    /**
     * Subscribe to `uiState` on an eager dispatcher tied to the test scheduler so the
     * `WhileSubscribed` upstream activates immediately and `.value` reflects later transitions.
     * backgroundScope auto-cancels at the end of runTest, so no manual job handling.
     */
    private fun TestScope.activate(vm: SecretBoxViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
    }

    @Test
    fun uiState_initial_isClosedWithSeededCount() = runTest {
        val vm = SecretBoxViewModel()
        activate(vm)

        val state = vm.uiState.value
        assertEquals(SecretBoxPhase.CLOSED, state.phase)
        assertEquals(5, state.unopenedCount)   // seed "05"
        assertNull(state.reward)
        assertFalse(state.allOpened)
    }

    @Test
    fun onBoxTap_movesToOpening() = runTest {
        val vm = SecretBoxViewModel()
        activate(vm)

        vm.onBoxTap()
        assertEquals(SecretBoxPhase.OPENING, vm.uiState.value.phase)
    }

    @Test
    fun onOpenAnimationEnd_revealsRewardFromPool() = runTest {
        val vm = SecretBoxViewModel()
        activate(vm)

        vm.onBoxTap()
        vm.onOpenAnimationEnd()
        val state = vm.uiState.value
        assertEquals(SecretBoxPhase.REWARD, state.phase)
        assertNotNull(state.reward)
        assertTrue(state.reward in SecretBoxMockData.rewards)
    }

    @Test
    fun onContinue_decrementsCountAndReturnsToClosed() = runTest {
        val vm = SecretBoxViewModel()
        activate(vm)

        vm.onBoxTap()
        vm.onOpenAnimationEnd()
        vm.onContinue()
        val state = vm.uiState.value
        assertEquals(SecretBoxPhase.CLOSED, state.phase)
        assertEquals(4, state.unopenedCount)   // 5 - 1
        assertNull(state.reward)
    }

    @Test
    fun onBoxTap_isNoOp_whenAllOpened() = runTest {
        // Drain the shared repository to zero, then a fresh VM must stay CLOSED on tap.
        repeat(5) { SecretBoxRepository.openOne() }
        val vm = SecretBoxViewModel()
        activate(vm)

        assertTrue(vm.uiState.value.allOpened)
        vm.onBoxTap()
        assertEquals(SecretBoxPhase.CLOSED, vm.uiState.value.phase)
    }
}

/** Swaps Dispatchers.Main for a test dispatcher so `viewModelScope` works in plain unit tests. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
