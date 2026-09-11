package com.thomaskioko.tvmaniac.lists.presenter

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.lists.presenter.model.DeleteConfirmation
import com.thomaskioko.tvmaniac.lists.presenter.model.ListShow
import com.thomaskioko.tvmaniac.lists.presenter.model.RemoveConfirmation
import com.thomaskioko.tvmaniac.lists.presenter.model.RenameDialog
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

public data class ListDetailState(
    val title: String,
    val emptyMessage: String,
    val renameLabel: String,
    val deleteLabel: String,
    val pagingDataFlow: Flow<PagingData<ListShow>> = emptyFlow(),
    val items: ImmutableList<ListShow> = persistentListOf(),
    val isRefreshLoading: Boolean = true,
    val canRefresh: Boolean = false,
    val isRefreshing: Boolean = false,
    val isAppendLoading: Boolean = false,
    val appendError: String? = null,
    val errorMessage: String? = null,
    val removeConfirmation: RemoveConfirmation? = null,
    val renameDialog: RenameDialog? = null,
    val deleteConfirmation: DeleteConfirmation? = null,
)
