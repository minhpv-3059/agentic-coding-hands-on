package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Full-width secondary CTA button for the other-user profile screen.
 *
 * Design node 6885:10427:
 *   - 335×40dp, border = 1dp solid #998C5F, background = rgba(255,234,158,0.10)
 *   - border-radius = 4dp (NOT pill — exact from design)
 *   - padding: 10dp all sides, gap=8dp between icon and label
 *   - Leading icon: pencil/edit 24×24dp (mm_media_icon)
 *   - Label: 14sp medium white, "Gửi lời cảm ơn và ghi nhận tới {firstName}…"
 *     maxLines=1, ellipsis, centered
 *
 * @param recipientName  full name of recipient; first name extracted for label
 * @param onSendKudos    fired when button is tapped
 */
@Composable
fun ProfileSendKudosCta(
    recipientName: String,
    onSendKudos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstName = recipientName.trim().split(" ").lastOrNull() ?: recipientName

    OutlinedButton(
        onClick = onSendKudos,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, KudosBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = KudosSecondaryButtonNormal,
            contentColor = KudosWhite
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 10.dp,
            vertical = 0.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading pencil/edit icon — 24×24dp (design node I6885:10427;28:2013)
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                tint = KudosGold,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.profile_send_cta, firstName),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = KudosWhite
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun ProfileSendKudosCtaPreview() {
    KudosAppTheme {
        ProfileSendKudosCta(
            recipientName = "Huỳnh Dương Xuân Nhật",
            onSendKudos = {},
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}
