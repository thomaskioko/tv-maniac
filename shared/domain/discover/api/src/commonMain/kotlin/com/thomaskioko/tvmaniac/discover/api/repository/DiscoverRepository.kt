package com.thomaskioko.tvmaniac.discover.api.repository

import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.datasource.cache.Show
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {

    fun observeShowsByCategoryID(
        categoryId: Int
    ): Flow<Resource<List<Show>>>

    suspend fun fetchDiscoverShows()
}
