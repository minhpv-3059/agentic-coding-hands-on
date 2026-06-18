package com.sun.kudos_demo.navigation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.profile.ProfileMockData
import com.sun.kudos_demo.feature.send.SendKudosMockData
import com.sun.kudos_demo.feature.send.SendKudosScreen
import com.sun.kudos_demo.feature.send.SendKudosViewModel

/**
 * Route composable for the Send Kudos screen — wires [SendKudosViewModel], the Android
 * Photo Picker, validation/submit, and navigation back to the feed (with the new kudo
 * already prepended via KudosRepository).
 */
@Composable
fun SendKudosRoute(navController: NavHostController, recipientId: String = "") {
    val context = LocalContext.current
    val vm: SendKudosViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    // Pre-select the recipient when arriving from another user's profile "Gửi lời cảm ơn" CTA.
    LaunchedEffect(recipientId) {
        if (recipientId.isNotBlank()) vm.onRecipientSelect(ProfileMockData.userById(recipientId))
    }

    val pickImages = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(SendKudosMockData.MAX_IMAGES)
    ) { uris -> if (uris.isNotEmpty()) vm.onImagesPicked(uris) }

    SendKudosScreen(
        uiState = state,
        onBack = { navController.popBackStack() },
        onRecipientQueryChange = vm::onRecipientQueryChange,
        onRecipientDropdownToggle = vm::onRecipientDropdownToggle,
        onRecipientSelect = vm::onRecipientSelect,
        onTitleDropdownToggle = vm::onTitleDropdownToggle,
        onTitleSelect = vm::onTitleSelect,
        onMessageChange = vm::onMessageChange,
        onToggleFormat = vm::onToggleFormat,
        onHashtagDropdownToggle = vm::onHashtagDropdownToggle,
        onHashtagToggle = vm::onHashtagToggle,
        onHashtagRemove = vm::onHashtagRemove,
        onAddImageClick = {
            pickImages.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onRemoveImage = vm::onRemoveImage,
        onAnonymousToggle = vm::onAnonymousToggle,
        onNicknameChange = vm::onNicknameChange,
        onCommunityStandardsClick = {
            navController.navigate(NavRoutes.KUDOS_COMMUNITY_STANDARDS) { launchSingleTop = true }
        },
        onCancel = { navController.popBackStack() },
        onSubmit = {
            if (vm.submit()) {
                Toast.makeText(context, context.getString(R.string.send_success_toast), Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        },
        onPreviewKudo = vm::previewKudo
    )
}
