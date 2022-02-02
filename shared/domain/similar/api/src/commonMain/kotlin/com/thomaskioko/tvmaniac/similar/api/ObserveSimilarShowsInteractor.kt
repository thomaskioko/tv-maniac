package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import com.thomaskioko.tvmaniac.showcommon.api.TvShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveSimilarShowsInteractor constructor(
    private val repository: SimilarShowsRepository,
) : FlowInteractor<Long, List<TvShow>>() {

    override fun run(params: Long): Flow<List<TvShow>> = repository.observeSimilarShows(params)
        .map { it.data?.toTvShowList() ?: emptyList() }
}
