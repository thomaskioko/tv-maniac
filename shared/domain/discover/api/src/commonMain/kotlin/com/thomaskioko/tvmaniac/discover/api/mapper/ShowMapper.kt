package com.thomaskioko.tvmaniac.discover.api.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow

fun List<Show>.toTvShowList(): List<TvShow> {
    return map { it.toTvShow() }
}

fun Show.toTvShow(): TvShow {
    return TvShow(
        id = id,
        title = title,
        overview = description,
        language = language,
        posterImageUrl = poster_image_url,
        backdropImageUrl = backdrop_image_url,
        votes = votes.toInt(),
        averageVotes = vote_average,
        genreIds = genre_ids,
        year = year,
        status = status,
        following = following
    )
}
