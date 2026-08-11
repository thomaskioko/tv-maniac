package com.thomaskioko.tvmaniac.ui.upnext

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextSortOption
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_upnext_sort_air_date
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextChangeSortOption
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextState
import com.thomaskioko.tvmaniac.presentation.upnext.model.UpNextEpisodeUiModel
import com.thomaskioko.tvmaniac.testtags.upnext.UpNextTestTags
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
class UpNextScrollPositionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `should keep scroll position given screen is covered and restored`() {
        var isCovered by mutableStateOf(false)

        composeTestRule.setContent {
            val stateHolder = rememberSaveableStateHolder()
            if (isCovered) {
                stateHolder.SaveableStateProvider(COVERING_SCREEN_KEY) { Text("Show details") }
            } else {
                stateHolder.SaveableStateProvider(UP_NEXT_KEY) {
                    TvManiacBackground {
                        UpNextScreen(
                            state = UpNextState(isLoading = false, episodes = upNextEpisodes()),
                            onAction = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag(UpNextTestTags.PAGE_LIST_TEST_TAG)
            .performScrollToNode(hasTestTag(UpNextTestTags.episodeRow(LAST_SHOW_ID)))
        composeTestRule.onNodeWithTag(UpNextTestTags.episodeRow(LAST_SHOW_ID)).assertIsDisplayed()

        isCovered = true
        composeTestRule.waitForIdle()
        isCovered = false
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(UpNextTestTags.episodeRow(LAST_SHOW_ID)).assertIsDisplayed()
    }

    @Test
    fun `should scroll back to top given sort option is changed`() {
        var sortOption by mutableStateOf(UpNextSortOption.LAST_WATCHED)

        composeTestRule.setContent {
            TvManiacBackground {
                UpNextScreen(
                    state = UpNextState(
                        isLoading = false,
                        sortOption = sortOption,
                        episodes = upNextEpisodes(),
                    ),
                    onAction = { action ->
                        if (action is UpNextChangeSortOption) sortOption = action.sortOption
                    },
                )
            }
        }

        composeTestRule.onNodeWithTag(UpNextTestTags.PAGE_LIST_TEST_TAG)
            .performScrollToNode(hasTestTag(UpNextTestTags.episodeRow(LAST_SHOW_ID)))

        composeTestRule
            .onNodeWithText(label_upnext_sort_air_date.resolve(composeTestRule.activity))
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(UpNextTestTags.episodeRow(FIRST_SHOW_ID)).assertIsDisplayed()
    }
}

private const val EPISODE_COUNT = 20
private const val FIRST_SHOW_ID = 1L
private const val LAST_SHOW_ID = EPISODE_COUNT.toLong()
private const val UP_NEXT_KEY = "up_next"
private const val COVERING_SCREEN_KEY = "show_details"

private fun upNextEpisodes() = (1..EPISODE_COUNT).map { index ->
    UpNextEpisodeUiModel(
        showId = index.toLong(),
        showName = "Show $index",
        imageUrl = null,
        showStatus = "Returning Series",
        showYear = "2024",
        episodeId = index * 100L,
        episodeName = "Episode $index",
        seasonId = index * 10L,
        seasonNumber = 1,
        episodeNumber = index.toLong(),
        runtime = 45,
        overview = "Overview $index",
        firstAired = null,
        seasonCount = 1,
        episodeCount = EPISODE_COUNT.toLong(),
        watchedCount = index.toLong(),
        totalCount = EPISODE_COUNT.toLong(),
        formattedEpisodeNumber = "S01E$index",
        remainingEpisodes = (EPISODE_COUNT - index).toLong(),
        formattedRuntime = "45m",
    )
}.toImmutableList()
