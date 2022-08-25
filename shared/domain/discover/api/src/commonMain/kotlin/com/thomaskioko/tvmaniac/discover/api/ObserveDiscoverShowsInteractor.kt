package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import com.thomaskioko.tvmaniac.discover.api.mapper.toTvShowList
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory.TRENDING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private const val FEATURED_LIST_SIZE = 5

class ObserveDiscoverShowsInteractor constructor(
    private val repository: DiscoverRepository,
) : FlowInteractor<Unit, DiscoverShowResult>() {

    override fun run(params: Unit): Flow<DiscoverShowResult> = combine(
        repository.observeShowsByCategoryID(TRENDING.type).toShowData(TRENDING),
        repository.observeShowsByCategoryID(TOP_RATED.type).toShowData(TOP_RATED),
        repository.observeShowsByCategoryID(POPULAR.type).toShowData(POPULAR),
    ) { trending, topRated, popular ->

        DiscoverShowResult(
            featuredShows = trending.copy(
                tvShows = trending.tvShows
                    .sortedBy { it.averageVotes }
                    .take(FEATURED_LIST_SIZE)
            ),
            trendingShows = trending,
            popularShows = popular,
            topRatedShows = topRated
        )
    }

    private fun Flow<Resource<List<Show>>>.toShowData(category: ShowCategory) = map {
        DiscoverShowResult.DiscoverShowsData(
            category = category,
            tvShows = it.data?.toTvShowList() ?: emptyList(),
            errorMessage = it.throwable?.message
        )
    }
}
