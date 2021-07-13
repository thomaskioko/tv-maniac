package com.thomaskioko.tvmaniac.datasource.cache.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Tv_show
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity

object ShowEntityMapper {

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
}