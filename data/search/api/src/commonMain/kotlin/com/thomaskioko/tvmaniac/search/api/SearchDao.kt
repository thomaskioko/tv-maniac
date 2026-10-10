package com.thomaskioko.tvmaniac.search.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface SearchDao {
    public fun upsertResult(query: String, tmdbId: Long, position: Long, score: Double?)

    public fun observeResults(query: String): Flow<List<ShowEntity>>

    public fun deleteResults(query: String)

    public fun deleteAllResults()

    public fun upsertRecentSearch(query: String, searchedAt: Long)

    public fun observeRecentSearches(): Flow<List<String>>

    public fun deleteAllRecentSearches()
}
