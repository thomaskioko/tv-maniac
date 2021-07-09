package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.TvShowsEntity
import com.thomaskioko.tvmaniac.datasource.network.model.ShowResponse

fun ShowResponse.toTvShowEntityList(): TvShowsEntity {
    return TvShowsEntity(
        showId = id,
        title = name,
        description = overview,
        language = originalLanguage,
        imageUrl = backdropPath ?: poster_Path,
        votes = voteCount,
        averageVotes = voteAverage,
        genreIds = genreIds
    )
}
