package com.thomaskioko.tvmaniac.data.category.implementation

import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase

class CategoryCacheImpl(
    private val database: TvManiacDatabase
) : CategoryCache {

    private val showCategoryQuery get() = database.showCategoryQueries

    override fun insert(category: Show_category) {
        database.transaction {
            showCategoryQuery.insertOrReplace(
                trakt_id = category.trakt_id,
                category_id = category.category_id
            )
        }
    }

    override fun insert(category: List<Show_category>) {
        category.forEach { insert(it) }
    }
}
