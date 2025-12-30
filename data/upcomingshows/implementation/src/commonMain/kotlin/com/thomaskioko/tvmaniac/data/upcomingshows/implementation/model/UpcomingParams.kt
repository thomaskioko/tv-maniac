package com.thomaskioko.tvmaniac.data.upcomingshows.implementation.model

import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_SORT_ORDER

internal data class UpcomingParams(
    val startDate: String,
    val endDate: String,
    val page: Long,
    val sortBy: String = DEFAULT_SORT_ORDER,
)
