package com.thomaskioko.tvmaniac.datasource.repository

import com.thomaskioko.tvmaniac.datasource.cache.db.TvShowCache
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowCategory
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowCategory.POPULAR_TV_SHOWS
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowCategory.TOP_RATED_TV_SHOWS
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowEntityList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService

class TvShowsRepositoryImpl(
    private val apiService: TvShowsService,
    private val cache: TvShowCache
) : TvShowsRepository {

    override suspend fun getPopularTvShows(page: Int): List<TvShowsEntity> {
        return if (cache.getTvShows().isEmpty()) {

            val entityList = apiService.getPopularShows(page).results
                .map { it.toTvShowEntityList(POPULAR_TV_SHOWS) }

            cache.insert(entityList)

            getShowsByCategory(POPULAR_TV_SHOWS)
        } else {
            getShowsByCategory(POPULAR_TV_SHOWS)
        }
    }

    override suspend fun getTopRatedTvShows(page: Int): List<TvShowsEntity> {
        return if (cache.getTvShows().isEmpty()) {

            apiService.getTopRatedShows(page).results
                .map { it.toTvShowEntityList(TOP_RATED_TV_SHOWS) }
                .map { cache.insert(it) }

            getShowsByCategory(TOP_RATED_TV_SHOWS)
        } else {
            getShowsByCategory(TOP_RATED_TV_SHOWS)
        }
    }

    private fun getShowsByCategory(category: TvShowCategory): List<TvShowsEntity> =
        cache.getTvShows()
            .filter { it.showCategory == category }

}
