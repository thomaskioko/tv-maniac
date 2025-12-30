package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.PagingConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public object CommonPagingConfig {

    public val CACHE_EXPIRE_TIME: Duration = 6.hours
    private const val PREFETCH_DISTANCE = 1
    private const val PAGE_SIZE = 20

    public val pagingConfig: PagingConfig = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE,
        prefetchDistance = PREFETCH_DISTANCE,
    )
}
