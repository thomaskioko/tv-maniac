package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.shows.api.model.TvShow

fun List<SelectShowsByCategory>.toTvShowList(): List<TvShow> {
    return map { it.toTvShow() }
}

fun SelectShowsByCategory.toTvShow(): TvShow {
    return TvShow(
        traktId = trakt_id,
        title = title,
        overview = overview,
        language = language,
        posterImageUrl = poster_url?.toImageUrl(),
        backdropImageUrl = backdrop_url?.toImageUrl(),
        votes = votes,
        rating = rating,
        genres = genres,
        year = year,
        status = status,
    )
}

fun String.toImageUrl() = FormatterUtil.formatPosterPath(this)