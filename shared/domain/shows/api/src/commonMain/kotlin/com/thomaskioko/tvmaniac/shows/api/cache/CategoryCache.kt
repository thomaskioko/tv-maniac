package com.thomaskioko.tvmaniac.shows.api.cache

import com.thomaskioko.tvmaniac.core.db.Category


interface CategoryCache {

    fun insert(category: Category)

    fun insert(categoryList: List<Category>)

    fun selectAll(): List<Category>
}
