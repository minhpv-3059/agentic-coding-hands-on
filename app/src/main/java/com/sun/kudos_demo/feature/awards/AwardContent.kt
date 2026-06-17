package com.sun.kudos_demo.feature.awards

import androidx.annotation.DrawableRes

/**
 * Immutable data for a single award type on the Awards tab.
 * Text fields are verbatim from MoMorph/Figma design nodes.
 */
data class AwardContent(
    val id: String,
    /** Shown in the dropdown + as the award title next to the badge icon. */
    val dropdownLabel: String,
    @DrawableRes val trophy: Int,
    /** Multi-paragraph award description — verbatim from Figma text node. */
    val description: String,
    /** e.g. "01", "02", "10" */
    val quantity: String,
    /** e.g. "Cá nhân", "Tập thể", "Cá nhân hoặc tập thể" */
    val quantityUnit: String,
    /** 1 row for most awards; 2 rows for Signature 2025 - Creator. */
    val values: List<AwardValue>
)

/** One value row: amount (18sp bold) + note (14sp light). */
data class AwardValue(
    val amount: String,
    val note: String
)
