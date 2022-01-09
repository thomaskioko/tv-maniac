package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowList
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel
import com.thomaskioko.tvmaniac.discover.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetWatchListInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<Unit, List<ShowUiModel>>() {

    override fun run(params: Unit): Flow<List<ShowUiModel>> = repository.observeWatchlist()
        .map { it.toTvShowList() }
}
