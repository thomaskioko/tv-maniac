package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.core.discover.DiscoverShowsData
import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowList
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.datasource.repository.util.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveShowsByCategoryInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<ShowCategory, DiscoverShowsData>() {

    override fun run(params: ShowCategory): Flow<DiscoverShowsData> =
        repository.observeShowsByCategoryID(params.type)
            .map {
                DiscoverShowsData(
                    isLoading = it.status == Status.LOADING,
                    category = params,
                    shows = it.data?.toTvShowList() ?: emptyList(),
                    errorMessage = it.message
                )
            }
}
