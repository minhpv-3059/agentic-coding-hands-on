package com.sun.kudos_demo.feature.secretbox

import kotlin.random.Random

/**
 * Mock reward pool for the Secret Box (clarifications.md Session 2026-06-16 — Phase 09).
 *
 * Six prizes mirror the design reward states B–G. Each [SecretBoxReward.imageResName] points at
 * a user-exported Figma PNG (320×320) resolved at runtime by name, so the build works with
 * swappable placeholders until the assets land. Opening a box reveals one prize at random.
 */
object SecretBoxMockData {

    val rewards: List<SecretBoxReward> = listOf(
        SecretBoxReward("scarf", "Khăn Root Further", "img_secretbox_scarf"),       // state B
        SecretBoxReward("stamps", "Tem Root Further", "img_secretbox_stamps"),      // state C
        SecretBoxReward("mug", "Cốc Root Further", "img_secretbox_mug"),            // state D
        SecretBoxReward("tshirt", "Áo thun Burberry", "img_secretbox_tshirt"),      // state E
        SecretBoxReward("combo", "Cốc & Tem Root Further", "img_secretbox_combo"),  // state F
        SecretBoxReward("gift", "Phần quà SAA 2025", "img_secretbox_gift")          // state G
    )

    /** Pick a random prize when a box is opened (mock — no server-side reward logic). */
    fun randomReward(random: Random = Random.Default): SecretBoxReward =
        rewards[random.nextInt(rewards.size)]
}
