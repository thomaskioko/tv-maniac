package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowCategory
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import com.thomaskioko.tvmaniac.datasource.network.model.ShowResponse

fun ShowResponse.toTvShowEntityList(showCategory: TvShowCategory): TvShowsEntity {
    return TvShowsEntity(
        showId = id,
        title = name,
        description = overview,
        language = originalLanguage,
        imageUrl = backdropPath ?: poster_Path,
        votes = voteCount,
        averageVotes = voteAverage,
        genreIds = genreIds,
        showCategory = showCategory
    )
}
