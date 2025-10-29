package com.thomaskioko.tvmaniac.screenshottests

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziOptions.CompareOptions
import com.github.takahirom.roborazzi.RoborazziOptions.RecordOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import org.robolectric.RuntimeEnvironment

val DefaultRoborazziOptions = RoborazziOptions(
    compareOptions = CompareOptions(changeThreshold = 0.01f),
    recordOptions = RecordOptions(resizeScale = 0.5),
)

enum class DefaultTestDevices(val spec: String) {
    Pixel7(RobolectricDeviceQualifiers.Pixel7),
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiDevice(
    name: String,
    content: @Composable () -> Unit,
) {
    DefaultTestDevices.entries.forEach {
        this.captureMultiTheme(
            deviceSpec = it.spec,
            name = name,
            content = content,
        )
    }
}

/** Takes two screenshots combining light/dark themes. */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiTheme(
    name: String,
    deviceSpec: String,
    overrideFileName: String? = null,
    shouldCompareDarkMode: Boolean = true,
    content: @Composable () -> Unit,
) {
    // Set qualifiers from specs
    RuntimeEnvironment.setQualifiers(deviceSpec)

    val darkModeValues = if (shouldCompareDarkMode) listOf(true, false) else listOf(false)

    var darkMode by mutableStateOf(true)

    this.setContent {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
        ) {
            TvManiacTheme(
                darkTheme = darkMode,
            ) {
                content()
            }
        }
    }

    darkModeValues.forEach { isDarkMode ->
        darkMode = isDarkMode
        val darkModeDesc = if (isDarkMode) "dark" else "light"

        val filename = overrideFileName ?: name

        this.onRoot()
            .captureRoboImage(
                "src/test/screenshots/" + filename + "_$darkModeDesc" + ".png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }
}
