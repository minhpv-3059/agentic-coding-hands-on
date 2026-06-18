package com.sun.kudos_demo.e2e

import android.os.SystemClock
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

/**
 * Shared helpers + stable on-screen markers for the Kudos E2E (Compose UI) tests.
 *
 * Markers are the Vietnamese strings (the app's default language) that each screen renders,
 * so a test can assert "we are on screen X" without reaching into navigation internals.
 */
object E2eMarkers {
    // Auth
    const val LOGIN_BUTTON = "LOGIN With Google"

    // Bottom-nav tab labels (hardcoded in BottomNavTab — language-independent)
    const val TAB_HOME = "SAA 2025"
    const val TAB_KUDOS = "Kudos"
    const val TAB_PROFILE = "Profile"

    // Per-screen unique markers (visible without deep scrolling)
    const val HOME_MARKER = "VỀ KUDOS"                              // Home hero CTA
    const val HOME_FAB_SEND_CD = "Gửi Kudos"                        // Home FAB pencil contentDescription
    const val FEED_MARKER = "Hôm nay, bạn muốn gửi kudos đến ai?"   // Feed send-prompt
    const val PROFILE_MARKER = "Bộ sưu tập icon của tôi"            // own-profile only

    // Send Kudos
    const val SEND_SCREEN_TITLE = "Gửi lời chúc Kudos"
    // Long enough that it can't accidentally match a reorganised banner (full text continues "… để gửi Kudos!").
    const val SEND_ERROR = "Bạn cần điền đủ Người nhận, Lời nhắn gửi và Hashtag"
    // RECIPIENT_QUERY filters KudosMockData.searchableUsers down to the single RECIPIENT_NAME row.
    const val RECIPIENT_QUERY = "Minh"
    const val RECIPIENT_NAME = "Trần Quang Minh"
    const val HASHTAG_ADD = "Hashtag (Tối đa"                       // "+ Hashtag (Tối đa 5)" — distinct from the Image add-button
    const val HASHTAG_OPTION = "#BE OPTIMISTIC"
}

/** Default timeout for waiting on navigation / async UI to settle (mock auth has a 1s delay). */
const val E2E_TIMEOUT_MS = 5_000L

/** True once at least one node containing [text] is present in the merged tree. */
fun ComposeTestRule.isDisplayingText(text: String): Boolean =
    onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()

/**
 * Wait until [text] appears, polling in real wall-clock time.
 *
 * We deliberately avoid [ComposeTestRule.waitUntil] here: the mock auth uses a real
 * coroutine `delay()`, and the login screen runs an infinite spinner animation. The
 * clock-synchronised waitUntil starves that delay so navigation never fires. A plain
 * real-time poll lets the main looper run the delayed work normally.
 */
fun ComposeTestRule.waitForText(text: String, timeoutMs: Long = E2E_TIMEOUT_MS) {
    val deadline = SystemClock.uptimeMillis() + timeoutMs
    while (SystemClock.uptimeMillis() < deadline) {
        if (isDisplayingText(text)) return
        SystemClock.sleep(100)
    }
    // Let any in-flight recomposition settle, then assert so the failure carries a
    // readable "node not found" message instead of a bare timeout.
    waitForIdle()
    onNodeWithText(text, substring = true).assertIsDisplayed()
}

/**
 * Log in from the Login screen and land on Home. The mock auth waits ~1s before navigating,
 * so we poll for the Home marker in real time.
 */
fun ComposeTestRule.loginToHome() {
    onNodeWithText(E2eMarkers.LOGIN_BUTTON).assertIsDisplayed()
    onNodeWithText(E2eMarkers.LOGIN_BUTTON).performClick()
    waitForText(E2eMarkers.HOME_MARKER)
}
