package com.thomaskioko.tvmaniac.followedshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.FollowedShowsQueries
import com.thomaskioko.tvmaniac.db.Id
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
            .map { toEntry(it.followed_id, it.trakt_id.id, it.tmdb_id?.id, it.followed_at, it.pending_action) }
    }

    override fun entriesObservable(): Flow<List<FollowedShowEntry>> {
        return queries.entries()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { list -> list.map { toEntry(it.followed_id, it.trakt_id.id, it.tmdb_id?.id, it.followed_at, it.pending_action) } }
    }

    override fun entryWithTraktId(traktId: Long): FollowedShowEntry? {
        return queries.entryWithTraktId(Id(traktId))
            .executeAsOneOrNull()
            ?.let { toEntry(it.followed_id, it.trakt_id.id, it.tmdb_id?.id, it.followed_at, it.pending_action) }
    }

    override fun entriesWithNoPendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithNoPendingAction()
            .executeAsList()
            .map { toEntry(it.followed_id, it.trakt_id.id, it.tmdb_id?.id, it.followed_at, it.pending_action) }
    }

    override fun entriesWithUploadPendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithUploadPendingAction()
            .executeAsList()
            .map { toEntry(it.followed_id, it.trakt_id.id, it.tmdb_id?.id, it.followed_at, it.pending_action) }
    }

    override fun entriesWithDeletePendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithDeletePendingAction()
            .executeAsList()
            .map { toEntry(it.followed_id, it.trakt_id.id, it.tmdb_id?.id, it.followed_at, it.pending_action) }
    }

    override fun upsert(entry: FollowedShowEntry): Long {
        queries.upsert(
            id = entry.id.takeIf { it > 0 },
            traktId = Id(entry.traktId),
            tmdbId = entry.tmdbId?.let { Id(it) },
            followedAt = entry.followedAt.toEpochMilliseconds(),
            pendingAction = entry.pendingAction.value,
        )
        return queries.entryWithTraktId(Id(entry.traktId)).executeAsOneOrNull()?.followed_id ?: 0
    }

    override fun updatePendingAction(id: Long, action: PendingAction) {
        val _ = queries.updatePendingAction(pendingAction = action.value, id = id)
    }

    override fun deleteById(id: Long) {
        val _ = queries.deleteById(id)
    }

    override fun deleteByTraktId(traktId: Long) {
        val _ = queries.deleteByTraktId(Id(traktId))
    }

    private fun toEntry(
        followedId: Long,
        traktId: Long,
        tmdbId: Long?,
        followedAt: Long,
        pendingAction: String,
    ): FollowedShowEntry = FollowedShowEntry(
        id = followedId,
        traktId = traktId,
        tmdbId = tmdbId,
        followedAt = Instant.fromEpochMilliseconds(followedAt),
        pendingAction = PendingAction.fromValue(pendingAction),
    )
}
