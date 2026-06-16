package com.sun.kudos_demo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sun.kudos_demo.feature.notifications.AppNotification
import com.sun.kudos_demo.feature.notifications.NotificationType
import com.sun.kudos_demo.feature.notifications.NotificationsScreen
import com.sun.kudos_demo.feature.notifications.NotificationsViewModel

/**
 * Route-level composable for the Notifications feature: binds the stateless
 * [NotificationsScreen] (Track A UI) to its ViewModel and the app's navigation.
 *
 * Tap a notification → mark it read (badge decrements) then navigate to the screen for
 * its type. Targets per clarifications.md (Session 2026-06-16): kudo types → View Kudo;
 * Level-up / Badge → my Profile; Secret Box → placeholder (Phase 09); Admin review is
 * out of app scope (mark-read only). The inline "Tiêu chuẩn cộng đồng" link opens the
 * Community Standards screen.
 */
@Composable
fun NotificationsRoute(navController: NavHostController) {
    val vm: NotificationsViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    NotificationsScreen(
        title = state.title,
        notifications = state.notifications,
        onBack = { navController.popBackStack() },
        onItemClick = { notif ->
            vm.markRead(notif.id)
            navController.navigateForNotification(notif)
        },
        onMarkAllRead = vm::markAllRead,
        onStandardsLink = { notification ->
            // Tapping the inline link also marks the item read (its own clickable swallows
            // the row click, so the row's mark-read never fires).
            vm.markRead(notification.id)
            navController.navigateSingleTop(NavRoutes.KUDOS_COMMUNITY_STANDARDS)
        }
    )
}

/** Open the destination screen for a tapped notification (see [NotificationType]). */
private fun NavHostController.navigateForNotification(notification: AppNotification) {
    when (notification.type) {
        NotificationType.KUDOS_RECEIVED,
        NotificationType.HEART_RECEIVED,
        NotificationType.CONTENT_HIDDEN ->
            notification.targetId?.let { navigateSingleTop(NavRoutes.kudosView(it)) }

        NotificationType.SECRET_BOX -> navigateSingleTop(NavRoutes.SECRET_BOX)

        NotificationType.LEVEL_UP,
        NotificationType.BADGE_COLLECTED -> navigateSingleTop(NavRoutes.PROFILE_ME)

        // Admin "review content" screen is out of this app's scope — mark-read only, no nav.
        NotificationType.REVIEW_REQUEST -> Unit
    }
}
