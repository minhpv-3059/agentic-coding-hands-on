package com.sun.kudos_demo.feature.secretbox

import com.sun.kudos_demo.R
import kotlin.random.Random

/**
 * Mock reward pool for the Secret Box (clarifications.md Session 2026-06-16 — Phase 09).
 *
 * Six prizes mirror the design reward states B–G. Each [SecretBoxReward.imageResName] points at
 * a user-exported Figma PNG (320×320) resolved at runtime by name, so the build works with
 * swappable placeholders until the assets land. Opening a box reveals one prize at random.
 *
 * Gift names are now [SecretBoxReward.nameRes] (@StringRes Int) so the runtime VN↔EN locale
 * switch is handled by stringResource() at the render site — no String literals here.
 */
object SecretBoxMockData {

    val rewards: List<SecretBoxReward> = listOf(
        SecretBoxReward("scarf",  R.string.sb_gift_scarf,  "img_secretbox_scarf"),   // state B
        SecretBoxReward("stamps", R.string.sb_gift_stamps, "img_secretbox_stamps"),  // state C
        SecretBoxReward("mug",    R.string.sb_gift_mug,    "img_secretbox_mug"),     // state D
        SecretBoxReward("tshirt", R.string.sb_gift_tshirt, "img_secretbox_tshirt"),  // state E
        SecretBoxReward("combo",  R.string.sb_gift_combo,  "img_secretbox_combo"),   // state F
        SecretBoxReward("gift",   R.string.sb_gift_gift,   "img_secretbox_gift")     // state G
    )

    /** Pick a random prize when a box is opened (mock — no server-side reward logic). */
    fun randomReward(random: Random = Random.Default): SecretBoxReward =
        rewards[random.nextInt(rewards.size)]
}
