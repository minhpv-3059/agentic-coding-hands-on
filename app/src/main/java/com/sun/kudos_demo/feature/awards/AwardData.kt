package com.sun.kudos_demo.feature.awards

import com.sun.kudos_demo.R

/**
 * Static award content extracted verbatim from MoMorph/Figma design text nodes.
 * All user-visible text is now @StringRes so the runtime language switch (VN/EN)
 * is handled automatically by the Android resource system.
 *
 * Descriptions: mms_C2.1.3 TEXT nodes on each screen (b2BuS8HYIt, 7y195PPTxQ,
 * O98TwiHaJe, FQoJZLkG_d, QQvsfK3yaK, c-QM3_zjkG).
 * Quantities/values: number row TEXT nodes on each screen.
 */
object AwardData {

    val awards: List<AwardContent> = listOf(
        AwardContent(
            id = "mvp",
            dropdownLabelRes = R.string.award_mvp_label,
            trophy = R.drawable.img_award_mvp,
            descriptionRes = R.string.award_mvp_desc,
            quantity = "01",
            quantityUnitRes = R.string.award_unit_individual,
            values = listOf(AwardValue("15.000.000 VNĐ", R.string.award_note_individual_prize))
        ),
        AwardContent(
            id = "best_manager",
            dropdownLabelRes = R.string.award_best_manager_label,
            trophy = R.drawable.img_award_best_manager,
            descriptionRes = R.string.award_best_manager_desc,
            quantity = "01",
            quantityUnitRes = R.string.award_unit_individual,
            values = listOf(AwardValue("10.000.000 VNĐ", R.string.award_note_each_prize))
        ),
        AwardContent(
            id = "signature_creator",
            dropdownLabelRes = R.string.award_signature_creator_label,
            trophy = R.drawable.img_award_signature_creator,
            descriptionRes = R.string.award_signature_creator_desc,
            quantity = "01",
            quantityUnitRes = R.string.award_unit_individual_or_group,
            values = listOf(
                AwardValue("5.000.000 VNĐ", R.string.award_note_individual_prize),
                AwardValue("8.000.000 VNĐ", R.string.award_note_group_prize)
            )
        ),
        AwardContent(
            id = "top_project",
            dropdownLabelRes = R.string.award_top_project_label,
            trophy = R.drawable.img_award_top_project,
            descriptionRes = R.string.award_top_project_desc,
            quantity = "02",
            quantityUnitRes = R.string.award_unit_group,
            values = listOf(AwardValue("15.000.000 VNĐ", R.string.award_note_each_prize))
        ),
        AwardContent(
            id = "top_project_leader",
            dropdownLabelRes = R.string.award_top_project_leader_label,
            trophy = R.drawable.img_award_top_project_leader,
            descriptionRes = R.string.award_top_project_leader_desc,
            quantity = "03",
            quantityUnitRes = R.string.award_unit_individual,
            values = listOf(AwardValue("7.000.000 VNĐ", R.string.award_note_each_prize))
        ),
        AwardContent(
            id = "top_talent",
            dropdownLabelRes = R.string.award_top_talent_label,
            trophy = R.drawable.img_award_top_talent,
            descriptionRes = R.string.award_top_talent_desc,
            quantity = "10",
            quantityUnitRes = R.string.award_unit_individual,
            values = listOf(AwardValue("7.000.000 VNĐ", R.string.award_note_each_prize))
        )
    )

    /** Find award by [id]; falls back to MVP (first) if id is blank or not found. */
    fun byId(id: String?): AwardContent =
        awards.firstOrNull { it.id == id } ?: awards.first()
}
