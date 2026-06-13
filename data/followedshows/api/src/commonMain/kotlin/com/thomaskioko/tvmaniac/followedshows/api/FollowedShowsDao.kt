package com.thomaskioko.tvmaniac.followedshows.api

import kotlinx.coroutines.flow.Flow

public interface FollowedShowsDao {
    public fun entries(): List<FollowedShowEntry>
    public fun entriesObservable(): Flow<List<FollowedShowEntry>>

    /** Looks up an entry by the show's Trakt ID. Use when the caller holds a Trakt id (e.g. pending uploads/deletes). */
    public fun entryWithTraktId(traktId: Long): FollowedShowEntry?

    /** Looks up an entry by the boundary TMDB ID. Use when the caller holds a tmdb boundary id (e.g. addFollowedShow, removeFollowedShow). */
    public fun entryWithTmdbId(tmdbId: Long): FollowedShowEntry?

    /** Returns the Trakt ID for a show identified by its boundary TMDB ID, or null if not found. */
    public fun traktIdForTmdbId(tmdbId: Long): Long?

    public fun entriesWithNoPendingAction(): List<FollowedShowEntry>
    public fun entriesExcludingDeleted(): List<FollowedShowEntry>
    public fun entriesWithUploadPendingAction(): List<FollowedShowEntry>
    public fun entriesWithDeletePendingAction(): List<FollowedShowEntry>
    public fun upsert(entry: FollowedShowEntry): Long
    public fun updatePendingAction(id: Long, action: PendingAction)
    public fun deleteById(id: Long)
    public fun deleteByShowId(showId: Long)
    public fun deleteAll()
    public fun countPendingActions(): Long
}
