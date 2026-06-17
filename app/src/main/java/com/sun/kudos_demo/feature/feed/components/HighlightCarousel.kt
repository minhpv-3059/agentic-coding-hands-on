package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.ui.components.KudosCard
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosContainer
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * Highlight Kudos carousel — design mms_B.2 (335×256, 3 cards centered with peek).
 * The center card is prominent; neighbours peek on both sides and are faded/scaled-down
 * (TC_FUN_038: inactive faded → active center). Side ◀ ▶ arrows (mms next) plus a
 * "n/total" pager row (mms_B.5_slide). Resets to page 0 when [kudos] changes (filter applied).
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

    // Reset to the first card whenever the list changes (a filter was applied).
    LaunchedEffect(kudos) { pagerState.animateScrollToPage(0) }

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 44.dp),   // ~10% faded neighbour peek each side
                pageSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val kudo = kudos[page]
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                    .absoluteValue.coerceIn(0f, 1f)
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
                    modifier = Modifier.graphicsLayer {
                        val s = lerp(0.9f, 1f, 1f - pageOffset)
                        scaleX = s
                        scaleY = s
                        alpha = lerp(0.45f, 1f, 1f - pageOffset)
                    }
                )
            }
            // Side arrows removed so the faded neighbour cards (peek) stay visible per design;
            // navigation is via swipe + the pager row below.
        }

        Spacer(Modifier.height(12.dp))

        // Pagination row: ◀  n/total  ▶  (design mms_B.5_slide)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CarouselNavButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                enabled = pagerState.currentPage > 0,
                contentDesc = stringResource(R.string.feed_carousel_prev)
            ) { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0)) } }

            Text(
                text = "${pagerState.currentPage + 1}/${kudos.size}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.25.sp
                ),
                color = KudosWhite,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            CarouselNavButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                enabled = pagerState.currentPage < kudos.size - 1,
                contentDesc = stringResource(R.string.feed_carousel_next)
            ) { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(kudos.size - 1)) } }
        }
    }
}

@Composable
private fun SideArrow(
    icon: ImageVector,
    enabled: Boolean,
    contentDesc: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = KudosGold.copy(alpha = if (enabled) 0.9f else 0.25f),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun CarouselNavButton(
    icon: ImageVector,
    enabled: Boolean,
    contentDesc: String,
    onClick: () -> Unit = {}
) {
    // Plain gold chevron — no circle background or border (design mms_B.5_slide)
    Icon(
        imageVector = icon,
        contentDescription = contentDesc,
        tint = if (enabled) KudosGold else KudosGray,
        modifier = Modifier
            .size(24.dp)
            .clickable(enabled = enabled, onClick = onClick)
    )
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
            text = stringResource(R.string.feed_empty_kudos),
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
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}
