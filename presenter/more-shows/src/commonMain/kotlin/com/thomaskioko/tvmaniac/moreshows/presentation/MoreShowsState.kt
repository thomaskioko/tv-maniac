package com.thomaskioko.tvmaniac.moreshows.presentation

import androidx.paging.PagingData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

public data class MoreShowsState(
    val categoryTitle: String? = null,
    val pagingDataFlow: Flow<PagingData<TvShow>> = emptyFlow(),
    val items: ImmutableList<TvShow> = persistentListOf(),
    val isRefreshLoading: Boolean = false,
    val isAppendLoading: Boolean = false,
    val appendError: String? = null,
    val errorMessage: String? = null,
)
