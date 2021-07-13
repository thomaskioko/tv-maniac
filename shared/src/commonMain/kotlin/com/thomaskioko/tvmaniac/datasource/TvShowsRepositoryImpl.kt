package com.thomaskioko.tvmaniac.datasource

import com.thomaskioko.tvmaniac.datasource.cache.db.TvShowCache
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowCategory
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowEntityList
import com.thomaskioko.tvmaniac.datasource.network.TvShowsService

class TvShowsRepositoryImpl(
    private val tvShowsService: TvShowsService,
    private val cache: TvShowCache
) : TvShowsRepository {

    override suspend fun getPopularTvShows(page: Int): List<TvShowsEntity>  {
        return if (cache.getTvShows().isEmpty()) {

            tvShowsService.getPopularShows(page).results
                .map { it.toTvShowEntityList(TvShowCategory.POPULAR_TV_SHOWS) }
                .map { cache.insert(it) }

           cache.getTvShows()
               .filter { it.showCategory == TvShowCategory.POPULAR_TV_SHOWS }
        } else {
         cache.getTvShows()
             .filter { it.showCategory == TvShowCategory.POPULAR_TV_SHOWS }
        }
    }

    override suspend fun getTopRatedTvShows(page: Int): List<TvShowsEntity> {
        return if (cache.getTvShows().isEmpty()) {

            tvShowsService.getTopRatedShows(page).results
                .map { it.toTvShowEntityList(TvShowCategory.POPULAR_TV_SHOWS) }
                .map { cache.insert(it) }

            cache.getTvShows()
                .filter { it.showCategory == TvShowCategory.TOP_RATED_TV_SHOWS }
        } else {
            cache.getTvShows()
                .filter { it.showCategory == TvShowCategory.TOP_RATED_TV_SHOWS }
        }
    }

}
