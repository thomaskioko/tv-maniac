package com.thomaskioko.tvmaniac.discover.api.interactor

import com.thomaskioko.tvmaniac.discover.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import com.thomaskioko.tvmaniac.showcommon.api.toTvShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveShowInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<Long, com.thomaskioko.tvmaniac.showcommon.api.TvShow>() {

    override fun run(params: Long): Flow<com.thomaskioko.tvmaniac.showcommon.api.TvShow> = repository.observeShow(params)
        .map { it.data?.toTvShow() ?: com.thomaskioko.tvmaniac.showcommon.api.TvShow.EMPTY_SHOW }
}
