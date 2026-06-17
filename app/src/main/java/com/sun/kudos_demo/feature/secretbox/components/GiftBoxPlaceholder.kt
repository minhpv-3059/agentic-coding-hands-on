package com.sun.kudos_demo.feature.secretbox.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.feature.secretbox.SecretBoxPhase
import com.sun.kudos_demo.feature.secretbox.SecretBoxReward
import com.sun.kudos_demo.ui.theme.KudosGold
import kotlinx.coroutines.delay

/**
 * Placeholder vùng media khi chưa có file video (secretbox_idle/tap/open chưa được export).
 *
 * CLOSED  → hộp tối bo góc + viền vàng + icon 🎁; tap → onBoxTap
 * OPENING → scale+fade ~900ms rồi gọi onOpenAnimationEnd() để flow tiếp tục
 * REWARD  → [SecretBoxRewardImage] (PNG quà hoặc placeholder gradient vàng)
 *
 * Thiết kế: giữ đúng vùng 320×320dp để layout không bị xê dịch khi video thật được bổ sung.
 */
@Composable
fun GiftBoxPlaceholder(
    phase: SecretBoxPhase,
    reward: SecretBoxReward?,
    onBoxTap: () -> Unit,
    onOpenAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var triggerOpen by remember { mutableStateOf(false) }

    val openScale by animateFloatAsState(
        targetValue = if (triggerOpen) 1.2f else 1f,
        animationSpec = tween(450),
        label = "openScale"
    )
    val openAlpha by animateFloatAsState(
        targetValue = if (triggerOpen) 0f else 1f,
        animationSpec = tween(900),
        label = "openAlpha"
    )

    LaunchedEffect(phase) {
        if (phase == SecretBoxPhase.OPENING) {
            triggerOpen = true
            delay(900)
            triggerOpen = false
            onOpenAnimationEnd()
        }
    }

    Box(
        modifier = modifier
            .size(320.dp)
            .clickable(
                enabled = phase == SecretBoxPhase.CLOSED,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onBoxTap
            ),
        contentAlignment = Alignment.Center
    ) {
        when (phase) {
            SecretBoxPhase.CLOSED, SecretBoxPhase.OPENING -> {
                // Hộp quà tĩnh — nền tối + viền vàng + icon 🎁
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(openScale)
                        .alpha(openAlpha)
                        .background(
                            color = Color(0xFF001A2A),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = KudosGold,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎁",
                        fontSize = 72.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            SecretBoxPhase.REWARD -> {
                // Hiện ảnh quà (PNG hoặc gradient placeholder)
                if (reward != null) {
                    SecretBoxRewardImage(
                        reward = reward,
                        modifier = Modifier.size(320.dp)
                    )
                }
            }
        }
    }
}
