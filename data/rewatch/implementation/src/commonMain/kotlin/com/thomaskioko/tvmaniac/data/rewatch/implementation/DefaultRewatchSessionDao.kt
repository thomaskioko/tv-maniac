package com.thomaskioko.tvmaniac.data.rewatch.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.rewatch.api.OpenRewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchCoverage
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSessionDao
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchStatus
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchTotals
import com.thomaskioko.tvmaniac.data.rewatch.api.UnsentRewatchEpisode
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Rewatch_session
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.UnsentEpisodes
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultRewatchSessionDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : RewatchSessionDao {

    private val queries = database.rewatchQueries

    override fun openSession(showId: Long, startedAt: Long): Long = queries.transactionWithResult {
        queries.openSession(showId = Id(showId), startedAt = startedAt)
        queries.lastInsertRowId().executeAsOne()
    }

    override fun addEpisodeToSession(sessionId: Long, episodeId: Long, watchedAt: Long): Long = queries.transactionWithResult {
        queries.addEpisodeToSession(
            sessionId = sessionId,
            episodeId = Id(episodeId),
            watchedAt = watchedAt,
        )
        queries.lastInsertRowId().executeAsOne()
    }

    override fun closeSession(sessionId: Long, closedAt: Long) {
        queries.closeSession(sessionId = sessionId, closedAt = closedAt)
    }

    override fun observeSessionsForShow(showId: Long): Flow<List<RewatchSession>> =
        queries.sessionsForShow(Id(showId))
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { rows -> rows.map { it.toRewatchSession() } }

    override fun observeRewatchStatus(showId: Long): Flow<RewatchStatus> =
        queries.rewatchStatusForShow(Id(showId))
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)
            .map { row ->
                if (row == null) {
                    RewatchStatus()
                } else {
                    RewatchStatus(
                        finishedCount = row.finished_count.toInt(),
                        openSession = row.open_session_id?.let { sessionId ->
                            OpenRewatchSession(
                                id = sessionId,
                                watchedEpisodes = row.watched_in_session.toInt(),
                                airedEpisodes = row.aired_total.toInt(),
                            )
                        },
                    )
                }
            }

    override fun openSessionForShow(showId: Long): RewatchSession? =
        queries.openSessionForShow(Id(showId)).executeAsOneOrNull()?.toRewatchSession()

    override fun sessionById(sessionId: Long): RewatchSession? =
        queries.sessionById(sessionId).executeAsOneOrNull()?.toRewatchSession()

    override fun setProviderSessionId(sessionId: Long, providerSessionId: Long) {
        queries.setProviderSessionId(sessionId = sessionId, providerSessionId = providerSessionId)
    }

    override fun observeEpisodeRewatches(episodeId: Long): Flow<Long> =
        queries.observeEpisodeRewatches(Id(episodeId))
            .asFlow()
            .mapToOne(dispatchers.databaseRead)

    override fun observeRewatchTotals(): Flow<RewatchTotals> =
        queries.rewatchTotals { viewings, minutes -> RewatchTotals(viewings = viewings, minutes = minutes) }
            .asFlow()
            .mapToOne(dispatchers.databaseRead)

    override fun removeEpisodeRewatches(episodeId: Long) {
        queries.removeEpisodeRewatches(Id(episodeId))
    }

    override fun unsentEpisodes(): List<UnsentRewatchEpisode> =
        queries.unsentEpisodes().executeAsList().map { it.toUnsentRewatchEpisode() }

    override fun markEpisodeSynced(rowId: Long, syncedAt: Long) {
        queries.markEpisodeSynced(rowId = rowId, syncedAt = syncedAt)
    }

    override fun sessionCoverage(sessionId: Long): RewatchCoverage? =
        queries.sessionCoverage(sessionId).executeAsOneOrNull()?.let { row ->
            RewatchCoverage(watchedInSession = row.watched_in_session, airedTotal = row.aired_total)
        }

    override fun upsertProviderSession(
        showId: Long,
        providerSessionId: Long,
        startedAt: Long,
        closedAt: Long?,
    ) {
        queries.transaction {
            val existing = queries.sessionByProviderSessionId(
                showId = Id(showId),
                providerSessionId = providerSessionId,
            ).executeAsOneOrNull()

            if (existing == null) {
                queries.insertProviderSession(
                    showId = Id(showId),
                    startedAt = startedAt,
                    closedAt = closedAt,
                    providerSessionId = providerSessionId,
                )
            } else {
                queries.mergeProviderSession(
                    startedAt = startedAt,
                    closedAt = closedAt,
                    sessionId = existing.id,
                )
            }
        }
    }

    override fun clearAll() {
        queries.transaction {
            queries.clearAllSessionEpisodes()
            queries.clearAllSessions()
        }
    }
}

private fun Rewatch_session.toRewatchSession(): RewatchSession = RewatchSession(
    id = id,
    showId = show_id.id,
    startedAt = started_at,
    closedAt = closed_at,
    providerSessionId = provider_session_id,
)

private fun UnsentEpisodes.toUnsentRewatchEpisode(): UnsentRewatchEpisode = UnsentRewatchEpisode(
    rowId = row_id,
    sessionId = session_id,
    showId = show_id.id,
    seasonNumber = season_number,
    episodeNumber = episode_number,
    watchedAt = watched_at,
)
