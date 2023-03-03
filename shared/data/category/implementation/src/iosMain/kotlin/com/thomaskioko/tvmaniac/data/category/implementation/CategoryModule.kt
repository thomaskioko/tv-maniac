package com.thomaskioko.tvmaniac.data.category.implementation

import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun categoryDataModule() : Module = module {
    single<CategoryCache> { CategoryCacheImpl(get()) }
}
