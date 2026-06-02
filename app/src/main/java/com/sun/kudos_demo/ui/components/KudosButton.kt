package com.sun.kudos_demo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosSecondaryButtonNormal

private val PillShape = RoundedCornerShape(50)

// Gold pill button — primary CTA (e.g. "LOGIN With Google").
// Caller controls width via modifier (add fillMaxWidth() for full-width usage).
@Composable
fun KudosPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = KudosGold,
            contentColor = KudosDarkText,
            disabledContainerColor = KudosGold.copy(alpha = 0.38f),
            disabledContentColor = KudosDarkText.copy(alpha = 0.38f)
        ),
        modifier = modifier.height(56.dp)
    ) {
        leadingIcon?.invoke()
        if (leadingIcon != null) Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

// Outlined gold button — secondary action (e.g. "ABOUT AWARD ↗")
@Composable
fun KudosSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showExternalIcon: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        border = BorderStroke(1.dp, if (enabled) KudosBorder else KudosBorder.copy(alpha = 0.38f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = KudosSecondaryButtonNormal,
            contentColor = KudosGold,
            disabledContainerColor = KudosSecondaryButtonNormal.copy(alpha = 0.38f),
            disabledContentColor = KudosGold.copy(alpha = 0.38f)
        ),
        modifier = modifier.height(44.dp)
    ) {
        Text(
            text = if (showExternalIcon) "$text ↗" else text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// Minimal text-only button — tertiary/inline action
@Composable
fun KudosTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = KudosGold,
            disabledContentColor = KudosGold.copy(alpha = 0.38f)
        ),
        modifier = modifier
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudosPrimaryButtonPreview() {
    KudosAppTheme {
        KudosPrimaryButton(text = "LOGIN With Google", onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun KudosSecondaryButtonPreview() {
    KudosAppTheme {
        KudosSecondaryButton(text = "ABOUT AWARD", onClick = {}, showExternalIcon = true)
    }
}
