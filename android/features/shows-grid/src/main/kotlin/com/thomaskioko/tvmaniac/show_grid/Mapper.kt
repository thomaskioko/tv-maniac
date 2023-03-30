package com.thomaskioko.tvmaniac.show_grid

import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.show_grid.model.TvShow

fun List<SelectShowsByCategory>.toTvShowList(): List<TvShow> = map { it.toTvShow() }

fun SelectShowsByCategory.toTvShow(): TvShow = TvShow(
    traktId = trakt_id,
    tmdbId = tmdb_id,
    title = title,
    posterImageUrl = poster_url,
    backdropImageUrl = backdrop_url,
)
