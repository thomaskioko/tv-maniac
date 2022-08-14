package com.thomaskioko.tvmaniac.similar.implementation.di

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowCacheImpl
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SimilarModule {

    @Singleton
    @Provides
    fun provideSimilarShowCache(database: TvManiacDatabase): SimilarShowCache {
        return SimilarShowCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideRelatedShowsRepository(
        tvShowsService: TvShowsService,
        similarShowCache: SimilarShowCache,
        tvShowCache: TvShowCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SimilarShowsRepository =
        SimilarShowsRepositoryImpl(
            apiService = tvShowsService,
            similarShowCache = similarShowCache,
            tvShowCache = tvShowCache,
            dispatcher = ioDispatcher
        )

}
