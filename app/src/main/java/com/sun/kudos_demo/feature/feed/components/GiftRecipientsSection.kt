package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.GiftRecipient
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.ui.components.KudoAvatar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray

private val PanelShape = RoundedCornerShape(8.dp)

/**
 * "10 SUNNER NHẬN QUÀ MỚI NHẤT" section — design node mms_D.3_10 SUNNER nhận quà (6885:9255).
 * Renders a bordered panel with header + list of [GiftRecipient] rows; each row is tappable.
 */
@Composable
fun GiftRecipientsSection(
    recipients: List<GiftRecipient>,
    modifier: Modifier = Modifier,
    onRecipientClick: (GiftRecipient) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(PanelShape)
            .background(KudosContainer2)
            .border(1.dp, KudosBorder, PanelShape)
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(R.string.feed_gift_recipients_title),
            style = MaterialTheme.typography.labelLarge,
            color = KudosGold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.padding(top = 12.dp))

        if (recipients.isEmpty()) {
            Text(
                text = stringResource(R.string.feed_gift_recipients_empty),
                style = MaterialTheme.typography.bodySmall,
                color = KudosGray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        KudoAvatar(
            name = recipient.user.name,
            size = 24.dp
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = recipient.user.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = KudosGold
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
