package com.thomaskioko.tvmaniac.search.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface SearchRepository {
    public suspend fun search(query: String)

    public fun observeSearchResults(query: String): Flow<List<ShowEntity>>
}
