package com.sun.kudos_demo.feature.send

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sun.kudos_demo.feature.feed.KudosMockData
import com.sun.kudos_demo.ui.theme.KudosAppTheme

// Mock data sourced directly from Figma design content (send-main.png / MoMorph design)
private val mockUsers = KudosMockData.searchableUsers

internal val previewFilledState = SendKudosUiState(
    selectedRecipientName = "Dương Huỳnh Xuân Nhật",
    recipientOptions = mockUsers,
    titleOptions = SendKudosMockData.titleOptions,
    message = "Tôi rất chị là quý bạn",
    selectedHashtags = listOf("BE OPTIMISTIC", "WASSHOI", "BE A TEAM"),
    hashtagOptions = SendKudosMockData.hashtagOptions,
    isAnonymous = true,
    anonymousNickname = "Doremon"   // A9: matches design + SendKudosMockData.DEFAULT_ANONYMOUS_NICKNAME
)

@Preview(showBackground = true, backgroundColor = 0xFF00101A, name = "Send Kudos — Filled")
@Composable
private fun SendKudosFilledPreview() {
    KudosAppTheme {
        SendKudosScreen(
            uiState = previewFilledState,
            onBack = {}, onRecipientQueryChange = {}, onRecipientDropdownToggle = {},
            onRecipientSelect = {}, onTitleDropdownToggle = {}, onTitleSelect = {},
            onMessageChange = {}, onToggleFormat = {}, onHashtagDropdownToggle = {},
            onHashtagToggle = {}, onHashtagRemove = {}, onAddImageClick = {},
            onRemoveImage = {}, onAnonymousToggle = {}, onNicknameChange = {},
            onCommunityStandardsClick = {}, onCancel = {}, onSubmit = {},
            onPreviewKudo = { KudosMockData.kudos.first() }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, name = "Send Kudos — Default (empty)")
@Composable
private fun SendKudosDefaultPreview() {
    KudosAppTheme {
        SendKudosScreen(
            uiState = SendKudosUiState(
                recipientOptions = mockUsers,
                titleOptions = SendKudosMockData.titleOptions,
                hashtagOptions = SendKudosMockData.hashtagOptions
            ),
            onBack = {}, onRecipientQueryChange = {}, onRecipientDropdownToggle = {},
            onRecipientSelect = {}, onTitleDropdownToggle = {}, onTitleSelect = {},
            onMessageChange = {}, onToggleFormat = {}, onHashtagDropdownToggle = {},
            onHashtagToggle = {}, onHashtagRemove = {}, onAddImageClick = {},
            onRemoveImage = {}, onAnonymousToggle = {}, onNicknameChange = {},
            onCommunityStandardsClick = {}, onCancel = {}, onSubmit = {},
            onPreviewKudo = { KudosMockData.kudos.first() }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, name = "Send Kudos — Validation Error")
@Composable
private fun SendKudosErrorPreview() {
    KudosAppTheme {
        SendKudosScreen(
            uiState = previewFilledState.copy(message = "", selectedHashtags = emptyList(), showError = true),
            onBack = {}, onRecipientQueryChange = {}, onRecipientDropdownToggle = {},
            onRecipientSelect = {}, onTitleDropdownToggle = {}, onTitleSelect = {},
            onMessageChange = {}, onToggleFormat = {}, onHashtagDropdownToggle = {},
            onHashtagToggle = {}, onHashtagRemove = {}, onAddImageClick = {},
            onRemoveImage = {}, onAnonymousToggle = {}, onNicknameChange = {},
            onCommunityStandardsClick = {}, onCancel = {}, onSubmit = {},
            onPreviewKudo = { KudosMockData.kudos.first() }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A, name = "Send Kudos — Recipient Dropdown Open")
@Composable
private fun SendKudosRecipientDropdownPreview() {
    KudosAppTheme {
        SendKudosScreen(
            uiState = SendKudosUiState(
                recipientOptions = mockUsers,
                recipientDropdownOpen = true,
                titleOptions = SendKudosMockData.titleOptions,
                hashtagOptions = SendKudosMockData.hashtagOptions
            ),
            onBack = {}, onRecipientQueryChange = {}, onRecipientDropdownToggle = {},
            onRecipientSelect = {}, onTitleDropdownToggle = {}, onTitleSelect = {},
            onMessageChange = {}, onToggleFormat = {}, onHashtagDropdownToggle = {},
            onHashtagToggle = {}, onHashtagRemove = {}, onAddImageClick = {},
            onRemoveImage = {}, onAnonymousToggle = {}, onNicknameChange = {},
            onCommunityStandardsClick = {}, onCancel = {}, onSubmit = {},
            onPreviewKudo = { KudosMockData.kudos.first() }
        )
    }
}
