package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class ObserveShowsInteractor constructor(
    private val traktRepository: TraktRepository,
    private val dispatcher: CoroutineDispatcher
) : FlowInteractor<Unit, DiscoverShowResult>() {

    override fun run(params: Unit): Flow<DiscoverShowResult> = combine(
        traktRepository.observeShowsByCategoryId(ShowCategory.TRENDING.id),
        traktRepository.observeShowsByCategoryId(ShowCategory.RECOMMENDED.id),
        traktRepository.observeShowsByCategoryId(ShowCategory.POPULAR.id),
        traktRepository.observeShowsByCategoryId(ShowCategory.ANTICIPATED.id),
        traktRepository.observeShowsByCategoryId(ShowCategory.FEATURED.id),

        ) { trending, recommended, popular, anticipated, featured ->

        DiscoverShowResult(
            trendingShows = trending.toShowData(ShowCategory.TRENDING),
            recommendedShows = recommended.toShowData(ShowCategory.RECOMMENDED),
            popularShows = popular.toShowData(ShowCategory.POPULAR),
            anticipatedShows = anticipated.toShowData(ShowCategory.ANTICIPATED),
            featuredShows = featured.toShowData(ShowCategory.FEATURED, 5),
            isEmpty = trending.isEmpty() && recommended.isEmpty() &&
                    popular.isEmpty() && anticipated.isEmpty() && featured.isEmpty()
        )
    }
        .flowOn(dispatcher)

}