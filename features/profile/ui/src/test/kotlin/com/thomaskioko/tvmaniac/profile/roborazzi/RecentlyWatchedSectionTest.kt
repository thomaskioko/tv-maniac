package com.thomaskioko.tvmaniac.profile.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileRecentItem
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.profile.ui.components.RecentlyWatchedSection
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import kotlinx.collections.immutable.persistentListOf
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
internal class RecentlyWatchedSectionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val episodes = persistentListOf(
        ProfileRecentItem(showId = 1, tmdbId = 1396, title = "Breaking Bad", posterUrl = null, episodeLabel = "S5E14"),
        ProfileRecentItem(showId = 2, tmdbId = 1399, title = "Game of Thrones", posterUrl = null, episodeLabel = "S8E3"),
        ProfileRecentItem(showId = 3, tmdbId = 66732, title = "Stranger Things", posterUrl = null, episodeLabel = "S4E9"),
    )

    @Test
    fun recentlyWatchedSectionContentState() {
        composeTestRule.captureMultiDevice("RecentlyWatchedSectionContentState") {
            TvManiacBackground {
                RecentlyWatchedSection(
                    recentlyWatched = SectionState.Content(episodes),
                    title = "Recently Watched",
                    retryLabel = "Retry",
                    onShowClick = {},
                    onRetry = {},
                    modifier = Modifier.padding(vertical = TvManiacSpacing.medium),
                )
            }
        }
    }

    @Test
    fun recentlyWatchedSectionLoadingState() {
        composeTestRule.captureMultiDevice("RecentlyWatchedSectionLoadingState") {
            TvManiacBackground {
                RecentlyWatchedSection(
                    recentlyWatched = SectionState.Loading,
                    title = "Recently Watched",
                    retryLabel = "Retry",
                    onShowClick = {},
                    onRetry = {},
                    modifier = Modifier.padding(vertical = TvManiacSpacing.medium),
                )
            }
        }
    }

    @Test
    fun recentlyWatchedSectionErrorState() {
        composeTestRule.captureMultiDevice("RecentlyWatchedSectionErrorState") {
            TvManiacBackground {
                RecentlyWatchedSection(
                    recentlyWatched = SectionState.Error(UiMessage(message = "Failed to load history")),
                    title = "Recently Watched",
                    retryLabel = "Retry",
                    onShowClick = {},
                    onRetry = {},
                    modifier = Modifier.padding(vertical = TvManiacSpacing.medium),
                )
            }
        }
    }
}
