package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.shows.api.model.DEFAULT_API_PAGE

public data class SimilarParams(
    val page: Long = DEFAULT_API_PAGE,
    val showId: Long,
)
