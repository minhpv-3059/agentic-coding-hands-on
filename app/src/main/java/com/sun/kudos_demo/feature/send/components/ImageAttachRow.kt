package com.sun.kudos_demo.feature.send.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.send.SendKudosMockData
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val ThumbShape = RoundedCornerShape(4.dp)
private val AddButtonShape = RoundedCornerShape(4.dp)

/**
 * "Image" attach row — renders a thumbnail for each picked image URI (each with an ×
 * remove button) plus a "+ Image (Tối đa 5)" trigger wired to the Android Photo Picker.
 *
 * Thumbnails are decoded straight from the content URI (downsampled) — no image lib,
 * matching the project's no-Coil convention.
 *
 * @param imageUris Currently attached image URIs.
 * @param onAddImageClick Tapped the "+ Image" trigger — opens the system photo picker.
 * @param onRemoveImage Called with the 0-based index when the × on a thumb is tapped.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImageAttachRow(
    imageUris: List<Uri>,
    onAddImageClick: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxReached = imageUris.size >= SendKudosMockData.MAX_IMAGES

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.send_image_label),
            style = MaterialTheme.typography.bodySmall,
            color = KudosDarkText
        )
        Spacer(Modifier.height(4.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            imageUris.forEachIndexed { index, uri ->
                ImageThumbnail(uri = uri, onRemove = { onRemoveImage(index) })
            }

            if (!maxReached) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, KudosBorder, AddButtonShape)
                        .clickable { onAddImageClick() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = KudosGray,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = stringResource(R.string.send_image_add_button, SendKudosMockData.MAX_IMAGES),
                        style = MaterialTheme.typography.labelSmall,
                        color = KudosGray
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageThumbnail(uri: Uri, onRemove: () -> Unit) {
    val thumb = rememberUriThumbnail(uri)
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .size(56.dp)
            .clip(ThumbShape)
            .border(1.dp, KudosBorder, ThumbShape)
            .background(KudosGray.copy(alpha = 0.15f), ThumbShape)
    ) {
        if (thumb != null) {
            Image(
                bitmap = thumb,
                contentDescription = stringResource(R.string.send_image_thumbnail_cd),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(ThumbShape)
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(16.dp)
                .padding(1.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                .clickable { onRemove() }
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.send_image_remove_cd),
                tint = Color.White,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

/** Decode a downsampled thumbnail for [uri] off the main thread. */
@Composable
private fun rememberUriThumbnail(uri: Uri): ImageBitmap? {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(uri) {
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val opts = BitmapFactory.Options().apply { inSampleSize = 4 }
                    BitmapFactory.decodeStream(input, null, opts)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    return bitmap
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun ImageAttachRowEmptyPreview() {
    KudosAppTheme {
        ImageAttachRow(
            imageUris = emptyList(),
            onAddImageClick = {},
            onRemoveImage = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
