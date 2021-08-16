package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow.WEEK
import com.thomaskioko.tvmaniac.datasource.mapper.toShow
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShow
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TvShowsRepositoryImpl(
    private val apiService: TvShowsService,
    private val cache: TvShowCache
) : TvShowsRepository {

    override fun getTvShow(tvShowId: Int): Flow<TvShow> {
        return cache.getTvShow(tvShowId)
            .map { it.toTvShow() }
    }

    override suspend fun getPopularTvShows(page: Int): List<TvShow> {
        return if (getShowsByCategory(POPULAR).isEmpty()) {

            val entityList = apiService.getPopularShows(page).results
                .map { it.toShow() }
                .map {
                    it.copy(
                        show_category = POPULAR
                    )
                }

            cache.insert(entityList)

            getShowsByCategory(POPULAR)
        } else {
            getShowsByCategory(POPULAR)
        }
    }

    override suspend fun getTopRatedTvShows(page: Int): List<TvShow> {
        return if (getShowsByCategory(TOP_RATED).isEmpty()) {

            apiService.getTopRatedShows(page).results
                .map { it.toShow() }
                .map {
                    it.copy(
                        show_category = TOP_RATED
                    )
                }
                .map { cache.insert(it) }

            getShowsByCategory(TOP_RATED)
        } else {
            getShowsByCategory(TOP_RATED)
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

            cache.getFeaturedTvShows(FEATURED, WEEK).toTvShowList()
        } else {
            cache.getFeaturedTvShows(FEATURED, WEEK).toTvShowList()
        }
    }

    override suspend fun getShowsByCategory(category: ShowCategory): List<TvShow> {
        return cache.getTvShows()
            .toTvShowList()
            .filter { it.showCategory == category }
    }

    override fun getWatchlist(): Flow<List<TvShow>> = cache.getWatchlist()
        .map { it.toTvShowList() }

    override suspend fun updateWatchlist(showId: Int, addToWatchList: Boolean) {
        cache.updateWatchlist(showId, addToWatchList)
    }

    override suspend fun getShowsByCategoryAndWindow(
        category: ShowCategory,
        timeWindow: TimeWindow
    ): List<TvShow> {
        return cache.getTvShows(category, timeWindow)
            .toTvShowList()
    }

}
