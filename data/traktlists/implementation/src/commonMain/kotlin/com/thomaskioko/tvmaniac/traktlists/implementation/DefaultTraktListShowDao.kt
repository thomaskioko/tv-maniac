package com.thomaskioko.tvmaniac.traktlists.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.traktlists.api.TraktListShowDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListShowEntry
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktListShowDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktListShowDao {

    override fun observeActiveCountByListId(): Flow<Map<Long, Long>> =
        database.traktListShowsQueries.countActiveByListId()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows -> rows.associate { it.list_id to it.show_count } }

    override fun observeByShowId(showId: Long): Flow<List<TraktListShowEntry>> =
        database.traktListShowsQueries.selectByShowId(show_id = Id<TmdbId>(showId))
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.map { row ->
                    TraktListShowEntry(
                        listId = row.list_id,
                        traktId = row.trakt_id,
                        listedAt = row.listed_at,
                        pendingAction = row.pending_action,
                    )
                }
            }

    override fun upsert(listId: Long, traktId: Long, listedAt: String, pendingAction: String) {
        database.traktListShowsQueries.upsert(
            list_id = listId,
            trakt_id = traktId,
            listed_at = listedAt,
            pending_action = pendingAction,
        )
    }

    override fun upsertSynced(listId: Long, traktId: Long, listedAt: String) {
        database.traktListShowsQueries.upsertSynced(
            list_id = listId,
            trakt_id = traktId,
            listed_at = listedAt,
        )
    }

    override fun deleteSyncedByListId(listId: Long) {
        database.traktListShowsQueries.deleteSyncedByListId(list_id = listId)
    }

    override fun updatePendingAction(listId: Long, traktId: Long, pendingAction: String) {
        database.traktListShowsQueries.updatePendingAction(
            pending_action = pendingAction,
            list_id = listId,
            trakt_id = traktId,
        )
    }

    override fun deleteByListIdAndTraktId(listId: Long, traktId: Long) {
        database.traktListShowsQueries.deleteByListIdAndTraktId(
            list_id = listId,
            trakt_id = traktId,
        )
    }

    override fun deleteAll() {
        database.traktListShowsQueries.deleteAll()
    }

    override fun countPendingActions(): Long =
        database.traktListShowsQueries.countPendingActions().executeAsOne()
}
