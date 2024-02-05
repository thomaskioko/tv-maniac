package com.thomaskioko.tvmaniac.util.paging

import androidx.paging.PagingConfig

object CommonPagingConfig {

  private const val PREFETCH_DISTANCE = 1
  private const val PAGE_SIZE = 20

  val pagingConfig =
    PagingConfig(
      pageSize = PAGE_SIZE,
      initialLoadSize = PAGE_SIZE,
      prefetchDistance = PREFETCH_DISTANCE,
    )
}
