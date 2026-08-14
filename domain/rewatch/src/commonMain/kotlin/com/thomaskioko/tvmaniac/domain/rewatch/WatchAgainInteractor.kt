package com.thomaskioko.tvmaniac.domain.rewatch

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject

@Inject
public class WatchAgainInteractor(
    private val rewatchRepository: RewatchRepository,
    private val dateTimeProvider: DateTimeProvider,
) : Interactor<WatchAgainInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        val watchedAt = dateTimeProvider.nowMillis()
        val sessionId = rewatchRepository.openSessionForShow(params.showId)?.id
            ?: rewatchRepository.startSession(showId = params.showId, startedAt = watchedAt)
            ?: return

        rewatchRepository.addEpisodeToSession(
            sessionId = sessionId,
            showId = params.showId,
            episodeId = params.episodeId,
            seasonNumber = params.seasonNumber,
            episodeNumber = params.episodeNumber,
            watchedAt = watchedAt,
        )
    }

    public data class Param(
        val showId: Long,
        val episodeId: Long,
        val seasonNumber: Long,
        val episodeNumber: Long,
    )
}
