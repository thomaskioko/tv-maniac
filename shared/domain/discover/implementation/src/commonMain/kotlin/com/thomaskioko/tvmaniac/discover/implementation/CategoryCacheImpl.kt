package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.datasource.cache.Category
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache

class CategoryCacheImpl(
    private val database: TvManiacDatabase
) : CategoryCache {

    private val categoryQueries get() = database.categoryQueries

    override fun insert(category: Category) {
        categoryQueries.insertOrReplace(
            id = category.id,
            name = category.name
        )
    }

    override fun insert(categoryList: List<Category>) {
        categoryList.forEach { insert(it) }
    }

    override fun selectAll(): List<Category> = categoryQueries.selectAll().executeAsList()
}
