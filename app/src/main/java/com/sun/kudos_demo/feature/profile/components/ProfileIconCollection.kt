package com.sun.kudos_demo.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosWhite

// Design node I6885:10351;3053:10046 → background: #323231
private val SlotBgColor = Color(0xFF323231)

/**
 * "Bộ sưu tập icon của tôi" — 6 dark empty circular slots in a row + centered label below.
 *
 * Design: mms_2_icon collection (6885:10349)
 *   - Row gap between slots: 14dp (design: 14px)
 *   - Each outer slot frame: 32dp
 *   - Inner circle: 31dp, bg #323231, border 1dp white, fully round
 *   - Column gap between row and label: 12dp
 *   - Label: Montserrat Regular 12sp/16sp, center, white
 *
 * @param slotCount number of empty icon slots (design shows 6)
 */
@Composable
fun ProfileIconCollection(
    modifier: Modifier = Modifier,
    slotCount: Int = 6
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(slotCount) {
                // 32dp outer container, 31dp inner circle with dark bg + white border
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(31.dp)
                            .clip(CircleShape)
                            .background(SlotBgColor)
                            .border(1.dp, KudosWhite, CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Bộ sưu tập icon của tôi",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Normal
            ),
            color = KudosWhite,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun ProfileIconCollectionPreview() {
    KudosAppTheme {
        ProfileIconCollection(modifier = Modifier.padding(16.dp))
    }
}
