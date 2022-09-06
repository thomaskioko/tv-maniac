package com.thomaskioko.tvmaniac.discover.api.mapper

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow

fun List<Show>.toTvShowList(): List<TvShow> {
    return map { it.toTvShow() }
}

fun Show.toTvShow(): TvShow {
    return TvShow(
        traktId = trakt_id,
        title = title,
        overview = overview,
        language = language,
        posterImageUrl = poster_image_url,
        backdropImageUrl = backdrop_image_url,
        votes = votes,
        rating = rating,
        genres = genres,
        year = year,
        status = status,
    )
}
