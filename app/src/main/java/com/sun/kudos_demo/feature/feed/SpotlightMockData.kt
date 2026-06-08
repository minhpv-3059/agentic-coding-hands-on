package com.sun.kudos_demo.feature.feed

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Mock graph for the Spotlight Board network chart. Nodes are placed on a deterministic
 * golden-angle spiral (no randomness — keeps layout stable across recompositions and the
 * build sandbox, which forbids Math.random/Date).
 */
object SpotlightMockData {

    private val names = listOf(
        "Nhật", "Nhân", "Hân", "Minh", "Anh", "Hà", "Đức", "Linh",
        "Trang", "Khoa", "Vy", "Sơn", "Mai", "Phúc", "Tú", "Quân",
        "Thảo", "Hùng", "Lan", "Bình", "Nga", "Kiên", "My", "Long"
    )

    val data: SpotlightData = buildGraph()

    private fun buildGraph(): SpotlightData {
        val n = names.size
        val goldenAngle = 2.399963f
        val nodes = names.mapIndexed { i, name ->
            val radius = sqrt((i + 0.5f) / n) * 0.46f
            val angle = i * goldenAngle
            SpotlightNode(
                id = "n$i",
                name = name,
                x = 0.5f + radius * cos(angle),
                y = 0.5f + radius * sin(angle),
                weight = 0.7f + (i % 5) * 0.22f
            )
        }
        val edges = buildList {
            for (i in 0 until n) {
                add(SpotlightEdge("n$i", "n${(i + 1) % n}"))
                if (i % 2 == 0) add(SpotlightEdge("n$i", "n${(i * 5 + 3) % n}"))
            }
        }
        return SpotlightData(totalKudos = 388, nodes = nodes, edges = edges)
    }
}
