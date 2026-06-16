package com.sun.kudos_demo.feature.secretbox.components

import android.content.Context
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.sun.kudos_demo.feature.secretbox.SecretBoxPhase
import com.sun.kudos_demo.feature.secretbox.SecretBoxReward

/**
 * Vùng media 320×320dp (node 6885:9441).
 * Có video → ExoPlayer: idle loop / tap+open one-shot / crossfade PNG quà.
 * Chưa có video → [GiftBoxPlaceholder] (Compose scale+fade ~900ms).
 * Không clip cứng — badge tròn video C có thể tràn nhẹ.
 */
@Composable
fun GiftBoxAnimation(
    phase: SecretBoxPhase,
    reward: SecretBoxReward?,
    onBoxTap: () -> Unit,
    onOpenAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val idleResId  = remember { context.rawResId("secretbox_idle") }
    val tapResId   = remember { context.rawResId("secretbox_tap") }
    val openResId  = remember { context.rawResId("secretbox_open") }
    val hasVideos  = idleResId != 0 && tapResId != 0 && openResId != 0

    if (hasVideos) {
        VideoBoxRegion(
            context      = context,
            phase        = phase,
            reward       = reward,
            idleResId    = idleResId,
            tapResId     = tapResId,
            openResId    = openResId,
            onBoxTap     = onBoxTap,
            onOpenAnimationEnd = onOpenAnimationEnd,
            modifier     = modifier
        )
    } else {
        // Fallback: placeholder Compose khi chưa có video file
        GiftBoxPlaceholder(
            phase              = phase,
            reward             = reward,
            onBoxTap           = onBoxTap,
            onOpenAnimationEnd = onOpenAnimationEnd,
            modifier           = modifier
        )
    }
}

// ---------------------------------------------------------------------------
// Vùng video thật — ExoPlayer bọc trong AndroidView
// ---------------------------------------------------------------------------

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun VideoBoxRegion(
    context: Context,
    phase: SecretBoxPhase,
    reward: SecretBoxReward?,
    idleResId: Int,
    tapResId: Int,
    openResId: Int,
    onBoxTap: () -> Unit,
    onOpenAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    // rememberUpdatedState để observer/listener luôn đọc phase mới nhất (tránh capture stale).
    val currentPhase by rememberUpdatedState(phase)

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    // Giải phóng player khi rời composition + pause/resume theo vòng đời (H-01: tránh
    // idle video vẫn decode/phát tiếng khi app xuống background).
    DisposableEffect(player, lifecycleOwner) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                // STATE_ENDED chỉ xảy ra khi REPEAT_MODE_OFF + video cuối kết thúc
                if (state == Player.STATE_ENDED) onOpenAnimationEnd()
            }
        }
        player.addListener(listener)

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> player.pause()
                // Không resume khi đang ở REWARD (video open đã kết thúc, chỉ hiện PNG quà).
                Lifecycle.Event.ON_START -> if (currentPhase != SecretBoxPhase.REWARD) player.play()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.removeListener(listener)
            player.release()
        }
    }

    // Điều khiển player theo phase
    LaunchedEffect(phase) {
        when (phase) {
            SecretBoxPhase.CLOSED -> {
                player.repeatMode = Player.REPEAT_MODE_ONE
                player.setMediaItem(MediaItem.fromUri(rawUri(context, idleResId)))
                player.prepare()
                player.play()
            }
            SecretBoxPhase.OPENING -> {
                // Phát tap → tiếp nối open (một lần duy nhất)
                player.repeatMode = Player.REPEAT_MODE_OFF
                player.setMediaItems(
                    listOf(
                        MediaItem.fromUri(rawUri(context, tapResId)),
                        MediaItem.fromUri(rawUri(context, openResId))
                    )
                )
                player.prepare()
                player.play()
            }
            SecretBoxPhase.REWARD -> {
                // PNG hiện qua AnimatedVisibility (phase==REWARD) → không cần lệnh player
            }
        }
    }

    Box(modifier = modifier.size(320.dp), contentAlignment = Alignment.Center) {
        // Player luôn render để tránh flicker khi crossfade
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = Modifier
                .size(320.dp)
                .clickable(
                    enabled           = phase == SecretBoxPhase.CLOSED,
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onBoxTap
                )
        )

        // PNG quà — crossfade hiện lên khi vào REWARD (single source of truth = phase).
        AnimatedVisibility(
            visible = phase == SecretBoxPhase.REWARD && reward != null,
            enter   = fadeIn(tween(400)),
            exit    = fadeOut(tween(200))
        ) {
            if (reward != null) {
                SecretBoxRewardImage(
                    reward   = reward,
                    modifier = Modifier.size(320.dp)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun Context.rawResId(name: String): Int =
    resources.getIdentifier(name, "raw", packageName)

private fun rawUri(context: Context, resId: Int): Uri =
    Uri.parse("android.resource://${context.packageName}/$resId")
