package com.thomaskioko.tvmaniac.ui.library.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.library.model.LibraryShowItem
import com.thomaskioko.tvmaniac.presentation.library.model.WatchProviderUiModel
import kotlinx.collections.immutable.persistentListOf

internal class LibraryListItemPreviewParameterProvider : PreviewParameterProvider<LibraryShowItem> {
    override val values: Sequence<LibraryShowItem> = sequenceOf(
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
            title = "Game of Thrones: A Very Long Title That Should Wrap to Multiple Lines",
            posterImageUrl = null,
            status = "Ended",
            year = "2011",
            rating = 9.2,
            genres = listOf("Drama", "Fantasy"),
            seasonCount = 8,
            episodeCount = 73,
            isFollowed = true,
            watchProviders = persistentListOf(),
        ),
        LibraryShowItem(
            traktId = 3,
            tmdbId = null,
            title = "Minimal Show",
            posterImageUrl = null,
            status = null,
            year = null,
            rating = null,
            genres = null,
            seasonCount = 0,
            episodeCount = 0,
            isFollowed = false,
            watchProviders = persistentListOf(),
        ),
    )
}
