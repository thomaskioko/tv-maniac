package com.thomaskioko.tvmaniac.continuewatching.api

import kotlinx.coroutines.flow.Flow

public interface ContinueWatchingDao {

    public fun entries(): List<ContinueWatchingEntry>

    public fun entriesObservable(): Flow<List<ContinueWatchingEntry>>

    public fun showIdsMissingShowDetails(): List<Long>

    public fun upsert(entry: ContinueWatchingEntry)

    public fun upsertPlaceholder(showId: Long, tmdbId: Long?, title: String?, year: Long?)

    public fun deleteByShowId(showId: Long)

    public fun deleteAll()

    public fun insertMembershipFromWatchedEpisodes()
}
