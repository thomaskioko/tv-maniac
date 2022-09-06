package com.thomaskioko.tvmaniac.discover.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.discover.api.cache.DiscoverCategoryCache
import kotlinx.coroutines.flow.Flow

class DiscoverCategoryCacheImpl(
    private val database: TvManiacDatabase
) : DiscoverCategoryCache {

    private val showCategoryQuery get() = database.showCategoryQueries

    override fun insert(category: Show_category) {
        showCategoryQuery.insertOrReplace(
            id = category.id,
            category_id = category.category_id
        )
    }

    override fun observeShowsByCategoryID(id: Int): Flow<List<SelectShows>> {
        return showCategoryQuery.selectShows(id)
            .asFlow()
            .mapToList()
    }
}
