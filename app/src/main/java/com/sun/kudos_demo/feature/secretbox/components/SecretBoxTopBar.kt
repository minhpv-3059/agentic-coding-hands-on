package com.sun.kudos_demo.feature.secretbox.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Top bar màn Secret Box — detail flow: back trái + title căn giữa, không trailing icon.
 * Pixel-chuẩn với NotificationsTopBar (cùng Box+statusBarsPadding+56dp pattern).
 * Design node: mms_TopNavigation-content (6885:9408) — title "Secret Box".
 */
@Composable
fun SecretBoxTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back button — bên trái, kích thước touch 48dp
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = KudosWhite
                )
            }
        }
        // Title "Secret Box" — căn giữa tuyệt đối
        Text(
            text = "Secret Box",
            style = MaterialTheme.typography.titleLarge,
            color = KudosWhite
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun SecretBoxTopBarPreview() {
    KudosAppTheme {
        SecretBoxTopBar(onBack = {})
    }
}
