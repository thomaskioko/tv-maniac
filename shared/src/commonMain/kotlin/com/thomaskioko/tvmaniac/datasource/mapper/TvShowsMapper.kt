package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Tv_show
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

fun List<Tv_show>.toTvShowsEntityList(): List<TvShowsEntity> {
    return map { it.toTvShowsEntity() }
}

fun Tv_show.toTvShowsEntity(): TvShowsEntity {
    return TvShowsEntity(
        showId = show_id.toInt(),
        title = title,
        description = description,
        language = language,
        imageUrl = image_url,
        votes = votes.toInt(),
        averageVotes = vote_average,
        genreIds = genre_ids,
        showCategory = show_category
    )
}
