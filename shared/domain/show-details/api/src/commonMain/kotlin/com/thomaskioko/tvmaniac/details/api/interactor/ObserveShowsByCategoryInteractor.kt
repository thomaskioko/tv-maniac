package com.thomaskioko.tvmaniac.details.api.interactor

import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.map
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

//TODO:: move to grid module
class ObserveShowsByCategoryInteractor constructor(
    private val repository: TmdbRepository, //TODO:: User trakt repository
) : FlowInteractor<Int, Flow<PagingData<TvShow>>>() {

    override fun run(params: Int): Flow<Flow<PagingData<TvShow>>> =
        flow {

            val list = repository.observePagedShowsByCategoryID(ShowCategory[params].type)
                .map { pagingData ->
                    pagingData.map { it.toTvShow() }
                }

            emit(list)
        }
            .distinctUntilChanged()
}

fun Show.toTvShow(): TvShow {
    return TvShow(
        traktId = trakt_id,
        tmdbId = tmdb_id,
        title = title,
        overview = overview,
        language = language?.uppercase(),
        posterImageUrl = poster_image_url,
        backdropImageUrl = backdrop_image_url,
        votes = votes,
        rating = rating,
        genres = genres,
        year = year,
        status = status.replaceFirstChar { it.uppercase() },
    )
}
