package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.core.util.combine
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow

class FetchShowsInteractor constructor(
    private val traktRepository: TraktRepository,
    private val tmdbRepository: TmdbRepository
) : FlowInteractor<Unit, Unit>() {

    override fun run(params: Unit): Flow<Unit> = combine(
        traktRepository.fetchShowsByCategoryID(ShowCategory.TRENDING.id),
        traktRepository.fetchShowsByCategoryID(ShowCategory.RECOMMENDED.id),
        traktRepository.fetchShowsByCategoryID(ShowCategory.POPULAR.id),
        traktRepository.fetchShowsByCategoryID(ShowCategory.ANTICIPATED.id),
        traktRepository.fetchShowsByCategoryID(ShowCategory.FEATURED.id),
        tmdbRepository.observeUpdateShowArtWork()
    ) { _, _, _, _, _, _ ->
    }

}