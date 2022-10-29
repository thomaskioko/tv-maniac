package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class UpdateSeasonEpisodesInteractor(
    private val traktRepository: TraktRepository,
    private val seasonEpisodesRepository: SeasonEpisodesRepository,
    private val episodeRepository: EpisodeRepository
) : FlowInteractor<Int, Unit>() {

    override fun run(params: Int): Flow<Unit> = combine(
        traktRepository.observeShow(params),
        seasonEpisodesRepository.updateSeasonEpisodes(showId = params),
        episodeRepository.observeUpdateEpisodeArtWork(params)
    ) { _, _, _->
    }
}
