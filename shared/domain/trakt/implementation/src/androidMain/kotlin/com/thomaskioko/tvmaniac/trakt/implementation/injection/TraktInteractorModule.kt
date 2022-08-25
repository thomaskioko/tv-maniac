package com.thomaskioko.tvmaniac.trakt.implementation.injection

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.trakt.api.ObserveTraktUserInteractor
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.TraktUserCache
import com.thomaskioko.tvmanic.trakt.implementation.TraktRepositoryImpl
import com.thomaskioko.tvmanic.trakt.implementation.TraktUserCacheImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object TraktInteractorModule {

    @Singleton
    @Provides
    fun provideTraktUserCache(database: TvManiacDatabase): TraktUserCache = TraktUserCacheImpl(database)

    @Singleton
    @Provides
    fun provideTraktRepository(
        cache: TraktUserCache,
        traktService: TraktService,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TraktRepository = TraktRepositoryImpl(cache, traktService, ioDispatcher)

    @Singleton
    @Provides
    fun provideObserveTrailerInteractor(
        repository: TraktRepository
    ): ObserveTraktUserInteractor = ObserveTraktUserInteractor(repository)
}
