package com.thomaskioko.tvmaniac.trakt.profile.implementation.di

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
import com.thomaskioko.tvmaniac.core.util.scope.IoDispatcher
import com.thomaskioko.tvmaniac.shows.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.TraktListCache
import com.thomaskioko.tvmaniac.trakt.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.TraktStatsCache
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.profile.implementation.cache.TraktListCacheImpl
import com.thomaskioko.tvmaniac.trakt.profile.implementation.ProfileRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.profile.implementation.cache.TraktStatsCacheImpl
import com.thomaskioko.tvmaniac.trakt.profile.implementation.cache.TraktUserCacheImpl
import com.thomaskioko.tvmaniac.trakt.service.api.TraktService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module
import javax.inject.Singleton

actual fun profileModule(): KoinModule = module {}

@Module
@InstallIn(SingletonComponent::class)
object TraktModule {

    @Singleton
    @Provides
    fun provideTraktUserCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TraktUserCache = TraktUserCacheImpl(database, ioDispatcher)


    @Singleton
    @Provides
    fun provideTraktFavoriteListCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TraktListCache = TraktListCacheImpl(database, ioDispatcher)


    @Singleton
    @Provides
    fun provideTraktStatsCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TraktStatsCache = TraktStatsCacheImpl(database, ioDispatcher)

    @Singleton
    @Provides
    fun provideTraktProfileRepository(
        traktService: TraktService,
        listCache: TraktListCache,
        statsCache: TraktStatsCache,
        traktUserCache: TraktUserCache,
        followedCache: TraktFollowedCache,
        dateUtilHelper: DateUtilHelper,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): ProfileRepository = ProfileRepositoryImpl(
        traktService,
        listCache,
        statsCache,
        traktUserCache,
        followedCache,
        dateUtilHelper,
        ioDispatcher,
    )
}
