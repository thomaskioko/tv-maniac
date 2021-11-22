package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.repository.TrendingShowData
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
) : Interactor<List<ShowCategory>, List<TrendingShowData>>() {

    override fun run(params: List<ShowCategory>): Flow<DomainResultState<List<TrendingShowData>>> =
        flow {
            emit(loading())

            emit(success(repository.getTrendingShows(params)))
        }
            .catch { emit(error(it)) }
}
