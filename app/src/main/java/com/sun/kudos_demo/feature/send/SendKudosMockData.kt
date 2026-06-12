package com.sun.kudos_demo.feature.send

import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.KudosMockData

/**
 * Mock option pools for the Send Kudos form (Phase 06), sourced from the MoMorph
 * design content (dropdown frames aKWA2klsnt / 5MU728Tjck and the form key-visual).
 * No invented data — values mirror what the design shows.
 */
object SendKudosMockData {

    /** Recipient pool for the "Người nhận" dropdown — reuses the feed's searchable Sunners. */
    val recipients: List<KudoUser> = KudosMockData.searchableUsers

    /** The signed-in Sunner — sender on non-anonymous kudos (id matches feed CURRENT_USER_ID "u1"). */
    val currentUser: KudoUser = KudoUser(id = "u1", name = "Phạm Văn Minh", code = "CEVC1", badge = null)

    /**
     * Preset "Danh hiệu" options (single-select dropdown). Phrased per the field helper
     * ("Ví dụ: Người truyền động lực cho tôi") and the kudo titles used across the feed.
     */
    val danhHieuOptions: List<String> = listOf(
        "Người truyền động lực cho tôi",
        "Người truyền lửa",
        "Đồng đội tin cậy",
        "Chiến binh thầm lặng",
        "Mentor tuyệt vời",
        "Hậu phương vững chắc",
        "Idol giới trẻ",
        "Người hùng của lòng em"
    )

    /**
     * Hashtag options shown in the form's hashtag dropdown — the Sun* core-value tags
     * exactly as labelled in the design (frame aKWA2klsnt). Stored without the leading '#';
     * the UI renders the '#'. Max 5 selectable.
     */
    val hashtagOptions: List<String> = listOf(
        "BE OPTIMISTIC",
        "WASSHOI",
        "BE A TEAM",
        "High-performing",
        "BE PROFESSIONAL",
        "THINK OUTSIDE THE BOX",
        "GET RISKY",
        "GO FAST"
    )

    const val MAX_HASHTAGS = 5
    const val MAX_IMAGES = 5

    /** Default placeholder nickname shown in the "Nickname ẩn danh" field (design value). */
    const val DEFAULT_ANONYMOUS_NICKNAME = "Doremon"
}
