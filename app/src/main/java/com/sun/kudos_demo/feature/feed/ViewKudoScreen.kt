package com.sun.kudos_demo.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.components.KudoDetailCard
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosGold

/**
 * View Kudo detail screen — stateless / presentational.
 *
 * Handles both variants with a single composable:
 *   - Normal  : kudo.isAnonymous == false → sender name + code + badge shown
 *   - Ẩn danh : kudo.isAnonymous == true  → KudoAvatar(anonymous=true) +
 *               anonymousAlias + "Người gửi ẩn danh" sub-label
 *
 * Design refs:
 *   - Normal   : https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/T0TR16k0vH
 *   - Ẩn danh  : https://momorph.ai/files/9ypp4enmFmdK3YAFJLIu6C/screens/5C2BL6GYXL
 *
 * Bottom nav is rendered by the app Scaffold — not included here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewKudoScreen(
    kudo: Kudo,
    isLiked: Boolean,
    onBack: () -> Unit,
    onToggleLike: () -> Unit,
    onCopyLink: () -> Unit,
    onSenderClick: (KudoUser) -> Unit,
    onRecipientClick: (KudoUser) -> Unit,
    onHashtagClick: (String) -> Unit,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        // Top bar: back arrow + "Kudo" title centered (design: _TopNavigation-content)
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.feed_screen_title_kudo_detail),
                    style = MaterialTheme.typography.titleLarge,
                    color = KudosGold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.feed_nav_back_desc),
                        tint = KudosGold
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = KudosBackground,
                titleContentColor = KudosGold,
                navigationIconContentColor = KudosGold
            )
        )

        // Scrollable content area — single KudoDetailCard on cream background
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            KudoDetailCard(
                kudo = kudo,
                isLiked = isLiked,
                onToggleLike = onToggleLike,
                onSenderClick = onSenderClick,
                onRecipientClick = onRecipientClick,
                onHashtagClick = onHashtagClick,
                onImageClick = onImageClick,
                onCopyLink = onCopyLink,
                onViewDetail = { /* navigate to public permalink when wired */ }
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

// --- Previews ---------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF00101A, name = "ViewKudo — Normal (k8)")
@Composable
private fun ViewKudoNormalPreview() {
    KudosAppTheme {
        val kudo = KudosMockData.kudoById("k8")
            ?: return@KudosAppTheme
        ViewKudoScreen(
            kudo = kudo,
            isLiked = false,
            onBack = {},
            onToggleLike = {},
            onCopyLink = {},
            onSenderClick = {},
            onRecipientClick = {},
            onHashtagClick = {},
            onImageClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, name = "ViewKudo — Ẩn danh (k4)")
@Composable
private fun ViewKudoAnonymousPreview() {
    KudosAppTheme {
        val kudo = KudosMockData.kudoById("k4")
            ?: return@KudosAppTheme
        ViewKudoScreen(
            kudo = kudo,
            isLiked = true,
            onBack = {},
            onToggleLike = {},
            onCopyLink = {},
            onSenderClick = {},
            onRecipientClick = {},
            onHashtagClick = {},
            onImageClick = {}
        )
    }
}
