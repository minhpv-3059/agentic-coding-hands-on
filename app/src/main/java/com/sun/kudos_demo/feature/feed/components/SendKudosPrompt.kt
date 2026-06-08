package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

private val PromptShape = RoundedCornerShape(24.dp)

/**
 * "Send Kudos" prompt bar — design node mms_A.1_Button ghi nhận (6885:9083).
 * Pill-shaped row with edit icon on the left + label text.
 */
@Composable
fun SendKudosPrompt(
    onSendKudos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(PromptShape)
            .background(KudosContainer)
            .border(1.dp, KudosBorder.copy(alpha = 0.4f), PromptShape)
            .clickable(onClick = onSendKudos)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = "Gửi Kudos",
            tint = KudosGold,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Hôm nay, bạn muốn gửi kudos đến ai?",
            style = MaterialTheme.typography.bodyMedium,
            color = KudosWhite.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun SendKudosPromptPreview() {
    KudosAppTheme {
        SendKudosPrompt(onSendKudos = {}, modifier = Modifier.padding(16.dp))
    }
}
