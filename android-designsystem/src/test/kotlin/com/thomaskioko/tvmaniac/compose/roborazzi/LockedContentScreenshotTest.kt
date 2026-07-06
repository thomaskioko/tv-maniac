package com.thomaskioko.tvmaniac.compose.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CustomThemes
import com.thomaskioko.tvmaniac.compose.components.PremiumBadge
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
internal class LockedContentScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun premiumBadge() {
        composeTestRule.captureMultiDevice("PremiumBadge") {
            Surface {
                PremiumBadge(text = "Premium")
            }
        }
    }

    @Test
    fun lockedContent() {
        composeTestRule.captureMultiDevice("LockedContent_Locked") {
            Surface {
                CustomThemes(
                    locked = true,
                    badgeText = "Premium",
                    title = "Calendar is a Premium feature",
                    message = "Upgrade to Premium to see your upcoming episodes.",
                    actionText = "Upgrade to Premium",
                    onActionClick = {},
                    modifier = Modifier.size(width = 320.dp, height = 280.dp),
                ) {
                    SampleContent()
                }
            }
        }
    }

    @Test
    fun lockedContentUnlocked() {
        composeTestRule.captureMultiDevice("LockedContent_Unlocked") {
            Surface {
                CustomThemes(
                    locked = false,
                    badgeText = "Premium",
                    modifier = Modifier.size(width = 320.dp, height = 240.dp),
                ) {
                    SampleContent()
                }
            }
        }
    }
}

private const val SAMPLE_CONTENT_ROW_COUNT = 2

@Composable
private fun SampleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(SAMPLE_CONTENT_ROW_COUNT) { index ->
            Text(
                text = "Sample content row ${index + 1}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
