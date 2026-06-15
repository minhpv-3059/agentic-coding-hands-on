package com.sun.kudos_demo.feature.send

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.sun.kudos_demo.data.KudosRepository
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * State holder for the Send Kudos form (Phase 06, mock-data only).
 *
 * Validates the required fields (recipient + message + hashtag), and on a successful
 * [submit] prepends the new kudo to [KudosRepository] so it surfaces at the top of the
 * feed — see clarifications.md "Session 2026-06-12".
 */
class SendKudosViewModel : ViewModel() {

    @Volatile private var submitted = false

    private val _uiState = MutableStateFlow(
        SendKudosUiState(
            recipientOptions = SendKudosMockData.recipients,
            danhHieuOptions = SendKudosMockData.danhHieuOptions,
            hashtagOptions = SendKudosMockData.hashtagOptions,
            anonymousNickname = SendKudosMockData.DEFAULT_ANONYMOUS_NICKNAME
        )
    )
    val uiState: StateFlow<SendKudosUiState> = _uiState.asStateFlow()

    private var selectedRecipient: KudoUser? = null

    // --- Recipient ---
    fun onRecipientQueryChange(query: String) = _uiState.update { state ->
        val filtered = if (query.isBlank()) SendKudosMockData.recipients
        else SendKudosMockData.recipients.filter {
            it.name.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true)
        }
        state.copy(
            recipientQuery = query,
            recipientOptions = filtered,
            recipientDropdownOpen = true
        )
    }

    fun onRecipientDropdownToggle(open: Boolean) =
        _uiState.update { it.copy(recipientDropdownOpen = open) }

    fun onRecipientSelect(user: KudoUser) {
        selectedRecipient = user
        _uiState.update {
            it.copy(selectedRecipientName = user.name, recipientDropdownOpen = false, showError = false)
        }
    }

    // --- Danh hiệu ---
    fun onDanhHieuDropdownToggle(open: Boolean) =
        _uiState.update { it.copy(danhHieuDropdownOpen = open) }

    fun onDanhHieuSelect(danhHieu: String) =
        _uiState.update { it.copy(selectedDanhHieu = danhHieu, danhHieuDropdownOpen = false) }

    // --- Message + formatting ---
    fun onMessageChange(message: String) =
        _uiState.update { it.copy(message = message, showError = false) }

    /** Formatting is applied in-field via RichTextFormatter; no persistent VM state to toggle. */
    fun onToggleFormat(format: String) = Unit

    // --- Hashtags (multi-select, max 5) ---
    fun onHashtagDropdownToggle(open: Boolean) =
        _uiState.update { it.copy(hashtagDropdownOpen = open) }

    fun onHashtagToggle(tag: String) = _uiState.update { state ->
        val selected = state.selectedHashtags
        val next = when {
            tag in selected -> selected - tag
            selected.size < SendKudosMockData.MAX_HASHTAGS -> selected + tag
            else -> selected
        }
        // A2: close dropdown in same update when cap is reached
        val dropdownOpen = next.size < SendKudosMockData.MAX_HASHTAGS && state.hashtagDropdownOpen
        state.copy(selectedHashtags = next, hashtagDropdownOpen = dropdownOpen, showError = false)
    }

    fun onHashtagRemove(tag: String) =
        _uiState.update { it.copy(selectedHashtags = it.selectedHashtags - tag) }

    // --- Images (Photo Picker) ---
    fun onImagesPicked(uris: List<Uri>) = _uiState.update { state ->
        val merged = (state.imageUris + uris).distinct().take(SendKudosMockData.MAX_IMAGES)
        state.copy(imageUris = merged)
    }

    fun onRemoveImage(index: Int) = _uiState.update { state ->
        if (index !in state.imageUris.indices) state
        else state.copy(imageUris = state.imageUris.filterIndexed { i, _ -> i != index })
    }

    // --- Anonymous ---
    fun onAnonymousToggle(on: Boolean) = _uiState.update { it.copy(isAnonymous = on) }

    fun onNicknameChange(nickname: String) = _uiState.update { it.copy(anonymousNickname = nickname) }

    // --- Submit ---
    /** Validate and, on success, prepend the new kudo to the feed. Returns true if sent. */
    fun submit(): Boolean {
        val state = _uiState.value
        val recipient = selectedRecipient
        if (recipient == null || state.message.isBlank() || state.selectedHashtags.isEmpty()) {
            _uiState.update { it.copy(showError = true) }
            return false
        }
        // A1: one-shot guard — prevents double-tap from prepending a duplicate kudo
        if (submitted) return false
        submitted = true
        KudosRepository.addKudo(buildKudo(state, recipient))
        return true
    }

    /** Build a transient kudo from the current form state for the preview dialog (no validation/commit). */
    fun previewKudo(): Kudo {
        val state = _uiState.value
        val recipient = selectedRecipient
            ?: KudoUser(id = "preview", name = state.selectedRecipientName ?: "Người nhận", code = "")
        return buildKudo(state, recipient)
    }

    private fun buildKudo(state: SendKudosUiState, recipient: KudoUser): Kudo {
        val nickname = state.anonymousNickname.ifBlank { SendKudosMockData.DEFAULT_ANONYMOUS_NICKNAME }
        return Kudo(
            id = "new-${System.currentTimeMillis()}",
            sender = if (state.isAnonymous) null else SendKudosMockData.currentUser,
            recipient = recipient,
            department = recipient.code,
            timeRange = currentTimeRange(),
            title = state.selectedDanhHieu?.uppercase(Locale.getDefault()) ?: "LỜI CẢM ƠN",
            message = state.message.trim(),
            hashtags = state.selectedHashtags.toList(),
            heartCount = 0,
            imageCount = state.imageUris.size,
            recipientKudosCount = 1,
            isAnonymous = state.isAnonymous,
            anonymousAlias = nickname
        )
    }

    private fun currentTimeRange(): String =
        SimpleDateFormat("HH:mm - MM/dd/yyyy", Locale.getDefault()).format(Date())
}
