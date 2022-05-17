package com.thomaskioko.tvmaniac.discover.implementation.di

import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.discover.api.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.DiscoverCategoryCache
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import com.thomaskioko.tvmaniac.discover.implementation.CategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.DiscoverCategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.DiscoverRepositoryImpl
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiscoverModule {

    @Singleton
    @Provides
    fun provideDiscoverRepository(
        tvShowsService: TvShowsService,
        tvShowCache: TvShowCache,
        categoryCache: CategoryCache,
        discoverCategoryCache: DiscoverCategoryCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): DiscoverRepository =
        DiscoverRepositoryImpl(
            apiService = tvShowsService,
            tvShowCache = tvShowCache,
            categoryCache = categoryCache,
            discoverCategoryCache = discoverCategoryCache,
            dispatcher = ioDispatcher
        )

    @Singleton
    @Provides
    fun provideCategoryCache(database: TvManiacDatabase): CategoryCache {
        return CategoryCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideSeasonWithDiscoverCategoryCache(database: TvManiacDatabase): DiscoverCategoryCache {
        return DiscoverCategoryCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideObserveShowsByCategoryInteractor(
        repository: DiscoverRepository
    ): ObserveDiscoverShowsInteractor = ObserveDiscoverShowsInteractor(repository)
}
