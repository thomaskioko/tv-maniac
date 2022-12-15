package com.thomaskioko.tvmaniac.trakt.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.scope.IoDispatcher
import com.thomaskioko.tvmaniac.shows.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktListCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktStatsCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.implementation.TraktRepositoryImpl
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
        cache: TraktUserCache,
        tvShowCache: TvShowCache,
        followedCache: TraktFollowedCache,
        favoriteCache: TraktListCache,
        showCategoryCache: ShowCategoryCache,
        dateUtilHelper: DateUtilHelper,
        statsCache: TraktStatsCache,
        traktService: TraktService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): TraktRepository =
        TraktRepositoryImpl(
            tvShowCache,
            cache,
            followedCache,
            favoriteCache,
            showCategoryCache,
            statsCache,
            traktService,
            dateUtilHelper,
            ioDispatcher,
        )

}
