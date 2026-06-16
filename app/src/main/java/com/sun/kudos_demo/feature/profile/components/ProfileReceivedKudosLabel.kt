package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Static read-only pill displaying the received kudos count.
 *
 * Design node 6885:10419 — inline-flex, 40dp height, padding=8dp, gap=8dp,
 * border=1dp #998C5F, background=rgba(255,234,158,0.10), border-radius=4dp.
 * Inner text: 14sp regular white, "Đã nhận {count} kudos", letterSpacing=0.25sp.
 *
 * This is NOT interactive on the other-user profile screen (design clarification:
 * the dropdown variant is only used on the own-profile screen).
 *
 * @param receivedCount number of kudos received, displayed as "Đã nhận N kudos"
 */
@Composable
fun ProfileReceivedKudosLabel(
    receivedCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .wrapContentWidth(),
        shape = RoundedCornerShape(4.dp),
        color = KudosSecondaryButtonNormal,
        border = BorderStroke(1.dp, KudosBorder)
    ) {
        Text(
            text = "Đã nhận $receivedCount kudos",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp,
                color = KudosWhite
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun ProfileReceivedKudosLabelPreview() {
    KudosAppTheme {
        ProfileReceivedKudosLabel(
            receivedCount = 5,
            modifier = Modifier.padding(16.dp)
        )
    }
}
