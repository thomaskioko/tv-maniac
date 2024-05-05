package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.PagingConfig
import kotlin.time.Duration.Companion.hours

val CACHE_EXPIRE_TIME = 6.hours

object CommonPagingConfig {

  private const val PREFETCH_DISTANCE = 2
  private const val PAGE_SIZE = 20

  val pagingConfig =
    PagingConfig(
      pageSize = PAGE_SIZE,
      initialLoadSize = PAGE_SIZE,
      prefetchDistance = PREFETCH_DISTANCE,
    )
}
