package com.sun.kudos_demo.navigation

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * Unit tests for [NavRoutes] to verify route string builders
 * and route constants are well-formed.
 */
class NavRoutesTest {
    @Test
    fun kudosView_producesCorrectRoute() {
        val id = "42"
        assertEquals("kudos/view/42", NavRoutes.kudosView(id))
    }

    @Test
    fun kudosView_withEmptyId() {
        assertEquals("kudos/view/", NavRoutes.kudosView(""))
    }

    @Test
    fun profileUser_producesCorrectRoute() {
        val userId = "user123"
        assertEquals("profile/user123", NavRoutes.profileUser(userId))
    }

    @Test
    fun profileUser_withEmptyUserId() {
        assertEquals("profile/", NavRoutes.profileUser(""))
    }

    @Test
    fun routeConstants_areWellFormed() {
        // Verify no trailing slashes or double slashes
        val routes = listOf(
            NavRoutes.LOGIN,
            NavRoutes.HOME,
            NavRoutes.KUDOS_FEED,
            NavRoutes.KUDOS_ALL,
            NavRoutes.KUDOS_SEND,
            NavRoutes.KUDOS_COMMUNITY_STANDARDS,
            NavRoutes.PROFILE_ME,
            NavRoutes.NOTIFICATIONS,
            NavRoutes.SECRET_BOX,
            NavRoutes.AWARDS,
            NavRoutes.RULES,
            NavRoutes.SEARCH,
            NavRoutes.ERROR_403,
            NavRoutes.ERROR_404
        )
        routes.forEach { route ->
            assert(!route.endsWith("/")) { "Route '$route' should not end with /" }
            assert(!route.contains("//")) { "Route '$route' should not contain //" }
        }
    }

    @Test
    fun argumentKeys_areNonEmpty() {
        assert(NavRoutes.ARG_KUDO_ID.isNotEmpty())
        assert(NavRoutes.ARG_USER_ID.isNotEmpty())
    }
}
