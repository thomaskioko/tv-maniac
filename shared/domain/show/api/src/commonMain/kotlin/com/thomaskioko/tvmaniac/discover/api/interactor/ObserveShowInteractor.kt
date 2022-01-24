package com.thomaskioko.tvmaniac.discover.api.interactor

import com.thomaskioko.tvmaniac.discover.api.mapper.toTvShow
import com.thomaskioko.tvmaniac.discover.api.model.TvShow
import com.thomaskioko.tvmaniac.discover.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveShowInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<Long, TvShow>() {

    override fun run(params: Long): Flow<TvShow> = repository.observeShow(params)
        .map { it.data?.toTvShow() ?: TvShow.EMPTY_SHOW }
}
