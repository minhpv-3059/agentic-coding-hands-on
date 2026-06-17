package com.sun.kudos_demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBgUpdate
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite
import kotlin.math.absoluteValue

/**
 * Circular avatar placeholder. Real photo assets from MoMorph are unreliable
 * (see clarifications.md) and the app ships no image-loading library, so we render
 * a deterministic colored circle with the user's initial. Anonymous senders show a
 * neutral incognito person glyph instead.
 *
 * @param name display name; the first letter becomes the initial. Ignored when [anonymous].
 * @param anonymous render the "Người gửi ẩn danh" placeholder (gray circle + person icon).
 */
@Composable
fun KudoAvatar(
    name: String?,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    anonymous: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (anonymous) KudosBgUpdate else avatarColor(name.orEmpty())),
        contentAlignment = Alignment.Center
    ) {
        if (anonymous) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Ẩn danh",
                tint = KudosGray,
                modifier = Modifier.size(size * 0.62f)
            )
        } else {
            Text(
                text = name?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = KudosWhite,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// Muted palette that reads on the dark theme; index chosen deterministically from the name.
private val avatarPalette = listOf(
    Color(0xFF5C6BC0), Color(0xFF26A69A), Color(0xFFAB7C4F),
    Color(0xFF8E6FB0), Color(0xFFC0734F), Color(0xFF4F8FC0)
)

private fun avatarColor(seed: String): Color =
    if (seed.isEmpty()) avatarPalette[0]
    else avatarPalette[seed.hashCode().absoluteValue % avatarPalette.size]

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudoAvatarPreview() {
    KudosAppTheme {
        Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
            KudoAvatar(name = "Huỳnh Dương", size = 48.dp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudoAvatarAnonymousPreview() {
    KudosAppTheme {
        Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
            KudoAvatar(name = null, size = 48.dp, anonymous = true)
        }
    }
}
