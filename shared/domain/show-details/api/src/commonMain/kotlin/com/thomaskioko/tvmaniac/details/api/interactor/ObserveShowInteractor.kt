package com.thomaskioko.tvmaniac.details.api.interactor

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailViewState
import com.thomaskioko.tvmaniac.details.api.toSeasonsEntityList
import com.thomaskioko.tvmaniac.details.api.toSimilarShowList
import com.thomaskioko.tvmaniac.details.api.toTrailerList
import com.thomaskioko.tvmaniac.details.api.toTvShow
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveShowInteractor constructor(
    private val traktRepository: TraktRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val trailerRepository: TrailerRepository
) : FlowInteractor<Int, ShowDetailViewState>() {

    override fun run(params: Int): Flow<ShowDetailViewState> = combine(
        traktRepository.observeShow(params),
        traktRepository.observeFollowedShow(params),
        similarShowsRepository.observeSimilarShows(params),
        seasonsRepository.observeShowSeasons(params),
        trailerRepository.observeTrailersByShowId(params),
    ) { show, isFollowed, similarShows, seasons, trailers ->

        ShowDetailViewState(
            tvShow = show.toTvShow(),
            isFollowed = isFollowed,
            similarShowList = similarShows.toSimilarShowList(),
            tvSeasonUiModels = seasons.toSeasonsEntityList(),
            trailersList = trailers.toTrailerList()
        )
    }
}
