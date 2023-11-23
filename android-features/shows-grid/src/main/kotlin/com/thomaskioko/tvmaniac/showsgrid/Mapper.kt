package com.thomaskioko.tvmaniac.showsgrid

import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.showsgrid.model.TvShow

fun ShowsByCategory.toTvShow(): TvShow = TvShow(
    traktId = id.id,
    tmdbId = tmdb_id,
    title = title,
    posterImageUrl = poster_url,
    backdropImageUrl = backdrop_url,
)
