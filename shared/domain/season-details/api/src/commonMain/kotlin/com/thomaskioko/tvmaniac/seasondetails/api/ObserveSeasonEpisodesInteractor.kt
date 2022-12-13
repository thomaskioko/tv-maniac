package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveSeasonEpisodesInteractor(
    private val traktRepository: TraktRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository
) : FlowInteractor<Int, SeasonsResult>() {

    override fun run(params: Int): Flow<SeasonsResult> = combine(
        traktRepository.observeShow(params),
        seasonDetailsRepository.observeSeasonEpisodes(showId = params),
    ) { show, seasons ->
        SeasonsResult(
            showTitle = show.data?.title ?: "",
            seasonsWithEpisodes = seasons.toSeasonWithEpisodes()
        )
    }
}
