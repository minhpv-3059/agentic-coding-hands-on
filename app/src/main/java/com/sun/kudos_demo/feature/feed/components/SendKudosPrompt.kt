package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

// Design node mms_A.1_Button ghi nhận (6885:9083):
// border-radius 4px, height 40px, padding 10px, bg gold 10%, border KudosBorder solid, label white centred.
private val PromptShape = RoundedCornerShape(4.dp)

/**
 * "Send Kudos" prompt bar — design node mms_A.1_Button ghi nhận (6885:9083).
 * Lightly-rounded rect with edit icon + label centred inside.
 */
@Composable
fun SendKudosPrompt(
    onSendKudos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(PromptShape)
            .background(KudosGold.copy(alpha = 0.10f))
            .border(1.dp, KudosBorder, PromptShape)
            .clickable(onClick = onSendKudos)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = stringResource(R.string.feed_send_kudos_desc),
            tint = KudosGold,
            modifier = Modifier.size(18.dp)
        )
        androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.feed_send_kudos_prompt),
            style = MaterialTheme.typography.bodyMedium,
            color = KudosWhite
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
