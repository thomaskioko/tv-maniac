package com.thomaskioko.tvmaniac.data.category.implementation

import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import me.tatarka.inject.annotations.Inject

@Inject
class CategoryCacheImpl(
    private val database: TvManiacDatabase,
) : CategoryCache {

    private val showCategoryQuery get() = database.show_categoryQueries

    override fun upsert(category: Show_category) {
        database.transaction {
            showCategoryQuery.insertOrReplace(
                id = category.id,
                category_id = category.category_id,
            )
        }
    }

    override fun upsert(category: List<Show_category>) {
        category.forEach { upsert(it) }
    }
}
