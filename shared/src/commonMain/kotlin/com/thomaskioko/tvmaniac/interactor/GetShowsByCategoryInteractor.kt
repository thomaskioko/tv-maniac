package com.thomaskioko.tvmaniac.interactor

import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.map
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.discover.api.model.ShowCategory
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel
import com.thomaskioko.tvmaniac.discover.api.repository.TvShowsRepository
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

fun Show.toTvShow(): ShowUiModel {
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
        isInWatchlist = is_watchlist
    )
}
