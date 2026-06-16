package com.sun.kudos_demo.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sun.kudos_demo.feature.auth.LoginScreen
import com.sun.kudos_demo.feature.home.HomeScreen
import com.sun.kudos_demo.feature.home.HomeViewModel
import com.sun.kudos_demo.feature.send.CommunityStandardsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavRoutes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.HOME) {
            val homeViewModel: HomeViewModel = viewModel()
            val homeState by homeViewModel.uiState.collectAsState()
            // launchSingleTop prevents duplicate destinations on rapid taps (test FUN_013).
            val navigateOnce: (String) -> Unit = { route ->
                navController.navigate(route) { launchSingleTop = true }
            }
            HomeScreen(
                onAboutAward = { navigateOnce(NavRoutes.AWARDS) },
                onAboutKudos = { navigateOnce(NavRoutes.KUDOS_FEED) },
                onAwardDetail = { navigateOnce(NavRoutes.AWARDS) }, // Award detail = Phase 10
                onKudosDetail = { navigateOnce(NavRoutes.KUDOS_FEED) },
                onSendKudos = { navigateOnce(NavRoutes.KUDOS_SEND) },
                onOpenKudosFeed = { navigateOnce(NavRoutes.KUDOS_FEED) },
                onSearch = { navigateOnce(NavRoutes.SEARCH) },
                onNotifications = { navigateOnce(NavRoutes.NOTIFICATIONS) },
                onLanguageClick = homeViewModel::toggleLanguage,
                days = homeState.countdown.days,
                hours = homeState.countdown.hours,
                minutes = homeState.countdown.minutes,
                currentLanguage = homeState.language.code,
                unreadCount = homeState.unreadNotifications
            )
        }

        composable(NavRoutes.KUDOS_FEED) { entry -> KudosFeedRoute(navController, entry) }
        composable(NavRoutes.KUDOS_ALL) { KudosAllRoute(navController) }
        composable(
            route = NavRoutes.KUDOS_VIEW,
            arguments = listOf(navArgument(NavRoutes.ARG_KUDO_ID) { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString(NavRoutes.ARG_KUDO_ID).orEmpty()
            ViewKudoRoute(navController, id)
        }
        composable(
            route = NavRoutes.KUDOS_SEND_WITH_ARG,
            arguments = listOf(navArgument(NavRoutes.ARG_RECIPIENT) {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            val recipient = entry.arguments?.getString(NavRoutes.ARG_RECIPIENT).orEmpty()
            SendKudosRoute(navController, recipient)
        }
        composable(NavRoutes.KUDOS_COMMUNITY_STANDARDS) {
            CommunityStandardsScreen(onBack = { navController.popBackStack() })
        }

        // PROFILE_ME uses a distinct path ("my-profile") so it can't be captured by the
        // PROFILE_USER wildcard ("profile/{userId}").
        composable(NavRoutes.PROFILE_ME) { MyProfileRoute(navController) }
        composable(
            route = NavRoutes.PROFILE_USER,
            arguments = listOf(navArgument(NavRoutes.ARG_USER_ID) { type = NavType.StringType })
        ) { UserProfileRoute(navController) }

        composable(NavRoutes.NOTIFICATIONS) { NotificationsRoute(navController) }
        composable(NavRoutes.SEARCH) { KudosSearchRoute(navController) }
        composable(NavRoutes.SECRET_BOX) { SecretBoxRoute(navController) }
        composable(NavRoutes.AWARDS) { PlaceholderScreen("Awards") }
        composable(NavRoutes.RULES) { PlaceholderScreen("Rules") }

        composable(NavRoutes.ERROR_403) { PlaceholderScreen("403 — Access Denied") }
        composable(NavRoutes.ERROR_404) { PlaceholderScreen("404 — Not Found") }
    }
}

/** Temporary screen body used until each real screen is implemented. */
@Composable
private fun PlaceholderScreen(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, style = MaterialTheme.typography.titleLarge)
    }
}
