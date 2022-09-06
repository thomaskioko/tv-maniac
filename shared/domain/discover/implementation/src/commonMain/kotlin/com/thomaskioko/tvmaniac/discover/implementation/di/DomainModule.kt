package com.thomaskioko.tvmaniac.discover.implementation.di

import com.thomaskioko.tvmaniac.discover.api.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.DiscoverCategoryCache
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import com.thomaskioko.tvmaniac.discover.implementation.CategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.DiscoverCategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.DiscoverRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val discoverDomainModule: Module = module {
    single<DiscoverRepository> {
        DiscoverRepositoryImpl(get(), get(), get(), get(), get(), get())
    }

    factory { ObserveDiscoverShowsInteractor(get()) }

    single<CategoryCache> { CategoryCacheImpl(get()) }
    single<DiscoverCategoryCache> { DiscoverCategoryCacheImpl(get()) }
}
