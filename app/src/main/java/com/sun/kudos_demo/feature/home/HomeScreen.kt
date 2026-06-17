package com.sun.kudos_demo.feature.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.home.components.HomeAwardsSection
import com.sun.kudos_demo.feature.home.components.HomeFab
import com.sun.kudos_demo.feature.home.components.HomeHeroSection
import com.sun.kudos_demo.feature.home.components.HomeKudosSection
import com.sun.kudos_demo.feature.home.components.HomeNoteSection
import com.sun.kudos_demo.ui.components.KudosTopBar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground

/**
 * [iOS] Home screen — stateless / presentational.
 *
 * Sections (top to bottom):
 *   1. KudosTopBar (pinned above scroll — overlays hero)
 *   2. Hero: bg keyvisual + ROOT FURTHER + countdown + event info + CTA buttons
 *   3. Theme description note (mms_3_note)
 *   4. Awards section: header + horizontal LazyRow of award cards
 *   5. Kudos section: header + banner + description + Chi tiết button
 *   6. FAB: pencil (send kudos) + S/Kudos (feed) — overlaid bottom-end
 *
 * Bottom nav is provided by the app Scaffold — NOT included here.
 *
 * Dynamic fields currently hardcoded as mock (to be driven by HomeViewModel):
 *   - days / hours / minutes (countdown)
 *   - currentLanguage
 *   - unreadCount
 */
@Composable
fun HomeScreen(
    onAboutAward: () -> Unit,
    onAboutKudos: () -> Unit,
    onAwardDetail: (awardId: String) -> Unit,
    onKudosDetail: () -> Unit,
    onSendKudos: () -> Unit,
    onOpenKudosFeed: () -> Unit,
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onLanguageClick: () -> Unit = {},
    // Mock values — hoistable to HomeViewModel later
    days: Int = 20,
    hours: Int = 20,
    minutes: Int = 20,
    currentLanguage: String = "VN",
    unreadCount: Int = 3,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        // Scrollable page content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero section occupies top — TopBar overlays it via z-ordering in Box
            HomeHeroSection(
                days = days,
                hours = hours,
                minutes = minutes,
                onAboutAward = onAboutAward,
                onAboutKudos = onAboutKudos
            )

            Spacer(Modifier.height(24.dp))

            // mms_3_note — theme description paragraph
            HomeNoteSection(
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(32.dp))

            // mms_4_awards — awards list
            HomeAwardsSection(
                onAwardDetail = onAwardDetail
            )

            Spacer(Modifier.height(32.dp))

            // mms_5_kudos — kudos section
            HomeKudosSection(
                onKudosDetail = onKudosDetail
            )

            // Bottom padding so content clears the FAB and bottom nav
            Spacer(Modifier.height(80.dp))
        }

        // KudosTopBar pinned at top, drawn over hero background
        KudosTopBar(
            currentLanguage = currentLanguage,
            unreadCount = unreadCount,
            onSearchClick = onSearch,
            onNotificationClick = onNotifications,
            onLanguageClick = onLanguageClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // FAB overlaid at bottom-end — pencil + kudos feed icons
        HomeFab(
            onSendKudos = onSendKudos,
            onOpenKudosFeed = onOpenKudosFeed,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, device = "id:pixel_5")
@Composable
private fun HomeScreenPreview() {
    KudosAppTheme {
        HomeScreen(
            onAboutAward = {},
            onAboutKudos = {},
            onAwardDetail = {},
            onKudosDetail = {},
            onSendKudos = {},
            onOpenKudosFeed = {},
            onSearch = {},
            onNotifications = {}
        )
    }
}
