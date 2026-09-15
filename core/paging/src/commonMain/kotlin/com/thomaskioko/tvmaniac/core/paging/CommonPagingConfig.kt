package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.PagingConfig
import com.thomaskioko.tvmaniac.shows.api.model.DEFAULT_PAGE_SIZE
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public object CommonPagingConfig {

    public val CACHE_EXPIRE_TIME: Duration = 6.hours
    private const val PREFETCH_DISTANCE = 1

    public val pagingConfig: PagingConfig = PagingConfig(
        pageSize = DEFAULT_PAGE_SIZE,
        initialLoadSize = DEFAULT_PAGE_SIZE,
        prefetchDistance = PREFETCH_DISTANCE,
    )
}
