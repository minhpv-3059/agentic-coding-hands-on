package com.sun.kudos_demo.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.components.AllKudosSection
import com.sun.kudos_demo.feature.feed.components.FeedHeroBanner
import com.sun.kudos_demo.feature.feed.components.HighlightCarousel
import com.sun.kudos_demo.feature.feed.components.SendKudosPrompt
import com.sun.kudos_demo.feature.home.components.SectionHeader
import com.sun.kudos_demo.ui.components.KudosTopBar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosBgUpdate

/**
 * Kudos Feed main screen — stateless / presentational.
 *
 * Sections (top→bottom):
 *  1. KudosTopBar — pinned overlay (same pattern as HomeScreen)
 *  2. FeedHeroBanner — key-visual + tagline
 *  3. SendKudosPrompt — "Hôm nay, bạn muốn gửi kudos đến ai?"
 *  4. HIGHLIGHT KUDOS — SectionHeader + [filterRow] slot + HighlightCarousel
 *  5. SPOTLIGHT BOARD — SectionHeader + [spotlight] slot
 *  6. ALL KUDOS — AllKudosSection (stats + gift recipients + card list + view-all link)
 *
 * @param filterRow slot for HashtagFilterDropdown + DepartmentFilterDropdown (owned by another agent)
 * @param spotlight slot for SpotlightBoard network chart (owned by another agent)
 */
@Composable
fun KudosFeedScreen(
    highlightKudos: List<Kudo>,
    allKudos: List<Kudo>,
    stats: KudoStats,
    giftRecipients: List<GiftRecipient>,
    likedKudoIds: Set<String>,
    currentUserId: String = "",
    currentLanguage: String,
    unreadCount: Int,
    filterRow: @Composable () -> Unit,
    spotlight: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onSendKudos: () -> Unit = {},
    onSearch: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onKudoDetail: (String) -> Unit = {},
    onToggleLike: (String) -> Unit = {},
    onCopyLink: (String) -> Unit = {},
    onSenderClick: (KudoUser) -> Unit = {},
    onRecipientClick: (KudoUser) -> Unit = {},
    onHashtagClick: (String) -> Unit = {},
    onOpenSecretBox: () -> Unit = {},
    onViewAllKudos: () -> Unit = {},
    onGiftRecipientClick: (GiftRecipient) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Hero banner — TopBar overlays from z-ordering
            FeedHeroBanner()

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // Send kudos prompt
                SendKudosPrompt(onSendKudos = onSendKudos)

                // --- HIGHLIGHT KUDOS ---
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader(
                        eyebrow = stringResource(R.string.feed_section_eyebrow),
                        title = stringResource(R.string.feed_section_highlight_kudos)
                    )
                    // Filter dropdowns slot — owned by filter agent
                    filterRow()
                    HighlightCarousel(
                        kudos = highlightKudos,
                        likedKudoIds = likedKudoIds,
                        currentUserId = currentUserId,
                        onToggleLike = onToggleLike,
                        onCopyLink = onCopyLink,
                        onKudoDetail = onKudoDetail,
                        onSenderClick = onSenderClick,
                        onRecipientClick = onRecipientClick,
                        onHashtagClick = onHashtagClick
                    )
                }

                // --- SPOTLIGHT BOARD ---
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader(
                        eyebrow = stringResource(R.string.feed_section_eyebrow),
                        title = stringResource(R.string.feed_section_spotlight_board)
                    )
                    // Spotlight network chart — owned by spotlight agent
                    spotlight()
                }

                // --- ALL KUDOS ---
                AllKudosSection(
                    kudos = allKudos,
                    stats = stats,
                    giftRecipients = giftRecipients,
                    likedKudoIds = likedKudoIds,
                    currentUserId = currentUserId,
                    onOpenSecretBox = onOpenSecretBox,
                    onViewAllKudos = onViewAllKudos,
                    onToggleLike = onToggleLike,
                    onCopyLink = onCopyLink,
                    onKudoDetail = onKudoDetail,
                    onSenderClick = onSenderClick,
                    onRecipientClick = onRecipientClick,
                    onHashtagClick = onHashtagClick,
                    onGiftRecipientClick = onGiftRecipientClick
                )

                // Bottom padding — clears nav bar
                Spacer(Modifier.height(80.dp))
            }
        }

        // KudosTopBar pinned at top — overlays hero (same pattern as HomeScreen)
        KudosTopBar(
            unreadCount = unreadCount,
            onSearchClick = onSearch,
            onNotificationClick = onNotifications,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, device = "id:pixel_5")
@Composable
private fun KudosFeedScreenPreview() {
    KudosAppTheme {
        KudosFeedScreen(
            highlightKudos = KudosMockData.kudos
                .sortedByDescending { it.heartCount }
                .take(5),
            allKudos = KudosMockData.kudos,
            stats = KudosMockData.stats,
            giftRecipients = KudosMockData.giftRecipients,
            likedKudoIds = setOf("k1", "k3"),
            currentLanguage = "VN",
            unreadCount = 2,
            filterRow = {},
            spotlight = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(200.dp)
                        .background(KudosBgUpdate)
                )
            }
        )
    }
}
