package com.sun.kudos_demo.ui

/**
 * Stable identifiers for Compose UI (instrumented) tests.
 *
 * Only a handful of interactive controls that are awkward to target by visible text
 * (text fields whose content changes, the gold "Gửi đi" action button) carry a testTag.
 * Everything else is reached via on-screen text / contentDescription, so production code
 * stays free of test-only annotations.
 */
object KudosTestTags {
    const val SEND_RECIPIENT_INPUT = "send_recipient_input"
    const val SEND_MESSAGE_INPUT = "send_message_input"
    const val SEND_SUBMIT_BUTTON = "send_submit_button"
}
