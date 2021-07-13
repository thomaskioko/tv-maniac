package com.thomaskioko.tvmaniac.datasource.cache.db

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity

interface TvShowCache {

    fun insert(entity: TvShowsEntity)

    fun insert(entityList: List<TvShowsEntity>)

    @Throws(NullPointerException::class)
    fun getTvShow(showId: Int): TvShowsEntity?

    fun getTvShows(): List<TvShowsEntity>

    fun deleteTvShows()
}