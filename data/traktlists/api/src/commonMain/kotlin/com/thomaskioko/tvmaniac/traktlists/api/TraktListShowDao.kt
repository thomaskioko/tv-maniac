package com.thomaskioko.tvmaniac.traktlists.api

import kotlinx.coroutines.flow.Flow

public interface TraktListShowDao {

    public fun observeByShowId(showId: Long): Flow<List<TraktListShowEntry>>

    public fun observeActiveCountByListId(): Flow<Map<Long, Long>>

    public fun upsert(listId: Long, traktId: Long, listedAt: String, pendingAction: String)

    public fun upsertSynced(listId: Long, traktId: Long, listedAt: String)

    public fun deleteSyncedByListId(listId: Long)

    public fun updatePendingAction(listId: Long, traktId: Long, pendingAction: String)

    public fun deleteByListIdAndTraktId(listId: Long, traktId: Long)

    public fun deleteAll()

    public fun countPendingActions(): Long
}
