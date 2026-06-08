package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.ui.components.KudosCard
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite
import kotlinx.coroutines.launch

/**
 * Horizontal pager carousel for Highlight Kudos.
 * Design: mms_B.2_HIGHLIGHT KUDOS (6885:9090) — cards with ◀ ▶ nav arrows and "n/5" pagination.
 *
 * Resets to page 0 via [LaunchedEffect] whenever [kudos] list identity changes.
 */
@Composable
fun HighlightCarousel(
    kudos: List<Kudo>,
    likedKudoIds: Set<String>,
    currentUserId: String = "",
    modifier: Modifier = Modifier,
    onToggleLike: (String) -> Unit = {},
    onCopyLink: (String) -> Unit = {},
    onKudoDetail: (String) -> Unit = {},
    onSenderClick: (KudoUser) -> Unit = {},
    onRecipientClick: (KudoUser) -> Unit = {},
    onHashtagClick: (String) -> Unit = {}
) {
    if (kudos.isEmpty()) {
        EmptyKudosHint(modifier = modifier.padding(vertical = 16.dp))
        return
    }

    val pagerState = rememberPagerState(pageCount = { kudos.size })
    val scope = rememberCoroutineScope()

    // Reset to first card whenever the list changes (filter applied)
    LaunchedEffect(kudos) {
        pagerState.animateScrollToPage(0)
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val kudo = kudos[page]
            KudosCard(
                kudo = kudo,
                isLiked = kudo.id in likedKudoIds,
                canLike = kudo.sender?.id != currentUserId,
                compact = true,
                onLike = { onToggleLike(kudo.id) },
                onCopyLink = { onCopyLink(kudo.id) },
                onDetail = { onKudoDetail(kudo.id) },
                onSenderClick = onSenderClick,
                onRecipientClick = onRecipientClick,
                onHashtagClick = onHashtagClick,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Pagination row: ◀  n/total  ▶
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CarouselNavButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                enabled = pagerState.currentPage > 0,
                contentDesc = "Trước",
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                    }
                }
            )

            Text(
                text = "${pagerState.currentPage + 1}/${kudos.size}",
                style = MaterialTheme.typography.labelMedium,
                color = KudosWhite,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            CarouselNavButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                enabled = pagerState.currentPage < kudos.size - 1,
                contentDesc = "Tiếp",
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(kudos.size - 1))
                    }
                }
            )
        }
    }
}

@Composable
private fun CarouselNavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    contentDesc: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .background(if (enabled) KudosContainer else KudosContainer.copy(alpha = 0.4f))
            .border(1.dp, if (enabled) KudosBorder else KudosBorder.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = if (enabled) KudosGold else KudosGray,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
internal fun EmptyKudosHint(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(KudosContainer)
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hiện tại chưa có Kudos nào.",
            style = MaterialTheme.typography.bodyMedium,
            color = KudosGray
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HighlightCarouselPreview() {
    KudosAppTheme {
        HighlightCarousel(
            kudos = KudosMockData.kudos.take(5),
            likedKudoIds = setOf("k1"),
            modifier = Modifier.padding(16.dp)
        )
    }
}
