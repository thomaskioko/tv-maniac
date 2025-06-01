package com.thomaskioko.tvmaniac.presentation.moreshows

import androidx.paging.ItemSnapshotList
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class MoreShowsState(
    val isLoading: Boolean = false,
    val categoryTitle: String? = null,
    val pagingDataFlow: Flow<PagingData<TvShow>> = emptyFlow(),
    val snapshotList: ItemSnapshotList<TvShow> = ItemSnapshotList(0, 0, emptyList()),
    val errorMessage: String? = null,
)
