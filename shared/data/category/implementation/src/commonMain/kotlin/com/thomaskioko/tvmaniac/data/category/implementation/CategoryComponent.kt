package com.thomaskioko.tvmaniac.data.category.implementation

import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import me.tatarka.inject.annotations.Provides

interface CategoryComponent {

    @Provides
    fun provideCategoryCache(bind: CategoryCacheImpl): CategoryCache = bind
}
