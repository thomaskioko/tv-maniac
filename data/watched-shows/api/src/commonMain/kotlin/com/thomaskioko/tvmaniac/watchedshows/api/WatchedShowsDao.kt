package com.thomaskioko.tvmaniac.watchedshows.api

import kotlinx.coroutines.flow.Flow

public interface WatchedShowsDao {

    public fun entries(): List<WatchedShowEntry>

    public fun entriesObservable(): Flow<List<WatchedShowEntry>>

    public fun upsert(entry: WatchedShowEntry)

    public fun deleteByTraktId(traktId: Long)

    public fun deleteAll()
}
