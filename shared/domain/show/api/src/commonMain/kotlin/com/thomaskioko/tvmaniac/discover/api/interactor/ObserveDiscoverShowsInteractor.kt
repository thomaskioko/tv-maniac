package com.thomaskioko.tvmaniac.discover.api.interactor

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.discover.api.DiscoverShowResult
import com.thomaskioko.tvmaniac.discover.api.model.ShowCategory
import com.thomaskioko.tvmaniac.discover.api.model.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.discover.api.model.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.discover.api.model.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel
import com.thomaskioko.tvmaniac.discover.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import com.thomaskioko.tvmaniac.shared.core.util.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private const val FEATURED_LIST_SIZE = 5

class ObserveDiscoverShowsInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<Unit, DiscoverShowResult>() {

    override fun run(params: Unit): Flow<DiscoverShowResult> = combine(
        repository.observeShowsByCategoryID(TRENDING.type).toShowData(TRENDING),
        repository.observeShowsByCategoryID(TOP_RATED.type).toShowData(TOP_RATED),
        repository.observeShowsByCategoryID(POPULAR.type).toShowData(POPULAR),
    ) { trending, topRated, popular ->

        DiscoverShowResult(
            featuredShows = trending.copy(
                showUiModels = trending.showUiModels
                    .sortedBy { it.votes }
                    .take(FEATURED_LIST_SIZE)
            ),
            trendingShows = trending,
            popularShows = popular,
            topRatedShows = topRated
        )
    }

    private fun Flow<Resource<List<Show>>>.toShowData(category: ShowCategory) = map {
        DiscoverShowResult.DiscoverShowsData(
            isLoading = it.status == Status.LOADING,
            category = category,
            showUiModels = it.data?.toTvShowList() ?: emptyList(),
            errorMessage = it.message
        )
    }

    private fun List<Show>.toTvShowList(): List<ShowUiModel> {
        return map { it.toTvShow() }
    }

    private fun Show.toTvShow(): ShowUiModel {
        return ShowUiModel(
            id = id.toInt(),
            title = title,
            overview = description,
            language = language,
            posterImageUrl = poster_image_url,
            backdropImageUrl = backdrop_image_url,
            votes = votes.toInt(),
            averageVotes = vote_average,
            genreIds = genre_ids,
            year = year,
            status = status,
            following = is_watchlist
        )
    }
}
