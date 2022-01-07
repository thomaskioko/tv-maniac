package com.thomaskioko.tvmaniac.interactor

import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.map
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShow
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.presentation.model.ShowUiModel
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetShowsByCategoryInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<Int, Flow<PagingData<ShowUiModel>>>() {

    override fun run(params: Int): Flow<Flow<PagingData<ShowUiModel>>> =
        flow {

            val list = repository.observePagedShowsByCategoryID(ShowCategory[params].type)
                .map { pagingData ->
                    pagingData.map { it.toTvShow() }
                }

            emit(list)
        }
            .distinctUntilChanged()
}
