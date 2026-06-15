package com.sun.kudos_demo.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.sun.kudos_demo.feature.feed.AllKudosScreen
import com.sun.kudos_demo.feature.feed.KudosFeedScreen
import com.sun.kudos_demo.feature.feed.KudosFeedViewModel
import com.sun.kudos_demo.data.KudosRepository
import com.sun.kudos_demo.feature.feed.KudosSearchScreen
import com.sun.kudos_demo.feature.feed.KudosSearchViewModel
import com.sun.kudos_demo.feature.feed.SpotlightMockData
import com.sun.kudos_demo.feature.feed.ViewKudoScreen
import com.sun.kudos_demo.feature.feed.components.SpotlightBoard
import com.sun.kudos_demo.ui.components.DepartmentFilterDropdown
import com.sun.kudos_demo.ui.components.HashtagFilterDropdown

/**
 * Route-level composables for the Kudos Feed feature. Each binds a stateless screen
 * (built by the Track A UI agents) to its ViewModel and the app's navigation, and injects
 * the cross-cutting slots — the filter dropdowns and the Spotlight network chart.
 * Kept out of [AppNavGraph] so that file stays small.
 */
@Composable
fun KudosFeedRoute(navController: NavHostController, backStackEntry: NavBackStackEntry) {
    val vm: KudosFeedViewModel = viewModel()
    val state by vm.uiState.collectAsState()
    var hashtagExpanded by remember { mutableStateOf(false) }
    var deptExpanded by remember { mutableStateOf(false) }
    val copyLink = rememberCopyLink()

    // A hashtag tapped on the View / All-Kudos screens is handed back here via savedStateHandle.
    LaunchedEffect(Unit) {
        backStackEntry.savedStateHandle.getStateFlow<String?>(PENDING_HASHTAG, null).collect { tag ->
            if (tag != null) {
                vm.applyHashtag(tag)
                backStackEntry.savedStateHandle[PENDING_HASHTAG] = null
            }
        }
    }

    KudosFeedScreen(
        highlightKudos = state.highlightKudos,
        allKudos = state.allKudos,
        stats = state.stats,
        giftRecipients = state.giftRecipients,
        likedKudoIds = state.likedKudoIds,
        currentUserId = state.currentUserId,
        currentLanguage = state.language.code,
        unreadCount = state.unreadCount,
        filterRow = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HashtagFilterDropdown(
                    items = state.hashtags,
                    selected = state.selectedHashtag,
                    expanded = hashtagExpanded,
                    onExpandedChange = { hashtagExpanded = it },
                    onSelect = { vm.selectHashtag(it); hashtagExpanded = false },
                    modifier = Modifier.weight(1f)
                )
                DepartmentFilterDropdown(
                    items = state.departments,
                    selected = state.selectedDepartment,
                    expanded = deptExpanded,
                    onExpandedChange = { deptExpanded = it },
                    onSelect = { vm.selectDepartment(it); deptExpanded = false },
                    modifier = Modifier.weight(1f)
                )
            }
        },
        spotlight = { SpotlightBoard(data = SpotlightMockData.data) },
        onSendKudos = { navController.navigateSingleTop(NavRoutes.KUDOS_SEND) },
        onSearch = { navController.navigateSingleTop(NavRoutes.SEARCH) },
        onNotifications = { navController.navigateSingleTop(NavRoutes.NOTIFICATIONS) },
        onLanguageClick = vm::toggleLanguage,
        onKudoDetail = { id -> navController.navigateSingleTop(NavRoutes.kudosView(id)) },
        onToggleLike = vm::toggleLike,
        onCopyLink = copyLink,
        onSenderClick = { user -> navController.navigateSingleTop(NavRoutes.profileUser(user.id)) },
        onRecipientClick = { user -> navController.navigateSingleTop(NavRoutes.profileUser(user.id)) },
        onHashtagClick = vm::applyHashtag,
        onOpenSecretBox = { navController.navigateSingleTop(NavRoutes.SECRET_BOX) },
        onViewAllKudos = { navController.navigateSingleTop(NavRoutes.KUDOS_ALL) },
        onGiftRecipientClick = { gr -> navController.navigateSingleTop(NavRoutes.profileUser(gr.user.id)) }
    )
}

