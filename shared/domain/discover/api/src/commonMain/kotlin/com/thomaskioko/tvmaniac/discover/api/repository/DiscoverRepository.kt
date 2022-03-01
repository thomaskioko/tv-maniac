package com.thomaskioko.tvmaniac.discover.api.repository

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {

    fun observeShowsByCategoryID(
        categoryId: Int
    ): Flow<Resource<List<Show>>>
}
