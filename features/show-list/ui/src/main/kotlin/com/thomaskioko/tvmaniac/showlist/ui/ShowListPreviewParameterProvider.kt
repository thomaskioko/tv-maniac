package com.thomaskioko.tvmaniac.showlist.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListCopy
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListState
import com.thomaskioko.tvmaniac.presentation.showlist.model.UserListModel
import kotlinx.collections.immutable.persistentListOf

private val previewCopy = ShowListCopy(
    sheetTitle = "Add to …",
    createListButtonText = "Create a List",
    createListDoneText = "Create",
    createListPlaceholder = "New list name",
    emptyListText = "You don't have any lists yet.",
    listsHeaderText = "Your Lists",
)

internal val loadingState = ShowListState(
    isLoading = true,
    labels = previewCopy,
)

internal val withLists = ShowListState(
    isLoading = false,
    labels = previewCopy,
    lists = persistentListOf(
        UserListModel(
            id = 1L,
            slug = "favorites",
            name = "Favorites",
            description = "My favorite shows",
            showCountText = "12 shows",
            isShowInList = true,
        ),
        UserListModel(
            id = 2L,
            slug = "watch-later",
            name = "Watch Later",
            description = "Shows to watch later",
            showCountText = "5 shows",
            isShowInList = false,
        ),
        UserListModel(
            id = 3L,
            slug = "sci-fi-marathon",
            name = "Sci-Fi Marathon",
            description = null,
            showCountText = "23 shows",
            isShowInList = true,
        ),
    ),
)

internal val emptyState = withLists.copy(
    isLoading = false,
    lists = persistentListOf(),
)

internal val withCreateField = withLists.copy(
    showCreateListField = true,
    createListName = "My New List",
)

internal val withCreateLoading = withLists.copy(
    showCreateListField = true,
    isCreatingList = true,
    createListName = "Sci-Fi Picks",
)

internal val withToggleInFlight = withLists.copy(
    lists = persistentListOf(
        UserListModel(
            id = 1L,
            slug = "favorites",
            name = "Favorites",
            description = "My favorite shows",
            showCountText = "12 shows",
            isShowInList = true,
            isToggling = true,
        ),
        UserListModel(
            id = 2L,
            slug = "watch-later",
            name = "Watch Later",
            description = "Shows to watch later",
            showCountText = "5 shows",
            isShowInList = false,
        ),
    ),
)

internal class ShowListPreviewParameterProvider : PreviewParameterProvider<ShowListState> {
    override val values: Sequence<ShowListState>
        get() = sequenceOf(
            loadingState,
            emptyState,
            withLists,
            withCreateField,
            withCreateLoading,
            withToggleInFlight,
        )
}
