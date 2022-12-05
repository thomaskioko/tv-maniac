package com.thomaskioko.tvmaniac.show_grid

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow
import com.thomaskioko.tvmaniac.shows.api.toTvShowList
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//TODO:: Fix pagination
class ObservePagedShowsByCategoryInteractor constructor(
    private val repository: TraktRepository,
) : FlowInteractor<Int, List<TvShow>>() {

    override fun run(params: Int): Flow<List<TvShow>> =
        repository.observeCachedShows(ShowCategory[params].id)
        .map { it.data?.toTvShowList() ?: emptyList() }

}

