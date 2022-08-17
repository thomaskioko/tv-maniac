package com.thomaskioko.tvmaniac.discover.api.cache

import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.Show_category
import kotlinx.coroutines.flow.Flow

interface DiscoverCategoryCache {

    fun insert(category: Show_category)

    fun observeShowsByCategoryID(id: Int): Flow<List<SelectShows>>
}
