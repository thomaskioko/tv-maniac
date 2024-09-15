package com.thomaskioko.tvmaniac.ui.moreshows

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsState
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import kotlinx.coroutines.flow.flowOf
import org.junit.Ignore
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
class MoreShowsScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Ignore("Flaky test")
  @Test
  fun moreShowsLoadedState() {
    composeTestRule.captureMultiDevice("MoreShowsLoadedState") {
      TvManiacBackground {
        MoreShowsScreen(
          state =
            MoreShowsState(
              categoryTitle = "Upcoming",
              pagingDataFlow = flowOf(PagingData.from(showList)),
            ),
          onAction = {},
        )
      }
    }
  }
}
