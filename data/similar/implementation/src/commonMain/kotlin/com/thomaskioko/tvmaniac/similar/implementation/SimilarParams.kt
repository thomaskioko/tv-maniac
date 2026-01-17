package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE

public data class SimilarParams(
    val page: Long = DEFAULT_API_PAGE,
    val showTraktId: Long,
)
