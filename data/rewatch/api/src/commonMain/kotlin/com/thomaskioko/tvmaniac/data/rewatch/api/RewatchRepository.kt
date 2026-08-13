package com.thomaskioko.tvmaniac.data.rewatch.api

import kotlinx.coroutines.flow.Flow

public interface RewatchRepository {
    public suspend fun startSession(showId: Long, startedAt: Long): Long?

    public suspend fun addEpisodeToSession(
        sessionId: Long,
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
    )

    public suspend fun closeSession(sessionId: Long, closedAt: Long)

    public suspend fun finishSession(sessionId: Long, closedAt: Long)

    public fun observeSessionsForShow(showId: Long): Flow<List<RewatchSession>>

    public fun observeRewatchStatus(showId: Long): Flow<RewatchStatus>

    public suspend fun openSessionForShow(showId: Long): RewatchSession?

    public fun observeEpisodeRewatches(episodeId: Long): Flow<Long>

    public suspend fun supportsRewatch(): Boolean

    public suspend fun syncPendingRewatches()

    public suspend fun syncRewatchSessions(showId: Long): List<RemoteRewatchSession>
}
