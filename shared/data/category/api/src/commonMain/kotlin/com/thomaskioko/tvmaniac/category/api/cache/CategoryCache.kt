package com.thomaskioko.tvmaniac.category.api.cache

import com.thomaskioko.tvmaniac.core.db.Show_category

interface CategoryCache {
    fun insert(category: Show_category)
    fun insert(category: List<Show_category>)
}
