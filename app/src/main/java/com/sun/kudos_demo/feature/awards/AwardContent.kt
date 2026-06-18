package com.sun.kudos_demo.feature.awards

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Immutable data for a single award type on the Awards tab.
 * All user-visible text fields are @StringRes Int so the system resolves the
 * correct locale at render time via stringResource().
 */
data class AwardContent(
    val id: String,
    /** Shown in the dropdown + as the award title next to the badge icon. */
    @StringRes val dropdownLabelRes: Int,
    @DrawableRes val trophy: Int,
    /** Multi-paragraph award description — resolved from string resources. */
    @StringRes val descriptionRes: Int,
    /** e.g. "01", "02", "10" — numeric, not localizable */
    val quantity: String,
    /** e.g. "Cá nhân" / "Individual" — resolved from string resources. */
    @StringRes val quantityUnitRes: Int,
    /** 1 row for most awards; 2 rows for Signature 2025 - Creator. */
    val values: List<AwardValue>
)

/** One value row: amount (18sp bold) + note (14sp light). */
data class AwardValue(
    /** e.g. "15.000.000 VNĐ" — a data value, not localizable */
    val amount: String,
    /** e.g. "cho giải cá nhân" / "for the individual prize" — resolved from string resources. */
    @StringRes val noteRes: Int
)
