package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.send.SendKudosUiState
import com.sun.kudos_demo.ui.components.KudosPrimaryButton
import com.sun.kudos_demo.ui.components.KudosSecondaryButton
import com.sun.kudos_demo.ui.theme.KudosFormCream
import com.sun.kudos_demo.ui.theme.KudosGold

private val FormShape = RoundedCornerShape(11.dp)

/**
 * Scrollable form body for the Send Kudos screen.
 * Extracted to keep SendKudosScreen.kt under 200 lines.
 * Accepts the full ui-state + callbacks from the parent screen.
 */
@Composable
fun SendKudosFormContent(
    uiState: SendKudosUiState,
    onRecipientQueryChange: (String) -> Unit,
    onRecipientDropdownToggle: (Boolean) -> Unit,
    onRecipientSelect: (KudoUser) -> Unit,
    onDanhHieuDropdownToggle: (Boolean) -> Unit,
    onDanhHieuSelect: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onToggleFormat: (String) -> Unit,
    onHashtagDropdownToggle: (Boolean) -> Unit,
    onHashtagToggle: (String) -> Unit,
    onHashtagRemove: (String) -> Unit,
    onAddImageClick: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    onAnonymousToggle: (Boolean) -> Unit,
    onNicknameChange: (String) -> Unit,
    onCommunityStandardsClick: () -> Unit,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(Modifier.height(8.dp))

        Text(
            text = "Gửi lời cám ơn và ghi nhận đến đồng đội",
            style = MaterialTheme.typography.titleMedium,
            color = KudosGold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Cream form card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(KudosFormCream, FormShape)
                .padding(horizontal = 12.dp, vertical = 18.dp)
        ) {
            RecipientField(
                query = uiState.recipientQuery,
                selectedRecipient = uiState.selectedRecipientName,
                options = uiState.recipientOptions,
                expanded = uiState.recipientDropdownOpen,
                hasError = uiState.showError && uiState.selectedRecipientName == null,
                onQueryChange = onRecipientQueryChange,
                onToggle = onRecipientDropdownToggle,
                onSelect = onRecipientSelect
            )
            Spacer(Modifier.height(16.dp))
            DanhHieuField(
                selectedDanhHieu = uiState.selectedDanhHieu,
                options = uiState.danhHieuOptions,
                expanded = uiState.danhHieuDropdownOpen,
                onToggle = onDanhHieuDropdownToggle,
                onSelect = onDanhHieuSelect,
                onCommunityStandardsClick = onCommunityStandardsClick
            )
            Spacer(Modifier.height(16.dp))
            MessageField(
                message = uiState.message,
                hasError = uiState.showError && uiState.message.isBlank(),
                onMessageChange = onMessageChange,
                onToggleFormat = onToggleFormat
            )
            Spacer(Modifier.height(16.dp))
            HashtagSection(
                selectedHashtags = uiState.selectedHashtags,
                options = uiState.hashtagOptions,
                expanded = uiState.hashtagDropdownOpen,
                hasError = uiState.showError && uiState.selectedHashtags.isEmpty(),
                onToggle = onHashtagDropdownToggle,
                onHashtagToggle = onHashtagToggle,
                onHashtagRemove = onHashtagRemove
            )
            Spacer(Modifier.height(16.dp))
            ImageAttachRow(
                imageUris = uiState.imageUris,
                onAddImageClick = onAddImageClick,
                onRemoveImage = onRemoveImage
            )
            Spacer(Modifier.height(16.dp))
            AnonymousSection(
                isAnonymous = uiState.isAnonymous,
                anonymousNickname = uiState.anonymousNickname,
                onAnonymousToggle = onAnonymousToggle,
                onNicknameChange = onNicknameChange
            )
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.showError) {
            SendKudosErrorBanner()
            Spacer(Modifier.height(8.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            KudosSecondaryButton(
                text = "Huỷ  ✕",
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.padding(horizontal = 8.dp))
            KudosPrimaryButton(
                text = "Gửi đi  ▷",
                onClick = onSubmit,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
