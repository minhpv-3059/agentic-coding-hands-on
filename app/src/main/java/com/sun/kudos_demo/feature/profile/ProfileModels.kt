package com.sun.kudos_demo.feature.profile

import androidx.annotation.DrawableRes

/**
 * Domain models for the Profile screens (Phase 07). Mock-data only — no real API.
 *
 * Shared contract between the presentational screens (Track A) and [ProfileViewModel]
 * (Track B). Avatars reuse [com.sun.kudos_demo.ui.components.KudoAvatar] placeholders
 * (real photos are unreliable — see clarifications.md).
 */

/** Which Kudos list the own-profile filter shows (design dropdown: Đã nhận / Đã gửi). */
enum class ProfileKudosTab { RECEIVED, SENT }

/** Aggregate counters shown in the own-profile statistics card (design section D.1). */
data class ProfileStats(
    val kudosReceived: Int,
    val kudosSent: Int,
    val heartsReceived: Int,
    val secretBoxOpened: Int,
    val secretBoxUnopened: Int
)

/**
 * An award icon in the other-user profile badge row (REVIVAL, TOUCH OF LIGHT, …).
 *
 * [icon] is null until the real Figma export is wired — null renders a styled placeholder
 * badge so the layout stays pixel-accurate (clarifications.md, Session 2026-06-15).
 */
data class AwardBadge(
    val id: String,
    val label: String,
    @DrawableRes val icon: Int? = null
)
