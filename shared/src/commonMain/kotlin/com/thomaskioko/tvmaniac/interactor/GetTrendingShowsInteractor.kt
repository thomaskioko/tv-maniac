package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest.THIS_WEEK
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest.TODAY
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetTrendingShowsInteractor constructor(
    private val repository: TvShowsRepository,
) : Interactor<List<TrendingDataRequest>, LinkedHashMap<TrendingDataRequest, List<TvShow>>>() {

    override fun run(params: List<TrendingDataRequest>): Flow<DomainResultState<LinkedHashMap<TrendingDataRequest, List<TvShow>>>> =
        flow {
            emit(loading())

            val trendingMap = linkedMapOf<TrendingDataRequest, List<TvShow>>()

            params.forEach {
                val result = when (it) {
                    FEATURED -> repository.getFeaturedShows()
                    TODAY, THIS_WEEK -> repository.getTrendingShows(it.timeWindow.window)
                    POPULAR -> repository.getPopularTvShows(1)
                }

                trendingMap[it] = result
            }

            emit(success(trendingMap))
        }
            .catch { emit(error(it)) }
}