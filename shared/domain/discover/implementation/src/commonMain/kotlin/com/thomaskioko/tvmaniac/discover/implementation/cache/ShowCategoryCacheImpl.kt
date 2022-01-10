package com.thomaskioko.tvmaniac.discover.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.datasource.cache.SelectShows
import com.thomaskioko.tvmaniac.datasource.cache.Show_category
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import kotlinx.coroutines.flow.Flow

class ShowCategoryCacheImpl(
    private val database: TvManiacDatabase
) : ShowCategoryCache {

    private val showCategoryQuery get() = database.showCategoryQueries

    override fun insert(category: Show_category) {
        showCategoryQuery.insertOrReplace(
            show_id = category.show_id,
            category_id = category.category_id
        )
    }

    override fun getShowsByCategoryID(id: Int): List<SelectShows> {
        return showCategoryQuery.selectShows(
            category_id = id.toLong()
        ).executeAsList()
    }

    override fun observeShowsByCategoryID(id: Int): Flow<List<SelectShows>> {
        return showCategoryQuery.selectShows(category_id = id.toLong())
            .asFlow()
            .mapToList()
    }
}
