package com.sun.kudos_demo.feature.feed.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.feature.feed.SpotlightData
import com.sun.kudos_demo.feature.feed.SpotlightMockData
import com.sun.kudos_demo.feature.feed.SpotlightNode
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

private val PanelShape = RoundedCornerShape(8.dp)
private val SearchPillShape = RoundedCornerShape(50)

/**
 * Spotlight Board (design mms_B.7) — a pan/zoomable word cloud of Sunner names with the total
 * count overlaid and a live search that highlights matching names (TC_FUN_028/036, MaxLength 100
 * per TC_FUN_034). Mock data only. Measured text layouts are cached per query for smooth gestures.
 *
 * Design panel: 335x159px, corner 8dp, bg = keyvisual + 0.65 scrim, border KudosBorder 1dp.
 * "388 KUDOS": white, labelMedium, Normal, TopCenter ~10dp top (node 6885:9219 x≈50%, y≈2.5%).
 * Search pill: compact overlay TopStart ~10dp (node 6885:9216 x≈2%, y≈5%).
 */
@Composable
fun SpotlightBoard(
    data: SpotlightData,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var query by remember { mutableStateOf("") }
    val trimmed = query.trim()

    val measurer = rememberTextMeasurer()
    val laidOut = remember(trimmed, data) { layoutWords(data, trimmed, measurer) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            // FIX (MEDIUM): 220dp → 180dp to match design ~2:1 proportion (335x159px)
            .height(180.dp)
            .clip(PanelShape)
            // FIX (MEDIUM): add 1dp KudosBorder border, corner radius 8dp (was 12dp)
            .border(1.dp, KudosBorder, PanelShape)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.6f, 4f)
                    offset += pan
                }
            }
    ) {
        // Layer 1a: fallback dark background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00070C))
        )

        // Layer 1b: keyvisual image — design uses bg glow as background behind names
        Image(
            painter = painterResource(id = R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Layer 1c: dark scrim 0.65 alpha so names stay legible over the keyvisual
        // FIX (MEDIUM): replace flat drawRect(KudosContainer2) with keyvisual + scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
        )

        // Layer 2: pan/zoom word cloud on Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale; scaleY = scale
                    translationX = offset.x; translationY = offset.y
                }
        ) {
            laidOut.forEach { (node, layout) ->
                drawText(
                    textLayoutResult = layout,
                    topLeft = Offset(
                        node.x * size.width - layout.size.width / 2f,
                        node.y * size.height - layout.size.height / 2f
                    )
                )
            }
        }

        // Layer 3: "388 KUDOS" label — fixed, not panned/zoomed
        // FIX (HIGH): was KudosGold+Bold+titleLarge+TopStart → now KudosWhite+Normal+labelMedium+TopCenter
        // Node 6885:9219: startX=156/335≈46.6% → TopCenter; y=4/159≈2.5% → ~10dp top padding
        Text(
            text = "${data.totalKudos} KUDOS",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
            color = KudosWhite,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
        )

        // Layer 4: compact search pill overlay inside the panel (design node 6885:9216)
        // FIX (HIGH): was full-width OutlinedTextField below panel → now compact pill TopStart inside panel
        SearchPillOverlay(
            query = query,
            onQueryChange = { query = it.take(100) },   // MaxLength 100 (TC_FUN_034)
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 10.dp)
        )

        // Layer 5 (LOW): faint ambient activity ticker near bottom
        Text(
            text = "08:30PM Nguyễn Bá Chức đã nhận được một Kudos mới",
            style = TextStyle(
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = KudosWhite.copy(alpha = 0.30f)
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 6.dp)
        )
    }
}

/** Compact search pill overlay anchored top-left inside the panel (design node 6885:9216). */
@Composable
private fun SearchPillOverlay(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(SearchPillShape)
            .background(KudosGold.copy(alpha = 0.10f))
            .border(1.dp, KudosBorder, SearchPillShape)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = KudosGold,
            modifier = Modifier.size(10.dp)
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                color = KudosWhite,
                fontSize = 10.sp
            ),
            cursorBrush = SolidColor(KudosGold),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.feed_spotlight_search_hint),
                        style = TextStyle(color = KudosGray, fontSize = 10.sp)
                    )
                }
                inner()
            },
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}

/** Pre-measure each word once (per query) so pan/zoom gestures don't re-measure every frame. */
private fun layoutWords(
    data: SpotlightData,
    query: String,
    measurer: TextMeasurer
): List<Pair<SpotlightNode, TextLayoutResult>> = data.nodes.map { node ->
    val hit = query.isNotEmpty() && node.name.contains(query, ignoreCase = true)
    val style = TextStyle(
        color = if (hit) KudosGold else KudosWhite.copy(alpha = 0.30f + node.weight * 0.45f),
        // FIX (HIGH): (9f+weight*5f) → (6f+weight*4f) ≈ 8.5..11.5sp — smaller labels match design
        fontSize = (6f + node.weight * 4f).sp,
        fontWeight = if (hit) FontWeight.Bold else FontWeight.Normal
    )
    node to measurer.measure(node.name, style)
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun SpotlightBoardPreview() {
    KudosAppTheme {
        SpotlightBoard(
            data = SpotlightMockData.data,
            modifier = Modifier.padding(16.dp)
        )
    }
}
