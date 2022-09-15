package com.thomaskioko.tvmaniac.discover.implementation.di

import com.thomaskioko.tvmaniac.discover.api.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.ObserveSyncImages
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import com.thomaskioko.tvmaniac.discover.implementation.CategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.DiscoverRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val discoverDomainModule: Module = module {
    single<DiscoverRepository> {
        DiscoverRepositoryImpl(get(), get(), get(), get(), get(), get())
    }

    factory { ObserveDiscoverShowsInteractor(get()) }
    factory { ObserveSyncImages(get()) }

    single<CategoryCache> { CategoryCacheImpl(get()) }
    single<ShowCategoryCache> { ShowCategoryCacheImpl(get()) }
}
