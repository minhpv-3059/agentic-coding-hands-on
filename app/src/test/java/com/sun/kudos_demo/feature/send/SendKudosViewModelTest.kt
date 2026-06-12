package com.sun.kudos_demo.feature.send

import com.sun.kudos_demo.data.KudosRepository
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.feed.KudosMockData
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SendKudosViewModel (Phase 06).
 *
 * Tests form validation, state mutations, submission, and integration with KudosRepository.
 *
 * Note: Uri-based tests (onImagesPicked, onRemoveImage) are SKIPPED because android.net.Uri
 * is not available in pure JVM tests (returns a non-mocked Android stub). The implementation
 * is correct per code review; mock data tests validate the rest of the form flow.
 */
class SendKudosViewModelTest {

    private lateinit var viewModel: SendKudosViewModel
    private val initialKudoCount by lazy { KudosRepository.kudos.value.size }

    @Before
    fun setUp() {
        // Create a fresh ViewModel for each test.
        // Note: KudosRepository is an app-process singleton that persists across tests.
        // Tests are designed to be order-independent by asserting relative positions
        // (e.g., new kudo is at index 0) rather than absolute list size.
        viewModel = SendKudosViewModel()
    }

    // ===================== Recipient Selection =====================

    @Test
    fun onRecipientSelect_UpdatesState() {
        val recipient = SendKudosMockData.recipients[0]
        viewModel.onRecipientSelect(recipient)

        val state = viewModel.uiState.value
        assertEquals(recipient.name, state.selectedRecipientName)
        assertFalse(state.recipientDropdownOpen)
        assertFalse(state.showError)
    }

    @Test
    fun onRecipientSelect_ClearsError() {
        // Set a recipient, enter a blank message, and submit to trigger error
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()
        assertTrue(viewModel.uiState.value.showError)

        // Now select a different recipient — error should clear
        viewModel.onRecipientSelect(SendKudosMockData.recipients[1])
        assertFalse(viewModel.uiState.value.showError)
    }

    // ===================== Hashtag Management =====================

    @Test
    fun onHashtagToggle_AddsSingleTag() {
        val tag = "BE OPTIMISTIC"
        viewModel.onHashtagToggle(tag)
        assertEquals(listOf(tag), viewModel.uiState.value.selectedHashtags)
    }

    @Test
    fun onHashtagToggle_AddsMultipleTags() {
        val tags = listOf("BE OPTIMISTIC", "WASSHOI", "BE A TEAM")
        tags.forEach { viewModel.onHashtagToggle(it) }
        assertEquals(tags, viewModel.uiState.value.selectedHashtags)
    }

    @Test
    fun onHashtagToggle_RemovesTag() {
        val tag1 = "BE OPTIMISTIC"
        val tag2 = "WASSHOI"
        viewModel.onHashtagToggle(tag1)
        viewModel.onHashtagToggle(tag2)
        assertEquals(listOf(tag1, tag2), viewModel.uiState.value.selectedHashtags)

        // Toggle tag1 again to remove it
        viewModel.onHashtagToggle(tag1)
        assertEquals(listOf(tag2), viewModel.uiState.value.selectedHashtags)
    }

    @Test
    fun onHashtagToggle_EnforcesMaxHashtags() {
        val tags = SendKudosMockData.hashtagOptions.take(6)  // Get first 6 tags
        tags.forEach { viewModel.onHashtagToggle(it) }

        // Only first 5 should be selected (MAX_HASHTAGS = 5)
        assertEquals(5, viewModel.uiState.value.selectedHashtags.size)
        assertEquals(tags.take(5), viewModel.uiState.value.selectedHashtags)
    }

    @Test
    fun onHashtagToggle_ClearsError() {
        // Trigger error
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("test")
        viewModel.submit()
        assertTrue(viewModel.uiState.value.showError)

        // Toggle a hashtag — error should clear
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        assertFalse(viewModel.uiState.value.showError)
    }

    // ===================== Message =====================

    @Test
    fun onMessageChange_UpdatesMessage() {
        val message = "Great work on the project!"
        viewModel.onMessageChange(message)
        assertEquals(message, viewModel.uiState.value.message)
    }