@Composable
fun KudosAllRoute(navController: NavHostController) {
    val vm: KudosFeedViewModel = viewModel()
    val state by vm.uiState.collectAsState()
    val copyLink = rememberCopyLink()

    AllKudosScreen(
        allKudos = state.allKudos,
        likedKudoIds = state.likedKudoIds,
        currentUserId = state.currentUserId,
        onBack = { navController.popBackStack() },
        onKudoDetail = { id -> navController.navigateSingleTop(NavRoutes.kudosView(id)) },
        onToggleLike = vm::toggleLike,
        onCopyLink = copyLink,
        onSenderClick = { user -> navController.navigateSingleTop(NavRoutes.profileUser(user.id)) },
        onRecipientClick = { user -> navController.navigateSingleTop(NavRoutes.profileUser(user.id)) },
        onHashtagClick = { tag -> navController.applyHashtagOnFeed(tag) }
    )
}

@Composable
fun ViewKudoRoute(navController: NavHostController, kudoId: String) {
    val kudo = KudosRepository.kudoById(kudoId)
    if (kudo == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không tìm thấy Kudo #$kudoId", style = MaterialTheme.typography.titleLarge)
        }
        return
    }
    val vm: KudosFeedViewModel = viewModel()
    val state by vm.uiState.collectAsState()
    val copyLink = rememberCopyLink()

    ViewKudoScreen(
        kudo = kudo,
        isLiked = kudo.id in state.likedKudoIds,
        onBack = { navController.popBackStack() },
        onToggleLike = { vm.toggleLike(kudo.id) },
        onCopyLink = { copyLink(kudo.id) },
        onSenderClick = { user -> navController.navigateSingleTop(NavRoutes.profileUser(user.id)) },
        onRecipientClick = { user -> navController.navigateSingleTop(NavRoutes.profileUser(user.id)) },
        onHashtagClick = { tag -> navController.applyHashtagOnFeed(tag) },
        onImageClick = { /* full-screen image viewer — out of scope for this phase */ }
    )
}

@Composable
fun KudosSearchRoute(navController: NavHostController) {
    val vm: KudosSearchViewModel = viewModel()
    val query by vm.query.collectAsState()
    val results by vm.results.collectAsState()
    val recent by vm.recent.collectAsState()

    KudosSearchScreen(
        query = query,
        onQueryChange = vm::updateQuery,
        results = results,
        recent = recent,
        onBack = { navController.popBackStack() },
        onResultClick = { user ->
            vm.selectResult(user)
            navController.navigateSingleTop(NavRoutes.profileUser(user.id))
        },
        onRemoveRecent = vm::removeRecent,
        onViewAllRecent = { }
    )
}

/** Copies a mock kudo share URL to the clipboard and shows the design's confirmation toast. */
@Composable
private fun rememberCopyLink(): (String) -> Unit {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    return remember(context, clipboard) {
        { id: String ->
            clipboard.setText(AnnotatedString("https://kudos.sun-asterisk.com/kudos/$id"))
            Toast.makeText(context, "Link copied — ready to share!", Toast.LENGTH_SHORT).show()
        }
    }
}

/** Navigate without stacking duplicate destinations on rapid taps. */
internal fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) { launchSingleTop = true }
}

private const val PENDING_HASHTAG = "pending_hashtag"

/**
 * Apply a hashtag filter on the Feed from a secondary screen (View / All Kudos): stash the tag
 * on the Feed's back-stack entry and pop back to it so its existing ViewModel picks it up
 * (TC_FUN_016/031). Falls back to a plain navigate if the Feed isn't on the back stack.
 */
private fun NavHostController.applyHashtagOnFeed(tag: String) {
    val feedEntry = runCatching { getBackStackEntry(NavRoutes.KUDOS_FEED) }.getOrNull()
    if (feedEntry != null) {
        feedEntry.savedStateHandle[PENDING_HASHTAG] = tag
        popBackStack(NavRoutes.KUDOS_FEED, inclusive = false)
    } else {
        navigateSingleTop(NavRoutes.KUDOS_FEED)
    }
}
