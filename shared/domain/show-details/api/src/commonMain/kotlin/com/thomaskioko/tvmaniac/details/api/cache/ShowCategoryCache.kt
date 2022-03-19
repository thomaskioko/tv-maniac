package com.thomaskioko.tvmaniac.details.api.cache

import com.thomaskioko.tvmaniac.datasource.cache.SelectShows

interface ShowCategoryCache {

    fun getShowsByCategoryID(id: Int): List<SelectShows>
}
