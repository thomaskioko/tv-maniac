package com.thomaskioko.tvmaniac.profile.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileListItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileRecentItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileShowItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import kotlinx.collections.immutable.persistentListOf

private val sampleShows = persistentListOf(
    ProfileShowItem(traktId = 1, tmdbId = 1396, title = "Breaking Bad", posterUrl = null),
    ProfileShowItem(traktId = 2, tmdbId = 1399, title = "Game of Thrones", posterUrl = null),
    ProfileShowItem(traktId = 3, tmdbId = 66732, title = "Stranger Things", posterUrl = null),
)

internal val unauthenticatedState = ProfileState(
    isLoading = false,
    userProfile = null,
    errorMessage = null,
    authenticated = false,
)

internal val authenticatedState = ProfileState(
    isLoading = false,
    userProfile = ProfileInfo(
        slug = "testuser",
        username = "testuser",
        fullName = "Test User",
        avatarUrl = null,
        stats = ProfileStats(
            showsWatched = 42,
            episodesWatched = 256,
            years = 0,
            months = 0,
            days = 5,
            hours = 12,
            minutes = 30,
        ),
        backgroundUrl = null,
    ),
    errorMessage = null,
    authenticated = true,
    userLists = SectionState.Content(
        persistentListOf(
            ProfileListItem(
                id = 1,
                name = "Watchlist",
                itemCount = 24,
                posterUrls = persistentListOf("/poster1.jpg", "/poster2.jpg", "/poster3.jpg", "/poster4.jpg"),
            ),
            ProfileListItem(
                id = 2,
                name = "Favorites",
                itemCount = 12,
                posterUrls = persistentListOf("/poster5.jpg", "/poster6.jpg"),
            ),
        ),
    ),
    inProgress = SectionState.Content(sampleShows),
    recentlyWatched = SectionState.Content(
        persistentListOf(
            ProfileRecentItem(traktId = 1, tmdbId = 1396, title = "Breaking Bad", posterUrl = null, episodeLabel = "S5E14"),
            ProfileRecentItem(traktId = 2, tmdbId = 1399, title = "Game of Thrones", posterUrl = null, episodeLabel = "S8E3"),
        ),
    ),
    library = SectionState.Content(sampleShows),
    watchlist = SectionState.Content(sampleShows),
    favorites = SectionState.Content(sampleShows),
)

internal class ProfilePreviewParameterProvider : PreviewParameterProvider<ProfileState> {
    override val values: Sequence<ProfileState>
        get() {
            return sequenceOf(
                unauthenticatedState,
                authenticatedState,
            )
        }
}
