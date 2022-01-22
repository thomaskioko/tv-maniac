package com.thomaskioko.tvmaniac.discover.api.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel

fun List<Show>.toTvShowList(): List<ShowUiModel> {
    return map { it.toTvShow() }
}

fun Show.toTvShow(): ShowUiModel {
    return ShowUiModel(
        id = id.toInt(),
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
