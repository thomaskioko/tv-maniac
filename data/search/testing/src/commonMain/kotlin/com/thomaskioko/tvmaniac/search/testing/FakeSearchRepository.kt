package com.thomaskioko.tvmaniac.search.testing

import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeSearchRepository : SearchRepository {
    private var stateFlow = MutableStateFlow<List<ShowEntity>>(emptyList())

    public suspend fun setSearchResult(result: List<ShowEntity>) {
        stateFlow.emit(result)
    }

    override suspend fun search(query: String) {
    }

    override fun observeSearchResults(query: String): Flow<List<ShowEntity>> = stateFlow.asStateFlow()
}
