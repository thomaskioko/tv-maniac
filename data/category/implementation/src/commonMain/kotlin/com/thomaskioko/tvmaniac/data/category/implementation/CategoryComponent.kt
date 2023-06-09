package com.thomaskioko.tvmaniac.data.category.implementation

import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface CategoryComponent {

    @ApplicationScope
    @Provides
    fun provideCategoryCache(bind: CategoryCacheImpl): CategoryCache = bind
}
