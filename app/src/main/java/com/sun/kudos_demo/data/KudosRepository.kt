package com.sun.kudos_demo.data

import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudosMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single in-memory source of truth for the live Kudos feed (mock-data only, no API).
 *
 * Seeded from [KudosMockData.kudos]. The Send Kudos flow (Phase 06) prepends newly
 * submitted kudos via [addKudo] so they appear at the top of the feed when the user
 * navigates back — see clarifications.md "Session 2026-06-12".
 *
 * App-process singleton: shared across the Feed, All Kudos and View Kudo screens.
 */
object KudosRepository {

    private val _kudos = MutableStateFlow(KudosMockData.kudos)

    /** Live feed list — emits a new value whenever a kudo is added. */
    val kudos: StateFlow<List<Kudo>> = _kudos.asStateFlow()

    /** Prepend a freshly created kudo so it surfaces at the top of the feed. */
    fun addKudo(kudo: Kudo) = _kudos.update { current -> listOf(kudo) + current }

    /** Look up a kudo by id across the live list (includes just-sent kudos). */
    fun kudoById(id: String): Kudo? = _kudos.value.firstOrNull { it.id == id }
}
