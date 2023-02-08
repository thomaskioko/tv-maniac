package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
import com.thomaskioko.tvmaniac.core.util.scope.IoDispatcher
import com.thomaskioko.tvmaniac.shows.api.ShowsStateMachine
import com.thomaskioko.tvmaniac.shows.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShowsModule {

    @Singleton
    @Provides
    fun provideTvShowCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TvShowCache = TvShowCacheImpl(database, ioDispatcher)


    @Singleton
    @Provides
    fun provideSeasonWithDiscoverCategoryCache(
        database: TvManiacDatabase
    ): ShowCategoryCache = ShowCategoryCacheImpl(database)


    @Singleton
    @Provides
    fun provideShowsStateMachine(
        traktShowRepository: TraktShowRepository,
        tmdbRepository: TmdbRepository,
    ): ShowsStateMachine = ShowsStateMachine(traktShowRepository, tmdbRepository)

    @Singleton
    @Provides
    fun provideTvShowsRepository(
        tmdbService: TmdbService,
        tvShowCache: TvShowCache,
        imageCache: ShowImageCache,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): TmdbRepository =
        TmdbRepositoryImpl(
            apiService = tmdbService,
            tvShowCache = tvShowCache,
            imageCache = imageCache,
            dispatcher = ioDispatcher
        )

}
