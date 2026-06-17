package com.sun.kudos_demo.feature.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.KudosMockData.recentSearches
import com.sun.kudos_demo.feature.feed.KudosMockData.searchableUsers
import com.sun.kudos_demo.feature.feed.components.UserResultRow
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosBgUpdate
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Search Sunner screen — two states in one composable (stateless/hoisted).
 *
 * State 1 (query == ""): shows "Recent" header + "View all" link + [recent] list with X buttons.
 * State 2 (query != ""): shows [results] list, no X buttons.
 *
 * Design refs:
 *   - [iOS] Sun*Kudos_Search Sunner  (screenId: 3jgwke3E8O) — recent state
 *   - [iOS] Sun*Kudos_Searching      (screenId: hldqjHoSRH) — results state
 *
 * Background key-visual (right side, muted) mirrors HomeHeroSection pattern using
 * bg_home_keyvisual.png as placeholder; no separate asset needed at this phase.
 *
 * Bottom nav is rendered by the app Scaffold — NOT included here.
 */
@Composable
fun KudosSearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<KudoUser>,
    recent: List<KudoUser>,
    onBack: () -> Unit,
    onResultClick: (KudoUser) -> Unit,
    onRemoveRecent: (KudoUser) -> Unit,
    onViewAllRecent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        // Key-visual background — right-side muted overlay matching design
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.25f,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top navigation row ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .padding(end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button — 48dp touch target
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.feed_search_back_desc),
                        tint = KudosWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Search bar — dark pill with cursor, placeholder text
                SearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    focusRequester = focusRequester,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                )
            }

            // ── Content area ──────────────────────────────────────────────────
            if (query.isBlank()) {
                RecentSection(
                    recent = recent,
                    onItemClick = onResultClick,
                    onRemove = onRemoveRecent,
                    onViewAll = onViewAllRecent
                )
            } else {
                ResultsSection(
                    results = results,
                    onItemClick = onResultClick
                )
            }
        }
    }
}

// ── Sub-composables ──────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        cursorBrush = SolidColor(KudosGold),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = KudosWhite),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        modifier = modifier
            .focusRequester(focusRequester)
            .clip(RoundedCornerShape(18.dp))
            .background(KudosBgUpdate),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.feed_search_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = KudosGray
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun RecentSection(
    recent: List<KudoUser>,
    onItemClick: (KudoUser) -> Unit,
    onRemove: (KudoUser) -> Unit,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // "Recent" title + "View all" row — height 32dp, padding h 20dp (design: Title frame)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.feed_search_recent_title),
                style = MaterialTheme.typography.titleMedium,
                color = KudosWhite,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onViewAll) {
                Text(
                    text = stringResource(R.string.feed_search_view_all),
                    style = MaterialTheme.typography.labelMedium,
                    color = KudosGold
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            items(recent, key = { it.id }) { user ->
                UserResultRow(
                    user = user,
                    onClick = { onItemClick(user) },
                    showRemoveButton = true,
                    onRemove = { onRemove(user) }
                )
            }
        }
    }
}

@Composable
private fun ResultsSection(
    results: List<KudoUser>,
    onItemClick: (KudoUser) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        items(results, key = { it.id }) { user ->
            UserResultRow(
                user = user,
                onClick = { onItemClick(user) },
                showRemoveButton = false
            )
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF00101A, device = "id:pixel_5")
@Composable
private fun KudosSearchScreenRecentPreview() {
    KudosAppTheme {
        KudosSearchScreen(
            query = "",
            onQueryChange = {},
            results = emptyList(),
            recent = recentSearches,
            onBack = {},
            onResultClick = {},
            onRemoveRecent = {},
            onViewAllRecent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, device = "id:pixel_5")
@Composable
private fun KudosSearchScreenResultsPreview() {
    val query = "Dương"
    val filtered = searchableUsers.filter {
        it.name.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true)
    }
    KudosAppTheme {
        KudosSearchScreen(
            query = query,
            onQueryChange = {},
            results = filtered,
            recent = recentSearches,
            onBack = {},
            onResultClick = {},
            onRemoveRecent = {},
            onViewAllRecent = {}
        )
    }
}
