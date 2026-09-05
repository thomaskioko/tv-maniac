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
                    state = ratingSheetState(title = "Lioness", subtitle = "2023", posterUrl = "/lioness.jpg", userRating = null),
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
                    state = ratingSheetState(
                        title = "Sacrificial Soldiers",
                        subtitle = "Lioness • S1E1",
                        backdropUrl = "/sacrificial-soldiers.jpg",
                        userRating = 8,
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun ratingSheetSeasonRated() {
        composeTestRule.captureMultiDevice("RatingSheetSeasonRated") {
            TvManiacBackground {
                RatingSheetContent(
                    state = ratingSheetState(title = "Season 1", subtitle = "Lioness", posterUrl = "/season-1.jpg", userRating = 7),
                    onAction = {},
                )
            }
        }
    }

    private fun ratingSheetState(
        title: String,
        subtitle: String?,
        userRating: Int?,
        posterUrl: String? = null,
        backdropUrl: String? = null,
    ) = RatingSheetState(
        headerLabel = "You're rating",
        title = title,
        subtitle = subtitle,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        scoreLabel = "Your rating",
        removeRatingLabel = "Remove rating",
        userRating = userRating,
    )
}
