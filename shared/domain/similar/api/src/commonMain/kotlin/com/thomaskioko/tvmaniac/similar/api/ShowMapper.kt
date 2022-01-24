package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.datasource.cache.SelectSimilarShows
import com.thomaskioko.tvmaniac.discover.api.model.TvShow

fun List<SelectSimilarShows>.toTvShowList(): List<TvShow> {
    return map { it.toTvShow() }
}

fun SelectSimilarShows.toTvShow(): TvShow {
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
