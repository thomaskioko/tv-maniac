package com.thomaskioko.tvmaniac.profile.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileShowItem
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.profile.ui.components.FavoritesSection
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
internal class FavoritesSectionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val shows = persistentListOf(
        ProfileShowItem(traktId = 1, tmdbId = 1396, title = "Breaking Bad", posterUrl = null),
        ProfileShowItem(traktId = 2, tmdbId = 1399, title = "Game of Thrones", posterUrl = null),
        ProfileShowItem(traktId = 3, tmdbId = 66732, title = "Stranger Things", posterUrl = null),
    )

    @Test
    fun favoritesSectionContentState() {
        composeTestRule.captureMultiDevice("FavoritesSectionContentState") {
            TvManiacBackground {
                FavoritesSection(
                    favorites = SectionState.Content(shows),
                    title = "Favorites",
                    retryLabel = "Retry",
                    onShowClick = {},
                    onRetry = {},
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        }
    }

    @Test
    fun favoritesSectionLoadingState() {
        composeTestRule.captureMultiDevice("FavoritesSectionLoadingState") {
            TvManiacBackground {
                FavoritesSection(
                    favorites = SectionState.Loading,
                    title = "Favorites",
                    retryLabel = "Retry",
                    onShowClick = {},
                    onRetry = {},
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        }
    }

    @Test
    fun favoritesSectionErrorState() {
        composeTestRule.captureMultiDevice("FavoritesSectionErrorState") {
            TvManiacBackground {
                FavoritesSection(
                    favorites = SectionState.Error(UiMessage(message = "Failed to load favorites")),
                    title = "Favorites",
                    retryLabel = "Retry",
                    onShowClick = {},
                    onRetry = {},
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        }
    }
}
