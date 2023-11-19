package com.thomaskioko.tvmaniac.category.api.cache

import com.thomaskioko.tvmaniac.core.db.Show_category

interface CategoryCache {
    fun upsert(category: Show_category)
    fun upsert(category: List<Show_category>)
}
