package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.GiftRecipient
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.ui.components.KudoAvatar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * "10 SUNNER NHẬN QUÀ MỚI NHẤT" section — design node mms_D.3_10 SUNNER nhận quà (6885:9255).
 * Renders a header + list of [GiftRecipient] rows; each row is tappable.
 */
@Composable
fun GiftRecipientsSection(
    recipients: List<GiftRecipient>,
    modifier: Modifier = Modifier,
    onRecipientClick: (GiftRecipient) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "10 SUNNER NHẬN QUÀ MỚI NHẤT",
            style = MaterialTheme.typography.labelLarge,
            color = KudosGold
        )
        Spacer(Modifier.height(12.dp))

        if (recipients.isEmpty()) {
            Text(
                text = "Chưa có dữ liệu.",
                style = MaterialTheme.typography.bodySmall,
                color = KudosGray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                recipients.forEach { recipient ->
                    GiftRecipientRow(
                        recipient = recipient,
                        onClick = { onRecipientClick(recipient) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GiftRecipientRow(
    recipient: GiftRecipient,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        KudoAvatar(
            name = recipient.user.name,
            size = 36.dp
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = recipient.user.name,
                style = MaterialTheme.typography.labelMedium,
                color = KudosWhite
            )
            Text(
                text = recipient.giftDescription,
                style = MaterialTheme.typography.bodySmall,
                color = KudosGray
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun GiftRecipientsSectionPreview() {
    KudosAppTheme {
        GiftRecipientsSection(
            recipients = KudosMockData.giftRecipients.take(5),
            modifier = Modifier.padding(16.dp)
        )
    }
}
