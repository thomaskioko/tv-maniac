package com.thomaskioko.tvmaniac.details.implementation.cache

import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.details.api.cache.ShowCategoryCache

class ShowCategoryCacheImpl(
    private val database: TvManiacDatabase
) : ShowCategoryCache {

    private val showCategoryQuery get() = database.showCategoryQueries

    override fun getShowsByCategoryID(id: Int): List<SelectShows> {
        return showCategoryQuery.selectShows(
            category_id = id.toLong()
        ).executeAsList()
    }
}
