package com.sun.kudos_demo.feature.profile

import com.sun.kudos_demo.R
import com.sun.kudos_demo.data.CurrentUser
import com.sun.kudos_demo.feature.profile.components.rankBadgeRes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Guards the rank-label → pill-drawable mapping and the logged-in identity. */
class RankBadgeMappingTest {

    @Test
    fun rankBadgeRes_LegendHero_MapsToLegendDrawable() {
        assertEquals(R.drawable.img_rank_legend_hero, rankBadgeRes("Legend Hero"))
    }

    @Test
    fun rankBadgeRes_RisingHero_MapsToRisingDrawable() {
        assertEquals(R.drawable.img_rank_rising_hero, rankBadgeRes("Rising Hero"))
    }

    @Test
    fun rankBadgeRes_IsCaseInsensitiveAndTrimmed() {
        assertEquals(R.drawable.img_rank_legend_hero, rankBadgeRes("  legend hero  "))
    }

    @Test
    fun rankBadgeRes_UnknownRank_ReturnsNull() {
        assertNull(rankBadgeRes("Super Hero"))
    }

    @Test
    fun currentUser_IsPhanVanMinh() {
        assertEquals("u1", CurrentUser.ID)
        assertEquals("Phan Văn Minh", CurrentUser.profile.name)
        assertEquals("CEVC1", CurrentUser.profile.code)
        assertEquals("Legend Hero", CurrentUser.profile.badge)
    }
}
