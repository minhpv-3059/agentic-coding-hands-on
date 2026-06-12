package com.sun.kudos_demo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

// Top app bar matching the design:
// [Logo]    [VN ▼]  [🔍]  [🔔 + badge]
// Height: 56 dp as per code-standards.md, horizontal padding: 16 dp
@Composable
fun KudosTopBar(
    modifier: Modifier = Modifier,
    currentLanguage: String = "VN",
    unreadCount: Int = 0,
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {}
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
            .background(gradient)      // gradient covers the status-bar area for icon legibility
            .statusBarsPadding()       // push header content below the status bar (edge-to-edge)
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // SAA Logo — 48×44 dp per design (ic_logo_saa.png)
        Image(
            painter = painterResource(R.drawable.ic_logo_saa),
            contentDescription = "SAA 2025",
            modifier = Modifier.size(width = 48.dp, height = 44.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Language selector: flag emoji + code + arrow — 90×32 in design
            Row(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable(onClick = onLanguageClick)
                    .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // VN flag — real Figma asset (ic_vn_flag); other locales fall back to emoji
                if (currentLanguage == "VN") {
                    Image(
                        painter = painterResource(R.drawable.ic_vn_flag),
                        contentDescription = "Vietnam",
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(text = "🇬🇧", style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = currentLanguage,
                    style = MaterialTheme.typography.labelMedium,
                    color = KudosWhite
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = KudosWhite,
                    modifier = Modifier.size(16.dp)
                )
            }

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
                            Badge(
                                containerColor = KudosGold,
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

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudosTopBarPreview() {
    KudosAppTheme {
        KudosTopBar(currentLanguage = "VN", unreadCount = 3)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudosTopBarNoUnreadPreview() {
    KudosAppTheme {
        KudosTopBar(currentLanguage = "VN", unreadCount = 0)
    }
}
