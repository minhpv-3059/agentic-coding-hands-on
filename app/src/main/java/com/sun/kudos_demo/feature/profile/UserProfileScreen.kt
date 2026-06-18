package com.sun.kudos_demo.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.feature.profile.components.ProfileAwardBadges
import com.sun.kudos_demo.feature.profile.components.ProfileReceivedKudosLabel
import com.sun.kudos_demo.feature.profile.components.ProfileSendKudosCta
import com.sun.kudos_demo.feature.profile.components.UserProfileHeader
import com.sun.kudos_demo.feature.profile.components.UserProfileSectionHeader
import com.sun.kudos_demo.ui.components.KudosCard
import com.sun.kudos_demo.ui.components.KudosTopBar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Presentational screen: another Sunner's profile ("Profile người khác").
 * Design frame 6885:10395, background #00101A.
 *
 * Unlike the own-profile screen, this is a detail view reached from search/feed: it shows a
 * **back arrow** in the header and **no bottom navigation** (per UX flow). The key-visual is
 * full-bleed behind the whole screen. NO ViewModel / navigation calls — lambdas wired by the route.
 */
@Composable
fun UserProfileScreen(
    name: String,
    teamCode: String,
    badge: String,
    recipientName: String,
    badges: List<AwardBadge>,
    receivedCount: Int,
    kudos: List<Kudo>,
    likedIds: Set<String>,
    currentLanguage: String,
    unreadCount: Int,
    onBack: () -> Unit,
    onSendKudos: () -> Unit,
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onLanguage: () -> Unit,
    onKudoDetail: (Kudo) -> Unit,
    onLike: (Kudo) -> Unit,
    onCopyLink: (Kudo) -> Unit,
    onUserClick: (KudoUser) -> Unit,
    onHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        // Full-bleed key-visual (shows behind header, fades to dark navy for lower content)
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        UserProfileContent(
            name = name, teamCode = teamCode, badge = badge,
            recipientName = recipientName, badges = badges,
            receivedCount = receivedCount, kudos = kudos, likedIds = likedIds,
            onSendKudos = onSendKudos,
            onKudoDetail = onKudoDetail, onLike = onLike, onCopyLink = onCopyLink,
            onUserClick = onUserClick, onHashtagClick = onHashtagClick
        )

        // Detail-screen header: back arrow + no scrim so the key-visual shows behind it
        KudosTopBar(
            unreadCount = unreadCount,
            showScrim = false,
            onBack = onBack,
            onSearchClick = onSearch,
            onNotificationClick = onNotifications,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/** Scrollable body extracted to keep [UserProfileScreen] readable. */
@Composable
private fun UserProfileContent(
    name: String,
    teamCode: String,
    badge: String,
    recipientName: String,
    badges: List<AwardBadge>,
    receivedCount: Int,
    kudos: List<Kudo>,
    likedIds: Set<String>,
    onSendKudos: () -> Unit,
    onKudoDetail: (Kudo) -> Unit,
    onLike: (Kudo) -> Unit,
    onCopyLink: (Kudo) -> Unit,
    onUserClick: (KudoUser) -> Unit,
    onHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // No bottom nav here — pad the list past the system navigation-bar inset so the last card
    // isn't hidden behind the gesture bar.
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp + navBarInset)
    ) {
        // Spacer clears the overlaid top bar (status bar + 56dp app bar)
        item { Spacer(Modifier.height(100.dp)) }

        // Hero member block (node 6885:10401), centered over the key-visual
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                UserProfileHeader(name = name, teamCode = teamCode, badge = badge)
            }
        }

        // Award badges (node 6885:10411)
        item {
            Spacer(Modifier.height(16.dp))
            ProfileAwardBadges(badges = badges)
        }

        // "Bộ sưu tập icon của tôi" (node 6885:10426) — 14sp medium white centered
        item {
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.profile_icon_collection_label),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp, lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium, color = KudosWhite
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }

        // CTA button (node 6885:10427) — h-margin 20dp
        item {
            ProfileSendKudosCta(
                recipientName = recipientName,
                onSendKudos = onSendKudos,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        // Section header (node 6885:10418)
        item {
            UserProfileSectionHeader()
            Spacer(Modifier.height(16.dp))
        }

        // Received-kudos label (node 6885:10419) — left-aligned pill
        item {
            ProfileReceivedKudosLabel(
                receivedCount = receivedCount,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        // Kudos list (nodes 6885:10421–10425) — gap 12dp
        items(kudos, key = { it.id }) { kudo ->
            KudosCard(
                kudo = kudo,
                isLiked = kudo.id in likedIds,
                modifier = Modifier.padding(horizontal = 20.dp),
                onLike = { onLike(kudo) },
                onCopyLink = { onCopyLink(kudo) },
                onDetail = { onKudoDetail(kudo) },
                onSenderClick = { user -> onUserClick(user) },
                onRecipientClick = { user -> onUserClick(user) },
                onHashtagClick = onHashtagClick
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, showSystemUi = true)
@Composable
private fun UserProfileScreenPreview() {
    KudosAppTheme {
        UserProfileScreen(
            name = "Huỳnh Dương Xuân Nhật", teamCode = "CEVC3", badge = "Rising Hero",
            recipientName = "Huỳnh Dương Xuân Nhật",
            badges = ProfileMockData.awardBadges,
            receivedCount = 5,
            kudos = KudosMockData.kudos.take(5),
            likedIds = setOf("k1", "k3"),
            currentLanguage = "VN", unreadCount = 2,
            onBack = {}, onSendKudos = {}, onSearch = {}, onNotifications = {}, onLanguage = {},
            onKudoDetail = {}, onLike = {}, onCopyLink = {}, onUserClick = {}, onHashtagClick = {}
        )
    }
}
