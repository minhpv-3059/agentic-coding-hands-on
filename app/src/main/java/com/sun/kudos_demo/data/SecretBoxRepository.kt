package com.sun.kudos_demo.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Opened / unopened Secret Box tallies shown on the Secret Box screen and the Profile stats. */
data class SecretBoxCounts(val unopened: Int, val opened: Int)

/**
 * Single in-memory source of truth for Secret Box counts (mock-data only, no unlock API).
 *
 * App-process singleton shared by the Secret Box screen and the Profile stats card so the
 * "đã mở / chưa mở" numbers stay in sync (clarifications.md Session 2026-06-16 — Phase 09).
 * Seeded from the designs: unopened = 5 ("Secret box chưa mở 05", authoritative for this
 * screen's counter) and opened = 25 (Profile design). Opening a box moves one from unopened
 * to opened. Not persisted across process death (in-memory, like [NotificationsRepository]).
 */
object SecretBoxRepository {

    /** Seeded tallies from the designs (unopened "05" on this screen, opened 25 on Profile). */
    private val SEED = SecretBoxCounts(unopened = 5, opened = 25)

    private val _counts = MutableStateFlow(SEED)

    /** Live counts — observed by the Secret Box screen and the Profile stats card. */
    val counts: StateFlow<SecretBoxCounts> = _counts.asStateFlow()

    /**
     * Open one box: unopened-- and opened++ (TC_SB_FUN_002, count decremented by 1).
     * No-op when there is nothing left to open (TC_SB_FUN_003).
     */
    fun openOne() = _counts.update { c ->
        if (c.unopened > 0) c.copy(unopened = c.unopened - 1, opened = c.opened + 1) else c
    }

    /** Test-only: restore the seeded counts so the singleton stays order-independent in unit tests. */
    internal fun resetForTest() { _counts.value = SEED }
}
