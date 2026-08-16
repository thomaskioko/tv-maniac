package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import dev.zacsweers.metro.Inject

@Inject
public class MarkShowWatchedInteractor(
    private val episodeRepository: EpisodeRepository,
) : Interactor<MarkShowWatchedParams>() {

    override suspend fun doWork(params: MarkShowWatchedParams) {
        episodeRepository.markShowWatched(
            showId = params.showId,
            watchedAt = params.watchedAt,
            useReleaseDate = params.useReleaseDate,
        )
    }
}

public data class MarkShowWatchedParams(
    val showId: Long,
    val watchedAt: Long? = null,
    val useReleaseDate: Boolean = false,
)
