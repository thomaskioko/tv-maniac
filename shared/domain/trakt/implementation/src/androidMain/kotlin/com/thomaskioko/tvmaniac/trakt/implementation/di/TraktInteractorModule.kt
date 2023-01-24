package com.thomaskioko.tvmaniac.trakt.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.scope.IoDispatcher
import com.thomaskioko.tvmaniac.shows.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.trakt.api.TraktProfileRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktListCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktStatsCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.implementation.TraktProfileRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.implementation.TraktShowRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktFollowedCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktListCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktStatsCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktUserCacheImpl
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
    fun provideTraktUserCache(database: TvManiacDatabase): TraktUserCache =
        TraktUserCacheImpl(database)


    @Singleton
    @Provides
    fun provideTraktFavoriteListCache(database: TvManiacDatabase): TraktListCache =
        TraktListCacheImpl(database)

    @Singleton
    @Provides
    fun provideTraktFollowedCache(database: TvManiacDatabase): TraktFollowedCache =
        TraktFollowedCacheImpl(database)

    @Singleton
    @Provides
    fun provideTraktStatsCache(database: TvManiacDatabase): TraktStatsCache =
        TraktStatsCacheImpl(database)

    @Singleton
    @Provides
    fun provideTraktRepository(
        tvShowCache: TvShowCache,
        traktUserCache: TraktUserCache,
        followedCache: TraktFollowedCache,
        showCategoryCache: ShowCategoryCache,
        dateUtilHelper: DateUtilHelper,
        traktService: TraktService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): TraktShowRepository =
        TraktShowRepositoryImpl(
            tvShowCache,
            traktUserCache,
            followedCache,
            showCategoryCache,
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
