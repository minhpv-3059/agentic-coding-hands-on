package com.sun.kudos_demo.feature.secretbox

/**
 * Visual phase of the single Secret Box screen (clarifications.md Session 2026-06-16 — Phase 09).
 *
 * One screen drives three states from the ViewModel rather than navigating between frames:
 *  - [CLOSED]  idle: looping box video (A) + "Click vào box để mở" + unopened counter
 *  - [OPENING] tap → open videos (B then C) play once; box reveals the prize slot
 *  - [REWARD]  prize PNG revealed + congratulations heading + prize name + "Tiếp tục"
 */
enum class SecretBoxPhase { CLOSED, OPENING, REWARD }

/**
 * A Secret Box prize revealed after the open animation. The 6 prize images are user-exported
 * Figma PNGs (320×320) — referenced by [imageResName] and resolved at runtime via
 * resources.getIdentifier so the app still builds while the assets land (swappable placeholder
 * precedent, phase 04/07). [name] is the caption shown under the prize.
 */
data class SecretBoxReward(
    val id: String,
    val name: String,
    val imageResName: String
)
