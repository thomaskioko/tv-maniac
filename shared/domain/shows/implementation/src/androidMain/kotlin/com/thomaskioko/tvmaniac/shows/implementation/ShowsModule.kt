package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.shared.core.ui.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.shared.core.ui.di.IoDispatcher
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.shows.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
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
    fun provideTvShowsRepository(
        tmdbService: TmdbService,
        tvShowCache: TvShowCache,
        @IoCoroutineScope coroutineScope: CoroutineScope,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher computationDispatcher: CoroutineDispatcher
    ): TmdbRepository =
        TmdbRepositoryImpl(
            apiService = tmdbService,
            tvShowCache = tvShowCache,
            coroutineScope = coroutineScope,
            dispatcher = ioDispatcher,
            computationDispatcher = computationDispatcher
        )

}
