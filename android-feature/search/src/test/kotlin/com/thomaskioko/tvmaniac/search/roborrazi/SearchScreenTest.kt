package com.thomaskioko.tvmaniac.search.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.ui.SearchScreen
import com.thomaskioko.tvmaniac.search.ui.createDiscoverShowList
import com.thomaskioko.tvmaniac.search.ui.createGenreShowList
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
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun searchScreenEmptyState() {
        composeTestRule.captureMultiDevice("SearchEmptyState") {
            TvManiacBackground {
                SearchScreen(
                    state = SearchShowState(
                        query = "test",
                        isUpdating = false,
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun searchScreenErrorState() {
        composeTestRule.captureMultiDevice("SearchErrorState") {
            TvManiacBackground {
                SearchScreen(
                    state = SearchShowState(
                        errorMessage = "Something went wrong",
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun searchShowContentAvailableState() {
        composeTestRule.captureMultiDevice("SearchShowContentAvailable") {
            TvManiacBackground {
                SearchScreen(
                    state = SearchShowState(
                        genres = createGenreShowList(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun searchResultAvailableState() {
        composeTestRule.captureMultiDevice("SearchResultAvailable") {
            TvManiacBackground {
                SearchScreen(
                    state = SearchShowState(
                        query = "loki",
                        searchResults = createDiscoverShowList(),
                    ),
                    onAction = {},
                )
            }
        }
    }
}
