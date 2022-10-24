package com.thomaskioko.tvmaniac.details.api.interactor

import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.map
import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow
import com.thomaskioko.tvmaniac.shows.api.toTvShow
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

//TODO:: move to grid module
class ObservePagedShowsByCategoryInteractor constructor(
    private val repository: TraktRepository,
) : FlowInteractor<Int, Flow<PagingData<TvShow>>>() {

    override fun run(params: Int): Flow<Flow<PagingData<TvShow>>> =
        flow {

            val list = repository.observePagedShowsByCategoryID(ShowCategory[params].id)
                .map { pagingData ->
                    pagingData.map { it.toTvShow() }
                }

            emit(list)
        }
            .distinctUntilChanged()
}

