package com.sun.kudos_demo.feature.send

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.send.components.KudoPreviewDialog
import com.sun.kudos_demo.feature.send.components.SendKudosFormContent
import com.sun.kudos_demo.ui.components.BottomNavTab
import com.sun.kudos_demo.ui.components.KudosBottomNav
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosWhite

// State model lives in SendKudosUiState.kt (same package).
// Form body lives in components/SendKudosFormContent.kt.
// Previews live in SendKudosScreenPreviews.kt.

/**
 * Send Kudos screen — stateless orchestrator.
 * Owns the Scaffold (top bar + bottom nav) and delegates the scrollable
 * form body to [SendKudosFormContent].
 *
 * Background: keyvisual BG image + shadow gradients — matches design nodes 6885:9884-9887.
 * Title "New Kudo": centered, white, 17sp/Medium — design node 6885:9894.
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
    onPreviewKudo: () -> Kudo,
    modifier: Modifier = Modifier
) {
    // Preview dialog state — local to this composable (no VM state needed)
    var previewKudo by remember { mutableStateOf<Kudo?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        // Design node 6885:9884: keyvisual BG image fills the full screen
        // The image is positioned at the right-hand side (x=368) in Figma and extends
        // beyond the artboard — we crop it on the right using ContentScale.Crop aligned End.
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    // Design node 6885:9894: title centered, white, 17sp/Medium
                    title = {
                        Text(
                            text = "New Kudo",
                            style = MaterialTheme.typography.titleMedium,
                            color = KudosWhite,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
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
                    // Transparent so the keyvisual shows through
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                KudosBottomNav(selectedTab = BottomNavTab.Kudos, onTabSelected = {})
            },
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
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
                onPreview = {
                    previewKudo = onPreviewKudo()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
    }

    // Preview dialog — shown when user taps "Xem trước"
    previewKudo?.let { kudo ->
        KudoPreviewDialog(
            kudo = kudo,
            onDismiss = { previewKudo = null }
        )
    }
}
