package com.sun.kudos_demo.data

import com.sun.kudos_demo.feature.feed.KudoUser

/**
 * Single source of truth for the signed-in Sunner (mock auth — no backend).
 *
 * The profile is "created" at login and its id persisted via [KudosPreferences.setCurrentUser],
 * then reused everywhere a current-user identity is needed — own profile, Send Kudos sender,
 * feed like rules — so the name/code/badge stay consistent across the whole app.
 *
 * Mirrors the existing [KudosRepository] pattern of a `data`-layer holder referencing feed models.
 */
object CurrentUser {

    /** Id used as the feed/send/profile current user. */
    const val ID = "u1"

    /** The logged-in Sunner shown on "Profile của tôi". */
    val profile = KudoUser(
        id = ID,
        name = "Phan Văn Minh",
        code = "CEVC1",
        badge = "Legend Hero"
    )
}
