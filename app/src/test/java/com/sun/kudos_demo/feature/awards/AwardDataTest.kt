package com.sun.kudos_demo.feature.awards

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for [AwardData] object (Phase 10).
 *
 * Tests that the awards list is properly initialized with correct IDs,
 * quantities, value counts, and byId() fallback behavior.
 */
class AwardDataTest {

    // ===================== Awards list initialization tests =====================

    @Test
    fun awardData_Awards_HasExactlySixAwards() {
        assertEquals("AwardData must contain exactly 6 awards", 6, AwardData.awards.size)
    }

    @Test
    fun awardData_Awards_AreInCorrectOrder() {
        val expectedIds = listOf("mvp", "best_manager", "signature_creator", "top_project", "top_project_leader", "top_talent")
        val actualIds = AwardData.awards.map { it.id }
        assertEquals("Awards must be in documented order", expectedIds, actualIds)
    }

    // ===================== Individual award tests =====================

    @Test
    fun awardData_MVP_HasCorrectId() {
        val mvp = AwardData.awards[0]
        assertEquals("First award must be mvp", "mvp", mvp.id)
    }

    @Test
    fun awardData_BestManager_HasCorrectId() {
        val bestManager = AwardData.awards[1]
        assertEquals("Second award must be best_manager", "best_manager", bestManager.id)
    }

    @Test
    fun awardData_SignatureCreator_HasCorrectId() {
        val signatureCreator = AwardData.awards[2]
        assertEquals("Third award must be signature_creator", "signature_creator", signatureCreator.id)
    }

    @Test
    fun awardData_TopProject_HasCorrectId() {
        val topProject = AwardData.awards[3]
        assertEquals("Fourth award must be top_project", "top_project", topProject.id)
    }

    @Test
    fun awardData_TopProjectLeader_HasCorrectId() {
        val topProjectLeader = AwardData.awards[4]
        assertEquals("Fifth award must be top_project_leader", "top_project_leader", topProjectLeader.id)
    }

    @Test
    fun awardData_TopTalent_HasCorrectId() {
        val topTalent = AwardData.awards[5]
        assertEquals("Sixth award must be top_talent", "top_talent", topTalent.id)
    }

    // ===================== Award quantities tests =====================

    @Test
    fun awardData_MVP_HasCorrectQuantity() {
        val mvp = AwardData.awards[0]
        assertEquals("MVP quantity must be 01", "01", mvp.quantity)
        assertEquals("MVP quantityUnit must be Cá nhân", "Cá nhân", mvp.quantityUnit)
    }

    @Test
    fun awardData_BestManager_HasCorrectQuantity() {
        val bestManager = AwardData.awards[1]
        assertEquals("Best Manager quantity must be 01", "01", bestManager.quantity)
        assertEquals("Best Manager quantityUnit must be Cá nhân", "Cá nhân", bestManager.quantityUnit)
    }

    @Test
    fun awardData_SignatureCreator_HasCorrectQuantity() {
        val signatureCreator = AwardData.awards[2]
        assertEquals("Signature Creator quantity must be 01", "01", signatureCreator.quantity)
        assertEquals("Signature Creator quantityUnit must be Cá nhân hoặc tập thể", "Cá nhân hoặc tập thể", signatureCreator.quantityUnit)
    }

    @Test
    fun awardData_TopProject_HasCorrectQuantity() {
        val topProject = AwardData.awards[3]
        assertEquals("Top Project quantity must be 02", "02", topProject.quantity)
        assertEquals("Top Project quantityUnit must be Tập thể", "Tập thể", topProject.quantityUnit)
    }

    @Test
    fun awardData_TopProjectLeader_HasCorrectQuantity() {
        val topProjectLeader = AwardData.awards[4]
        assertEquals("Top Project Leader quantity must be 03", "03", topProjectLeader.quantity)
        assertEquals("Top Project Leader quantityUnit must be Cá nhân", "Cá nhân", topProjectLeader.quantityUnit)
    }

    @Test
    fun awardData_TopTalent_HasCorrectQuantity() {
        val topTalent = AwardData.awards[5]
        assertEquals("Top Talent quantity must be 10", "10", topTalent.quantity)
        assertEquals("Top Talent quantityUnit must be Cá nhân", "Cá nhân", topTalent.quantityUnit)
    }

    // ===================== Award values tests =====================

    @Test
    fun awardData_MVP_HasExactlyOneValue() {
        val mvp = AwardData.awards[0]
        assertEquals("MVP must have exactly 1 value row", 1, mvp.values.size)
    }

    @Test
    fun awardData_BestManager_HasExactlyOneValue() {
        val bestManager = AwardData.awards[1]
        assertEquals("Best Manager must have exactly 1 value row", 1, bestManager.values.size)
    }

    @Test
    fun awardData_SignatureCreator_HasExactlyTwoValues() {
        val signatureCreator = AwardData.awards[2]
        assertEquals("Signature Creator must have exactly 2 value rows", 2, signatureCreator.values.size)
    }

    @Test
    fun awardData_TopProject_HasExactlyOneValue() {
        val topProject = AwardData.awards[3]
        assertEquals("Top Project must have exactly 1 value row", 1, topProject.values.size)
    }

    @Test
    fun awardData_TopProjectLeader_HasExactlyOneValue() {
        val topProjectLeader = AwardData.awards[4]
        assertEquals("Top Project Leader must have exactly 1 value row", 1, topProjectLeader.values.size)
    }

