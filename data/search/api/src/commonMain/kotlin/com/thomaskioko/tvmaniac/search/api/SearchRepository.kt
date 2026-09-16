package com.thomaskioko.tvmaniac.search.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface SearchRepository {
    public suspend fun search(query: String, forceRefresh: Boolean)

    public fun observeSearchResults(query: String): Flow<List<ShowEntity>>

    public fun observeRecentSearches(): Flow<List<String>>

    public suspend fun saveRecentSearch(query: String)

    public suspend fun clearRecentSearches()
}
