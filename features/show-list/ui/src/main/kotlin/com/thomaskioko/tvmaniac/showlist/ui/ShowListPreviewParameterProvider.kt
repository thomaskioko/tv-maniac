package com.thomaskioko.tvmaniac.showlist.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListState
import com.thomaskioko.tvmaniac.presentation.showlist.model.TraktListModel
import kotlinx.collections.immutable.persistentListOf

internal val loggedOutState = ShowListState(
    isLoggedIn = false,
    sheetTitle = "Add to …",
    loginRequiredTitle = "Login Required",
    loginRequiredMessage = "Please log in with Trakt to manage your lists.",
    loginRequiredConfirmText = "OK",
)

internal val loggedInWithLists = ShowListState(
    isLoggedIn = true,
    sheetTitle = "Add to …",
    listsHeaderText = "Your Lists",
    createListButtonText = "Create a List",
    createListDoneText = "Create",
    createListPlaceholder = "New list name",
    traktLists = persistentListOf(
        TraktListModel(
            id = 1L,
            slug = "favorites",
            name = "Favorites",
            description = "My favorite shows",
            showCountText = "12 shows",
            isShowInList = true,
        ),
        TraktListModel(
            id = 2L,
            slug = "watch-later",
            name = "Watch Later",
            description = "Shows to watch later",
            showCountText = "5 shows",
            isShowInList = false,
        ),
        TraktListModel(
            id = 3L,
            slug = "sci-fi-marathon",
            name = "Sci-Fi Marathon",
            description = null,
            showCountText = "23 shows",
            isShowInList = true,
        ),
    ),
)

internal val loggedInEmpty = loggedInWithLists.copy(
    traktLists = persistentListOf(),
    emptyListText = "You don't have any lists yet.",
)

internal val loggedInWithCreateField = loggedInWithLists.copy(
    showCreateListField = true,
    createListName = "My New List",
)

internal val loggedInWithCreateLoading = loggedInWithLists.copy(
    showCreateListField = true,
    isCreatingList = true,
    createListName = "Sci-Fi Picks",
)

internal class ShowListPreviewParameterProvider : PreviewParameterProvider<ShowListState> {
    override val values: Sequence<ShowListState>
        get() = sequenceOf(
            loggedOutState,
            loggedInEmpty,
            loggedInWithLists,
            loggedInWithCreateField,
            loggedInWithCreateLoading,
        )
}
