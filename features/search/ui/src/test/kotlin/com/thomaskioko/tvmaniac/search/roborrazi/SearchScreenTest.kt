package com.thomaskioko.tvmaniac.search.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.ui.SearchScreen
import com.thomaskioko.tvmaniac.search.ui.createDiscoverShowList
import com.thomaskioko.tvmaniac.search.ui.createGenreRowList
import com.thomaskioko.tvmaniac.search.ui.previewCategories
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
                        isRefreshing = false,
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
                        isRefreshing = false,
                        message = UiMessage(message = "Oops! Something went wrong!"),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun searchScreenBrowsingGenres() {
        composeTestRule.captureMultiDevice("SearchBrowsingGenres") {
            TvManiacBackground {
                SearchScreen(
                    state = SearchShowState(
                        isRefreshing = false,
                        genreRows = createGenreRowList(),
                        categoryTitle = "Category",
                        categories = previewCategories(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun searchScreenBrowsingGenresRefreshing() {
        composeTestRule.captureMultiDevice("SearchBrowsingGenres_Refreshing") {
            TvManiacBackground {
                SearchScreen(
                    state = SearchShowState(
                        isRefreshing = true,
                        genreRows = createGenreRowList(),
                        categoryTitle = "Category",
                        categories = previewCategories(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun searchScreenSearchResults() {
        composeTestRule.captureMultiDevice("SearchResults") {
            TvManiacBackground {
                SearchScreen(
                    state = SearchShowState(
                        query = "loki",
                        isRefreshing = false,
                        searchResults = createDiscoverShowList(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun searchScreenSearchResultsUpdating() {
        composeTestRule.captureMultiDevice("SearchResults_Updating") {
            TvManiacBackground {
                SearchScreen(
                    state = SearchShowState(
                        query = "loki",
                        isRefreshing = false,
                        isUpdating = true,
                        searchResults = createDiscoverShowList(),
                    ),
                    onAction = {},
                )
            }
        }
    }
}
