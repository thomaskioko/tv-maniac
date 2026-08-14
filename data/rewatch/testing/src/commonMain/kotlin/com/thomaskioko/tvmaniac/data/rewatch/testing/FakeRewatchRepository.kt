package com.thomaskioko.tvmaniac.data.rewatch.testing

import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

public class FakeRewatchRepository : RewatchRepository {

    private var nextSessionId: Long = 1
    private val sessionsByShow = MutableStateFlow<Map<Long, List<RewatchSession>>>(emptyMap())
    private val rewatchesByEpisode = MutableStateFlow<Map<Long, Long>>(emptyMap())
    private val statusByShow = MutableStateFlow<Map<Long, RewatchStatus>>(emptyMap())
    private var supportsRewatchValue: Boolean = true
    private var remoteSessionsByShow: Map<Long, List<RemoteRewatchSession>> = emptyMap()
    private var syncException: Throwable? = null

    public var lastAddEpisodeSessionId: Long? = null
        private set

    public var finishedSessionId: Long? = null
        private set

    public fun setSessionsForShow(showId: Long, sessions: List<RewatchSession>) {
        sessionsByShow.value += (showId to sessions)
    }

    public fun setRewatchesForEpisode(episodeId: Long, rewatches: Long) {
        rewatchesByEpisode.value += (episodeId to rewatches)
    }

    public fun setRewatchStatusForShow(showId: Long, status: RewatchStatus) {
        statusByShow.value += (showId to status)
    }

    public fun setSupportsRewatch(value: Boolean) {
        supportsRewatchValue = value
    }

    public fun setRemoteSessionsForShow(showId: Long, sessions: List<RemoteRewatchSession>) {
        remoteSessionsByShow += (showId to sessions)
    }

    public fun setSyncException(exception: Throwable) {
        syncException = exception
    }

    override suspend fun startSession(showId: Long, startedAt: Long): Long {
        val sessionId = nextSessionId++
        val session = RewatchSession(id = sessionId, showId = showId, startedAt = startedAt, closedAt = null)
        setSessionsForShow(showId, sessionsByShow.value[showId].orEmpty() + session)
        return sessionId
    }

    override suspend fun addEpisodeToSession(
        sessionId: Long,
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
    ) {
        lastAddEpisodeSessionId = sessionId
    }

    override suspend fun closeSession(sessionId: Long, closedAt: Long) {
        val showId = sessionsByShow.value.entries.firstOrNull { entry -> entry.value.any { it.id == sessionId } }?.key ?: return
        val updated = sessionsByShow.value[showId].orEmpty().map { session ->
            if (session.id == sessionId) session.copy(closedAt = closedAt) else session
        }
        setSessionsForShow(showId, updated)
    }

    override suspend fun finishSession(sessionId: Long, closedAt: Long) {
        finishedSessionId = sessionId
        closeSession(sessionId = sessionId, closedAt = closedAt)
    }

    override fun observeSessionsForShow(showId: Long): Flow<List<RewatchSession>> =
        sessionsByShow.asStateFlow().map { it[showId].orEmpty() }

    override fun observeRewatchStatus(showId: Long): Flow<RewatchStatus> =
        statusByShow.asStateFlow().map { it[showId] ?: RewatchStatus() }

    override suspend fun openSessionForShow(showId: Long): RewatchSession? =
        sessionsByShow.value[showId].orEmpty().firstOrNull { it.closedAt == null }

    override fun observeEpisodeRewatches(episodeId: Long): Flow<Long> =
        rewatchesByEpisode.asStateFlow().map { it[episodeId] ?: 0L }

    override suspend fun removeEpisodeRewatches(episodeId: Long) {
        rewatchesByEpisode.value -= episodeId
    }

    override suspend fun supportsRewatch(): Boolean = supportsRewatchValue

    override suspend fun syncPendingRewatches() {
        syncException?.let { throw it }
    }

    override suspend fun syncRewatchSessions(showId: Long): List<RemoteRewatchSession> =
        remoteSessionsByShow[showId].orEmpty()
}
