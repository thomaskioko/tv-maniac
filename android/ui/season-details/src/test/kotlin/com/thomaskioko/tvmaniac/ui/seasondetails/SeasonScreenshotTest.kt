package com.thomaskioko.tvmaniac.ui.seasondetails

import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.seasondetails.ui.SeasonDetailsScreen
import com.thomaskioko.tvmaniac.seasondetails.ui.seasonDetailsContent
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
class SeasonScreenshotTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun seasonDetailsScreenContentLoadedState() {
    composeTestRule.captureMultiDevice("SeasonDetailsLoadedContent") {
      TvManiacBackground {
        SeasonDetailsScreen(
          state = seasonDetailsContent,
          onAction = {},
          listState = LazyListState(),
        )
      }
    }
  }
}
