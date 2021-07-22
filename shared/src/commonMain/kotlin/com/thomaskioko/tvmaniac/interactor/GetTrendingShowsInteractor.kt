package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShows
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
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
) : Interactor<List<TrendingDataRequest>, LinkedHashMap<TrendingDataRequest, List<TvShows>>>() {

    override fun run(params: List<TrendingDataRequest>): Flow<DomainResultState<LinkedHashMap<TrendingDataRequest, List<TvShows>>>> =
        flow {
            emit(loading())

            val trendingMap = linkedMapOf<TrendingDataRequest, List<TvShows>>()

            params.forEach {

                val result = if (it != TrendingDataRequest.FEATURED) {
                    repository.getTrendingShows(it.timeWindow.window)
                } else {
                    repository.getFeaturedShows()
                }

                trendingMap[it] = result
            }

            emit(success(trendingMap))
        }
            .catch { emit(error(it)) }
}