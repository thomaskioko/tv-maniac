package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import dev.zacsweers.metro.Inject

@Inject
public class MarkEpisodeWatchedInteractor(
    private val episodeRepository: EpisodeRepository,
) : Interactor<MarkEpisodeWatchedParams>() {

    override suspend fun doWork(params: MarkEpisodeWatchedParams) {
        if (params.markPreviousEpisodes) {
            episodeRepository.markEpisodeAndPreviousEpisodesWatched(
                showId = params.showId,
                episodeId = params.episodeId,
                seasonNumber = params.seasonNumber,
                episodeNumber = params.episodeNumber,
                watchedAt = params.watchedAt,
                useReleaseDate = params.useReleaseDate,
            )
        } else {
            episodeRepository.markEpisodeAsWatched(
                showId = params.showId,
                episodeId = params.episodeId,
                seasonNumber = params.seasonNumber,
                episodeNumber = params.episodeNumber,
                watchedAt = params.watchedAt,
                useReleaseDate = params.useReleaseDate,
            )
        }
    }
}

public data class MarkEpisodeWatchedParams(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val markPreviousEpisodes: Boolean = false,
    val watchedAt: Long? = null,
    val useReleaseDate: Boolean = false,
)
