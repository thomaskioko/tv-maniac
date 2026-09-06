package com.thomaskioko.tvmaniac.lists.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.lists.api.ListShowDao
import com.thomaskioko.tvmaniac.lists.api.ListShowEntry
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultListShowDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ListShowDao {

    override fun observeActiveCountByListId(): Flow<Map<Long, Long>> =
        database.listShowsQueries.countActiveByListId()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows -> rows.associate { it.list_id to it.show_count } }

    override fun observeByShowId(showId: Long): Flow<List<ListShowEntry>> =
        database.listShowsQueries.selectByTmdbId(tmdb_id = Id<TmdbId>(showId))
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.map { row ->
                    ListShowEntry(
                        listId = row.list_id,
                        tmdbId = row.tmdb_id.id,
                        listedAt = row.listed_at,
                        pendingAction = row.pending_action,
                    )
                }
            }

    override fun upsert(listId: Long, tmdbId: Long, listedAt: String, pendingAction: String) {
        database.listShowsQueries.upsert(
            list_id = listId,
            tmdb_id = Id(tmdbId),
            listed_at = listedAt,
            pending_action = pendingAction,
        )
    }

    override fun upsertSynced(listId: Long, tmdbId: Long, listedAt: String) {
        database.listShowsQueries.upsertSynced(
            list_id = listId,
            tmdb_id = Id(tmdbId),
            listed_at = listedAt,
        )
    }

    override fun deleteSyncedByListId(listId: Long) {
        database.listShowsQueries.deleteSyncedByListId(list_id = listId)
    }

    override fun updatePendingAction(listId: Long, tmdbId: Long, pendingAction: String) {
        database.listShowsQueries.updatePendingAction(
            pending_action = pendingAction,
            list_id = listId,
            tmdb_id = Id(tmdbId),
        )
    }

    override fun deleteByListIdAndTmdbId(listId: Long, tmdbId: Long) {
        database.listShowsQueries.deleteByListIdAndTmdbId(
            list_id = listId,
            tmdb_id = Id(tmdbId),
        )
    }

    override fun deleteByListId(listId: Long) {
        database.listShowsQueries.deleteByListId(list_id = listId)
    }

    override fun deleteAll() {
        database.listShowsQueries.deleteAll()
    }

    override fun countPendingActions(): Long =
        database.listShowsQueries.countPendingActions().executeAsOne()
}
