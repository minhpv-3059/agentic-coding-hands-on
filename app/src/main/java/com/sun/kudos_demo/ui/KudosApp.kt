package com.sun.kudos_demo.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sun.kudos_demo.navigation.AppNavGraph
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

    val selectedTab = BottomNavTab.entries.firstOrNull { it.route == currentRoute }
    val showBottomBar = selectedTab != null

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                KudosBottomNav(
                    selectedTab = selectedTab,
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
