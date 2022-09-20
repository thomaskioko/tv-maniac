package com.thomaskioko.tvmaniac.shared.domain.trailers.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.ObserveTrailerInteractor
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerCache
import com.thomaskioko.tvmaniac.shared.domain.trailers.implementation.TrailerCacheImpl
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.shared.domain.trailers.implementation.TrailerRepositoryImpl
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrailersModule {
    @Singleton
    @Provides
    fun provideTrailerCache(database: TvManiacDatabase): TrailerCache = TrailerCacheImpl(database)

    @Singleton
    @Provides
    fun provideTrailerRepository(
        tmdbService: TmdbService,
        cache: TrailerCache,
        tvShowCache: TvShowCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TrailerRepository = TrailerRepositoryImpl(tmdbService, cache, tvShowCache, ioDispatcher)

    @Singleton
    @Provides
    fun provideObserveTrailerInteractor(
        repository: TrailerRepository
    ): ObserveTrailerInteractor = ObserveTrailerInteractor(repository)
}
