package com.thomaskioko.tvmaniac.search.testing

import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeSearchRepository : SearchRepository {
    private var stateFlow = MutableStateFlow<List<ShowEntity>>(emptyList())
    private var searchError: Throwable? = null

    public suspend fun setSearchResult(result: List<ShowEntity>) {
        stateFlow.emit(result)
    }

    public fun setSearchError(error: Throwable?) {
        searchError = error
    }

    override suspend fun search(query: String) {
        searchError?.let { throw it }
    }

    override fun observeSearchResults(query: String): Flow<List<ShowEntity>> = stateFlow.asStateFlow()
}
