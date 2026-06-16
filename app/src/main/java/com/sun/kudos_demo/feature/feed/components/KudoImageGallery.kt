package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosBgUpdate
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosGray

/**
 * Horizontal row of square image thumbnails (placeholder boxes) for attached photos.
 *
 * Design ref: [iOS] Sun*Kudos_View kudo — "list" row (id 6885:10168):
 *   - 5 thumbnails, 32×32 dp each, gap 4 dp
 *   - White background, border 0.45 dp KudosBorder (#998C5F), radius 8 dp
 *   - Each tap triggers onImageClick(index)
 *
 * When imageCount == 0, nothing is rendered.
 */
@Composable
fun KudoImageGallery(
    imageCount: Int,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (imageCount <= 0) return

    val thumbnailShape = RoundedCornerShape(8.dp)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(imageCount) { index ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(thumbnailShape)
                    .background(KudosBgUpdate)
                    .border(width = 0.5.dp, color = KudosBorder, shape = thumbnailShape)
                    .clickable(role = Role.Image) { onImageClick(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Ảnh đính kèm ${index + 1}",
                    tint = KudosGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun KudoImageGalleryPreview() {
    KudosAppTheme {
        KudoImageGallery(
            imageCount = 5,
            onImageClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1)
@Composable
private fun KudoImageGalleryEmptyPreview() {
    KudosAppTheme {
        KudoImageGallery(
            imageCount = 0,
            onImageClick = {}
        )
    }
}
