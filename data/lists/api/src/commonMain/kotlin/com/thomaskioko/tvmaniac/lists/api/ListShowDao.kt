package com.thomaskioko.tvmaniac.lists.api

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

public interface ListShowDao {

    public fun observeByShowId(showId: Long): Flow<List<ListShowEntry>>

    public fun observeActiveCountByListId(): Flow<Map<Long, Long>>

    public fun selectPendingForSyncedLists(): List<ListShowEntry>

    public fun getPagedShows(listId: Long): PagingSource<Int, ListShowItem>

    public fun getTmdbIdsMissingPoster(listId: Long): List<Long>

    public fun upsert(listId: Long, tmdbId: Long, listedAt: String, pendingAction: String)

    public fun upsertSynced(listId: Long, tmdbId: Long, listedAt: String)

    public fun deleteSyncedByListId(listId: Long)

    public fun updatePendingAction(listId: Long, tmdbId: Long, pendingAction: String)

    public fun deleteByListIdAndTmdbId(listId: Long, tmdbId: Long)

    public fun deleteAll()

    public fun countPendingActions(): Long
}
