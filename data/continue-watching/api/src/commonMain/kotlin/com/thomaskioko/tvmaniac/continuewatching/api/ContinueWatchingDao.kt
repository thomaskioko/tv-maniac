package com.thomaskioko.tvmaniac.continuewatching.api

import kotlinx.coroutines.flow.Flow

public interface ContinueWatchingDao {

    public fun entries(): List<ContinueWatchingEntry>

    public fun entriesObservable(): Flow<List<ContinueWatchingEntry>>

    public fun traktIdsMissingShowDetails(): List<Long>

    public fun upsert(entry: ContinueWatchingEntry)

    public fun deleteByTraktId(traktId: Long)

    public fun deleteAll()
}
