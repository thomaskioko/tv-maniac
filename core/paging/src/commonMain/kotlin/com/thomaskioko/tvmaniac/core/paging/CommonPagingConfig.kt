package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.PagingConfig
import kotlin.time.Duration.Companion.hours

object CommonPagingConfig {

  val CACHE_EXPIRE_TIME = 6.hours
  private const val PREFETCH_DISTANCE = 1
  private const val PAGE_SIZE = 20

  val pagingConfig =
    PagingConfig(
      pageSize = PAGE_SIZE,
      initialLoadSize = PAGE_SIZE,
      prefetchDistance = PREFETCH_DISTANCE,
    )
}
