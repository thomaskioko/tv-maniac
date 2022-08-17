package com.thomaskioko.tvmaniac.shared.domain.genre.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.genre.api.GenreCache
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.genre.implementation.GenreCacheImpl
import com.thomaskioko.tvmaniac.genre.implementation.GenreRepositoryImpl
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GenreModule {
    @Singleton
    @Provides
    fun provideGenreCache(database: TvManiacDatabase): GenreCache {
        return GenreCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideGenreRepository(
        tmdbService: TmdbService,
        cache: GenreCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): GenreRepository = GenreRepositoryImpl(tmdbService, cache, ioDispatcher)
}
