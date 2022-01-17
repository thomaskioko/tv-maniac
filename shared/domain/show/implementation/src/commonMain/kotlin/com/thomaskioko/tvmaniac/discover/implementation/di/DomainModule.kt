package com.thomaskioko.tvmaniac.discover.implementation.di

import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.discover.api.interactor.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.discover.implementation.cache.CategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.repository.TvShowsRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val discoverDomainModule: Module = module {
    single<TvShowsRepository> {
        TvShowsRepositoryImpl(get(), get(), get(), get(), get(), get())
    }

    factory { ObserveDiscoverShowsInteractor(get()) }

    single<TvShowCache> { TvShowCacheImpl(get()) }
    single<ShowCategoryCache> { ShowCategoryCacheImpl(get()) }
    single<CategoryCache> { CategoryCacheImpl(get()) }
}
