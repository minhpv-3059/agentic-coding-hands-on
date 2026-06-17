package com.sun.kudos_demo.feature.feed

/**
 * Domain models for the Kudos Feed (Phase 05). Mock-data only — no real API.
 *
 * Real photo assets from MoMorph are unreliable (see clarifications.md), so
 * avatars are rendered as placeholders by [com.sun.kudos_demo.ui.components.KudoAvatar];
 * no image URLs are carried on the user model.
 */

/** A Sunner shown as sender or recipient on a Kudo. */
data class KudoUser(
    val id: String,
    val name: String,
    val code: String,            // e.g. "CECV10"
    val badge: String? = null    // "Rising Hero", "Legend Hero", or null
)

/**
 * A single Kudo post. Anonymous kudos hide the sender: [sender] is null and the
 * card shows [anonymousAlias] with the "Người gửi ẩn danh" sub-label.
 */
data class Kudo(
    val id: String,
    val sender: KudoUser?,                       // null when anonymous
    val recipient: KudoUser,
    val department: String,                      // recipient's department code — drives the Phòng ban filter
    val timeRange: String,                       // "10:00 - 10/30/2025"
    val title: String,                           // "IDOL GIỚI TRẺ"
    val message: String,
    val hashtags: List<String> = emptyList(),    // ["Dedicated", "Inspring"] — rendered with leading '#'
    val heartCount: Int = 0,
    val imageCount: Int = 0,                      // attached photos (placeholder thumbnails on the detail screen)
    val recipientKudosCount: Int = 0,            // total kudos the recipient has received — drives the star badge
    val isAnonymous: Boolean = false,
    val anonymousAlias: String = "Anh Hùng Xạ Điêu"
) {
    /** Display name for the sender slot (alias when anonymous). */
    val senderDisplayName: String get() = if (isAnonymous) anonymousAlias else sender?.name.orEmpty()
}

/** Hoa thị (star badge) level for a recipient: 1 ≥10, 2 ≥20, 3 ≥50 kudos received (TC_FUN_006). */
fun starLevel(kudosReceived: Int): Int = when {
    kudosReceived >= 50 -> 3
    kudosReceived >= 20 -> 2
    kudosReceived >= 10 -> 1
    else -> 0
}

/** Personal statistics block (design section D.1). */
data class KudoStats(
    val received: Int,
    val sent: Int,
    val heartsReceived: Int,
    val secretBoxOpened: Int,
    val secretBoxUnopened: Int,
    val fireBonusActive: Boolean = false   // x2 fire badge next to "Số tim bạn nhận được" (TC_FUN_013)
)

/** A row in "10 Sunner nhận quà mới nhất" (design section D.3). */
data class GiftRecipient(
    val user: KudoUser,
    val giftDescription: String            // "Nhận được 1 áo phông SAA"
)

/** A node (a Sunner) in the Spotlight network chart. [x]/[y] are normalized 0..1 layout positions. */
data class SpotlightNode(
    val id: String,
    val name: String,
    val x: Float,
    val y: Float,
    val weight: Float = 1f                 // relative node radius
)

/** An undirected connection between two Spotlight nodes (a kudos relationship). */
data class SpotlightEdge(val from: String, val to: String)

/** Backing data for the Spotlight Board network chart (design section B.7). */
data class SpotlightData(
    val totalKudos: Int,
    val nodes: List<SpotlightNode>,
    val edges: List<SpotlightEdge>
)
