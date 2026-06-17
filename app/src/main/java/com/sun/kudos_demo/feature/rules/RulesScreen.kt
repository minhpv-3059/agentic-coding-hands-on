package com.sun.kudos_demo.feature.rules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.feature.rules.components.HeroSection
import com.sun.kudos_demo.feature.rules.components.RulesIconGrid
import com.sun.kudos_demo.ui.theme.KudosBackground
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal
import com.sun.kudos_demo.ui.theme.KudosWhite

/**
 * Rules / "Thể lệ" — static content screen (design zIuFaHAid4, node 6885:10860).
 * Detail flow: back arrow + centered title over the swirl key-visual (no bottom nav).
 * "Đóng" + back → [onClose]; "Viết Kudos" → [onWriteKudos].
 */
@Composable
fun RulesScreen(
    onClose: () -> Unit,
    onWriteKudos: () -> Unit,
    modifier: Modifier = Modifier,
    // Optional demo hooks — the mock app has no real 403/404 trigger, so these let the
    // error screens be reached for the demo. Null → footer hidden (keeps the design clean).
    onDemoForbidden: (() -> Unit)? = null,
    onDemoNotFound: (() -> Unit)? = null
) {
    Box(modifier.fillMaxSize().background(KudosBackground)) {
        // Swirl key-visual backdrop (full-bleed, matching Profile/Home precedent)
        Image(
            painter = painterResource(R.drawable.bg_home_keyvisual),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(Modifier.fillMaxSize()) {
            RulesTopBar(onBack = onClose)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 4.1 — intro
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.rules_title),
                        color = KudosGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    )
                    GoldSubtitle(stringResource(R.string.rules_recipient_subtitle))
                    BodyText(stringResource(R.string.rules_recipient_desc))
                }

                HorizontalDivider(thickness = 1.dp, color = KudosDivider)

                // 4.2 — Hero tiers
                HeroSection()

                // 4.3 — sender gifts + 6-icon grid
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GoldSubtitle(stringResource(R.string.rules_sender_subtitle))
                    BodyText(stringResource(R.string.rules_sender_desc))
                    RulesIconGrid()
                    BodyText(stringResource(R.string.rules_sender_conclusion))
                }

                // 4.4 — National Kudos
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.rules_national_title),
                        color = KudosGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                    BodyText(stringResource(R.string.rules_national_desc))
                }

                // 4.5 — actions (gap 12dp, both 40dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onClose,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, KudosBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = KudosSecondaryButtonNormal,
                            contentColor = KudosGold
                        ),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text(stringResource(R.string.rules_close), style = MaterialTheme.typography.labelLarge)
                    }
                    Surface(
                        onClick = onWriteKudos,
                        shape = RoundedCornerShape(4.dp),
                        color = KudosGold,
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                stringResource(R.string.rules_write_kudos),
                                style = MaterialTheme.typography.labelLarge,
                                color = KudosDarkText
                            )
                        }
                    }
                }

                // Demo-only footer (not part of the design) — opens the 403/404 error screens.
                if (onDemoForbidden != null || onDemoNotFound != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Demo:",
                            color = KudosWhite.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.labelSmall
                        )
                        onDemoForbidden?.let {
                            Text(
                                "403",
                                color = KudosGold.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .clickable(onClick = it)
                                    .padding(horizontal = 4.dp)
                            )
                        }
                        onDemoNotFound?.let {
                            Text(
                                "404",
                                color = KudosGold.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .clickable(onClick = it)
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RulesTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.rules_close),
                tint = KudosWhite,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = stringResource(R.string.rules_title),
            color = KudosWhite,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun GoldSubtitle(text: String) {
    Text(
        text = text,
        color = KudosGold,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.25.sp
        )
    )
}

@Composable
private fun BodyText(text: String, color: Color = KudosWhite) {
    Text(text = text, color = color, style = MaterialTheme.typography.bodyMedium)
}
