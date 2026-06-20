package com.thomaskioko.tvmaniac.followedshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.FollowedShowsQueries
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFollowedShowsDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
) : FollowedShowsDao {

    private val queries: FollowedShowsQueries
        get() = database.followedShowsQueries

    override fun entries(): List<FollowedShowEntry> {
        return queries.entries()
            .executeAsList()
            .map {
                toEntry(
                    followedId = it.followed_id,
                    showId = it.tmdb_id.id,
                    tmdbId = it.tmdb_id.id,
                    followedAt = it.followed_at,
                    pendingAction = it.pending_action,
                )
            }
    }

    override fun entriesObservable(): Flow<List<FollowedShowEntry>> {
        return queries.entries()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { list ->
                list.map {
                    toEntry(
                        followedId = it.followed_id,
                        showId = it.tmdb_id.id,
                        tmdbId = it.tmdb_id.id,
                        followedAt = it.followed_at,
                        pendingAction = it.pending_action,
                    )
                }
            }
    }

    override fun entryWithTraktId(traktId: Long): FollowedShowEntry? {
        val internalShowId = showIdResolver.showIdForTraktId(traktId) ?: return null
        return queries.entryWithShowId(internalShowId)
            .executeAsOneOrNull()
            ?.let {
                toEntry(
                    followedId = it.followed_id,
                    showId = it.tmdb_id?.id ?: it.trakt_id,
                    tmdbId = it.tmdb_id?.id,
                    followedAt = it.followed_at,
                    pendingAction = it.pending_action,
                )
            }
    }

    override fun entryWithTmdbId(tmdbId: Long): FollowedShowEntry? {
        val internalShowId = showIdResolver.showIdForTmdbId(tmdbId) ?: return null
        return queries.entryWithShowId(internalShowId)
            .executeAsOneOrNull()
            ?.let {
                toEntry(
                    followedId = it.followed_id,
                    showId = it.tmdb_id?.id ?: it.trakt_id,
                    tmdbId = it.tmdb_id?.id,
                    followedAt = it.followed_at,
                    pendingAction = it.pending_action,
                )
            }
    }

    override fun traktIdForTmdbId(tmdbId: Long): Long? {
        val internalShowId = showIdResolver.showIdForTmdbId(tmdbId) ?: return null
        return queries.entryWithShowId(internalShowId)
            .executeAsOneOrNull()
            ?.trakt_id
    }

    override fun entriesWithNoPendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithNoPendingAction()
            .executeAsList()
            .map {
                toEntry(
                    followedId = it.followed_id,
                    showId = it.tmdb_id.id,
                    tmdbId = it.tmdb_id.id,
                    followedAt = it.followed_at,
                    pendingAction = it.pending_action,
                )
            }
    }

    override fun entriesExcludingDeleted(): List<FollowedShowEntry> {
        return queries.entriesExcludingDeleted()
            .executeAsList()
            .map {
                toEntry(
                    followedId = it.followed_id,
                    showId = it.tmdb_id?.id ?: it.trakt_id,
                    tmdbId = it.tmdb_id?.id,
                    followedAt = it.followed_at,
                    pendingAction = it.pending_action,
                )
            }
    }

    override fun entriesWithUploadPendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithUploadPendingAction()
            .executeAsList()
            .map {
                toEntry(
                    followedId = it.followed_id,
                    showId = it.tmdb_id?.id ?: it.trakt_id,
                    tmdbId = it.tmdb_id?.id,
                    followedAt = it.followed_at,
                    pendingAction = it.pending_action,
                )
            }
    }

    override fun entriesWithDeletePendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithDeletePendingAction()
            .executeAsList()
            .map {
                toEntry(
                    followedId = it.followed_id,
                    showId = it.tmdb_id?.id ?: it.trakt_id,
                    tmdbId = it.tmdb_id?.id,
                    followedAt = it.followed_at,
                    pendingAction = it.pending_action,
                )
            }
    }

    override fun upsert(entry: FollowedShowEntry): Long {
        val showId = showIdResolver.showIdForTmdbId(entry.showId) ?: return 0
        queries.upsert(
            showId = showId,
            tmdbId = entry.tmdbId?.let { Id(it) },
            followedAt = entry.followedAt.toEpochMilliseconds(),
            pendingAction = entry.pendingAction.value,
        )
        return queries.entryWithShowId(showId).executeAsOneOrNull()?.followed_id ?: 0
    }

    override fun updatePendingAction(id: Long, action: PendingAction) {
        val _ = queries.updatePendingAction(pendingAction = action.value, id = id)
    }

    override fun deleteById(id: Long) {
        val _ = queries.deleteById(id)
    }

    override fun deleteByShowId(showId: Long) {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return
        val _ = queries.deleteByShowId(internalShowId)
    }

    override fun deleteAll() {
        queries.deleteAll()
    }

    override fun countPendingActions(): Long = queries.countEntriesWithPendingAction().executeAsOne()

    private fun toEntry(
        followedId: Long,
        showId: Long,
        tmdbId: Long?,
        followedAt: Long,
        pendingAction: String,
    ): FollowedShowEntry = FollowedShowEntry(
        id = followedId,
        showId = showId,
        tmdbId = tmdbId,
        followedAt = Instant.fromEpochMilliseconds(followedAt),
        pendingAction = PendingAction.fromValue(pendingAction),
    )
}