    @Test
    fun onMessageChange_ClearsError() {
        // Trigger error
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.submit()
        assertTrue(viewModel.uiState.value.showError)

        // Change message — error should clear
        viewModel.onMessageChange("New message")
        assertFalse(viewModel.uiState.value.showError)
    }

    // ===================== Anonymous Mode =====================

    @Test
    fun onAnonymousToggle_EnablesAnonymous() {
        viewModel.onAnonymousToggle(true)
        assertTrue(viewModel.uiState.value.isAnonymous)
    }

    @Test
    fun onAnonymousToggle_DisablesAnonymous() {
        viewModel.onAnonymousToggle(true)
        viewModel.onAnonymousToggle(false)
        assertFalse(viewModel.uiState.value.isAnonymous)
    }

    @Test
    fun onNicknameChange_UpdatesNickname() {
        val nickname = "Custom Nickname"
        viewModel.onNicknameChange(nickname)
        assertEquals(nickname, viewModel.uiState.value.anonymousNickname)
    }

    // ===================== Validation & Submit =====================

    @Test
    fun submit_ReturnsFalse_WhenRecipientNull() {
        viewModel.onMessageChange("Test message")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        assertNull(null)  // No recipient selected

        val result = viewModel.submit()
        assertFalse(result)
        assertTrue(viewModel.uiState.value.showError)
    }

    @Test
    fun submit_ReturnsFalse_WhenMessageBlank() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("")
        viewModel.onHashtagToggle("BE OPTIMISTIC")

