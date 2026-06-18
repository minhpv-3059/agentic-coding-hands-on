package com.sun.kudos_demo.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sun.kudos_demo.MainActivity
import com.sun.kudos_demo.ui.KudosTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E flow 3 & 4 — the Send Kudos form: validation guard + the full happy-path that ends
 * with the freshly-sent kudo surfacing at the top of the live feed.
 */
@RunWith(AndroidJUnit4::class)
class SendKudosFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    /** Flow 3: submitting an empty form shows the "missing required fields" banner. */
    @Test
    fun sendKudos_emptyForm_showsValidationError() {
        composeRule.loginToHome()

        // Open Send Kudos via the Home FAB (pencil).
        composeRule.onNodeWithContentDescription(E2eMarkers.HOME_FAB_SEND_CD).performClick()
        composeRule.waitForText(E2eMarkers.SEND_SCREEN_TITLE)

        // Submit with nothing filled in.
        composeRule.onNodeWithTag(KudosTestTags.SEND_SUBMIT_BUTTON).performScrollTo().performClick()

        // The red validation banner appears.
        composeRule.waitForText(E2eMarkers.SEND_ERROR)
        composeRule.onNodeWithText(E2eMarkers.SEND_ERROR, substring = true).assertIsDisplayed()
    }

    /** Flow 4: fill the form, send, and verify the new kudo appears in the feed. */
    @Test
    fun sendKudos_happyPath_appearsInFeed() {
        composeRule.loginToHome()

        // Home → Feed → open Send from the feed prompt (so submit pops back to the feed).
        composeRule.onNodeWithText(E2eMarkers.TAB_KUDOS).performClick()
        composeRule.waitForText(E2eMarkers.FEED_MARKER)
        composeRule.onNodeWithText(E2eMarkers.FEED_MARKER).performClick()
        composeRule.waitForText(E2eMarkers.SEND_SCREEN_TITLE)

        // Recipient — type a query, then pick the single match from the dropdown.
        composeRule.onNodeWithTag(KudosTestTags.SEND_RECIPIENT_INPUT)
            .performTextInput(E2eMarkers.RECIPIENT_QUERY)
        composeRule.waitForText(E2eMarkers.RECIPIENT_NAME)
        composeRule.onNodeWithText(E2eMarkers.RECIPIENT_NAME).performClick()

        // Message.
        composeRule.onNodeWithTag(KudosTestTags.SEND_MESSAGE_INPUT)
            .performScrollTo()
            .performTextInput(KUDO_MESSAGE)
        Espresso.closeSoftKeyboard()

        // Hashtag — open the "+ Hashtag (Tối đa 5)" menu and pick one.
        composeRule.onNode(hasText(E2eMarkers.HASHTAG_ADD, substring = true) and hasClickAction())
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText(E2eMarkers.HASHTAG_OPTION).performClick()
        // The VM keeps the multi-select menu open after one pick (size < max), so a focusable
        // popup is still present — back dismisses only the popup, not the Send screen.
        Espresso.pressBack()
        composeRule.waitForIdle()

        // Send.
        composeRule.onNodeWithTag(KudosTestTags.SEND_SUBMIT_BUTTON).performScrollTo().performClick()

        // Back on the feed — the new kudo is prepended. waitForText confirms it is in the tree;
        // performScrollTo() is the real guard: it only succeeds if the node is reachable from the
        // feed's scroll container (not the still-unmounting Send screen).
        composeRule.waitForText(KUDO_MESSAGE)
        composeRule.onNodeWithText(KUDO_MESSAGE, substring = true).performScrollTo().assertIsDisplayed()
    }

    private companion object {
        /** Distinctive message so the sent kudo is unambiguously findable in the feed. */
        const val KUDO_MESSAGE = "E2EHAPPYMARKER cảm ơn đồng đội"
    }
}
