package com.thomaskioko.tvmaniac.genreshows.presentation

import androidx.paging.PagingData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

public data class GenreShowsState(
    val title: String,
    val pagingDataFlow: Flow<PagingData<GenreShow>> = emptyFlow(),
    val items: ImmutableList<GenreShow> = persistentListOf(),
    val isRefreshLoading: Boolean = false,
    val isAppendLoading: Boolean = false,
    val hasNextPage: Boolean = true,
    val appendError: String? = null,
    val errorMessage: String? = null,
)

public data class GenreShow(
    val tmdbId: Long,
    val showId: Long,
    val title: String,
    val posterImageUrl: String?,
    val inLibrary: Boolean,
)
