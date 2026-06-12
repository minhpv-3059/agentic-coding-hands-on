package com.sun.kudos_demo.feature.feed

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Mock data for the Spotlight Board word cloud (design mms_B.7). The board scatters Sunner
 * names of varying sizes — NOT a node-and-edge graph. Names are placed on a deterministic
 * golden-angle spiral (no randomness — stable across recomposition and the build sandbox,
 * which forbids Math.random/Date). [SpotlightData.edges] is unused for the word cloud.
 */
object SpotlightMockData {

    // The seven names that fill the board in the design.
    private val names = listOf(
        "Đỗ Hoàng Hiệp", "Dương Thúy An", "Mai Phương Thúy", "Nguyễn Văn Quy",
        "Lê Kiều Trang", "Nguyễn Bá Chức", "Nguyễn Hoàng Linh"
    )

    val data: SpotlightData = buildCloud()

    private fun buildCloud(): SpotlightData {
        // Reduced from 84 → 32 nodes to eliminate overlap and spread names across full panel
        val count = 32
        val goldenAngle = 2.399963f
        val nodes = (0 until count).map { i ->
            // radius now spans edge-to-edge (0.95f factor) instead of cramping to centre (0.5f)
            val radius = sqrt((i + 0.5f) / count) * 0.95f
            val angle = i * goldenAngle
            val rawX = 0.5f + radius * cos(angle)
            val rawY = 0.5f + radius * sin(angle)
            SpotlightNode(
                id = "w$i",
                name = names[i % names.size],
                // clamp so words don't clip the rounded panel border
                x = rawX.coerceIn(0.06f, 0.94f),
                y = rawY.coerceIn(0.06f, 0.94f),
                weight = 0.5f + ((i * 37) % 100) / 100f   // 0.5..1.5 font-size factor, deterministic
            )
        }
        return SpotlightData(totalKudos = 388, nodes = nodes, edges = emptyList())
    }
}
