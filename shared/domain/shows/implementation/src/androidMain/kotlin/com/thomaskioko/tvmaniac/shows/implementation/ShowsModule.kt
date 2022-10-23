package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.ObserveSyncImages
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.shared.core.ui.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.shared.core.ui.di.IoDispatcher
import com.thomaskioko.tvmaniac.shows.api.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.shows.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowImageCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.shows.implementation.cache.CategoryCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowImageCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShowsModule {

    @Singleton
    @Provides
    fun provideTvShowCache(database: TvManiacDatabase): TvShowCache {
        return TvShowCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideCategoryCache(
        database: TvManiacDatabase
    ): CategoryCache = CategoryCacheImpl(database)

    @Singleton
    @Provides
    fun provideSeasonWithDiscoverCategoryCache(
        database: TvManiacDatabase
    ): ShowCategoryCache = ShowCategoryCacheImpl(database)

    @Singleton
    @Provides
    fun provideShowImageCache(database: TvManiacDatabase): ShowImageCache {
        return ShowImageCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideObserveShowsByCategoryInteractor(
        repository: TraktRepository,
        tmdbRepository: TmdbRepository
    ): ObserveDiscoverShowsInteractor = ObserveDiscoverShowsInteractor(repository,tmdbRepository)

    @Singleton
    @Provides
    fun provideObserveSyncImages(
        tmdbRepository: TmdbRepository
    ): ObserveSyncImages = ObserveSyncImages(tmdbRepository)

    @Singleton
    @Provides
    fun provideTvShowsRepository(
        tmdbService: TmdbService,
        tvShowCache: TvShowCache,
        imageCache: ShowImageCache,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher computationDispatcher: CoroutineDispatcher
    ): TmdbRepository =
        TmdbRepositoryImpl(
            apiService = tmdbService,
            tvShowCache = tvShowCache,
            imageCache = imageCache,
            dispatcher = ioDispatcher,
            computationDispatcher = computationDispatcher
        )

}
