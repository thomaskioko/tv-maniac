package com.thomaskioko.tvmaniac.trakt.implementation.di

import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
import com.thomaskioko.tvmaniac.core.util.scope.IoDispatcher
import com.thomaskioko.tvmaniac.trakt.api.TraktProfileRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktListCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktStatsCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.trakt.implementation.TraktProfileRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.implementation.TraktShowRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktFollowedCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktListCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktShowCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktStatsCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktUserCacheImpl
import com.thomaskioko.tvmaniac.trakt.service.api.TraktService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module
import javax.inject.Singleton
import org.koin.core.module.Module as KoinModule

actual fun traktModule(): KoinModule = module {}

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
    fun provideTraktFollowedCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TraktFollowedCache = TraktFollowedCacheImpl(database, ioDispatcher)

    @Singleton
    @Provides
    fun provideTraktStatsCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TraktStatsCache = TraktStatsCacheImpl(database, ioDispatcher)

    @Singleton
    @Provides
    fun provideTvShowCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TvShowCache = TraktShowCacheImpl(database, ioDispatcher)

    @Singleton
    @Provides
    fun provideTraktRepository(
        tvShowCache: TvShowCache,
        traktUserCache: TraktUserCache,
        followedCache: TraktFollowedCache,
        categoryCache: CategoryCache,
        dateUtilHelper: DateUtilHelper,
        traktService: TraktService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): TraktShowRepository =
        TraktShowRepositoryImpl(
            tvShowCache,
            traktUserCache,
            followedCache,
            categoryCache,
            traktService,
            dateUtilHelper,
            ioDispatcher,
        )

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
    ): TraktProfileRepository =
        TraktProfileRepositoryImpl(
            traktService,
            listCache,
            statsCache,
            traktUserCache,
            followedCache,
            dateUtilHelper,
            ioDispatcher,
        )
}
