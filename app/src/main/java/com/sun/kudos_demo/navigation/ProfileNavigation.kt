package com.sun.kudos_demo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sun.kudos_demo.feature.profile.MyProfileScreen
import com.sun.kudos_demo.feature.profile.MyProfileViewModel
import com.sun.kudos_demo.feature.profile.UserProfileScreen
import com.sun.kudos_demo.feature.profile.UserProfileViewModel
import com.sun.kudos_demo.ui.components.BottomNavTab

/**
 * Route-level composables for the Profile feature (Phase 07). Each binds a stateless screen
 * (built by the Track A UI agents) to its ViewModel and the app navigation. Kept out of
 * [AppNavGraph] so that file stays small.
 */
@Composable
fun MyProfileRoute(navController: NavHostController) {
    val vm: MyProfileViewModel = viewModel()
    val state by vm.uiState.collectAsState()
    val copyLink = rememberCopyLink()

    MyProfileScreen(
        name = state.user.name,
        teamCode = state.user.code,
        badge = state.user.badge ?: "Legend Hero",
        stats = state.stats,
        selectedFilter = state.filter,
        receivedCount = state.receivedCount,
        sentCount = state.sentCount,
        kudos = state.visibleKudos,
        likedIds = state.likedIds,
        currentLanguage = state.language.code,
        unreadCount = state.unreadCount,
        selectedTab = BottomNavTab.Profile,
        onFilterChange = vm::setFilter,
        onOpenSecretBox = { navController.navigateSingleTop(NavRoutes.SECRET_BOX) },
        onSearch = { navController.navigateSingleTop(NavRoutes.SEARCH) },
        onNotifications = { navController.navigateSingleTop(NavRoutes.NOTIFICATIONS) },
        onLanguage = vm::toggleLanguage,
        onTabSelected = navController::navigateBottomNav,
        onKudoDetail = { kudo -> navController.navigateSingleTop(NavRoutes.kudosView(kudo.id)) },
        onLike = { kudo -> vm.toggleLike(kudo.id) },
        onCopyLink = { kudo -> copyLink(kudo.id) },
        onUserClick = { user -> navController.navigateSingleTop(NavRoutes.profileUser(user.id)) },
        onHashtagClick = { tag -> navController.applyHashtagOnFeed(tag) }
    )
}

@Composable
fun UserProfileRoute(navController: NavHostController) {
    val vm: UserProfileViewModel = viewModel()
    val state by vm.uiState.collectAsState()
    val copyLink = rememberCopyLink()

    UserProfileScreen(
        name = state.user.name,
        teamCode = state.user.code,
        badge = state.badge,
        recipientName = state.user.name,
        badges = state.badges,
        receivedCount = state.receivedCount,
        kudos = state.receivedKudos,
        likedIds = state.likedIds,
        currentLanguage = state.language.code,
        unreadCount = state.unreadCount,
        selectedTab = BottomNavTab.Profile,
        onSendKudos = { navController.navigateSingleTop(NavRoutes.kudosSend(state.user.id)) },
        onSearch = { navController.navigateSingleTop(NavRoutes.SEARCH) },
        onNotifications = { navController.navigateSingleTop(NavRoutes.NOTIFICATIONS) },
        onLanguage = vm::toggleLanguage,
        onTabSelected = navController::navigateBottomNav,
        onKudoDetail = { kudo -> navController.navigateSingleTop(NavRoutes.kudosView(kudo.id)) },
        onLike = { kudo -> vm.toggleLike(kudo.id) },
        onCopyLink = { kudo -> copyLink(kudo.id) },
        onUserClick = { user -> navController.navigateSingleTop(NavRoutes.profileUser(user.id)) },
        onHashtagClick = { tag -> navController.applyHashtagOnFeed(tag) }
    )
}

/** Switch bottom-nav sections, preserving each tab's back stack (saveState/restoreState). */
internal fun NavHostController.navigateBottomNav(tab: BottomNavTab) {
    navigate(tab.route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
