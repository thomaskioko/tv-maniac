package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow.WEEK
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory.POPULAR_TV_SHOWS
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory.TOP_RATED_TV_SHOWS
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory.TRENDING
import com.thomaskioko.tvmaniac.datasource.mapper.toShow
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowsEntityList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.presentation.model.TvShow

class TvShowsRepositoryImpl(
    private val apiService: TvShowsService,
    private val cache: TvShowCache
) : TvShowsRepository {

    override suspend fun getTvShow(tvShowId: Int): TvShow {
        return cache.getTvShow(tvShowId)
            .toTvShowsEntity()
    }

    override suspend fun getPopularTvShows(page: Int): List<TvShow> {
        return if (getShowsByCategory(POPULAR_TV_SHOWS).isEmpty()) {

            val entityList = apiService.getPopularShows(page).results
                .map { it.toShow() }
                .map {
                    it.copy(
                        show_category = POPULAR_TV_SHOWS
                    )
                }

            cache.insert(entityList)

            getShowsByCategory(POPULAR_TV_SHOWS)
        } else {
            getShowsByCategory(POPULAR_TV_SHOWS)
        }
    }

    override suspend fun getTopRatedTvShows(page: Int): List<TvShow> {
        return if (cache.getTvShows().isEmpty()) {

            apiService.getTopRatedShows(page).results
                .map { it.toShow() }
                .map {
                    it.copy(
                        show_category = TOP_RATED_TV_SHOWS
                    )
                }
                .map { cache.insert(it) }

            getShowsByCategory(TOP_RATED_TV_SHOWS)
        } else {
            getShowsByCategory(TOP_RATED_TV_SHOWS)
        }
    }

    override suspend fun getTrendingShows(
        timeWindow: String
    ): List<TvShow> {
        return if (getShowsByCategoryAndWindow(TRENDING, TimeWindow[timeWindow]).isEmpty()) {

            apiService.getTrendingShows(timeWindow).results
                .map { it.toShow() }
                .map {
                    it.copy(
                        show_category = TRENDING,
                        time_window = TimeWindow[timeWindow]
                    )
                }
                .map { cache.insert(it) }

            getShowsByCategoryAndWindow(TRENDING, TimeWindow[timeWindow])
        } else {
            getShowsByCategoryAndWindow(TRENDING, TimeWindow[timeWindow])
        }
    }

    override suspend fun getFeaturedShows(): List<TvShow> {
        return if (getShowsByCategoryAndWindow(FEATURED, WEEK).isEmpty()) {

            apiService.getTrendingShows(WEEK.window).results
                .map { it.toShow() }
                .map {
                    it.copy(
                        show_category = FEATURED,
                        time_window = WEEK
                    )
                }
                .map { cache.insert(it) }

            cache.getFeaturedTvShows(FEATURED, WEEK).toTvShowsEntityList()
        } else {
            cache.getFeaturedTvShows(FEATURED, WEEK).toTvShowsEntityList()
        }
    }

    override suspend fun getShowsByCategoryAndWindow(
        category: TvShowCategory,
        timeWindow: TimeWindow
    ): List<TvShow> {
        return cache.getTvShows(category, timeWindow)
            .toTvShowsEntityList()
    }

    private fun getShowsByCategory(category: TvShowCategory): List<TvShow> =
        cache.getTvShows()
            .toTvShowsEntityList()
            .filter { it.showCategory == category }

}
