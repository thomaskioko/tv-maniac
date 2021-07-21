package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
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
) : Interactor<List<TrendingDataRequest>, LinkedHashMap<TrendingDataRequest, List<TvShowsEntity>>>() {

    override fun run(params: List<TrendingDataRequest>): Flow<DomainResultState<LinkedHashMap<TrendingDataRequest, List<TvShowsEntity>>>> =
        flow {
            emit(loading())

            val trendingMap = linkedMapOf<TrendingDataRequest, List<TvShowsEntity>>()

            params.forEach {
                val result = repository.getTrendingShows(
                    it.timeWindow.window
                )

                trendingMap[it] = result
            }

            emit(success(trendingMap))
        }
            .catch { emit(error(it)) }
}