    @Test
    fun awardData_TopTalent_HasExactlyOneValue() {
        val topTalent = AwardData.awards[5]
        assertEquals("Top Talent must have exactly 1 value row", 1, topTalent.values.size)
    }

    @Test
    fun awardData_SignatureCreator_Values_AreForPersonalAndTeam() {
        val signatureCreator = AwardData.awards[2]
        val (personal, team) = signatureCreator.values
        assertTrue("First value must be for personal (cá nhân)", personal.note.contains("cá nhân"))
        assertTrue("Second value must be for team (tập thể)", team.note.contains("tập thể"))
    }

    // ===================== byId() tests =====================

    @Test
    fun byId_WithValidMvpId_ReturnsMvp() {
        val award = AwardData.byId("mvp")
        assertEquals("byId(\"mvp\") must return MVP award", "mvp", award.id)
    }

    @Test
    fun byId_WithValidBestManagerId_ReturnsBestManager() {
        val award = AwardData.byId("best_manager")
        assertEquals("byId(\"best_manager\") must return Best Manager", "best_manager", award.id)
    }

    @Test
    fun byId_WithValidSignatureCreatorId_ReturnsSignatureCreator() {
        val award = AwardData.byId("signature_creator")
        assertEquals("byId(\"signature_creator\") must return Signature Creator", "signature_creator", award.id)
    }

    @Test
    fun byId_WithValidTopProjectId_ReturnsTopProject() {
        val award = AwardData.byId("top_project")
        assertEquals("byId(\"top_project\") must return Top Project", "top_project", award.id)
    }

    @Test
    fun byId_WithValidTopProjectLeaderId_ReturnsTopProjectLeader() {
        val award = AwardData.byId("top_project_leader")
        assertEquals("byId(\"top_project_leader\") must return Top Project Leader", "top_project_leader", award.id)
    }

    @Test
    fun byId_WithValidTopTalentId_ReturnsTopTalent() {
        val award = AwardData.byId("top_talent")
        assertEquals("byId(\"top_talent\") must return Top Talent", "top_talent", award.id)
    }

    @Test
    fun byId_WithNullId_FallsBackToMvp() {
        val award = AwardData.byId(null)
        assertEquals("byId(null) must fall back to MVP (first award)", "mvp", award.id)
    }

    @Test
    fun byId_WithEmptyStringId_FallsBackToMvp() {
        val award = AwardData.byId("")
        assertEquals("byId(\"\") must fall back to MVP (first award)", "mvp", award.id)
    }

    @Test
    fun byId_WithUnknownId_FallsBackToMvp() {
        val award = AwardData.byId("unknown_award_id")
        assertEquals("byId(\"unknown_award_id\") must fall back to MVP (first award)", "mvp", award.id)
    }

    @Test
    fun byId_WithRandomInvalidId_FallsBackToMvp() {
        val award = AwardData.byId("nope_not_real")
        assertEquals("byId with invalid ID must fall back to MVP", "mvp", award.id)
    }

    // ===================== Award content integrity tests =====================

    @Test
    fun awardData_AllAwards_HaveNonBlankDropdownLabels() {
        assertTrue("All awards must have non-blank dropdownLabel",
            AwardData.awards.all { it.dropdownLabel.isNotBlank() })
    }

    @Test
    fun awardData_AllAwards_HaveNonBlankDescriptions() {
        assertTrue("All awards must have non-blank description",
            AwardData.awards.all { it.description.isNotBlank() })
    }

    @Test
    fun awardData_AllAwards_HaveValidTrophyResources() {
        assertTrue("All awards must have trophy resource > 0",
            AwardData.awards.all { it.trophy > 0 })
    }

    @Test
    fun awardData_AllAwards_HaveNonBlankQuantities() {
        assertTrue("All awards must have non-blank quantity",
            AwardData.awards.all { it.quantity.isNotBlank() })
    }

    @Test
    fun awardData_AllAwards_HaveNonBlankQuantityUnits() {
        assertTrue("All awards must have non-blank quantityUnit",
            AwardData.awards.all { it.quantityUnit.isNotBlank() })
    }

    @Test
    fun awardData_AllAwards_HaveAtLeastOneValue() {
        assertTrue("All awards must have at least 1 value",
            AwardData.awards.all { it.values.isNotEmpty() })
    }

    @Test
    fun awardData_AllAwardValues_HaveNonBlankAmounts() {
        assertTrue("All award values must have non-blank amount",
            AwardData.awards.all { award -> award.values.all { it.amount.isNotBlank() } })
    }

    @Test
    fun awardData_AllAwardValues_HaveNonBlankNotes() {
        assertTrue("All award values must have non-blank note",
            AwardData.awards.all { award -> award.values.all { it.note.isNotBlank() } })
    }

    // ===================== Award uniqueness tests =====================

    @Test
    fun awardData_AllAwards_HaveUniqueIds() {
        val ids = AwardData.awards.map { it.id }
        assertEquals("All award IDs must be unique", ids.size, ids.distinct().size)
    }

    @Test
    fun awardData_AllAwards_HaveUniqueDropdownLabels() {
        val labels = AwardData.awards.map { it.dropdownLabel }
        assertEquals("All award labels must be unique", labels.size, labels.distinct().size)
    }
}
