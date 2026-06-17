package com.sun.kudos_demo.feature.notifications.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Nút "Đánh dấu đọc tất cả" — design node 6885:9392.
 *
 * Kích thước: width ~181dp, height 40dp (padding vertical 16dp trong component gốc).
 * Layout: icon filter/list (24dp) + gap 4dp + text "Đánh dấu đọc tất cả" (FontWeight 700, 14sp, white).
 * Căn trái, cách mép trái screen 20dp (padding đặt ở ngoài, tại NotificationsScreen).
 */
@Composable
fun MarkAllReadButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon "list / filter" — design node I6885:9392;186:1709 (24x24 MM_MEDIA_IC)
        // Figma export 500 → dùng Material Icons.Filled.Menu làm closest match (3 dòng ngang)
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = null,
            tint = KudosWhite,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "Đánh dấu đọc tất cả",
            style = MaterialTheme.typography.titleMedium.copy(
                // design: Montserrat 700 14sp — titleMedium là Medium 14sp, override weight
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = KudosWhite
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun MarkAllReadButtonPreview() {
    KudosAppTheme {
        MarkAllReadButton(onClick = {})
    }
}
