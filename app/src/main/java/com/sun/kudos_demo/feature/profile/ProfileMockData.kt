package com.sun.kudos_demo.feature.profile

import com.sun.kudos_demo.R
import com.sun.kudos_demo.data.CurrentUser
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.KudosMockData

/**
 * Mock data for the Profile screens (Phase 07), sourced from the MoMorph design content.
 *
 * Reuses the feed's Sunner pool and kudos so profile cards stay visually consistent with
 * the rest of the app. The signed-in user's id matches the feed's current user ("u1") so
 * the received / sent split lines up with the feed model.
 */
object ProfileMockData {

    /** Signed-in Sunner's id — single source of truth in [CurrentUser]; feed/send align on it. */
    const val CURRENT_USER_ID = CurrentUser.ID

    /** "Profile của tôi" identity — the logged-in user established at login ([CurrentUser]). */
    val currentUser: KudoUser = CurrentUser.profile

    /** Statistics card values (design section D.1: received 5, the rest 25). */
    val currentUserStats = ProfileStats(
        kudosReceived = 5,
        kudosSent = 25,
        heartsReceived = 25,
        secretBoxOpened = 25,
        secretBoxUnopened = 25
    )

    /**
     * Earned award badges shown on the other-user profile (design nodes 6885:10412–10417),
     * wired to the real Figma exports in res/drawable-nodpi/img_badge_*.png.
     */
    val awardBadges: List<AwardBadge> = listOf(
        AwardBadge("revival", "REVIVAL", R.drawable.img_badge_revival),
        AwardBadge("touch_of_light", "TOUCH OF LIGHT", R.drawable.img_badge_touch_of_light),
        AwardBadge("stay_gold", "STAY GOLD", R.drawable.img_badge_stay_gold),
        AwardBadge("flow_to_horizon", "FLOW TO HORIZON", R.drawable.img_badge_flow_to_horizon),
        AwardBadge("beyond_the_boundary", "BEYOND THE BOUNDARY", R.drawable.img_badge_beyond_boundary),
        AwardBadge("root_futher", "ROOT FUTHER", R.drawable.img_badge_root_futher)
    )

    private val others: List<KudoUser> = KudosMockData.searchableUsers

    /** Every Sunner reachable from the feed/search, indexed by id for profile lookups. */
    private val userIndex: Map<String, KudoUser> =
        (KudosMockData.kudos.flatMap { listOfNotNull(it.sender, it.recipient) } + others)
            .associateBy { it.id }

    /** Identity for a profile opened by an unknown id — the design depicts a Rising Hero. */
    private fun fallbackUser(id: String) =
        KudoUser(id = id, name = "Huỳnh Dương Xuân Nhật", code = "CEVC3", badge = "Rising Hero")

    /** Resolve the Sunner behind a profile route arg; never null so the screen always renders. */
    fun userById(id: String): KudoUser =
        if (id == CURRENT_USER_ID) currentUser else userIndex[id] ?: fallbackUser(id)

    /** Resolve the signed-in Sunner from a persisted login session id (null → canonical current user). */
    fun resolveCurrentUser(persistedId: String?): KudoUser =
        persistedId?.let { userById(it) } ?: currentUser

    /** 5 kudos received by [user] (design: profile shows 5 cards). Deterministic ids per user. */
    fun receivedKudosFor(user: KudoUser, idPrefix: String = user.id): List<Kudo> =
        KudosMockData.kudos.take(5).mapIndexed { i, k ->
            k.copy(id = "$idPrefix-recv-$i", sender = others[(i + 1) % others.size], recipient = user)
        }

    /** 5 kudos received by the current user (design dropdown: "Đã nhận (5)"). */
    val myReceivedKudos: List<Kudo> = receivedKudosFor(currentUser, "me")

    /** 5 kudos sent by the current user (design dropdown: "Đã gửi (5)"). */
    val mySentKudos: List<Kudo> = KudosMockData.kudos.take(5).mapIndexed { i, k ->
        k.copy(
            id = "me-sent-$i",
            sender = currentUser,
            recipient = others[i % others.size],
            department = others[i % others.size].code,
            isAnonymous = false
        )
    }

    /**
     * All profile kudos by id so the View-Kudo detail screen can resolve a tapped profile card.
     * Lazy so the per-user generation only runs on first detail navigation, not at class load.
     */
    private val allProfileKudos: Map<String, Kudo> by lazy {
        buildMap {
            (myReceivedKudos + mySentKudos).forEach { put(it.id, it) }
            userIndex.keys.forEach { id ->
                receivedKudosFor(userById(id), id).forEach { put(it.id, it) }
            }
        }
    }

    /** Look up a profile-only kudo (ids like "me-sent-0", "s2-recv-1") for the detail screen. */
    fun kudoById(id: String): Kudo? = allProfileKudos[id]
}
