package com.thomaskioko.tvmaniac.datasource.cache.category

import com.thomaskioko.tvmaniac.datasource.cache.Category

interface CategoryCache {

    fun insert(category: Category)

    fun insert(categoryList: List<Category>)

    fun selectAll(): List<Category>
}
