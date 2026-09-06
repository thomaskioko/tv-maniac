package com.thomaskioko.tvmaniac.lists.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailState
import com.thomaskioko.tvmaniac.lists.presenter.model.ListShow
import com.thomaskioko.tvmaniac.lists.presenter.model.RemoveConfirmation
import kotlinx.coroutines.flow.flowOf

private const val LIST_TITLE = "Comfort Watches"
private const val LIST_EMPTY_MESSAGE = "No shows in this list yet."

internal val listDetailShows: List<ListShow> = listOf(
    ListShow(tmdbId = 1396L, title = "Breaking Bad", posterUrl = "/breaking-bad.jpg"),
    ListShow(tmdbId = 60059L, title = "Better Call Saul", posterUrl = null),
)

internal val listDetailLoadingState: ListDetailState = ListDetailState(
    title = LIST_TITLE,
    emptyMessage = LIST_EMPTY_MESSAGE,
    pagingDataFlow = flowOf(
        PagingData.from(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        ),
    ),
)

private val idlePage: PagingData<ListShow> = PagingData.from(
    data = emptyList(),
    sourceLoadStates = LoadStates(
        refresh = LoadState.NotLoading(endOfPaginationReached = true),
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true),
    ),
)

internal val listDetailEmptyState: ListDetailState = ListDetailState(
    title = LIST_TITLE,
    emptyMessage = LIST_EMPTY_MESSAGE,
    pagingDataFlow = flowOf(idlePage),
    isRefreshLoading = false,
)

internal val listDetailErrorState: ListDetailState = ListDetailState(
    title = LIST_TITLE,
    emptyMessage = LIST_EMPTY_MESSAGE,
    pagingDataFlow = flowOf(idlePage),
    isRefreshLoading = false,
    errorMessage = "Something went wrong",
)

internal val listDetailContentState: ListDetailState = ListDetailState(
    title = LIST_TITLE,
    emptyMessage = LIST_EMPTY_MESSAGE,
    pagingDataFlow = flowOf(PagingData.from(listDetailShows)),
)

internal val listDetailLoadMoreErrorState: ListDetailState = ListDetailState(
    title = LIST_TITLE,
    emptyMessage = LIST_EMPTY_MESSAGE,
    pagingDataFlow = flowOf(
        PagingData.from(
            data = listDetailShows,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.Error(RuntimeException("Couldn't load more shows.")),
            ),
        ),
    ),
    appendError = "Couldn't load more shows.",
)

internal val listDetailRemoveConfirmationState: ListDetailState = listDetailContentState.copy(
    removeConfirmation = RemoveConfirmation(
        tmdbId = 1396L,
        title = "Remove from list?",
        message = "Remove Breaking Bad from Comfort Watches?",
        confirmLabel = "Remove",
    ),
)

internal class ListDetailPreviewParameterProvider : PreviewParameterProvider<ListDetailState> {
    override val values: Sequence<ListDetailState>
        get() = sequenceOf(
            listDetailLoadingState,
            listDetailEmptyState,
            listDetailErrorState,
            listDetailContentState,
            listDetailLoadMoreErrorState,
            listDetailRemoveConfirmationState,
        )
}
