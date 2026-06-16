package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.ui.components.KudoAvatar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * A single row representing a KudoUser in search results or recent searches.
 *
 * Design ref: [iOS] Sun*Kudos_Search Sunner / Searching (Frame 556 → Frame 557/558)
 * Height: 60dp — Avatar(40dp) | name + code stack | optional X button (recent only).
 *
 * @param showRemoveButton when true renders the X icon on the right edge (recent state).
 * @param onRemove called when user taps X; only wired when [showRemoveButton] = true.
 */
@Composable
fun UserResultRow(
    user: KudoUser,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showRemoveButton: Boolean = false,
    onRemove: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar container: 60dp total (10dp padding each side → 40dp avatar inside)
        KudoAvatar(
            name = user.name,
            size = 40.dp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        // Name + code stack — grows to fill remaining space
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyMedium,
                color = KudosWhite,
                maxLines = 1
            )
            Text(
                text = user.code,
                style = MaterialTheme.typography.bodySmall,
                color = KudosGray,
                maxLines = 1
            )
        }

        if (showRemoveButton) {
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove from recent",
                    tint = KudosGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun UserResultRowResultPreview() {
    KudosAppTheme {
        UserResultRow(
            user = KudosMockData.searchableUsers.first(),
            onClick = {},
            showRemoveButton = false
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun UserResultRowRecentPreview() {
    KudosAppTheme {
        UserResultRow(
            user = KudosMockData.searchableUsers.first(),
            onClick = {},
            showRemoveButton = true,
            onRemove = {}
        )
    }
}
