package com.thomaskioko.tvmaniac.discover.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.discover.api.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.ObserveSyncImages
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import com.thomaskioko.tvmaniac.discover.implementation.CategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.DiscoverRepositoryImpl
import com.thomaskioko.tvmaniac.discover.implementation.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.showcommon.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.trakt.api.TraktService
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
        traktService: TraktService,
        tmdbService: TmdbService,
        tvShowCache: TvShowCache,
        categoryCache: CategoryCache,
        showCategoryCache: ShowCategoryCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): DiscoverRepository =
        DiscoverRepositoryImpl(
            traktService = traktService,
            tmdbService = tmdbService,
            tvShowCache = tvShowCache,
            categoryCache = categoryCache,
            showCategoryCache = showCategoryCache,
            dispatcher = ioDispatcher
        )

    @Singleton
    @Provides
    fun provideCategoryCache(database: TvManiacDatabase): CategoryCache {
        return CategoryCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideSeasonWithDiscoverCategoryCache(database: TvManiacDatabase): ShowCategoryCache {
        return ShowCategoryCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideObserveShowsByCategoryInteractor(
        repository: DiscoverRepository,
    ): ObserveDiscoverShowsInteractor = ObserveDiscoverShowsInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveSyncImages(
        tmdbRepository: TmdbRepository
    ): ObserveSyncImages = ObserveSyncImages(tmdbRepository)
}
