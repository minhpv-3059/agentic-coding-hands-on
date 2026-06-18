package com.sun.kudos_demo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sun.kudos_demo.feature.awards.AwardViewModel
import com.sun.kudos_demo.feature.awards.AwardsScreen

/**
 * Route-level composable for the Awards tab (Phase 10). Binds the stateless [AwardsScreen]
 * to [AwardViewModel] and the app navigation. The optional `award` route arg pre-selects an
 * award type; absent → MVP.
 */
@Composable
fun AwardsRoute(navController: NavHostController) {
    val vm: AwardViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    AwardsScreen(
        uiState = state,
        onAwardSelect = vm::selectAward,
        onDropdownExpandedChange = vm::setDropdownExpanded,
        onLanguageClick = vm::toggleLanguage,
        onSearch = { navController.navigateSingleTop(NavRoutes.SEARCH) },
        onNotifications = { navController.navigateSingleTop(NavRoutes.NOTIFICATIONS) },
        onKudosDetail = { navController.navigateSingleTop(NavRoutes.RULES) }
    )
}
