package com.thomaskioko.tvmaniac.ui.upnext.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextState
import com.thomaskioko.tvmaniac.presentation.upnext.model.UpNextEpisodeUiModel
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.ui.upnext.UpNextScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
class UpNextScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun upNextScreenLoadingState() {
        composeTestRule.captureMultiDevice("UpNextScreenLoadingState") {
            TvManiacBackground {
                UpNextScreen(
                    state = UpNextState(
                        isLoading = true,
                        episodes = persistentListOf(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun upNextScreenEmptyState() {
        composeTestRule.captureMultiDevice("UpNextScreenEmptyState") {
            TvManiacBackground {
                UpNextScreen(
                    state = UpNextState(
                        isLoading = false,
                        episodes = persistentListOf(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun upNextScreenContentLoaded() {
        composeTestRule.captureMultiDevice("UpNextScreenContentLoaded") {
            TvManiacBackground {
                UpNextScreen(
                    state = UpNextState(
                        isLoading = false,
                        sortOption = UpNextSortOption.LAST_WATCHED,
                        episodes = sampleEpisodes(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun upNextScreenContentLoadedSortByAirDate() {
        composeTestRule.captureMultiDevice("UpNextScreenContentLoadedSortByAirDate") {
            TvManiacBackground {
                UpNextScreen(
                    state = UpNextState(
                        isLoading = false,
                        sortOption = UpNextSortOption.AIR_DATE,
                        episodes = sampleEpisodes(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun upNextScreenContentWithLoading() {
        composeTestRule.captureMultiDevice("UpNextScreenContentWithLoading") {
            TvManiacBackground {
                UpNextScreen(
                    state = UpNextState(
                        isLoading = true,
                        sortOption = UpNextSortOption.LAST_WATCHED,
                        episodes = sampleEpisodes(),
                    ),
                    onAction = {},
                )
            }
        }
    }
}

private fun sampleEpisodes() = listOf(
    UpNextEpisodeUiModel(
        showTraktId = 1,
        showTmdbId = 1396,
        showName = "Breaking Bad",
        imageUrl = null,
        showStatus = "Ended",
        showYear = "2008",
        episodeId = 101,
        episodeName = "Ozymandias",
        seasonId = 10,
        seasonNumber = 5,
        episodeNumber = 14,
        runtime = 47,
        overview = "Everyone copes with radically changed circumstances.",
        firstAired = null,
        seasonCount = 5,
        episodeCount = 62,
        watchedCount = 55,
        totalCount = 62,
        formattedEpisodeNumber = "S05E14",
        remainingEpisodes = 7,
        formattedRuntime = "47m",
    ),
    UpNextEpisodeUiModel(
        showTraktId = 2,
        showTmdbId = 1399,
        showName = "Game of Thrones",
        imageUrl = null,
        showStatus = "Ended",
        showYear = "2011",
        episodeId = 201,
        episodeName = "The Rains of Castamere",
        seasonId = 20,
        seasonNumber = 3,
        episodeNumber = 9,
        runtime = 52,
        overview = "Robb and Catelyn receive an important dinner invitation.",
        firstAired = null,
        seasonCount = 8,
        episodeCount = 73,
        watchedCount = 20,
        totalCount = 73,
        formattedEpisodeNumber = "S03E09",
        remainingEpisodes = 53,
        formattedRuntime = "52m",
    ),
    UpNextEpisodeUiModel(
        showTraktId = 3,
        showTmdbId = 66732,
        showName = "Stranger Things",
        imageUrl = null,
        showStatus = "Returning Series",
        showYear = "2016",
        episodeId = 301,
        episodeName = "Chapter One: The Vanishing of Will Byers",
        seasonId = 30,
        seasonNumber = 1,
        episodeNumber = 1,
        runtime = 50,
        overview = "On his way home from a friend's house, young Will sees something terrifying.",
        firstAired = null,
        seasonCount = 4,
        episodeCount = 34,
        watchedCount = 0,
        totalCount = 34,
        formattedEpisodeNumber = "S01E01",
        remainingEpisodes = 34,
        formattedRuntime = "50m",
    ),
).toImmutableList()
