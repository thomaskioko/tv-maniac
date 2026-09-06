package com.thomaskioko.tvmaniac.lists.presenter

import com.thomaskioko.tvmaniac.lists.presenter.model.UserListItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ListsState(
    val title: String,
    val emptyMessage: String,
    val isLoading: Boolean = true,
    val lists: ImmutableList<UserListItem> = persistentListOf(),
    val errorMessage: String? = null,
)
