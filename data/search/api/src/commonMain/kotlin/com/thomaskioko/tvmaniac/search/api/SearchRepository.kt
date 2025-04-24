package com.thomaskioko.tvmaniac.search.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
  suspend fun search(query: String)

  fun observeSearchResults(query: String): Flow<List<ShowEntity>>
}
