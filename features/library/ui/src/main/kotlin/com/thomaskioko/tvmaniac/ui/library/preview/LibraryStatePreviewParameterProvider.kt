package com.thomaskioko.tvmaniac.ui.library.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.library.LibraryState
import com.thomaskioko.tvmaniac.presentation.library.model.LibraryShowItem
import com.thomaskioko.tvmaniac.presentation.library.model.ShowStatus
import com.thomaskioko.tvmaniac.presentation.library.model.WatchProviderUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

internal class LibraryStatePreviewParameterProvider : PreviewParameterProvider<LibraryState> {
    override val values: Sequence<LibraryState> = sequenceOf(
        LibraryState(
            isRefreshing = true,
            items = persistentListOf(),
        ),
        LibraryState(
            isRefreshing = false,
            items = persistentListOf(),
        ),
        LibraryState(
            isRefreshing = false,
            isGridMode = true,
            items = previewLibraryItems(),
            availableGenres = previewGenres(),
            availableStatuses = previewStatuses(),
        ),
        LibraryState(
            isRefreshing = false,
            isGridMode = false,
            items = previewLibraryItems(),
            availableGenres = previewGenres(),
            availableStatuses = previewStatuses(),
            selectedGenres = persistentSetOf("Drama", "Crime"),
            selectedStatuses = persistentSetOf(ShowStatus.ENDED),
        ),
        LibraryState(
            isRefreshing = true,
            isGridMode = true,
            items = previewLibraryItems(),
        ),
        LibraryState(
            query = "Breaking",
            isSearchActive = true,
            isRefreshing = false,
            isGridMode = false,
            items = persistentListOf(
                LibraryShowItem(
                    traktId = 1,
                    tmdbId = 1396,
                    title = "Breaking Bad",
                    posterImageUrl = null,
                    status = "Ended",
                    year = "2008",
                    rating = 9.5,
                    genres = listOf("Drama", "Crime"),
                    seasonCount = 5,
                    episodeCount = 62,
                    isFollowed = true,
                    watchProviders = persistentListOf(),
                ),
            ),
        ),
    )
}

private fun previewGenres() = persistentListOf(
    "Action & Adventure",
    "Animation",
    "Comedy",
    "Crime",
    "Drama",
    "Fantasy",
    "Sci-Fi",
)

private fun previewStatuses() = persistentListOf(
    ShowStatus.RETURNING_SERIES,
    ShowStatus.PLANNED,
    ShowStatus.IN_PRODUCTION,
    ShowStatus.ENDED,
    ShowStatus.CANCELED,
)

private fun previewLibraryItems(): ImmutableList<LibraryShowItem> = listOf(
    LibraryShowItem(
        traktId = 1,
        tmdbId = 1396,
        title = "Breaking Bad",
        posterImageUrl = null,
        status = "Ended",
        year = "2008",
        rating = 9.5,
        genres = listOf("Drama", "Crime"),
        seasonCount = 5,
        episodeCount = 62,
        isFollowed = true,
        watchProviders = persistentListOf(
            WatchProviderUiModel(id = 1, name = "Netflix", logoUrl = null),
            WatchProviderUiModel(id = 2, name = "Amazon Prime", logoUrl = null),
        ),
    ),
    LibraryShowItem(
        traktId = 2,
        tmdbId = 1399,
        title = "Game of Thrones",
        posterImageUrl = null,
        status = "Ended",
        year = "2011",
        rating = 9.2,
        genres = listOf("Drama", "Fantasy"),
        seasonCount = 8,
        episodeCount = 73,
        isFollowed = true,
        watchProviders = persistentListOf(
            WatchProviderUiModel(id = 3, name = "HBO Max", logoUrl = null),
        ),
    ),
    LibraryShowItem(
        traktId = 3,
        tmdbId = 66732,
        title = "Stranger Things",
        posterImageUrl = null,
        status = "Returning Series",
        year = "2016",
        rating = 8.7,
        genres = listOf("Drama", "Sci-Fi"),
        seasonCount = 4,
        episodeCount = 34,
        isFollowed = true,
        watchProviders = persistentListOf(
            WatchProviderUiModel(id = 1, name = "Netflix", logoUrl = null),
        ),
    ),
    LibraryShowItem(
        traktId = 4,
        tmdbId = 94997,
        title = "House of the Dragon",
        posterImageUrl = null,
        status = "Returning Series",
        year = "2022",
        rating = 8.4,
        genres = listOf("Drama", "Fantasy"),
        seasonCount = 2,
        episodeCount = 18,
        isFollowed = false,
        watchProviders = persistentListOf(),
    ),
    LibraryShowItem(
        traktId = 5,
        tmdbId = 60625,
        title = "Rick and Morty",
        posterImageUrl = null,
        status = "Returning Series",
        year = "2013",
        rating = 9.1,
        genres = listOf("Animation", "Comedy"),
        seasonCount = 7,
        episodeCount = 71,
        isFollowed = true,
        watchProviders = persistentListOf(),
    ),
).toImmutableList()
