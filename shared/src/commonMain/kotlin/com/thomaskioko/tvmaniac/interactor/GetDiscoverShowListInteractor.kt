package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.repository.TrendingShowData
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetDiscoverShowListInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<List<ShowCategory>, List<TrendingShowData>>() {

    override fun run(params: List<ShowCategory>): Flow<List<TrendingShowData>> =
        flow {
            emit(repository.getDiscoverShowList(params))
        }
}
