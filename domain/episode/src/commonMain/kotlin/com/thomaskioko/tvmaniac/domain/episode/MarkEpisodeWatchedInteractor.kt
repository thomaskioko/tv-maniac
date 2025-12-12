package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import me.tatarka.inject.annotations.Inject

@Inject
class MarkEpisodeWatchedInteractor(
    private val episodeRepository: EpisodeRepository,
) : Interactor<MarkEpisodeWatchedParams>() {

    override suspend fun doWork(params: MarkEpisodeWatchedParams) {
        if (params.markPreviousEpisodes) {
            episodeRepository.markEpisodeAndPreviousEpisodesWatched(
                showId = params.showId,
                episodeId = params.episodeId,
                seasonNumber = params.seasonNumber,
                episodeNumber = params.episodeNumber,
            )
        } else {
            episodeRepository.markEpisodeAsWatched(
                showId = params.showId,
                episodeId = params.episodeId,
                seasonNumber = params.seasonNumber,
                episodeNumber = params.episodeNumber,
            )
        }
    }
}

data class MarkEpisodeWatchedParams(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val markPreviousEpisodes: Boolean = false,
)
