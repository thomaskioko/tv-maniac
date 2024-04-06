package com.thomaskioko.tvmaniac.presentation.moreshows

import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class MoreShowsState(
  val isLoading: Boolean = false,
  val categoryTitle: String? = null,
  val pagingDataFlow: Flow<PagingData<TvShow>> = emptyFlow(),
  val errorMessage: String? = null,
)
