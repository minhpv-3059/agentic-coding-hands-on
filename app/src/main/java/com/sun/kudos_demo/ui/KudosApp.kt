package com.sun.kudos_demo.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sun.kudos_demo.navigation.AppNavGraph
import com.sun.kudos_demo.navigation.NavRoutes
import com.sun.kudos_demo.ui.components.BottomNavTab
import com.sun.kudos_demo.ui.components.KudosBottomNav

/**
 * Root composable: hosts the navigation graph inside a Scaffold and shows the
 * bottom navigation bar only on the top-level tab destinations.
 */
@Composable
fun KudosApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    // Strip any query-arg suffix so tab matching works for optional-arg routes
    // (e.g. the Awards tab registers as "awards?award={award}").
    val baseRoute = currentRoute?.substringBefore("?")

    // Secondary Kudos screens (All Kudos / View / Search) keep the bottom nav with the
    // Kudos tab active, matching the design.
    val kudosFamily = baseRoute in setOf(NavRoutes.KUDOS_ALL, NavRoutes.KUDOS_VIEW, NavRoutes.SEARCH)
    // Profile screens (own + other) render their own bottom bar per design, so suppress the
    // global one here to avoid a duplicate bar.
    val isProfileScreen = baseRoute == NavRoutes.PROFILE_ME || baseRoute == NavRoutes.PROFILE_USER
    val effectiveTab = when {
        isProfileScreen -> null
        else -> BottomNavTab.entries.firstOrNull { it.route == baseRoute }
            ?: if (kudosFamily) BottomNavTab.Kudos else null
    }
    val showBottomBar = effectiveTab != null

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            // Bottom nav hidden on LOGIN screen
            if (showBottomBar) {
                KudosBottomNav(
                    selectedTab = effectiveTab,
                    onTabSelected = { tab -> navController.navigateToTab(tab.route) }
                )
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Switch top-level tabs without stacking duplicates: pop back to the graph
 * start, keep a single instance, and restore previously saved tab state.
 */
private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
