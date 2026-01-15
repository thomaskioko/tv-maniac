package com.thomaskioko.tvmaniac.followedshows.api

import kotlinx.coroutines.flow.Flow

public interface FollowedShowsDao {
    public fun entries(): List<FollowedShowEntry>
    public fun entriesObservable(): Flow<List<FollowedShowEntry>>
    public fun entryWithTraktId(traktId: Long): FollowedShowEntry?
    public fun entriesWithNoPendingAction(): List<FollowedShowEntry>
    public fun entriesWithUploadPendingAction(): List<FollowedShowEntry>
    public fun entriesWithDeletePendingAction(): List<FollowedShowEntry>
    public fun upsert(entry: FollowedShowEntry): Long
    public fun updatePendingAction(id: Long, action: PendingAction)
    public fun deleteById(id: Long)
    public fun deleteByTraktId(traktId: Long)
}
