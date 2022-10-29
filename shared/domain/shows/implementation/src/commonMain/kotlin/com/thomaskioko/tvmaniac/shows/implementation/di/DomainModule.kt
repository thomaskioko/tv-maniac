package com.thomaskioko.tvmaniac.shows.implementation.di

import com.thomaskioko.tvmaniac.shows.api.ObserveSyncImages
import com.thomaskioko.tvmaniac.shows.api.UpdateShowsInteractor
import com.thomaskioko.tvmaniac.shows.api.ObserveShowsInteractor
import com.thomaskioko.tvmaniac.shows.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowImageCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.shows.implementation.TmdbRepositoryImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.CategoryCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowImageCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.TvShowCacheImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val showDomainModule: Module = module {
    single<TmdbRepository> { TmdbRepositoryImpl(get(), get(), get(), get()) }

    single<TvShowCache> { TvShowCacheImpl(get()) }
    single<CategoryCache> { CategoryCacheImpl(get()) }
    single<ShowCategoryCache> { ShowCategoryCacheImpl(get()) }
    single<ShowImageCache> { ShowImageCacheImpl(get()) }

    factory { UpdateShowsInteractor(get(), get()) }
    factory { ObserveShowsInteractor(get(), get()) }
    factory { ObserveSyncImages(get(), get()) }
}
