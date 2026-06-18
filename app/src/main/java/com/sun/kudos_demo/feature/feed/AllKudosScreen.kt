package com.sun.kudos_demo.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.components.EmptyKudosHint
import com.sun.kudos_demo.ui.components.KudosCard
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * All Kudos screen — stateless / presentational.
 * Design: [iOS] Sun*Kudos_All Kudos (screen j_a2GQWKDJ).
 *
 * Layout:
 *   - Small top bar: back button + "All Kudos" title centred
 *   - "ALL KUDOS" gold heading
 *   - LazyColumn of KudosCard(compact=false)
 */
@Composable
fun AllKudosScreen(
    allKudos: List<Kudo>,
    likedKudoIds: Set<String>,
    currentUserId: String = "",
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onKudoDetail: (String) -> Unit = {},
    onToggleLike: (String) -> Unit = {},
    onCopyLink: (String) -> Unit = {},
    onSenderClick: (KudoUser) -> Unit = {},
    onRecipientClick: (KudoUser) -> Unit = {},
    onHashtagClick: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        // --- Top bar ---
        AllKudosTopBar(onBack = onBack)

        HorizontalDivider(color = KudosDivider, thickness = 1.dp)

        // --- Heading ---
        Text(
            text = stringResource(R.string.feed_screen_heading_all_kudos),
            style = MaterialTheme.typography.headlineMedium,
            color = KudosGold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // --- List ---
        if (allKudos.isEmpty()) {
            EmptyKudosHint(modifier = Modifier.padding(horizontal = 20.dp))
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp
                )
            ) {
                items(allKudos, key = { it.id }) { kudo ->
                    KudosCard(
                        kudo = kudo,
                        isLiked = kudo.id in likedKudoIds,
                        canLike = kudo.sender?.id != currentUserId,
                        compact = false,
                        onLike = { onToggleLike(kudo.id) },
                        onCopyLink = { onCopyLink(kudo.id) },
                        onDetail = { onKudoDetail(kudo.id) },
                        onSenderClick = onSenderClick,
                        onRecipientClick = onRecipientClick,
                        onHashtagClick = onHashtagClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AllKudosTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(KudosBackground)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back button — left aligned
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.feed_nav_back_desc),
                    tint = KudosWhite
                )
            }
        }
        // Title — centred
        Text(
            text = stringResource(R.string.feed_screen_title_all_kudos),
            style = MaterialTheme.typography.titleLarge,
            color = KudosWhite
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, device = "id:pixel_5")
@Composable
private fun AllKudosScreenPreview() {
    KudosAppTheme {
        AllKudosScreen(
            allKudos = KudosMockData.kudos,
            likedKudoIds = setOf("k2")
        )
    }
}
