package com.thomaskioko.tvmaniac.search.testing

import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

public class FakeSearchRepository : SearchRepository {
    private val resultsByQuery = MutableStateFlow<Map<String, List<ShowEntity>>>(emptyMap())
    private val recentSearches = MutableStateFlow<List<String>>(emptyList())
    private var searchError: Throwable? = null
    private var searchDelay: Duration = Duration.ZERO

    private val _searchCalls = mutableListOf<Pair<String, Boolean>>()
    public val searchCalls: List<Pair<String, Boolean>> get() = _searchCalls

    public fun setSearchResult(query: String, result: List<ShowEntity>) {
        resultsByQuery.value += (query to result)
    }

    public fun setSearchError(error: Throwable?) {
        searchError = error
    }

    public fun setSearchDelay(delay: Duration) {
        searchDelay = delay
    }

    public fun setRecentSearches(queries: List<String>) {
        recentSearches.value = queries
    }

    override suspend fun search(query: String, forceRefresh: Boolean) {
        _searchCalls += (query to forceRefresh)
        delay(searchDelay)
        searchError?.let { throw it }
    }

    override fun observeSearchResults(query: String): Flow<List<ShowEntity>> =
        resultsByQuery.asStateFlow().map { it[query] ?: emptyList() }

    override fun observeRecentSearches(): Flow<List<String>> = recentSearches.asStateFlow()

    override suspend fun saveRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty() || trimmed in recentSearches.value) return
        recentSearches.value = listOf(trimmed) + recentSearches.value
    }

    override suspend fun clearRecentSearches() {
        recentSearches.value = emptyList()
    }
}
