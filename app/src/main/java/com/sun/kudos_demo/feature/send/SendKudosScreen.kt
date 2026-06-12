package com.sun.kudos_demo.feature.send

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.send.components.SendKudosFormContent
import com.sun.kudos_demo.ui.components.BottomNavTab
import com.sun.kudos_demo.ui.components.KudosBottomNav
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

// State model lives in SendKudosUiState.kt (same package).
// Form body lives in components/SendKudosFormContent.kt.
// Previews live in SendKudosScreenPreviews.kt.

/**
 * Send Kudos screen — stateless orchestrator.
 * Owns the Scaffold (top bar + bottom nav) and delegates the scrollable
 * form body to [SendKudosFormContent].
 *
 * Track B wires this to [SendKudosViewModel] via the route composable in KudosFeedNavigation.kt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendKudosScreen(
    uiState: SendKudosUiState,
    onBack: () -> Unit,
    onRecipientQueryChange: (String) -> Unit,
    onRecipientDropdownToggle: (Boolean) -> Unit,
    onRecipientSelect: (KudoUser) -> Unit,
    onDanhHieuDropdownToggle: (Boolean) -> Unit,
    onDanhHieuSelect: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onToggleFormat: (String) -> Unit,
    onHashtagDropdownToggle: (Boolean) -> Unit,
    onHashtagToggle: (String) -> Unit,
    onHashtagRemove: (String) -> Unit,
    onAddImageClick: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    onAnonymousToggle: (Boolean) -> Unit,
    onNicknameChange: (String) -> Unit,
    onCommunityStandardsClick: () -> Unit,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Kudo",
                        style = MaterialTheme.typography.titleLarge,
                        color = KudosGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = KudosWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            KudosBottomNav(selectedTab = BottomNavTab.Kudos, onTabSelected = {})
        },
        containerColor = KudosBackground,
        modifier = modifier
    ) { innerPadding ->
        SendKudosFormContent(
            uiState = uiState,
            onRecipientQueryChange = onRecipientQueryChange,
            onRecipientDropdownToggle = onRecipientDropdownToggle,
            onRecipientSelect = onRecipientSelect,
            onDanhHieuDropdownToggle = onDanhHieuDropdownToggle,
            onDanhHieuSelect = onDanhHieuSelect,
            onMessageChange = onMessageChange,
            onToggleFormat = onToggleFormat,
            onHashtagDropdownToggle = onHashtagDropdownToggle,
            onHashtagToggle = onHashtagToggle,
            onHashtagRemove = onHashtagRemove,
            onAddImageClick = onAddImageClick,
            onRemoveImage = onRemoveImage,
            onAnonymousToggle = onAnonymousToggle,
            onNicknameChange = onNicknameChange,
            onCommunityStandardsClick = onCommunityStandardsClick,
            onCancel = onCancel,
            onSubmit = onSubmit,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}
