package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.feature.send.SendKudosUiState
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosFormCream
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal

// Design node 6885:9903: cream card border-radius = 10.72dp ≈ 11dp
private val FormShape = RoundedCornerShape(11.dp)

// Design nodes 6885:10003 / 6885:10004: corner radius = 4dp (NOT pill)
private val ActionButtonShape = RoundedCornerShape(4.dp)

/**
 * Scrollable form body for the Send Kudos screen.
 * Extracted to keep SendKudosScreen.kt under 200 lines.
 *
 * Action buttons fix: "Huỷ" and "Gửi đi" use 4dp rounded-rect (design node radius=4dp),
 * height 40dp, rendered as LOCAL composables — the shared KudosPrimaryButton / KudosSecondaryButton
 * remain pill-shaped (used elsewhere). Only this screen uses 4dp rect.
 *
 * Preview: "Xem trước" eye-icon button added in the action row (Part B).
 */
@Composable
fun SendKudosFormContent(
    uiState: SendKudosUiState,
    onRecipientQueryChange: (String) -> Unit,
    onRecipientDropdownToggle: (Boolean) -> Unit,
    onRecipientSelect: (KudoUser) -> Unit,
    onTitleDropdownToggle: (Boolean) -> Unit,
    onTitleSelect: (String) -> Unit,
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
    onPreview: () -> Unit,
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

        // Cream form card — design node 6885:9903: rgba(255,248,225,1), radius 11dp
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
            TitleField(
                selectedTitle = uiState.selectedTitle,
                options = uiState.titleOptions,
                expanded = uiState.titleDropdownOpen,
                onToggle = onTitleDropdownToggle,
                onSelect = onTitleSelect
            )
            Spacer(Modifier.height(16.dp))
            // Issue 2: community standards link is on the toolbar row (design node 6885:9931)
            MessageField(
                message = uiState.message,
                hasError = uiState.showError && uiState.message.isBlank(),
                onMessageChange = onMessageChange,
                onToggleFormat = onToggleFormat,
                onCommunityStandardsClick = onCommunityStandardsClick
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

        // Preview affordance (Part B) — own row so the action row stays faithful to the
        // 2-button design. Opens a dialog showing the full kudo as it appears in the feed.
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onPreview,
                shape = ActionButtonShape,
                border = BorderStroke(1.dp, KudosBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = KudosSecondaryButtonNormal,
                    contentColor = KudosGold
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text(text = "Xem trước Kudo", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Action row — design nodes 6885:10003 / 6885:10004: Huỷ + Gửi đi, 4dp rounded-rect, height 40dp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // "Huỷ" — outlined, rgba(255,234,158,0.10) bg, #998C5F border
            OutlinedButton(
                onClick = onCancel,
                shape = ActionButtonShape,
                border = BorderStroke(1.dp, KudosBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = KudosSecondaryButtonNormal,
                    contentColor = KudosGold
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(text = "Huỷ", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.padding(horizontal = 6.dp))
            // "Gửi đi" — solid gold rgba(255,234,158,1), dark text
            Button(
                onClick = onSubmit,
                shape = ActionButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KudosGold,
                    contentColor = KudosDarkText,
                    disabledContainerColor = KudosGold.copy(alpha = 0.38f),
                    disabledContentColor = KudosDarkText.copy(alpha = 0.38f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(text = "Gửi đi", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
