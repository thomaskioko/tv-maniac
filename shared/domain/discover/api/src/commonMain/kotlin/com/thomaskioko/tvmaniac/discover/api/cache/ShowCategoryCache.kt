package com.thomaskioko.tvmaniac.discover.api.cache

import com.thomaskioko.tvmaniac.core.db.Show_category

interface ShowCategoryCache {
    fun insert(category: Show_category)
}
