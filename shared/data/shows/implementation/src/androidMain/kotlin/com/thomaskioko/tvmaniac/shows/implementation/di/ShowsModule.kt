package com.thomaskioko.tvmaniac.shows.implementation.di

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
import com.thomaskioko.tvmaniac.core.util.scope.IoDispatcher
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.shows.api.cache.FollowedCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsCache
import com.thomaskioko.tvmaniac.shows.implementation.ShowsRepositoryImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.FollowedCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowCacheImpl
import com.thomaskioko.tvmaniac.trakt.service.api.TraktService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module
import javax.inject.Singleton

actual fun showsModule(): KoinModule = module {}

@Module
@InstallIn(SingletonComponent::class)
object TraktModule {

    @Singleton
    @Provides
    fun provideFollowedCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): FollowedCache = FollowedCacheImpl(database, ioDispatcher)


    @Singleton
    @Provides
    fun provideShowsCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): ShowsCache = ShowCacheImpl(database, ioDispatcher)

    @Singleton
    @Provides
    fun provideShowsRepository(
        showsCache: ShowsCache,
        followedCache: FollowedCache,
        categoryCache: CategoryCache,
        dateUtilHelper: DateUtilHelper,
        traktService: TraktService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): ShowsRepository = ShowsRepositoryImpl(
        showsCache,
        followedCache,
        categoryCache,
        traktService,
        dateUtilHelper,
        ioDispatcher,
    )

}
