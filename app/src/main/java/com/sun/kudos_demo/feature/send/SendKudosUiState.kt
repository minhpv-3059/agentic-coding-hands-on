package com.sun.kudos_demo.feature.send

import android.net.Uri
import com.sun.kudos_demo.feature.feed.KudoUser

/**
 * UI state for the Send Kudos form screen, held by [SendKudosViewModel].
 */
data class SendKudosUiState(
    val recipientQuery: String = "",
    val selectedRecipientName: String? = null,
    val recipientOptions: List<KudoUser> = emptyList(),
    val recipientDropdownOpen: Boolean = false,
    val selectedTitle: String? = null,
    val titleOptions: List<String> = emptyList(),
    val titleDropdownOpen: Boolean = false,
    val message: String = "",
    val selectedHashtags: List<String> = emptyList(),
    val hashtagOptions: List<String> = emptyList(),
    val hashtagDropdownOpen: Boolean = false,
    val imageUris: List<Uri> = emptyList(),         // attached image thumbnails (Photo Picker)
    val isAnonymous: Boolean = false,
    val anonymousNickname: String = "",
    val showError: Boolean = false,
)
