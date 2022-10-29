package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveSeasonEpisodesInteractor(
    private val traktRepository: TraktRepository,
    private val seasonEpisodesRepository: SeasonEpisodesRepository
) : FlowInteractor<Int, SeasonsResult>() {

    override fun run(params: Int): Flow<SeasonsResult> = combine(
        traktRepository.observeShow(params),
        seasonEpisodesRepository.observeSeasonEpisodes(showId = params),
    ) { show, seasons ->
        SeasonsResult(
            tvShow = show.toTvShow(),
            seasonsWithEpisodes = seasons.toSeasonWithEpisodes()
        )
    }
}
