package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class MarkEpisodeWatchedInteractor(
    private val episodeRepository: EpisodeRepository,
) : Interactor<MarkEpisodeWatchedParams>() {

    override suspend fun doWork(params: MarkEpisodeWatchedParams) {
        if (params.markPreviousEpisodes) {
            episodeRepository.markEpisodeAndPreviousEpisodesWatched(
                showTraktId = params.showTraktId,
                episodeId = params.episodeId,
                seasonNumber = params.seasonNumber,
                episodeNumber = params.episodeNumber,
            )
        } else {
            episodeRepository.markEpisodeAsWatched(
                showTraktId = params.showTraktId,
                episodeId = params.episodeId,
                seasonNumber = params.seasonNumber,
                episodeNumber = params.episodeNumber,
            )
        }
    }
}

public data class MarkEpisodeWatchedParams(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val markPreviousEpisodes: Boolean = false,
)
