package com.sun.kudos_demo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.auth.LanguageManager
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosAccentRed
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosWhite

// Top app bar matching the design:
// [Logo]    [VN ▼]  [🔍]  [🔔 + badge]
// Height: 56 dp as per code-standards.md, horizontal padding: 16 dp
//
// The language selector self-manages a dropdown panel (tap → open VN/EN options) and drives
// the app-wide [LanguageManager], so every header screen switches language consistently —
// matching the Login dropdown behaviour.
@Composable
fun KudosTopBar(
    modifier: Modifier = Modifier,
    unreadCount: Int = 0,
    showScrim: Boolean = true,
    onBack: (() -> Unit)? = null,
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    // Gradient overlay matching LoginHeader: top-heavy dark, fades to transparent
    val gradient = Brush.verticalGradient(
        0f to KudosBackground,
        0.764f to Color(0x4D00101A),
        0.846f to Color(0x3300101A),
        1.0f to Color(0x0000101A)
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            // Scrim aids status-bar icon legibility; profile screens disable it so the
            // full-bleed key-visual shows through behind the header.
            .then(if (showScrim) Modifier.background(gradient) else Modifier)
            .statusBarsPadding()       // push header content below the status bar (edge-to-edge)
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Leading: back arrow on detail screens (e.g. other-user profile), else the SAA logo
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = KudosWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            // SAA Logo — 48×44 dp per design (ic_logo_saa.png)
            Image(
                painter = painterResource(R.drawable.ic_logo_saa),
                contentDescription = "SAA 2025",
                modifier = Modifier.size(width = 48.dp, height = 44.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LanguageSelector()

            // Search icon
            IconButton(onClick = onSearchClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = KudosWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Notification icon with badge dot (shown when unreadCount > 0)
            IconButton(onClick = onNotificationClick, modifier = Modifier.size(40.dp)) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            // Red unread dot — consistent with the notification list's unread
                            // indicator (B.1.3). Previously gold/cream which read as white.
                            Badge(
                                containerColor = KudosAccentRed,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications ($unreadCount unread)",
                        tint = KudosWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

/** Header language selector — opens a dropdown panel and drives the global [LanguageManager]. */
@Composable
private fun LanguageSelector() {
    val language by LanguageManager.language.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val dropOffsetY = with(LocalDensity.current) { 28.dp.roundToPx() }

    Box {
        LanguageTrigger(selected = language, onClick = { expanded = !expanded })
        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, dropOffsetY),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true)
            ) {
                LanguageDropdownPanel(
                    selected = language,
                    onSelect = {
                        LanguageManager.set(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudosTopBarPreview() {
    KudosAppTheme {
        KudosTopBar(unreadCount = 3)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudosTopBarNoUnreadPreview() {
    KudosAppTheme {
        KudosTopBar(unreadCount = 0)
    }
}
