package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.ui.components.KudosCard
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosGold

/**
 * Preview dialog — shows the kudo as it will appear in the feed.
 *
 * Reuses [KudosCard] so the layout/markdown rendering is identical to the feed.
 * No-op lambdas are passed for card interactions (like/copy/detail) since this is
 * a read-only preview.
 *
 * Triggered by the "Xem trước" (eye icon) button in [SendKudosFormContent].
 * The kudo is built via [SendKudosViewModel.previewKudo] — tolerant of an unselected recipient.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KudoPreviewDialog(
    kudo: Kudo,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = KudosBackground,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.send_preview_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = KudosGold
                )
                Spacer(Modifier.height(12.dp))

                // Reuse the existing feed card — identical to how it renders in the feed.
                // canLike=false so the heart is greyed out (preview only).
                KudosCard(
                    kudo = kudo,
                    isLiked = false,
                    canLike = false,
                    compact = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(R.string.send_preview_close),
                        style = MaterialTheme.typography.labelLarge,
                        color = KudosGold
                    )
                }
            }
        }
    }
}
