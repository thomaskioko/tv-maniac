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
import com.thomaskioko.tvmaniac.db.Followed_shows as DbFollowedShow

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
            .map { it.toEntry() }
    }

    override fun entriesObservable(): Flow<List<FollowedShowEntry>> {
        return queries.entries()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { list -> list.map { it.toEntry() } }
    }

    override fun entryWithTmdbId(tmdbId: Long): FollowedShowEntry? {
        return queries.entryWithTmdbId(tmdbId)
            .executeAsOneOrNull()
            ?.toEntry()
    }

    override fun entriesWithNoPendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithNoPendingAction()
            .executeAsList()
            .map { it.toEntry() }
    }

    override fun entriesWithUploadPendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithUploadPendingAction()
            .executeAsList()
            .map { it.toEntry() }
    }

    override fun entriesWithDeletePendingAction(): List<FollowedShowEntry> {
        return queries.entriesWithDeletePendingAction()
            .executeAsList()
            .map { it.toEntry() }
    }

    override fun upsert(entry: FollowedShowEntry): Long {
        queries.upsert(
            id = entry.id.takeIf { it > 0 },
            tmdbId = entry.tmdbId,
            followedAt = entry.followedAt.toEpochMilliseconds(),
            pendingAction = entry.pendingAction.value,
            traktId = entry.traktId,
        )
        return queries.entryWithTmdbId(entry.tmdbId).executeAsOneOrNull()?.id ?: 0
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

    private fun DbFollowedShow.toEntry(): FollowedShowEntry = FollowedShowEntry(
        id = id,
        tmdbId = tmdb_id,
        followedAt = Instant.fromEpochMilliseconds(followed_at),
        pendingAction = PendingAction.fromValue(pending_action),
        traktId = trakt_id,
    )
}
