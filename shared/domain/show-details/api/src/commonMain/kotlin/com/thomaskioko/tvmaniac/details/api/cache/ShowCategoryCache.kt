package com.thomaskioko.tvmaniac.details.api.cache

import com.thomaskioko.tvmaniac.core.db.SelectShows

interface ShowCategoryCache {

    fun getShowsByCategoryID(id: Int): List<SelectShows>
}
