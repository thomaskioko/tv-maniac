package com.thomaskioko.tvmaniac.shows.implementation.cache

import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.cache.ShowCategoryCache

class ShowCategoryCacheImpl(
    private val database: TvManiacDatabase
) : ShowCategoryCache {

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
