package com.thomaskioko.tvmaniac.lists.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.lists.presenter.ListsState
import com.thomaskioko.tvmaniac.lists.presenter.model.UserListItem
import kotlinx.collections.immutable.persistentListOf

private const val LISTS_TITLE = "Lists"
private const val LISTS_EMPTY_MESSAGE = "You don't have any lists yet."

internal val loadingState: ListsState = ListsState(
    title = LISTS_TITLE,
    emptyMessage = LISTS_EMPTY_MESSAGE,
    isLoading = true,
)

internal val emptyState: ListsState = ListsState(
    title = LISTS_TITLE,
    emptyMessage = LISTS_EMPTY_MESSAGE,
    isLoading = false,
)

internal val contentState: ListsState = ListsState(
    title = LISTS_TITLE,
    emptyMessage = LISTS_EMPTY_MESSAGE,
    isLoading = false,
    lists = persistentListOf(
        UserListItem(
            id = 1,
            name = "Watchlist",
            itemCount = 24,
            itemCountLabel = "24 shows",
            posterUrls = persistentListOf("/a.jpg", "/b.jpg", "/c.jpg", "/d.jpg"),
        ),
        UserListItem(
            id = 2,
            name = "Anime",
            itemCount = 0,
            itemCountLabel = "0 shows",
            posterUrls = persistentListOf(),
        ),
    ),
)

internal val errorState: ListsState = emptyState.copy(
    errorMessage = "Something went wrong",
)

internal class ListsPreviewParameterProvider : PreviewParameterProvider<ListsState> {
    override val values: Sequence<ListsState>
        get() = sequenceOf(loadingState, emptyState, contentState, errorState)
}
