package com.sun.kudos_demo.feature.secretbox

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.secretbox.components.GiftBoxAnimation
import com.sun.kudos_demo.feature.secretbox.components.SecretBoxCounter
import com.sun.kudos_demo.feature.secretbox.components.SecretBoxHeader
import com.sun.kudos_demo.feature.secretbox.components.SecretBoxRewardView
import com.sun.kudos_demo.feature.secretbox.components.SecretBoxTopBar
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBackground

/**
 * Màn hình Secret Box — 1 màn, 3 visual state (CLOSED / OPENING / REWARD).
 * Stateless/presentational — không có bottom nav (detail flow).
 * Container panel: node 6885:9434 — #00101A, radius 7dp, gap 24dp, padding 14/7dp.
 */
@Composable
fun SecretBoxScreen(
    phase: SecretBoxPhase,
    unopenedCount: Int,
    allOpened: Boolean,
    reward: SecretBoxReward?,
    onBack: () -> Unit,
    onBoxTap: () -> Unit,
    onOpenAnimationEnd: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KudosBackground)
    ) {
        // Nền key-visual full-bleed — giống NotificationsScreen / MyProfileScreen
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar — absorb status bar inset bên trong
            SecretBoxTopBar(onBack = onBack)

            // Content panel — full chiều rộng device (design node 6885:9434 width=375, startX=0).
            // 7.3/13.7px của node là padding TRONG → đặt SAU background để nền #00101A chạm sát
            // 2 mép màn hình (không lộ key-visual). weight(1f) cấp giới hạn chiều cao cho verticalScroll.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color(0xFF00101A))
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 7.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(14.dp))

                // a. Header — đổi theo phase
                SecretBoxHeader(phase = phase, allOpened = allOpened)

                Spacer(Modifier.height(24.dp))

                // b. Vùng media 320×320dp (video ExoPlayer hoặc placeholder)
                // Không tap nếu allOpened (TC_SB_FUN_003)
                GiftBoxAnimation(
                    phase = phase,
                    reward = reward,
                    onBoxTap = if (allOpened) ({}) else onBoxTap,
                    onOpenAnimationEnd = onOpenAnimationEnd
                )

                Spacer(Modifier.height(24.dp))

                // c. Divider Rectangle 18 — #2E3940 (node 6885:9444)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF2E3940))
                )

                Spacer(Modifier.height(24.dp))

                // d. Counter hoặc reward view
                when (phase) {
                    SecretBoxPhase.CLOSED, SecretBoxPhase.OPENING ->
                        SecretBoxCounter(unopenedCount = unopenedCount)

                    SecretBoxPhase.REWARD -> if (reward != null) {
                        SecretBoxRewardView(
                            reward = reward,
                            onContinue = onContinue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 7.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// --- Previews ---
@Preview(
    showBackground = true,
    backgroundColor = 0xFF00101A,
    device = "id:pixel_5",
    name = "SecretBox — CLOSED"
)
@Composable
private fun PreviewClosed() {
    KudosAppTheme {
        SecretBoxScreen(
            phase = SecretBoxPhase.CLOSED,
            unopenedCount = 5,
            allOpened = false,
            reward = null,
            onBack = {},
            onBoxTap = {},
            onOpenAnimationEnd = {},
            onContinue = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF00101A,
    device = "id:pixel_5",
    name = "SecretBox — OPENING (placeholder)"
)
@Composable
private fun PreviewOpening() {
    KudosAppTheme {
        SecretBoxScreen(
            phase = SecretBoxPhase.OPENING,
            unopenedCount = 5,
            allOpened = false,
            reward = null,
            onBack = {},
            onBoxTap = {},
            onOpenAnimationEnd = {},
            onContinue = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF00101A,
    device = "id:pixel_5",
    name = "SecretBox — REWARD"
)
@Composable
private fun PreviewReward() {
    KudosAppTheme {
        SecretBoxScreen(
            phase = SecretBoxPhase.REWARD,
            unopenedCount = 4,
            allOpened = false,
            reward = SecretBoxReward(
                id = "b",
                name = "Khăn Root Further",
                imageResName = "reward_khan_root_further"
            ),
            onBack = {},
            onBoxTap = {},
            onOpenAnimationEnd = {},
            onContinue = {}
        )
    }
}
