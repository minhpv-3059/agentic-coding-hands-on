package com.sun.kudos_demo.feature.awards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.awards.components.AwardHeaderSection
import com.sun.kudos_demo.feature.awards.components.AwardKvSection
import com.sun.kudos_demo.feature.awards.components.AwardTrophyCard
import com.sun.kudos_demo.feature.awards.components.AwardsKudosSection
import com.sun.kudos_demo.ui.components.KudosTopBar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground

/**
 * Awards tab screen — stateless / presentational.
 *
 * Layout (top to bottom):
 *   1. KudosTopBar — pinned above scroll, overlays the keyvisual (showScrim=true)
 *   2. bg_home_keyvisual — full-bleed background, same as Home
 *   3. Scrollable content column (horizontal padding 20dp):
 *      a. Spacer — 56dp (TopBar height) so content starts below the bar
 *      b. mms_B_Highlight — AwardHeaderSection (SectionHeader + dropdown selector)
 *      c. mms_A_KV — AwardKvSection (fire icon + KUDOS wordmark header group)
 *      d. mms_C2.1 — AwardTrophyCard (trophy image + info block + dividers + stat rows)
 *      e. mms_5_kudos — AwardsKudosSection (kudos promo banner + Chi tiết button)
 *      f. Bottom padding (80dp — clears the nav bar)
 *
 * Caller (AwardsNavigation) drives state via [AwardViewModel]; this composable is
 * purely presentational and does not hold any state directly.
 *
 * Required signature (spec):
 *   AwardsScreen(uiState, onAwardSelect, onDropdownExpandedChange, onLanguageClick,
 *                onSearch, onNotifications, onKudosDetail, modifier)
 */
@Composable
fun AwardsScreen(
    uiState: AwardsUiState,
    onAwardSelect: (AwardContent) -> Unit,
    onDropdownExpandedChange: (Boolean) -> Unit,
    onLanguageClick: () -> Unit = {},
    onSearch: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onKudosDetail: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        // Full-bleed keyvisual — drawn behind everything, same asset as Home
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        )

        // Scrollable content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Reserve space for the pinned TopBar: status-bar inset + 56dp bar height,
            // so content (mms_A_KV) starts at the design's content top (y≈144).
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Spacer(Modifier.height(56.dp))

            // mms_A_KV — "Hệ thống ghi nhận và cảm ơn" + KUDOS wordmark (top of content)
            AwardKvSection(
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Content frame children are spaced 38dp apart (design node 6885:10738, gap 38)
            Spacer(Modifier.height(38.dp))

            // mms_B_Highlight — section header + award dropdown selector
            AwardHeaderSection(
                awards = uiState.awards,
                selected = uiState.selected,
                expanded = uiState.dropdownExpanded,
                onExpandedChange = onDropdownExpandedChange,
                onSelect = onAwardSelect,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(38.dp))

            // award — Trophy image + info block (description, dividers, stat rows)
            AwardTrophyCard(
                award = uiState.selected,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(38.dp))

            // kudos (mms_5) — Kudos promo section (banner + note + Chi tiết button)
            AwardsKudosSection(
                onKudosDetail = onKudosDetail,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Bottom padding — clears the global bottom nav bar
            Spacer(Modifier.height(80.dp))
        }

        // KudosTopBar pinned at top, drawn over keyvisual
        KudosTopBar(
            currentLanguage = uiState.language.code,
            unreadCount = uiState.unreadCount,
            showScrim = true,
            onLanguageClick = onLanguageClick,
            onSearchClick = onSearch,
            onNotificationClick = onNotifications,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, device = "id:pixel_5")
@Composable
private fun AwardsScreenPreview() {
    KudosAppTheme {
        AwardsScreen(
            uiState = AwardsUiState(),
            onAwardSelect = {},
            onDropdownExpandedChange = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, device = "id:pixel_5")
@Composable
private fun AwardsScreenSignaturePreview() {
    KudosAppTheme {
        AwardsScreen(
            uiState = AwardsUiState(
                selected = AwardData.awards.first { it.id == "signature_creator" }
            ),
            onAwardSelect = {},
            onDropdownExpandedChange = {}
        )
    }
}
