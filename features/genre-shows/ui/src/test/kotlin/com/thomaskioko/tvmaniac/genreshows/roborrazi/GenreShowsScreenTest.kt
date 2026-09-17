package com.thomaskioko.tvmaniac.genreshows.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.genreshows.ui.GenreShowsScreen
import com.thomaskioko.tvmaniac.genreshows.ui.genreShowsAppendErrorState
import com.thomaskioko.tvmaniac.genreshows.ui.genreShowsAppendLoadingState
import com.thomaskioko.tvmaniac.genreshows.ui.genreShowsEmptyState
import com.thomaskioko.tvmaniac.genreshows.ui.genreShowsLoadedState
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
class GenreShowsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun genreShowsLoadedState() {
        composeTestRule.captureMultiDevice("GenreShowsLoadedState") {
            TvManiacBackground {
                GenreShowsScreen(
                    state = genreShowsLoadedState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun genreShowsAppendLoadingState() {
        composeTestRule.captureMultiDevice("GenreShowsAppendLoadingState") {
            TvManiacBackground {
                GenreShowsScreen(
                    state = genreShowsAppendLoadingState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun genreShowsAppendErrorState() {
        composeTestRule.captureMultiDevice("GenreShowsAppendErrorState") {
            TvManiacBackground {
                GenreShowsScreen(
                    state = genreShowsAppendErrorState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun genreShowsEmptyState() {
        composeTestRule.captureMultiDevice("GenreShowsEmptyState") {
            TvManiacBackground {
                GenreShowsScreen(
                    state = genreShowsEmptyState,
                    onAction = {},
                )
            }
        }
    }
}
