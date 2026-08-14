package com.thomaskioko.tvmaniac.data.rewatch.api

import kotlinx.coroutines.flow.Flow

public interface RewatchSessionDao {
    public fun openSession(showId: Long, startedAt: Long): Long

    public fun addEpisodeToSession(sessionId: Long, episodeId: Long, watchedAt: Long): Long

    public fun addSyncedEpisodeToSession(sessionId: Long, episodeId: Long, watchedAt: Long, syncedAt: Long)

    public fun episodeIdForNumber(showId: Long, seasonNumber: Long, episodeNumber: Long): Long?

    public fun episodeRewatchCount(episodeId: Long): Long

    public fun closeSession(sessionId: Long, closedAt: Long)

    public fun observeSessionsForShow(showId: Long): Flow<List<RewatchSession>>

    public fun observeRewatchStatus(showId: Long): Flow<RewatchStatus>

    public fun openSessionForShow(showId: Long): RewatchSession?

    public fun sessionById(sessionId: Long): RewatchSession?

    public fun setProviderSessionId(sessionId: Long, providerSessionId: Long)

    public fun observeEpisodeRewatches(episodeId: Long): Flow<Long>

    public fun removeEpisodeRewatches(episodeId: Long)

    public fun removeSeasonRewatches(showId: Long, seasonNumber: Long)

    public fun observeRewatchTotals(): Flow<RewatchTotals>

    public fun unsentEpisodes(): List<UnsentRewatchEpisode>

    public fun markEpisodeSynced(rowId: Long, syncedAt: Long)

    public fun sessionCoverage(sessionId: Long): RewatchCoverage?

    public fun upsertProviderSession(
        showId: Long,
        providerSessionId: Long,
        startedAt: Long,
        closedAt: Long?,
    ): Long

    public fun clearAll()
}
