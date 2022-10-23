package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.core.util.combine
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow

private const val FEATURED_LIST_SIZE = 5

class ObserveDiscoverShowsInteractor constructor(
    private val traktRepository: TraktRepository,
    private val tmdbRepository: TmdbRepository
) : FlowInteractor<Unit, DiscoverShowResult>() {

    override fun run(params: Unit): Flow<DiscoverShowResult> = combine(
        traktRepository.observeShowsByCategoryID(ShowCategory.TRENDING.type),
        traktRepository.observeShowsByCategoryID(ShowCategory.RECOMMENDED.type),
        traktRepository.observeShowsByCategoryID(ShowCategory.POPULAR.type),
        traktRepository.observeShowsByCategoryID(ShowCategory.ANTICIPATED.type),
        traktRepository.observeShowsByCategoryID(ShowCategory.FEATURED.type),
        tmdbRepository.observeUpdateShowArtWork()
    ) { trending, recommended, popular, anticipated, featured, _ ->

        DiscoverShowResult(
            trendingShows = trending.toShowData(ShowCategory.TRENDING),
            recommendedShows = recommended.toShowData(ShowCategory.RECOMMENDED),
            popularShows = popular.toShowData(ShowCategory.POPULAR),
            anticipatedShows = anticipated.toShowData(ShowCategory.ANTICIPATED),
            featuredShows = featured.toShowData(ShowCategory.FEATURED, FEATURED_LIST_SIZE),
            isEmpty = trending.isResultEmpty() && recommended.isResultEmpty() &&
                    popular.isResultEmpty() && anticipated.isResultEmpty() && featured.isResultEmpty()
        )
    }

    private fun Resource<List<SelectShowsByCategory>>.toShowData(
        category: ShowCategory, resultLimit: Int) = DiscoverShowResult.DiscoverShowsData(
            category = category,
            tvShows = data?.toTvShowList()?.take(resultLimit) ?: emptyList(),
            errorMessage = throwable?.message
        )

    private fun Resource<List<SelectShowsByCategory>>.toShowData(category: ShowCategory) =
        DiscoverShowResult.DiscoverShowsData(
            category = category,
            tvShows = data?.toTvShowList() ?: emptyList(),
            errorMessage = throwable?.message
        )

    private fun Resource<List<SelectShowsByCategory>>.isResultEmpty() = data.isNullOrEmpty()
}