package com.thomaskioko.tvmaniac.followedshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.FollowedShowsQueries
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFollowedShowsDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : FollowedShowsDao {

    private val queries: FollowedShowsQueries
        get() = database.followedShowsQueries

    override fun entries(): List<FollowedShowEntry> {
        return queries.entries()
            .executeAsList()
            .map { toEntry(it.followed_id, it.tmdb_id, it.followed_at, it.pending_action, it.trakt_id) }
    }

    override fun entriesObservable(): Flow<List<FollowedShowEntry>> {
        return queries.entries()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { list -> list.map { toEntry(it.followed_id, it.tmdb_id, it.followed_at, it.pending_action, it.trakt_id) } }
    }

    override fun entryWithTmdbId(tmdbId: Long): FollowedShowEntry? {
        return queries.entryWithTmdbId(tmdbId)
            .executeAsOneOrNull()
            ?.let { toEntry(it.followed_id, it.tmdb_id, it.followed_at, it.pending_action, it.trakt_id) }
    }

    override fun entriesWithNoPendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithNoPendingAction()
            .executeAsList()
            .map { toEntry(it.followed_id, it.tmdb_id, it.followed_at, it.pending_action, it.trakt_id) }
    }

    override fun entriesWithUploadPendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithUploadPendingAction()
            .executeAsList()
            .map { toEntry(it.followed_id, it.tmdb_id, it.followed_at, it.pending_action, it.trakt_id) }
    }

    override fun entriesWithDeletePendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithDeletePendingAction()
            .executeAsList()
            .map { toEntry(it.followed_id, it.tmdb_id, it.followed_at, it.pending_action, it.trakt_id) }
    }

    override fun upsert(entry: FollowedShowEntry): Long {
        queries.upsert(
            id = entry.id.takeIf { it > 0 },
            tmdbId = entry.tmdbId,
            followedAt = entry.followedAt.toEpochMilliseconds(),
            pendingAction = entry.pendingAction.value,
            traktId = entry.traktId,
        )
        return queries.entryWithTmdbId(entry.tmdbId).executeAsOneOrNull()?.followed_id ?: 0
    }

    override fun updatePendingAction(id: Long, action: PendingAction) {
        val _ = queries.updatePendingAction(pendingAction = action.value, id = id)
    }

    override fun deleteById(id: Long) {
        val _ = queries.deleteById(id)
    }

    override fun deleteByTmdbId(tmdbId: Long) {
        val _ = queries.deleteByTmdbId(tmdbId)
    }

    private fun toEntry(
        followedId: Long,
        tmdbId: Long,
        followedAt: Long,
        pendingAction: String,
        traktId: Long?,
    ): FollowedShowEntry = FollowedShowEntry(
        id = followedId,
        tmdbId = tmdbId,
        followedAt = Instant.fromEpochMilliseconds(followedAt),
        pendingAction = PendingAction.fromValue(pendingAction),
        traktId = traktId,
    )
}
