package com.thomaskioko.tvmaniac.discover.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverViewState
import com.thomaskioko.tvmaniac.discover.ui.DiscoverLazyColumn
import com.thomaskioko.tvmaniac.discover.ui.DiscoverScaffold
import com.thomaskioko.tvmaniac.discover.ui.discoverCatalogContentSuccess
import com.thomaskioko.tvmaniac.discover.ui.discoverFeaturedContentSuccess
import com.thomaskioko.tvmaniac.discover.ui.discoverStartWatchingContentSuccess
import com.thomaskioko.tvmaniac.discover.ui.discoverUpNextContentSuccess
import com.thomaskioko.tvmaniac.discover.ui.section.DiscoverCatalogSection
import com.thomaskioko.tvmaniac.discover.ui.section.DiscoverFeaturedSection
import com.thomaskioko.tvmaniac.discover.ui.section.DiscoverStartWatchingSection
import com.thomaskioko.tvmaniac.discover.ui.section.DiscoverUpNextSection
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags
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
class DiscoverScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun discoverScreenLoadingState() {
        composeTestRule.captureMultiDevice("DiscoverScreenLoadingState") {
            TvManiacBackground {
                DiscoverScaffold(
                    hostState = DiscoverViewState(isLoading = true),
                    snackBarHostState = remember { SnackbarHostState() },
                    dismissSnackbarState = rememberDismissState { true },
                    onHostAction = {},
                    content = {},
                )
            }
        }
    }

    @Test
    fun discoverScreenEmptyState() {
        composeTestRule.captureMultiDevice("DiscoverScreenEmptyState") {
            TvManiacBackground {
                DiscoverScaffold(
                    hostState = DiscoverViewState(isEmpty = true),
                    snackBarHostState = remember { SnackbarHostState() },
                    dismissSnackbarState = rememberDismissState { true },
                    onHostAction = {},
                    content = {},
                )
            }
        }
    }

    @Test
    fun discoverScreenErrorState() {
        composeTestRule.captureMultiDevice("DiscoverScreenErrorState") {
            TvManiacBackground {
                DiscoverScaffold(
                    hostState = DiscoverViewState(
                        showError = true,
                        message = UiMessage(message = "Opps! Something went wrong"),
                    ),
                    snackBarHostState = remember { SnackbarHostState() },
                    dismissSnackbarState = rememberDismissState { true },
                    onHostAction = {},
                    content = {},
                )
            }
        }
    }

    @Test
    fun discoverScreenDataLoaded() {
        composeTestRule.captureMultiDevice("DiscoverScreenDataLoaded") {
            TvManiacBackground {
                DiscoverScaffold(
                    hostState = DiscoverViewState(),
                    snackBarHostState = remember { SnackbarHostState() },
                    dismissSnackbarState = rememberDismissState { true },
                    onHostAction = {},
                ) {
                    DiscoverLazyColumn(
                        isRefreshing = false,
                        onSearch = {},
                        onRefresh = {},
                    ) {
                        item(key = DiscoverTestTags.FEATURED_PAGER_TEST_TAG) {
                            val pagerState = rememberPagerState(
                                pageCount = { discoverFeaturedContentSuccess.featuredShows.size },
                            )
                            DiscoverFeaturedSection(
                                state = discoverFeaturedContentSuccess,
                                pagerState = pagerState,
                                onAction = {},
                            )
                        }
                        item(key = DiscoverTestTags.UP_NEXT_SECTION_TEST_TAG) {
                            DiscoverUpNextSection(
                                state = discoverUpNextContentSuccess,
                                title = "Up Next",
                                onAction = {},
                            )
                        }
                        item(key = DiscoverTestTags.ROW_KEY_START_WATCHING) {
                            DiscoverStartWatchingSection(
                                state = discoverStartWatchingContentSuccess,
                                onAction = {},
                            )
                        }
                        item(key = DiscoverTestTags.CATALOG_SECTION_TEST_TAG) {
                            DiscoverCatalogSection(
                                state = discoverCatalogContentSuccess,
                                onAction = {},
                            )
                        }
                    }
                }
            }
        }
    }
}
