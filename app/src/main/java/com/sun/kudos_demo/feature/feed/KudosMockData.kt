package com.sun.kudos_demo.feature.feed

/**
 * Mock data for the Kudos Feed, sourced from the MoMorph design content
 * (names, codes, badges, titles, messages, hashtags, heart counts).
 *
 * Single source of truth for both the ViewModels and the @Preview composables so
 * the feed, cards, detail and search screens stay visually consistent.
 */
object KudosMockData {

    val departments = listOf("CEVC2", "CEVC3", "CEVC4", "CEVC1", "OPD", "Infra")

    val hashtags = listOf("Dedicated", "Inspring", "Teamwork", "Creative", "Leadership", "Supportive")

    private const val SHORT_MESSAGE =
        "Cảm ơn người em bình thường nhưng phi thường :D Cảm ơn sự chăm chỉ, cần mẫn của " +
            "em đã tạo động lực rất…"

    private const val LONG_MESSAGE =
        "Cảm ơn người em bình thường nhưng phi thường :D Cảm ơn sự chăm chỉ, cần mẫn của em đã " +
            "tạo động lực rất nhiều cho team, để luôn nhắc mình luôn phải nỗ lực hơn nữa trong công " +
            "việc. <3 và cuộc sống. Cảm ơn người em bình thường nhưng phi thường :D Cảm ơn sự chăm " +
            "chỉ, cần mẫn của em đã tạo động lực rất nhiều cho team, để luôn nhắc mình luôn phải nỗ " +
            "lực hơn nữa trong công việc. <3 và cuộc sống…"

    private fun user(id: String, name: String, code: String, badge: String?) =
        KudoUser(id = id, name = name, code = code, badge = badge)

    private val nhat = user("u1", "Huỳnh Dương Xuân Nhật", "CECV10", "Rising Hero")
    private val nhan = user("u2", "Dương Xuân Huỳnh Nhân", "CECV10", "Legend Hero")
    private val han = user("u3", "Dương Huỳnh Xuân Hân", "CECV1", "Rising Hero")
    private val anh = user("u4", "Nguyễn Hoàng Anh", "OPD1", "Legend Hero")
    private val minh = user("u5", "Trần Quang Minh", "CEVC4", null)

    /** Eight kudos with varied hearts so the Highlight carousel (top-5 by hearts) is meaningful. */
    val kudos: List<Kudo> = listOf(
        Kudo("k1", nhat, nhan, "CEVC2", "10:00 - 10/30/2025", "IDOL GIỚI TRẺ", SHORT_MESSAGE,
            listOf("Dedicated", "Inspring"), heartCount = 1000, recipientKudosCount = 52),
        Kudo("k2", nhan, han, "CEVC3", "10:00 - 10/30/2025", "NGƯỜI TRUYỀN LỬA", SHORT_MESSAGE,
            listOf("Teamwork", "Dedicated"), heartCount = 850, recipientKudosCount = 25),
        Kudo("k3", han, minh, "CEVC4", "10:00 - 10/30/2025", "CHIẾN BINH THẦM LẶNG", SHORT_MESSAGE,
            listOf("Creative", "Inspring"), heartCount = 720, recipientKudosCount = 12),
        Kudo("k4", null, nhan, "CEVC2", "10:00 - 10/30/2025", "NGƯỜI HÙNG CỦA LÒNG EM", LONG_MESSAGE,
            listOf("Dedicated", "Inspring"), heartCount = 500, imageCount = 5,
            recipientKudosCount = 30, isAnonymous = true),
        Kudo("k5", anh, nhat, "OPD", "10:00 - 10/30/2025", "MENTOR TUYỆT VỜI", SHORT_MESSAGE,
            listOf("Leadership"), heartCount = 300, recipientKudosCount = 8),
        Kudo("k6", minh, anh, "Infra", "10:00 - 10/30/2025", "HẬU PHƯƠNG VỮNG CHẮC", SHORT_MESSAGE,
            listOf("Supportive", "Teamwork"), heartCount = 120, recipientKudosCount = 10),
        Kudo("k7", nhat, minh, "CEVC1", "10:00 - 10/30/2025", "ĐỒNG ĐỘI ĂN Ý", SHORT_MESSAGE,
            listOf("Dedicated"), heartCount = 45, recipientKudosCount = 4),
        Kudo("k8", nhan, nhat, "CEVC3", "10:00 - 10/30/2025", "NGƯỜI HÙNG CỦA LÒNG EM", LONG_MESSAGE,
            listOf("Inspring", "Creative"), heartCount = 10, imageCount = 5, recipientKudosCount = 18)
    )

    val stats = KudoStats(
        received = 25, sent = 25, heartsReceived = 25,
        secretBoxOpened = 25, secretBoxUnopened = 25, fireBonusActive = true
    )

    val giftRecipients: List<GiftRecipient> = List(10) { i ->
        GiftRecipient(
            user = listOf(nhat, nhan, han, anh, minh)[i % 5].copy(id = "g$i"),
            giftDescription = "Nhận được 1 áo phông SAA"
        )
    }

    /** Pool the Sunner search screen filters over. */
    val searchableUsers: List<KudoUser> = listOf(
        user("s1", "Dương Huỳnh Xuân Nhật", "CECV1", null),
        user("s2", "Dương Huỳnh Xuân Nhân", "CECV1", null),
        user("s3", "Dương Hoàng Xuân Nhi", "CECV2", null),
        user("s4", "Huỳnh Dương Xuân Nhật", "CECV10", "Rising Hero"),
        user("s5", "Nguyễn Hoàng Anh", "OPD1", "Legend Hero"),
        user("s6", "Trần Quang Minh", "CEVC4", null),
        user("s7", "Lê Thị Thu Hà", "CEVC3", null),
        user("s8", "Phạm Văn Đức", "Infra", null)
    )

    /** Default "Recent" searches shown before the user types. */
    val recentSearches: List<KudoUser> = listOf(searchableUsers[0], searchableUsers[3])

    fun kudoById(id: String): Kudo? = kudos.firstOrNull { it.id == id }
}
