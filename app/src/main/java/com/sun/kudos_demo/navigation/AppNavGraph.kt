package com.sun.kudos_demo.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

/**
 * App navigation graph. Phase 02 wires every route to a placeholder;
 * real screen content arrives in later phases (03–11).
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    // TODO Phase 03: replace with auth-aware start destination (login vs home).
    startDestination: String = NavRoutes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.LOGIN) { PlaceholderScreen("Login") }
        composable(NavRoutes.HOME) { PlaceholderScreen("Home") }

        composable(NavRoutes.KUDOS_FEED) { PlaceholderScreen("Kudos Feed") }
        composable(
            route = NavRoutes.KUDOS_VIEW,
            arguments = listOf(navArgument(NavRoutes.ARG_KUDO_ID) { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString(NavRoutes.ARG_KUDO_ID).orEmpty()
            PlaceholderScreen("View Kudo #$id")
        }
        composable(NavRoutes.KUDOS_SEND) { PlaceholderScreen("Send Kudos") }

        // Literal PROFILE_ME must be registered before PROFILE_USER so "profile/me"
        // matches the literal route and is not captured as userId = "me".
        composable(NavRoutes.PROFILE_ME) { PlaceholderScreen("My Profile") }
        composable(
            route = NavRoutes.PROFILE_USER,
            arguments = listOf(navArgument(NavRoutes.ARG_USER_ID) { type = NavType.StringType })
        ) { entry ->
            val userId = entry.arguments?.getString(NavRoutes.ARG_USER_ID).orEmpty()
            PlaceholderScreen("Profile of $userId")
        }

        composable(NavRoutes.NOTIFICATIONS) { PlaceholderScreen("Notifications") }
        composable(NavRoutes.SECRET_BOX) { PlaceholderScreen("Secret Box") }
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
