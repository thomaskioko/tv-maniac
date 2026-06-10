package com.thomaskioko.tvmaniac.profile.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthProviderOption
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileLabels
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileListItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileRecentItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileShowItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import kotlinx.collections.immutable.persistentListOf

internal val sampleProfileLabels: ProfileLabels = ProfileLabels(
    title = "Profile",
    settingsContentDescription = "Settings",
    profilePictureContentDescription = "Profile picture for Test User",
    editButton = "Edit",
    statsTitle = "Stats",
    episodesWatched = "Episodes Watched",
    showsWatched = "Shows Watched",
    watchTime = "Watch Time",
    monthsShort = "M",
    daysShort = "D",
    hoursShort = "H",
    lists = "Lists",
    viewButton = "View",
    userListsTitle = "Your Lists",
    progressTitle = "Progress",
    completedFilter = "Completed",
    inProgressFilter = "In Progress",
    progressEmpty = "Nothing here yet",
    recentlyWatchedTitle = "Recently Watched",
    favoritesTitle = "Favorites",
    viewAllButton = "More",
    retry = "Retry",
    unauthenticatedTitle = "Discover.\nTrack.\nWatchlist.\n& More ...",
    footerDescription = "Sign in to your account to track, discover, and share with the community.",
    featureDiscoverTitle = "Discover",
    featureDiscoverDescription = "Browse over one million movies and TV shows and see \"Where to Watch\" them.",
    featureTrackTitle = "Track",
    featureTrackDescription = "Check-in, mark watch and manage your all-time watch history.",
    featureManageTitle = "Watchlist",
    featureManageDescription = "Create custom list, personalize it just the way you like it.",
    featureMoreTitle = "More",
    featureMoreDescription = "More features coming soon.",
)

private val sampleShows = persistentListOf(
    ProfileShowItem(showId = 1, tmdbId = 1396, title = "Breaking Bad", posterUrl = null),
    ProfileShowItem(showId = 2, tmdbId = 1399, title = "Game of Thrones", posterUrl = null),
    ProfileShowItem(showId = 3, tmdbId = 66732, title = "Stranger Things", posterUrl = null),
)

internal val unauthenticatedState = ProfileState(
    isLoading = false,
    userProfile = null,
    errorMessage = null,
    authenticated = false,
    labels = sampleProfileLabels,
    authProviders = persistentListOf(
        AuthProviderOption(provider = AccountProvider.TRAKT, label = "Continue with Trakt"),
        AuthProviderOption(provider = AccountProvider.SIMKL, label = "Continue with Simkl"),
    ),
)

internal val authenticatedState = ProfileState(
    isLoading = false,
    userProfile = ProfileInfo(
        slug = "testuser",
        username = "testuser",
        fullName = "Test User",
        avatarUrl = null,
        stats = ProfileStats(
            showsWatched = "42",
            episodesWatched = "256",
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
                itemCountLabel = "24 shows",
                posterUrls = persistentListOf(
                    "/poster1.jpg",
                    "/poster2.jpg",
                    "/poster3.jpg",
                    "/poster4.jpg",
                ),
            ),
            ProfileListItem(
                id = 2,
                name = "Favorites",
                itemCount = 12,
                itemCountLabel = "12 shows",
                posterUrls = persistentListOf("/poster5.jpg", "/poster6.jpg"),
            ),
        ),
    ),
    inProgress = SectionState.Content(sampleShows),
    completed = SectionState.Content(sampleShows),
    recentlyWatched = SectionState.Content(
        persistentListOf(
            ProfileRecentItem(
                showId = 1,
                tmdbId = 1396,
                title = "Breaking Bad",
                posterUrl = null,
                episodeLabel = "S5E14",
            ),
            ProfileRecentItem(
                showId = 2,
                tmdbId = 1399,
                title = "Game of Thrones",
                posterUrl = null,
                episodeLabel = "S8E3",
            ),
            ProfileRecentItem(
                showId = 3,
                tmdbId = 1394,
                title = "Stranger Things",
                posterUrl = null,
                episodeLabel = "S1E3",
            ),
        ),
    ),
    library = SectionState.Content(sampleShows),
    watchlist = SectionState.Content(sampleShows),
    favorites = SectionState.Content(sampleShows),
    labels = sampleProfileLabels,
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
