package com.thomaskioko.tvmaniac.episodedetail.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.episodedetail.ui.EpisodeDetailContent
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetState
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
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
class EpisodeDetailScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun episodeDetailAllActions() {
        composeTestRule.captureMultiDevice("EpisodeDetailAllActions") {
            TvManiacBackground {
                EpisodeDetailContent(
                    state = EpisodeDetailSheetState(
                        isLoading = false,
                        episodeTitle = "The Walking Dead: Daryl Dixon",
                        showName = "The Walking Dead",
                        seasonEpisodeNumber = "S02E01",
                        overview = "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                        rating = 8.5,
                        voteCount = 1234,
                        isWatched = false,
                        availableActions = persistentListOf(
                            EpisodeSheetActionItem.TOGGLE_WATCHED,
                            EpisodeSheetActionItem.OPEN_SHOW,
                            EpisodeSheetActionItem.OPEN_SEASON,
                            EpisodeSheetActionItem.UNFOLLOW,
                        ),
                    ),
                )
            }
        }
    }

    @Test
    fun episodeDetailWatched() {
        composeTestRule.captureMultiDevice("EpisodeDetailWatched") {
            TvManiacBackground {
                EpisodeDetailContent(
                    state = EpisodeDetailSheetState(
                        isLoading = false,
                        episodeTitle = "Wednesday",
                        showName = "Wednesday",
                        seasonEpisodeNumber = "S02E03",
                        overview = "Wednesday arrives at Nevermore Academy and begins investigating a series of mysterious events.",
                        rating = 7.9,
                        voteCount = 856,
                        isWatched = true,
                        availableActions = persistentListOf(
                            EpisodeSheetActionItem.TOGGLE_WATCHED,
                            EpisodeSheetActionItem.OPEN_SHOW,
                            EpisodeSheetActionItem.OPEN_SEASON,
                            EpisodeSheetActionItem.UNFOLLOW,
                        ),
                    ),
                )
            }
        }
    }

    @Test
    fun episodeDetailSeasonDetailsSource() {
        composeTestRule.captureMultiDevice("EpisodeDetailSeasonDetailsSource") {
            TvManiacBackground {
                EpisodeDetailContent(
                    state = EpisodeDetailSheetState(
                        isLoading = false,
                        episodeTitle = "House of the Dragon",
                        showName = "House of the Dragon",
                        seasonEpisodeNumber = "S03E01",
                        overview = "King Viserys hosts a tournament to celebrate the birth of his heir.",
                        isWatched = false,
                        availableActions = persistentListOf(
                            EpisodeSheetActionItem.TOGGLE_WATCHED,
                        ),
                    ),
                )
            }
        }
    }

    @Test
    fun episodeDetailNoOverview() {
        composeTestRule.captureMultiDevice("EpisodeDetailNoOverview") {
            TvManiacBackground {
                EpisodeDetailContent(
                    state = EpisodeDetailSheetState(
                        isLoading = false,
                        episodeTitle = "Severance",
                        showName = "Severance",
                        seasonEpisodeNumber = "S02E05",
                        rating = 9.1,
                        voteCount = 2500,
                        isWatched = false,
                        availableActions = persistentListOf(
                            EpisodeSheetActionItem.TOGGLE_WATCHED,
                            EpisodeSheetActionItem.OPEN_SHOW,
                            EpisodeSheetActionItem.OPEN_SEASON,
                            EpisodeSheetActionItem.UNFOLLOW,
                        ),
                    ),
                )
            }
        }
    }
}
