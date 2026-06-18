package com.sun.kudos_demo.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.components.LanguageDropdownPanel
import com.sun.kudos_demo.ui.components.LanguageTrigger
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()
    val language by vm.language.collectAsState()

    Box(Modifier.fillMaxSize().background(KudosBackground)) {
        Image(
            painter = painterResource(R.drawable.bg_login_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(Modifier.fillMaxSize()) {
            LoginHeader(
                language = language,
                onLanguageClick = vm::toggleLanguageDropdown,
                modifier = Modifier.height(104.dp)
            )
            Spacer(Modifier.height(148.dp))
            Image(
                painter = painterResource(R.drawable.img_root_further),
                contentDescription = "ROOT FURTHER",
                modifier = Modifier.padding(start = 20.dp).width(247.dp).height(109.dp)
            )
            Spacer(Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.login_description),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                color = KudosWhite,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
            Spacer(Modifier.weight(1f))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LoginGoogleButton(
                    isLoading = state.isLoading,
                    onClick = { vm.onLoginClick(onLoginSuccess) },
                    modifier = Modifier.width(246.dp).height(40.dp)
                )
            }
            Spacer(Modifier.height(98.dp))
            Box(Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.login_copyright),
                    style = MaterialTheme.typography.bodySmall,
                    color = KudosWhite
                )
            }
        }

        // Dropdown dismiss overlay + reusable dropdown panel
        if (state.showLanguageDropdown) {
            Box(
                Modifier.fillMaxSize().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = vm::dismissLanguageDropdown
                )
            )
            LanguageDropdownPanel(
                selected = language,
                onSelect = vm::selectLanguage,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 96.dp, end = 20.dp)
            )
        }
    }
}

@Composable
private fun LoginHeader(
    language: AppLanguage,
    onLanguageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.verticalGradient(
        0f to Color(0xFF00101A),
        0.764f to Color(0x4D00101A),
        0.846f to Color(0x3300101A),
        1.0f to Color(0x0000101A)
    )
    Box(modifier.fillMaxWidth().background(gradient)) {
        Image(
            painter = painterResource(R.drawable.ic_logo_saa),
            contentDescription = "SAA 2025",
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 8.dp)
                .width(48.dp).height(44.dp)
        )
        LanguageTrigger(
            selected = language,
            onClick = onLanguageClick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 4.dp)
                .heightIn(min = 48.dp)
                .padding(horizontal = 4.dp),
        )
    }
}

@Composable
private fun LoginGoogleButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = !isLoading,
        shape = RoundedCornerShape(4.dp),
        color = KudosGold,
        modifier = modifier
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.size(20.dp), color = KudosDarkText, strokeWidth = 2.dp)
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.login_google_button),
                        style = MaterialTheme.typography.labelLarge,
                        color = KudosDarkText
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
