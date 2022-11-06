package com.thomaskioko.tvmaniac.shows.implementation.di

import com.thomaskioko.tvmaniac.shows.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.shows.implementation.TmdbRepositoryImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.TvShowCacheImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val showDomainModule: Module = module {
    single<TmdbRepository> { TmdbRepositoryImpl(get(), get(), get(), get()) }
    single<TvShowCache> { TvShowCacheImpl(get()) }
    single<ShowCategoryCache> { ShowCategoryCacheImpl(get()) }
}
