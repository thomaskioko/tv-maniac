package com.thomaskioko.tvmaniac.ui.search

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.presentation.search.EmptySearchResult
import com.thomaskioko.tvmaniac.presentation.search.SearchResultAvailable
import com.thomaskioko.tvmaniac.presentation.search.ShowContentAvailable
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
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun searchScreenEmptyState() {
        composeTestRule.captureMultiDevice("SearchEmptyState") {
            TvManiacBackground {
                SearchScreen(
                    state = EmptySearchResult(),
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
                    state = EmptySearchResult(),
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
                    state = ShowContentAvailable(
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
                    SearchResultAvailable(
                        results = createDiscoverShowList(),
                    ),
                    onAction = {},
                )
            }
        }
    }
}
