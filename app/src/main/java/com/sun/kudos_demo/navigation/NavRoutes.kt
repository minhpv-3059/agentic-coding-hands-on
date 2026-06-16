package com.sun.kudos_demo.navigation

/**
 * Central registry of navigation route patterns.
 * Parameterised routes expose a builder to produce a concrete path.
 */
object NavRoutes {
    const val LOGIN = "auth/login"
    const val HOME = "home"

    const val KUDOS_FEED = "kudos/feed"
    const val KUDOS_ALL = "kudos/all"
    const val KUDOS_VIEW = "kudos/view/{id}"
    const val KUDOS_SEND = "kudos/send"
    const val KUDOS_COMMUNITY_STANDARDS = "kudos/community-standards"

    // Distinct path (no slash segment) so it can't be captured by the PROFILE_USER
    // wildcard "profile/{userId}" — "profile/me" would otherwise match as userId = "me".
    const val PROFILE_ME = "my-profile"
    const val PROFILE_USER = "profile/{userId}"

    const val NOTIFICATIONS = "notifications"
    const val SECRET_BOX = "secret-box"
    const val AWARDS = "awards"
    const val RULES = "rules"
    const val SEARCH = "search"

    const val ERROR_403 = "error/403"
    const val ERROR_404 = "error/404"

    // Argument keys for parameterised routes
    const val ARG_KUDO_ID = "id"
    const val ARG_USER_ID = "userId"
    const val ARG_RECIPIENT = "recipient"

    /** Send-Kudos route pattern carrying an optional pre-filled recipient id. */
    const val KUDOS_SEND_WITH_ARG = "$KUDOS_SEND?$ARG_RECIPIENT={$ARG_RECIPIENT}"

    fun kudosView(id: String) = "kudos/view/$id"
    fun profileUser(userId: String) = "profile/$userId"

    /** Open Send Kudos with [recipientId] pre-selected (from another user's profile CTA). */
    fun kudosSend(recipientId: String) = "$KUDOS_SEND?$ARG_RECIPIENT=$recipientId"
}
