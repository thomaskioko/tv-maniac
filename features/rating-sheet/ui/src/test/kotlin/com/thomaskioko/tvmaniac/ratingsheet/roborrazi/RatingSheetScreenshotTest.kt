package com.thomaskioko.tvmaniac.ratingsheet.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.ratingsheet.presenter.RatingSheetState
import com.thomaskioko.tvmaniac.ratingsheet.ui.RatingSheetContent
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
class RatingSheetScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun ratingSheetUnrated() {
        composeTestRule.captureMultiDevice("RatingSheetUnrated") {
            TvManiacBackground {
                RatingSheetContent(
                    state = RatingSheetState(title = "Your rating", removeRatingLabel = "Remove rating", userRating = null),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun ratingSheetRated() {
        composeTestRule.captureMultiDevice("RatingSheetRated") {
            TvManiacBackground {
                RatingSheetContent(
                    state = RatingSheetState(title = "Your rating", removeRatingLabel = "Remove rating", userRating = 8),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun ratingSheetHalfRated() {
        composeTestRule.captureMultiDevice("RatingSheetHalfRated") {
            TvManiacBackground {
                RatingSheetContent(
                    state = RatingSheetState(title = "Your rating", removeRatingLabel = "Remove rating", userRating = 7),
                    onAction = {},
                )
            }
        }
    }
}
