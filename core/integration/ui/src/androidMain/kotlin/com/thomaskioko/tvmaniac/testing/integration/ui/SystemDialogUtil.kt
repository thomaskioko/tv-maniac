@file:OptIn(ExperimentalTestApi::class)

package com.thomaskioko.tvmaniac.testing.integration.ui

import android.os.Build
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

/**
 * System-level escape hatches for integration tests.
 *
 * Most Compose primitives live on [BaseRobot]. This file is reserved for operations that work
 * outside the Compose hierarchy and therefore cannot be driven by the Compose test driver alone.
 */

/**
 * Catalogue of platform dialogs that integration tests may need to dismiss.
 *
 * Each entry carries the UiAutomator selectors needed to locate its action button. Callers stay
 * declarative ("dismiss the notification permission prompt") instead of dealing with resource ids
 * or locale-specific button text. Add new entries here as new dialogs surface in flow tests.
 */
public enum class SystemDialog(internal val selectors: List<BySelector>) {
    /** Android 13+ POST_NOTIFICATIONS prompt. Locates the platform deny button. */
    NotificationPermissionDeny(
        selectors = listOf(
            By.res("com.android.permissioncontroller", "permission_deny_button"),
            By.textContains("Don't allow"),
            By.textContains("Deny"),
        ),
    ),
}

/**
 * Clicks the action button for [dialog] via UiAutomator and waits for it to disappear.
 *
 * Use to dismiss platform dialogs that detach the Compose owner. The Compose test driver cannot
 * drive these because they live outside the Compose hierarchy. The dialog catalogue ([SystemDialog])
 * encodes the selectors so call sites do not import UiAutomator.
 *
 * No-op on Robolectric and pre-Tiramisu where no real system dialog appears. If no selector
 * resolves within [appearTimeoutMillis], returns silently. The next caller assertion will surface
 * the real symptom if the dialog is still up.
 *
 * @param dialog Catalogue entry whose action button to click.
 * @param appearTimeoutMillis Maximum wait per selector for a match before giving up.
 * @param dismissTimeoutMillis Maximum wait for the matched selector to disappear after the click.
 */
public fun dismissSystemDialog(
    dialog: SystemDialog,
    appearTimeoutMillis: Long = 1_000,
    dismissTimeoutMillis: Long = 1_000,
) {
    if (Build.FINGERPRINT.startsWith("robolectric", ignoreCase = true)) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    var matched: BySelector? = null
    val target = dialog.selectors.firstNotNullOfOrNull { selector ->
        device.wait(Until.findObject(selector), appearTimeoutMillis)?.also { matched = selector }
    } ?: return

    target.click()
    matched?.let { device.wait(Until.gone(it), dismissTimeoutMillis) }
}
