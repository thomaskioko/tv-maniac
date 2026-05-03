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

private const val SYSTEM_PERMISSION_PACKAGE = "com.android.permissioncontroller"

/**
 * Catalogue of platform dialogs that integration tests may need to dismiss.
 *
 * Each entry carries the UiAutomator selectors needed to locate its action button. Callers stay
 * declarative ("dismiss the notification permission prompt") instead of dealing with resource ids
 * or locale-specific button text. Add new entries here as new dialogs surface in flow tests.
 */
public enum class SystemDialog(
    internal val packageName: String,
    internal val selectors: List<BySelector>,
) {
    /** Android 13+ POST_NOTIFICATIONS prompt. Locates the platform deny button. */
    NotificationPermissionDeny(
        packageName = SYSTEM_PERMISSION_PACKAGE,
        selectors = listOf(
            By.res(SYSTEM_PERMISSION_PACKAGE, "permission_deny_button"),
            By.textContains("Don't allow"),
            By.textContains("Deny"),
        ),
    ),
}

/**
 * Clicks the action button for [dialog] via UiAutomator and waits for the platform window to
 * release the foreground.
 *
 * Use to dismiss platform dialogs that detach the Compose owner. The Compose test driver cannot
 * drive these because they live outside the Compose hierarchy. The dialog catalogue ([SystemDialog])
 * encodes the selectors so call sites do not import UiAutomator.
 *
 * No-op on Robolectric and pre-Tiramisu where no real system dialog appears.
 *
 * Returning early would let a still-on-screen permission window swallow the next test action
 * (a `pressBack` aimed at our own activity ends up dismissing the system window instead). The
 * helper therefore (1) waits up to [appearTimeoutMillis] per selector for the dialog to surface,
 * (2) clicks the matched action, (3) waits for the action to disappear, (4) waits for the system
 * package to leave the foreground, and (5) idles the device so subsequent UiAutomator and
 * Compose queries see a settled tree.
 *
 * @param dialog Catalogue entry whose action button to click.
 * @param appearTimeoutMillis Maximum wait per selector for a match before giving up.
 * @param dismissTimeoutMillis Maximum wait for the platform window to leave the foreground after
 *   the click.
 */
public fun dismissSystemDialog(
    dialog: SystemDialog,
    appearTimeoutMillis: Long = 3_000,
    dismissTimeoutMillis: Long = 5_000,
) {
    if (Build.FINGERPRINT.startsWith("robolectric", ignoreCase = true)) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val device = UiDevice.getInstance(instrumentation)

    var matchedSelector: BySelector? = null
    for (selector in dialog.selectors) {
        if (device.hasObject(selector)) {
            matchedSelector = selector
            break
        }
        if (device.wait(Until.hasObject(selector), appearTimeoutMillis)) {
            matchedSelector = selector
            break
        }
    }

    if (matchedSelector == null) return

    device.findObject(matchedSelector).click()

    device.wait(Until.gone(By.pkg(dialog.packageName)), dismissTimeoutMillis)
    device.wait(
        Until.hasObject(By.pkg(instrumentation.targetContext.packageName)),
        dismissTimeoutMillis,
    )
}
