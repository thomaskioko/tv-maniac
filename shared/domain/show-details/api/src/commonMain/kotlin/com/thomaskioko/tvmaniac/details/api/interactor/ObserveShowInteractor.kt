package com.thomaskioko.tvmaniac.details.api.interactor

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.core.util.combine
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailViewState
import com.thomaskioko.tvmaniac.showcommon.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.details.api.toGenreModelList
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
import kotlinx.coroutines.flow.Flow

class ObserveShowInteractor constructor(
    private val tmdbRepository: TmdbRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val genreRepository: GenreRepository,
    private val lastAirRepository: LastAirEpisodeRepository,
    private val trailerRepository: TrailerRepository
) : FlowInteractor<Long, ShowDetailViewState>() {

    override fun run(params: Long): Flow<ShowDetailViewState> = combine(
        tmdbRepository.observeShow(params),
        similarShowsRepository.observeSimilarShows(params),
        seasonsRepository.observeShowSeasons(params),
        genreRepository.observeGenres(),
        lastAirRepository.observeAirEpisodes(params),
        trailerRepository.observeTrailersByShowId(params),
    ) { show, similarShows, seasons, genre, lastAirEp, trailers ->

        val tvShow = show.toTvShow()
        ShowDetailViewState(
            tvShow = tvShow,
            similarShowList = similarShows.toSimilarShowList(),
            tvSeasonUiModels = seasons.toSeasonsEntityList(),
            genreUIList = genre.toGenreModelList(tvShow.genreIds),
            lastAirEpList = lastAirEp.toLastAirEpisodeList(),
            trailersList = trailers.toTrailerList()
        )
    }
}
