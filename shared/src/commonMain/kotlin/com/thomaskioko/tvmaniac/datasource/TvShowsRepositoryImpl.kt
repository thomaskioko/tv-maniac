package com.thomaskioko.tvmaniac.datasource

import com.thomaskioko.tvmaniac.datasource.cache.TvShowsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowEntityList
import com.thomaskioko.tvmaniac.datasource.network.TvShowsService

class TvShowsRepositoryImpl(
    private val tvShowsService: TvShowsService
) : TvShowsRepository {

    override suspend fun getPopularTvShows(page: Int): List<TvShowsEntity> {

        return tvShowsService.getPopularShows(page).results
            .map { it.toTvShowEntityList() }

    }

    override suspend fun getTopRatedTvShows(page: Int): List<TvShowsEntity> {
        return tvShowsService.getTopRatedShows(page).results
            .map { it.toTvShowEntityList() }
    }

}
