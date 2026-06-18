package com.sun.kudos_demo.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sun.kudos_demo.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E flow 1 & 2 — authentication entry + bottom-tab navigation.
 *
 * Runs the real [MainActivity] so the NavHost, ViewModels and mock auth all participate,
 * exactly as a user would experience them.
 */
@RunWith(AndroidJUnit4::class)
class LoginAndNavigationFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    /** Flow 1: the Login screen authenticates and routes the user to Home. */
    @Test
    fun login_navigatesToHome() {
        composeRule.onNodeWithText(E2eMarkers.LOGIN_BUTTON).assertIsDisplayed()

        composeRule.onNodeWithText(E2eMarkers.LOGIN_BUTTON).performClick()

        // Mock auth delays ~1s before navigating — poll for the Home marker.
        composeRule.waitForText(E2eMarkers.HOME_MARKER)
        composeRule.onNodeWithText(E2eMarkers.HOME_MARKER).assertIsDisplayed()
    }

    /** Flow 2: the bottom navigation switches between Home → Kudos → Profile → Home. */
    @Test
    fun bottomNav_switchesBetweenTabs() {
        composeRule.loginToHome()

        // Home → Kudos feed
        composeRule.onNodeWithText(E2eMarkers.TAB_KUDOS).performClick()
        composeRule.waitForText(E2eMarkers.FEED_MARKER)
        composeRule.onNodeWithText(E2eMarkers.FEED_MARKER).assertIsDisplayed()

        // Kudos → Profile
        composeRule.onNodeWithText(E2eMarkers.TAB_PROFILE).performClick()
        composeRule.waitForText(E2eMarkers.PROFILE_MARKER)
        composeRule.onNodeWithText(E2eMarkers.PROFILE_MARKER).assertIsDisplayed()

        // Profile → back Home (SAA 2025 tab)
        composeRule.onNodeWithText(E2eMarkers.TAB_HOME).performClick()
        composeRule.waitForText(E2eMarkers.HOME_MARKER)
        composeRule.onNodeWithText(E2eMarkers.HOME_MARKER).assertIsDisplayed()
    }
}
