package com.sun.kudos_demo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sun.kudos_demo.feature.secretbox.SecretBoxScreen
import com.sun.kudos_demo.feature.secretbox.SecretBoxViewModel

/**
 * Route-level composable for the Secret Box feature (Phase 09): binds the stateless
 * [SecretBoxScreen] (Track A UI) to its [SecretBoxViewModel] and the app's navigation.
 *
 * Entry points: the Profile "Mở Secret Box 🎁" button and the "Secret Box" notification both
 * navigate to [NavRoutes.SECRET_BOX]. Detail flow — back arrow pops, no bottom nav.
 */
@Composable
fun SecretBoxRoute(navController: NavHostController) {
    val vm: SecretBoxViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    SecretBoxScreen(
        phase = state.phase,
        unopenedCount = state.unopenedCount,
        allOpened = state.allOpened,
        reward = state.reward,
        onBack = { navController.popBackStack() },
        onBoxTap = vm::onBoxTap,
        onOpenAnimationEnd = vm::onOpenAnimationEnd,
        onContinue = vm::onContinue
    )
}
