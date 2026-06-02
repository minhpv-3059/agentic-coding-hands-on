package com.sun.kudos_demo.navigation

/**
 * Central registry of navigation route patterns.
 * Parameterised routes expose a builder to produce a concrete path.
 */
object NavRoutes {
    const val LOGIN = "auth/login"
    const val HOME = "home"

    const val KUDOS_FEED = "kudos/feed"
    const val KUDOS_VIEW = "kudos/view/{id}"
    const val KUDOS_SEND = "kudos/send"

    const val PROFILE_ME = "profile/me"
    const val PROFILE_USER = "profile/{userId}"

    const val NOTIFICATIONS = "notifications"
    const val SECRET_BOX = "secret-box"
    const val AWARDS = "awards"
    const val RULES = "rules"

    const val ERROR_403 = "error/403"
    const val ERROR_404 = "error/404"

    // Argument keys for parameterised routes
    const val ARG_KUDO_ID = "id"
    const val ARG_USER_ID = "userId"

    fun kudosView(id: String) = "kudos/view/$id"
    fun profileUser(userId: String) = "profile/$userId"
}
