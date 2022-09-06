package com.thomaskioko.tvmaniac.details.api.interactor

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.core.util.combine
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailViewState
import com.thomaskioko.tvmaniac.details.api.toLastAirEpisodeList
import com.thomaskioko.tvmaniac.details.api.toSeasonsEntityList
import com.thomaskioko.tvmaniac.details.api.toSimilarShowList
import com.thomaskioko.tvmaniac.details.api.toTrailerList
import com.thomaskioko.tvmaniac.details.api.toTvShow
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow

class ObserveShowInteractor constructor(
    private val traktRepository: TraktRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val genreRepository: GenreRepository,
    private val lastAirRepository: LastAirEpisodeRepository,
    private val trailerRepository: TrailerRepository
) : FlowInteractor<Int, ShowDetailViewState>() {

    override fun run(params: Int): Flow<ShowDetailViewState> = combine(
        traktRepository.observeShow(params),
        traktRepository.observeFollowedShow(params),
        similarShowsRepository.observeSimilarShows(params),
        seasonsRepository.observeShowSeasons(params),
        lastAirRepository.observeAirEpisodes(params),
        trailerRepository.observeTrailersByShowId(params),
        genreRepository.observeGenres(),
    ) { show, isFollowed, similarShows, seasons, lastAirEp, trailers, _ ->

        val tvShow = show.toTvShow()
        ShowDetailViewState(
            tvShow = tvShow,
            isFollowed = isFollowed,
            similarShowList = similarShows.toSimilarShowList(),
            tvSeasonUiModels = seasons.toSeasonsEntityList(),
            lastAirEpList = lastAirEp.toLastAirEpisodeList(),
            trailersList = trailers.toTrailerList()
        )
    }
}
