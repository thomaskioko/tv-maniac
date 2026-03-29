package com.thomaskioko.tvmaniac.traktlists.api

import kotlinx.coroutines.flow.Flow

public interface TraktListShowDao {

    public fun observeByShowTraktId(showTraktId: Long): Flow<List<TraktListShowEntry>>

    public fun upsert(listId: Long, showTraktId: Long, listedAt: String, pendingAction: String)

    public fun updatePendingAction(listId: Long, showTraktId: Long, pendingAction: String)

    public fun deleteByListIdAndShowId(listId: Long, showTraktId: Long)
}
