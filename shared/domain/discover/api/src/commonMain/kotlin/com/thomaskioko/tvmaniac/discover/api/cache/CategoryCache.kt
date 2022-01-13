package com.thomaskioko.tvmaniac.discover.api.cache

import com.thomaskioko.tvmaniac.datasource.cache.Category

interface CategoryCache {

    fun insert(category: Category)

    fun insert(categoryList: List<Category>)

    fun selectAll(): List<Category>
}
