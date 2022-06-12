package com.thomaskioko.tvmaniac.details.api.interactor

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailsResult
import com.thomaskioko.tvmaniac.showcommon.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.details.api.toGenreModelList
import com.thomaskioko.tvmaniac.details.api.toLastAirEpisodeList
import com.thomaskioko.tvmaniac.details.api.toSeasonsEntityList
import com.thomaskioko.tvmaniac.details.api.toSimilarShowList
import com.thomaskioko.tvmaniac.details.api.toTvShow
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveShowInteractor constructor(
    private val tvShowsRepository: TvShowsRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val genreRepository: GenreRepository,
    private val lastAirRepository: LastAirEpisodeRepository,
) : FlowInteractor<Long, ShowDetailsResult>() {

    override fun run(params: Long): Flow<ShowDetailsResult> = combine(
        tvShowsRepository.observeShow(params),
        similarShowsRepository.observeSimilarShows(params),
        seasonsRepository.observeShowSeasons(params),
        genreRepository.observeGenres(),
        lastAirRepository.observeAirEpisodes(params)
    ) { show, similarShows, seasons, genre, lastAirEp ->

        ShowDetailsResult(
            tvShow = show.toTvShow(),
            similarShowList = similarShows.toSimilarShowList(),
            tvSeasonUiModels = seasons.toSeasonsEntityList(),
            genreUIList = genre.toGenreModelList(),
            lastAirEpList = lastAirEp.toLastAirEpisodeList()
        )
    }
}
