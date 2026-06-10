package com.thomaskioko.tvmaniac.showlist.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthProviderOption
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListCopy
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListState
import com.thomaskioko.tvmaniac.presentation.showlist.model.TraktListModel
import kotlinx.collections.immutable.persistentListOf

private val previewCopy = ShowListCopy(
    sheetTitle = "Add to …",
    createListButtonText = "Create a List",
    createListDoneText = "Create",
    createListPlaceholder = "New list name",
    emptyListText = "You don't have any lists yet.",
    listsHeaderText = "Your Lists",
    loginRequiredTitle = "Login Required",
    loginRequiredMessage = "Please log in to manage your lists.",
)

internal val loggedOutState = ShowListState(
    isLoggedIn = false,
    labels = previewCopy,
    authProviders = persistentListOf(
        AuthProviderOption(AccountProvider.TRAKT, "Continue with Trakt"),
        AuthProviderOption(AccountProvider.SIMKL, "Continue with Simkl"),
    ),
)

internal val loggedInLoading = ShowListState(
    isLoggedIn = true,
    isLoading = true,
    labels = previewCopy,
)

internal val loggedInWithLists = ShowListState(
    isLoggedIn = true,
    isLoading = false,
    labels = previewCopy,
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
    isLoading = false,
    traktLists = persistentListOf(),
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

internal val loggedInWithToggleInFlight = loggedInWithLists.copy(
    traktLists = persistentListOf(
        TraktListModel(
            id = 1L,
            slug = "favorites",
            name = "Favorites",
            description = "My favorite shows",
            showCountText = "12 shows",
            isShowInList = true,
            isToggling = true,
        ),
        TraktListModel(
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
            loggedOutState,
            loggedInLoading,
            loggedInEmpty,
            loggedInWithLists,
            loggedInWithCreateField,
            loggedInWithCreateLoading,
            loggedInWithToggleInFlight,
        )
}
