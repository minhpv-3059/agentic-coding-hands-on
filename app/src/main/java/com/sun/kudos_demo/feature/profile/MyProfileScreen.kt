package com.sun.kudos_demo.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.profile.components.ProfileHeader
import com.sun.kudos_demo.feature.profile.components.ProfileIconCollection
import com.sun.kudos_demo.feature.profile.components.ProfileKudosFilter
import com.sun.kudos_demo.feature.profile.components.ProfileSectionHeader
import com.sun.kudos_demo.feature.profile.components.ProfileStatsCard
import com.sun.kudos_demo.ui.components.BottomNavTab
import com.sun.kudos_demo.ui.components.KudosBottomNav
import com.sun.kudos_demo.ui.components.KudosCard
import com.sun.kudos_demo.ui.components.KudosTopBar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground

/**
 * Presentational "Profile của tôi" screen — pixel-perfect to design hSH7L8doXB.
 *
 * Layout (top → bottom):
 *  1. KudosTopBar  — fixed at top (Box overlay)
 *  2. Key-visual background (bg_home_keyvisual) filling ~288dp from top
 *  3. LazyColumn scrollable content:
 *       • Hero spacer (clears top bar ~100dp) + ProfileHeader (avatar + name + badge)
 *       • ProfileIconCollection (6 dark circles + label)
 *       • ProfileStatsCard (stats + Mở Secret Box button)
 *       • ProfileSectionHeader ("Sun* Annual Awards 2025" / "KUDOS")
 *       • ProfileKudosFilter (pill dropdown)
 *       • KudosCard list
 *  4. KudosBottomNav — fixed at bottom
 *
 * All params are pure data / lambdas — no ViewModel, no navigation calls.
 */
@Composable
fun MyProfileScreen(
    name: String,
    teamCode: String,
    badge: String,
    stats: ProfileStats,
    iconSlotCount: Int = 6,
    selectedFilter: ProfileKudosTab,
    receivedCount: Int,
    sentCount: Int,
    kudos: List<Kudo>,
    likedIds: Set<String>,
    currentLanguage: String,
    unreadCount: Int,
    selectedTab: BottomNavTab,
    onFilterChange: (ProfileKudosTab) -> Unit,
    onOpenSecretBox: () -> Unit,
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onLanguage: () -> Unit,
    onTabSelected: (BottomNavTab) -> Unit,
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
        // Key-visual background (same drawable as Home) — full-bleed behind the whole screen so
        // it shows from the header down and fades to dark navy for the lower content (design).
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Scrollable main content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 0.dp,
                bottom = 80.dp  // clear bottom nav
            )
        ) {
            // Spacer to clear top bar + status bar area (~100dp) before hero content
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }

            // Hero section — ProfileHeader centered over key-visual
            item {
                ProfileHeader(
                    name = name,
                    teamCode = teamCode,
                    badge = badge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Icon collection row (6 dark circles)
            item {
                ProfileIconCollection(
                    slotCount = iconSlotCount,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Stats card (design: full-width 336dp → 20dp side padding each side)
            item {
                ProfileStatsCard(
                    stats = stats,
                    onOpenSecretBox = onOpenSecretBox,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // Section header "Sun* Annual Awards 2025" / "KUDOS"
            item {
                ProfileSectionHeader(
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Kudos filter pill
            item {
                ProfileKudosFilter(
                    selectedTab = selectedFilter,
                    receivedCount = receivedCount,
                    sentCount = sentCount,
                    onFilterChange = onFilterChange,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Kudos list
            items(kudos, key = { it.id }) { kudo ->
                KudosCard(
                    kudo = kudo,
                    isLiked = kudo.id in likedIds,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp),
                    onLike = { onLike(kudo) },
                    onCopyLink = { onCopyLink(kudo) },
                    onDetail = { onKudoDetail(kudo) },
                    onSenderClick = { user -> onUserClick(user) },
                    onRecipientClick = { user -> onUserClick(user) },
                    onHashtagClick = onHashtagClick
                )
            }
        }

        // Top bar — overlays the key-visual (same z-order pattern as Home/Feed)
        KudosTopBar(
            currentLanguage = currentLanguage,
            unreadCount = unreadCount,
            showScrim = false, // full-bleed key-visual shows behind the header
            onSearchClick = onSearch,
            onNotificationClick = onNotifications,
            onLanguageClick = onLanguage,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Bottom nav — pinned at bottom
        KudosBottomNav(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(
    showBackground = true,
    backgroundColor = 0xFF00101A,
    device = "spec:width=375dp,height=812dp,dpi=320"
)
@Composable
private fun MyProfileScreenPreview() {
    // Mock data straight from Figma design content
    val mockStats = ProfileStats(
        kudosReceived = 5,
        kudosSent = 25,
        heartsReceived = 25,
        secretBoxOpened = 25,
        secretBoxUnopened = 25
    )
    val mockKudos = KudosMockData.kudos.take(3)

    KudosAppTheme {
        MyProfileScreen(
            name = "Huỳnh Dương Xuân Nhật",
            teamCode = "CEVC3",
            badge = "Legend Hero",
            stats = mockStats,
            iconSlotCount = 6,
            selectedFilter = ProfileKudosTab.SENT,
            receivedCount = 5,
            sentCount = 5,
            kudos = mockKudos,
            likedIds = emptySet(),
            currentLanguage = "VN",
            unreadCount = 1,
            selectedTab = BottomNavTab.Profile,
            onFilterChange = {},
            onOpenSecretBox = {},
            onSearch = {},
            onNotifications = {},
            onLanguage = {},
            onTabSelected = {},
            onKudoDetail = {},
            onLike = {},
            onCopyLink = {},
            onUserClick = {},
            onHashtagClick = {}
        )
    }
}
