package com.thomaskioko.tvmaniac.details.api.interactor

import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveFollowingInteractor constructor(
    private val repository: TraktRepository,
) : FlowInteractor<Unit, List<TvShow>>() {

    override fun run(params: Unit): Flow<List<TvShow>> = repository.observeFollowedShows()
        .map { it.toTvShowList() }
}

fun List<SelectFollowedShows>.toTvShowList(): List<TvShow> {
    return map {
        TvShow(
            traktId = it.id,
            title = it.title,
            overview = it.overview,
            language = it.language,
            posterImageUrl = it.poster_image_url,
            backdropImageUrl = it.backdrop_image_url,
            votes = it.votes,
            rating = it.rating,
            genres = it.genres,
            year = it.year,
            status = it.status,
        )
    }
}
