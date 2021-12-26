package com.thomaskioko.tvmaniac.interactor

import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.map
import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShow
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetShowsByCategoryInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<Int, Flow<PagingData<TvShow>>>() {

    override fun run(params: Int): Flow<Flow<PagingData<TvShow>>> =
        flow {

            val list = when (val category = ShowCategory[params]) {
                TRENDING, FEATURED -> repository.getPagedShowsByCategoryID(category.type)
                POPULAR -> repository.getPagedShowsByCategoryID(category.type)
                TOP_RATED -> repository.getPagedShowsByCategoryID(category.type)
            }.map { pagingData ->
                pagingData.map { it.toTvShow() }
            }

            emit(list)
        }
            .distinctUntilChanged()
}
