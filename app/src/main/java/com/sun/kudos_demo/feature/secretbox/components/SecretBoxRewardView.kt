package com.sun.kudos_demo.feature.secretbox.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.feature.secretbox.SecretBoxReward
import com.sun.kudos_demo.ui.components.KudosPrimaryButton
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGold

/**
 * Phần dưới của REWARD state: ảnh quà (320×320) + caption tên quà + nút "Tiếp tục".
 *
 * Dùng trong SecretBoxScreen khi phase == REWARD.
 * [SecretBoxRewardImage] cũng được export để GiftBoxAnimation dùng trong crossfade.
 *
 * Design refs:
 *  - Ảnh quà: mms_C_Box image (6885:9661) — 320×320dp, centered
 *  - Caption "Khăn Root Further": node 6885:9666 — Montserrat Regular 14sp/20sp #FFEA9E, center
 *    (letterSpacing 0.25dp, fontWeight 400)
 *  - Nút "Tiếp tục": KudosPrimaryButton (gold fill, pill, 56dp height) — reuse shared token
 *    (ProfileSendKudosCta / KudosButton.kt precedent)
 */

/**
 * Ảnh quà 320×320dp — dùng drawable theo [SecretBoxReward.imageResName].
 * Nếu drawable chưa có (id=0) → placeholder gradient vàng tối với tên quà.
 */
@Composable
fun SecretBoxRewardImage(
    reward: SecretBoxReward,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val drawableId = context.resources.getIdentifier(
        reward.imageResName, "drawable", context.packageName
    )

    Box(
        modifier = modifier.size(320.dp),
        contentAlignment = Alignment.Center
    ) {
        if (drawableId != 0) {
            Image(
                painter = painterResource(drawableId),
                contentDescription = reward.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(320.dp)
            )
        } else {
            // Placeholder: hình chữ nhật bo góc gradient vàng + tên quà căn giữa
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                KudosGold.copy(alpha = 0.30f),
                                KudosGold.copy(alpha = 0.08f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = KudosGold.copy(alpha = 0.60f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reward.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    color = KudosGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

/**
 * Toàn bộ phần reward hiển thị dưới divider: ảnh quà + tên quà (caption) + nút Tiếp tục.
 *
 * Design caption node 6885:9666:
 *   fontSize=14sp, lineHeight=20sp, fontWeight=400 (Regular), letterSpacing=0.25dp, color=#FFEA9E
 */
@Composable
fun SecretBoxRewardView(
    reward: SecretBoxReward,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Caption tên quà — Montserrat Regular 14sp/20sp #FFEA9E, letterSpacing 0.25
        Text(
            text = reward.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp
            ),
            color = KudosGold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Nút "Tiếp tục" — gold fill, pill, 56dp, full-width (KudosPrimaryButton pattern)
        KudosPrimaryButton(
            text = "Tiếp tục",
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun SecretBoxRewardViewPreview() {
    KudosAppTheme {
        SecretBoxRewardView(
            reward = SecretBoxReward(
                id = "b",
                name = "Khăn Root Further",
                imageResName = "reward_khan_root_further"
            ),
            onContinue = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