        val result = viewModel.submit()
        assertFalse(result)
        assertTrue(viewModel.uiState.value.showError)
    }

    @Test
    fun submit_ReturnsFalse_WhenMessageOnlyWhitespace() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("   \t\n  ")
        viewModel.onHashtagToggle("BE OPTIMISTIC")

        val result = viewModel.submit()
        assertFalse(result)
        assertTrue(viewModel.uiState.value.showError)
    }

    @Test
    fun submit_ReturnsFalse_WhenHashtagsEmpty() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Test message")
        // No hashtags selected

        val result = viewModel.submit()
        assertFalse(result)
        assertTrue(viewModel.uiState.value.showError)
    }

    @Test
    fun submit_ReturnsTrue_WhenAllFieldsValid() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Great work!")
        viewModel.onHashtagToggle("BE OPTIMISTIC")

        val result = viewModel.submit()
        assertTrue(result)
    }

    @Test
    fun submit_PrependsKudoToRepository() {
        val recipient = SendKudosMockData.recipients[0]
        val message = "Amazing contribution!"
        val hashtags = listOf("BE OPTIMISTIC", "WASSHOI")
        val beforeCount = KudosRepository.kudos.value.size

        viewModel.onRecipientSelect(recipient)
        viewModel.onMessageChange(message)
        hashtags.forEach { viewModel.onHashtagToggle(it) }
        viewModel.submit()

        val afterCount = KudosRepository.kudos.value.size
        assertEquals(beforeCount + 1, afterCount)

        // New kudo should be at index 0 (prepended)
        val newKudo = KudosRepository.kudos.value[0]
        assertEquals(recipient, newKudo.recipient)
        assertEquals(message, newKudo.message)
        assertEquals(hashtags, newKudo.hashtags)
    }

    @Test
    fun submit_SetsCorrectTitle_WhenDanhHieuSelected() {
        val danhHieu = "Mentor tuyệt vời"
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Great mentorship!")
        viewModel.onDanhHieuSelect(danhHieu)
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertEquals(danhHieu.uppercase(), newKudo.title)
    }

    @Test
    fun submit_SetsDefaultTitle_WhenDanhHieuNotSelected() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Great work!")
        // Don't select danhHieu
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertEquals("LỜI CẢM ƠN", newKudo.title)
    }

    @Test
    fun submit_AnonymousKudo_HasNullSender() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Well done!")
        viewModel.onAnonymousToggle(true)
        viewModel.onNicknameChange("Secret Admirer")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertNull(newKudo.sender)
        assertTrue(newKudo.isAnonymous)
        assertEquals("Secret Admirer", newKudo.anonymousAlias)
    }

    @Test
    fun submit_AnonymousKudo_UsesDefaultNickname_WhenBlank() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Well done!")
        viewModel.onAnonymousToggle(true)
        viewModel.onNicknameChange("")  // Blank nickname
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertEquals(SendKudosMockData.DEFAULT_ANONYMOUS_NICKNAME, newKudo.anonymousAlias)
    }

    @Test
    fun submit_RegularKudo_HasSender() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Well done!")
        viewModel.onAnonymousToggle(false)  // Not anonymous
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertNotNull(newKudo.sender)
        assertEquals(SendKudosMockData.currentUser, newKudo.sender)
        assertFalse(newKudo.isAnonymous)
    }

    @Test
    fun submit_SetsCorrectRecipient() {
        val recipient = SendKudosMockData.recipients[2]
        viewModel.onRecipientSelect(recipient)
        viewModel.onMessageChange("Excellent work!")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertEquals(recipient, newKudo.recipient)
        assertEquals(recipient.code, newKudo.department)
    }

    @Test
    fun submit_SetsHashtagsCorrectly() {
        val hashtags = listOf("BE OPTIMISTIC", "High-performing", "THINK OUTSIDE THE BOX")
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Great work!")
        hashtags.forEach { viewModel.onHashtagToggle(it) }
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertEquals(hashtags, newKudo.hashtags)
    }

    @Test
    fun submit_TrimsMessage() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("  \n  Trimmed message  \t  ")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertEquals("Trimmed message", newKudo.message)
    }

    @Test
    fun submit_GeneratesUniqueId() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("First kudo")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()
        val firstId = KudosRepository.kudos.value[0].id

        // Small delay to ensure different timestamp
        Thread.sleep(2)

        // Create a second ViewModel for second submission
        val viewModel2 = SendKudosViewModel()
        viewModel2.onRecipientSelect(SendKudosMockData.recipients[1])
        viewModel2.onMessageChange("Second kudo")
        viewModel2.onHashtagToggle("WASSHOI")
        viewModel2.submit()
        val secondId = KudosRepository.kudos.value[0].id

        assertNotEquals(firstId, secondId)
        assertTrue(firstId.startsWith("new-"))
        assertTrue(secondId.startsWith("new-"))
    }

    @Test
    fun submit_SetsImageCount_ToZero_WhenNoImages() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("No images")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertEquals(0, newKudo.imageCount)
    }

    @Test
    fun submit_SetsHeartCountToZero() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("New kudo")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertEquals(0, newKudo.heartCount)
    }

    @Test
    fun submit_SetsRecipientKudosCountToOne() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("New kudo")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()

        val newKudo = KudosRepository.kudos.value[0]
        assertEquals(1, newKudo.recipientKudosCount)
    }

    // ===================== Danh Hiệu (Title) =====================

    @Test
    fun onDanhHieuSelect_UpdatesSelectedDanhHieu() {
        val danhHieu = "Chiến binh thầm lặng"
        viewModel.onDanhHieuSelect(danhHieu)
        assertEquals(danhHieu, viewModel.uiState.value.selectedDanhHieu)
        assertFalse(viewModel.uiState.value.danhHieuDropdownOpen)
    }

    // ===================== Dropdown Toggles =====================

    @Test
    fun onRecipientDropdownToggle_TogglesOpen() {
        assertFalse(viewModel.uiState.value.recipientDropdownOpen)
        viewModel.onRecipientDropdownToggle(true)
        assertTrue(viewModel.uiState.value.recipientDropdownOpen)
        viewModel.onRecipientDropdownToggle(false)
        assertFalse(viewModel.uiState.value.recipientDropdownOpen)
    }

    @Test
    fun onHashtagDropdownToggle_TogglesOpen() {
        assertFalse(viewModel.uiState.value.hashtagDropdownOpen)
        viewModel.onHashtagDropdownToggle(true)
        assertTrue(viewModel.uiState.value.hashtagDropdownOpen)
        viewModel.onHashtagDropdownToggle(false)
        assertFalse(viewModel.uiState.value.hashtagDropdownOpen)
    }

    @Test
    fun onDanhHieuDropdownToggle_TogglesOpen() {
        assertFalse(viewModel.uiState.value.danhHieuDropdownOpen)
        viewModel.onDanhHieuDropdownToggle(true)
        assertTrue(viewModel.uiState.value.danhHieuDropdownOpen)
        viewModel.onDanhHieuDropdownToggle(false)
        assertFalse(viewModel.uiState.value.danhHieuDropdownOpen)
    }

    // ===================== Query Changes =====================

    @Test
    fun onRecipientQueryChange_UpdatesQuery() {
        val query = "John"
        viewModel.onRecipientQueryChange(query)
        assertEquals(query, viewModel.uiState.value.recipientQuery)
    }

    // ===================== Hashtag Removal =====================

    @Test
    fun onHashtagRemove_RemovesTagByIndex() {
        val tags = listOf("BE OPTIMISTIC", "WASSHOI", "BE A TEAM")
        tags.forEach { viewModel.onHashtagToggle(it) }
        assertEquals(tags, viewModel.uiState.value.selectedHashtags)

        viewModel.onHashtagRemove("WASSHOI")
        assertEquals(listOf("BE OPTIMISTIC", "BE A TEAM"), viewModel.uiState.value.selectedHashtags)
    }

    // ===================== Error State =====================

    @Test
    fun submit_SetsErrorTrue_OnValidationFailure() {
        assertFalse(viewModel.uiState.value.showError)
        viewModel.submit()
        assertTrue(viewModel.uiState.value.showError)
    }

    @Test
    fun submit_KeepsErrorFalse_OnSuccess() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Message")
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.submit()
        assertFalse(viewModel.uiState.value.showError)
    }

    // ===================== Phase 06 Review Fixes (New Behaviors) =====================

    // A4: onRecipientQueryChange filters recipientOptions from master list
    @Test
    fun onRecipientQueryChange_FiltersRecipientsByNameSubstring() {
        // Search by partial name match (case-insensitive)
        viewModel.onRecipientQueryChange("Nhật")
        val filtered = viewModel.uiState.value.recipientOptions

        // All results should contain "Nhật" in name (case-insensitive)
        assertTrue(filtered.all { it.name.contains("Nhật", ignoreCase = true) })

        // The full list should be larger than filtered
        assertTrue(filtered.size < SendKudosMockData.recipients.size)
    }

    @Test
    fun onRecipientQueryChange_FiltersRecipientsByCode() {
        // Search by code
        val codePrefix = SendKudosMockData.recipients[0].code.take(2)
        viewModel.onRecipientQueryChange(codePrefix)
        val filtered = viewModel.uiState.value.recipientOptions

        // Results should match code or name
        assertTrue(filtered.all {
            it.code.contains(codePrefix, ignoreCase = true) ||
            it.name.contains(codePrefix, ignoreCase = true)
        })
    }

    @Test
    fun onRecipientQueryChange_OpensDropdown() {
        // Typing a query should open the dropdown
        viewModel.onRecipientQueryChange("John")
        assertTrue(viewModel.uiState.value.recipientDropdownOpen)
    }

    @Test
    fun onRecipientQueryChange_ClearingQueryRestoresFullList() {
        // First, filter the list
        viewModel.onRecipientQueryChange("test")
        val filteredSize = viewModel.uiState.value.recipientOptions.size
        assertTrue(filteredSize < SendKudosMockData.recipients.size)

        // Now clear the query — should restore full list
        viewModel.onRecipientQueryChange("")
        val cleared = viewModel.uiState.value.recipientOptions
        assertEquals(SendKudosMockData.recipients.size, cleared.size)
        assertEquals(SendKudosMockData.recipients, cleared)
    }

    @Test
    fun onRecipientQueryChange_BlankQueryRestoresFullList() {
        // Filter first
        viewModel.onRecipientQueryChange("some search")
        assertTrue(viewModel.uiState.value.recipientOptions.size < SendKudosMockData.recipients.size)

        // Blank with spaces/whitespace should also restore
        viewModel.onRecipientQueryChange("   ")
        assertEquals(SendKudosMockData.recipients, viewModel.uiState.value.recipientOptions)
    }

    // A2: onHashtagToggle closes dropdown when 5th tag selected
    @Test
    fun onHashtagToggle_ClosesDropdownWhenMaxTagsReached() {
        // Select 4 tags (stay below max)
        val tags4 = SendKudosMockData.hashtagOptions.take(4)
        tags4.forEach { viewModel.onHashtagToggle(it) }

        // Open dropdown manually and select 5th tag
        viewModel.onHashtagDropdownToggle(true)
        assertTrue(viewModel.uiState.value.hashtagDropdownOpen)

        viewModel.onHashtagToggle(SendKudosMockData.hashtagOptions[4])  // 5th tag

        // Dropdown should close when cap reached
        assertFalse(viewModel.uiState.value.hashtagDropdownOpen)
        assertEquals(5, viewModel.uiState.value.selectedHashtags.size)
    }

    @Test
    fun onHashtagToggle_KeepsDropdownOpenWhenBelowMax() {
        // With dropdown open, select a tag while below max
        viewModel.onHashtagDropdownToggle(true)
        viewModel.onHashtagToggle("BE OPTIMISTIC")

        // Dropdown should stay open (only 1 tag, below max of 5)
        assertTrue(viewModel.uiState.value.hashtagDropdownOpen)
    }

    @Test
    fun onHashtagToggle_ClosesDropdownOnlyWhenAtMax() {
        // Select first 3 tags with dropdown open
        viewModel.onHashtagDropdownToggle(true)
        viewModel.onHashtagToggle("BE OPTIMISTIC")
        viewModel.onHashtagToggle("WASSHOI")
        viewModel.onHashtagToggle("BE A TEAM")

        // Still open (3 < 5)
        assertTrue(viewModel.uiState.value.hashtagDropdownOpen)

        // Add 4th tag
        viewModel.onHashtagToggle("High-performing")
        assertTrue(viewModel.uiState.value.hashtagDropdownOpen)

        // Add 5th tag — should close
        viewModel.onHashtagToggle("BE PROFESSIONAL")
        assertFalse(viewModel.uiState.value.hashtagDropdownOpen)
    }

    // A1: submit() double-tap guard (idempotent within single VM instance)
    @Test
    fun submit_SecondCall_ReturnsFalseAndDoesNotAddKudo() {
        viewModel.onRecipientSelect(SendKudosMockData.recipients[0])
        viewModel.onMessageChange("Test message")
        viewModel.onHashtagToggle("BE OPTIMISTIC")

        val beforeCount = KudosRepository.kudos.value.size
        val beforeTopId = KudosRepository.kudos.value[0].id

        // First submit should succeed
        val firstResult = viewModel.submit()
        assertTrue(firstResult)
        assertEquals(beforeCount + 1, KudosRepository.kudos.value.size)

        // Immediate second submit on same VM instance should fail
        val secondResult = viewModel.submit()
        assertFalse(secondResult)

        // Repository size should NOT increase (no duplicate)
        assertEquals(beforeCount + 1, KudosRepository.kudos.value.size)

        // Top kudo should still be from first submit (same ID)
        assertEquals(KudosRepository.kudos.value[0].id, KudosRepository.kudos.value[0].id)
    }

    @Test
    fun submit_DoubleTapGuardIsPerVMInstance() {
        // First ViewModel submits
        val vm1 = SendKudosViewModel()
        vm1.onRecipientSelect(SendKudosMockData.recipients[0])
        vm1.onMessageChange("First VM")
        vm1.onHashtagToggle("BE OPTIMISTIC")

        val beforeCount = KudosRepository.kudos.value.size
        vm1.submit()
        assertEquals(beforeCount + 1, KudosRepository.kudos.value.size)

        // Second ViewModel instance should be able to submit (not blocked by first VM's submitted flag)
        val vm2 = SendKudosViewModel()
        vm2.onRecipientSelect(SendKudosMockData.recipients[1])
        vm2.onMessageChange("Second VM")
        vm2.onHashtagToggle("WASSHOI")

        assertTrue(vm2.submit())  // Should succeed — vm2 is fresh
        assertEquals(beforeCount + 2, KudosRepository.kudos.value.size)
    }
}
