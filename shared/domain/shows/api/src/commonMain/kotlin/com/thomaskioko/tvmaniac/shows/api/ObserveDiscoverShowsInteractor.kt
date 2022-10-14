package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private const val FEATURED_LIST_SIZE = 5

class ObserveDiscoverShowsInteractor constructor(
    private val discoverRepository: TraktRepository,
) : FlowInteractor<Unit, DiscoverShowResult>() {

    override fun run(params: Unit): Flow<DiscoverShowResult> = combine(
        discoverRepository.observeShowsByCategoryID(ShowCategory.TRENDING.type).toShowData(
            ShowCategory.TRENDING
        ),
        discoverRepository.observeShowsByCategoryID(ShowCategory.RECOMMENDED.type).toShowData(
            ShowCategory.RECOMMENDED
        ),
        discoverRepository.observeShowsByCategoryID(ShowCategory.POPULAR.type).toShowData(
            ShowCategory.POPULAR
        ),
        discoverRepository.observeShowsByCategoryID(ShowCategory.ANTICIPATED.type).toShowData(
            ShowCategory.ANTICIPATED
        ),
        discoverRepository.observeShowsByCategoryID(ShowCategory.FEATURED.type).toShowData(
            ShowCategory.FEATURED
        ),
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