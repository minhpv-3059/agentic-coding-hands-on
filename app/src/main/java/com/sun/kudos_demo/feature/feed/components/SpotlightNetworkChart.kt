package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.feature.feed.SpotlightData
import com.sun.kudos_demo.feature.feed.SpotlightMockData
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Interactive Spotlight Board network chart (design B.7). Renders the Sunner graph on a
 * Canvas, supports pan + pinch-zoom (a graphicsLayer transform), and highlights the node
 * whose name matches the live search query. Mock data only — no backend.
 */
@Composable
fun SpotlightNetworkChart(
    data: SpotlightData,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var query by remember { mutableStateOf("") }

    val highlightedId = remember(query) {
        if (query.isBlank()) null
        else data.nodes.firstOrNull { it.name.contains(query.trim(), ignoreCase = true) }?.id
    }
    val noMatch = query.isNotBlank() && highlightedId == null
    val measurer = rememberTextMeasurer()
    val nodeById = remember(data) { data.nodes.associateBy { it.id } }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "${data.totalKudos} KUDOS",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = KudosGold
        )
        Spacer(Modifier.height(8.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 4f)
                        offset += pan
                    }
                }
                .graphicsLayer { scaleX = scale; scaleY = scale; translationX = offset.x; translationY = offset.y }
        ) {
            drawRect(color = KudosContainer2)
            fun pos(id: String) = nodeById[id]?.let { Offset(it.x * size.width, it.y * size.height) }

            // Edges first so nodes sit on top.
            data.edges.forEach { edge ->
                val a = pos(edge.from); val b = pos(edge.to)
                if (a != null && b != null) {
                    drawLine(color = KudosBorder.copy(alpha = 0.35f), start = a, end = b, strokeWidth = 1f)
                }
            }
            // Nodes + labels.
            data.nodes.forEach { node ->
                val center = Offset(node.x * size.width, node.y * size.height)
                val highlighted = node.id == highlightedId
                val radius = (if (highlighted) 9f else 5f) * node.weight + 2f
                drawCircle(
                    color = if (highlighted) KudosGold else KudosWhite.copy(alpha = 0.55f),
                    radius = radius,
                    center = center
                )
                drawText(
                    textMeasurer = measurer,
                    text = node.name,
                    topLeft = Offset(center.x + radius + 2f, center.y - 7f),
                    style = TextStyle(
                        color = if (highlighted) KudosGold else KudosWhite.copy(alpha = 0.7f),
                        fontSize = if (highlighted) 12.sp else 9.sp,
                        fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it.take(100) },     // MaxLength 100 (TC_FUN_034)
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Tìm kiếm sunner", color = KudosGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KudosGray) },
            supportingText = if (noMatch) {
                { Text("Không tìm thấy sunner phù hợp", color = KudosGray) }
            } else null,
            keyboardOptions = KeyboardOptions.Default,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = KudosWhite,
                unfocusedTextColor = KudosWhite,
                focusedBorderColor = KudosGold,
                unfocusedBorderColor = KudosBorder,
                cursorColor = KudosGold
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun SpotlightNetworkChartPreview() {
    KudosAppTheme {
        SpotlightNetworkChart(
            data = SpotlightMockData.data,
            modifier = Modifier.padding(16.dp)
        )
    }
}
