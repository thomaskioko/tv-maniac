package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.cache.TvShowCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module
import javax.inject.Singleton

actual fun similarDataModule() : org.koin.core.module.Module = module {  }

@Module
@InstallIn(SingletonComponent::class)
object SimilarModule {

    @Singleton
    @Provides
    fun provideSimilarShowCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SimilarShowCache = SimilarShowCacheImpl(database, ioDispatcher)

    @Singleton
    @Provides
    fun provideRelatedShowsRepository(
        traktService: TraktService,
        similarShowCache: SimilarShowCache,
        tvShowCache: TvShowCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SimilarShowsRepository =
        SimilarShowsRepositoryImpl(
            traktService = traktService,
            similarShowCache = similarShowCache,
            tvShowCache = tvShowCache,
            dispatcher = ioDispatcher
        )

}
