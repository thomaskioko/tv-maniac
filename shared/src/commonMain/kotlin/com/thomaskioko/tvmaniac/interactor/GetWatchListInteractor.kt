package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowList
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetWatchListInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<Unit, List<TvShow>>() {

    override fun run(params: Unit): Flow<List<TvShow>> = repository.getWatchlist()
        .onStart { loading<List<TvShow>>() }
        .map { it.toTvShowList() }
        .map { it }
}
