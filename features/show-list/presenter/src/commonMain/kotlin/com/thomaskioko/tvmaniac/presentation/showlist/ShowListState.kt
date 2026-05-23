package com.thomaskioko.tvmaniac.presentation.showlist

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presentation.showlist.model.TraktListModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ShowListState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = true,
    val traktLists: ImmutableList<TraktListModel> = persistentListOf(),
    val showCreateListField: Boolean = false,
    val isCreatingList: Boolean = false,
    val createListName: String = "",
    val createListError: String? = null,
    val labels: ShowListCopy = ShowListCopy(),
    val message: UiMessage? = null,
)

public data class ShowListCopy(
    val sheetTitle: String = "",
    val createListButtonText: String = "",
    val createListDoneText: String = "",
    val createListPlaceholder: String = "",
    val emptyListText: String = "",
    val listsHeaderText: String = "",
    val loginRequiredTitle: String = "",
    val loginRequiredMessage: String = "",
    val loginRequiredConfirmText: String = "",
)
