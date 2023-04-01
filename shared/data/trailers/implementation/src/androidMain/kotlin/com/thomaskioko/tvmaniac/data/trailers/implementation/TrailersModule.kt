package com.thomaskioko.tvmaniac.data.trailers.implementation

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.AppUtils
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module
import javax.inject.Singleton

actual fun trailersModule() : KoinModule = module {  }

@Module
@InstallIn(SingletonComponent::class)
object TrailersModule {
    @Singleton
    @Provides
    fun provideTrailerCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TrailerCache = TrailerCacheImpl(database ,ioDispatcher)

    @Singleton
    @Provides
    fun provideTrailerRepository(
        tmdbService: TmdbService,
        cache: TrailerCache,
        showsCache: ShowsCache,
        appUtils: AppUtils,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TrailerRepository = TrailerRepositoryImpl(
        tmdbService,
        cache,
        showsCache,
        appUtils,
        ioDispatcher
    )
}
