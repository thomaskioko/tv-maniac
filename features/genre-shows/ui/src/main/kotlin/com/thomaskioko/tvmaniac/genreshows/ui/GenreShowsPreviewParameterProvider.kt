package com.thomaskioko.tvmaniac.genreshows.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.genreshows.presentation.GenreShow
import com.thomaskioko.tvmaniac.genreshows.presentation.GenreShowsState
import kotlinx.coroutines.flow.flowOf

private const val GENRE_TITLE = "Drama"

public val genreShowList: List<GenreShow> = List(6) { index ->
    GenreShow(
        tmdbId = index.toLong(),
        showId = index.toLong(),
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        inLibrary = index % 2 == 0,
    )
}

internal val genreShowsLoadedState: GenreShowsState = GenreShowsState(
    title = GENRE_TITLE,
    pagingDataFlow = flowOf(PagingData.from(genreShowList)),
)

internal val genreShowsAppendLoadingState: GenreShowsState = GenreShowsState(
    title = GENRE_TITLE,
    pagingDataFlow = flowOf(
        PagingData.from(
            data = genreShowList,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.Loading,
            ),
        ),
    ),
    isAppendLoading = true,
)

internal val genreShowsAppendErrorState: GenreShowsState = GenreShowsState(
    title = GENRE_TITLE,
    pagingDataFlow = flowOf(
        PagingData.from(
            data = genreShowList,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.Error(RuntimeException("Couldn't load more shows.")),
            ),
        ),
    ),
    appendError = "Couldn't load more shows.",
)

internal val genreShowsEmptyState: GenreShowsState = GenreShowsState(
    title = GENRE_TITLE,
    pagingDataFlow = flowOf(
        PagingData.from(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        ),
    ),
)

internal class GenreShowsPreviewParameterProvider : PreviewParameterProvider<GenreShowsState> {
    override val values: Sequence<GenreShowsState>
        get() = sequenceOf(
            genreShowsLoadedState,
            genreShowsAppendLoadingState,
            genreShowsAppendErrorState,
        )
}
