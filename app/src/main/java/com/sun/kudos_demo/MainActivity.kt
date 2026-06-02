package com.sun.kudos_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sun.kudos_demo.ui.KudosApp
import com.sun.kudos_demo.ui.theme.KudosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KudosAppTheme {
                KudosApp()
            }
        }
    }
}
