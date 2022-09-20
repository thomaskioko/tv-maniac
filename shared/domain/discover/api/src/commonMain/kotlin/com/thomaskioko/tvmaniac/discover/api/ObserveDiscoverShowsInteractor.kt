package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.discover.api.mapper.toTvShowList
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.ANTICIPATED
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.RECOMMENDED
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.TRENDING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private const val FEATURED_LIST_SIZE = 5

class ObserveDiscoverShowsInteractor constructor(
    private val discoverRepository: DiscoverRepository,
) : FlowInteractor<Unit, DiscoverShowResult>() {

    override fun run(params: Unit): Flow<DiscoverShowResult> = combine(
        discoverRepository.observeShowsByCategoryID(TRENDING.type).toShowData(TRENDING),
        discoverRepository.observeShowsByCategoryID(RECOMMENDED.type).toShowData(RECOMMENDED),
        discoverRepository.observeShowsByCategoryID(POPULAR.type).toShowData(POPULAR),
        discoverRepository.observeShowsByCategoryID(ANTICIPATED.type).toShowData(ANTICIPATED),
        discoverRepository.observeShowsByCategoryID(FEATURED.type).toShowData(FEATURED),
    ) { trending, recommended, popular, anticipated, featured ->

        DiscoverShowResult(
            trendingShows = trending,
            recommendedShows = recommended,
            popularShows = popular,
            anticipatedShows = anticipated,
            featuredShows = featured.copy(
                tvShows = featured.tvShows
                    .take(FEATURED_LIST_SIZE)
            ),
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
