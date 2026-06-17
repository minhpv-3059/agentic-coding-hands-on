package com.sun.kudos_demo

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.sun.kudos_demo.data.KudosPreferences
import com.sun.kudos_demo.feature.auth.AppLanguage
import com.sun.kudos_demo.feature.auth.LanguageManager
import com.sun.kudos_demo.ui.KudosApp
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import kotlinx.coroutines.flow.first
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs = remember { KudosPreferences(applicationContext) }

            // Seed the global language from persisted prefs once on startup. `loaded` gates the
            // persist effect below so the default-VN first frame can't overwrite a stored "EN".
            var loaded by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                LanguageManager.loadInitial(AppLanguage.fromCode(prefs.languageCode.first()))
                loaded = true
            }

            // Observe the runtime language and re-derive a locale-overridden context so every
            // stringResource() re-resolves against values / values-en — no Activity recreation.
            val language by LanguageManager.language.collectAsState()
            LaunchedEffect(language, loaded) {
                if (loaded) prefs.setLanguageCode(language.code)
            }

            val baseContext = LocalContext.current
            val localizedContext = remember(language, baseContext) {
                val config = Configuration(baseContext.resources.configuration)
                config.setLocale(Locale.forLanguageTag(language.locale))
                baseContext.createConfigurationContext(config)
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration,
            ) {
                KudosAppTheme {
                    KudosApp()
                }
            }
        }
    }
}
