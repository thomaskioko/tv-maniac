package com.thomaskioko.tvmaniac.datasource.cache.show_category

import com.thomaskioko.tvmaniac.datasource.cache.SelectShows
import com.thomaskioko.tvmaniac.datasource.cache.Show_category
import kotlinx.coroutines.flow.Flow

interface ShowCategoryCache {

    fun insert(category: Show_category)

    fun getShowsByCategoryID(id: Int): List<SelectShows>

    fun observeShowsByCategoryID(id: Int): Flow<List<SelectShows>>
}
