package com.thomaskioko.tvmaniac.trakt.implementation.injection

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shared.core.ui.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.shared.core.ui.di.IoDispatcher
import com.thomaskioko.tvmaniac.shows.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.trakt.api.ObserveTraktUserInteractor
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktListCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktUserCache
import com.thomaskioko.tvmanic.trakt.implementation.TraktRepositoryImpl
import com.thomaskioko.tvmanic.trakt.implementation.cache.TraktFollowedCacheImpl
import com.thomaskioko.tvmanic.trakt.implementation.cache.TraktListCacheImpl
import com.thomaskioko.tvmanic.trakt.implementation.cache.TraktUserCacheImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
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
    fun provideTraktRepository(
        cache: TraktUserCache,
        tvShowCache: TvShowCache,
        followedCache: TraktFollowedCache,
        favoriteCache: TraktListCache,
        categoryCache: CategoryCache,
        showCategoryCache: ShowCategoryCache,
        traktService: TraktService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @IoCoroutineScope coroutineScope: CoroutineScope,
    ): TraktRepository =
        TraktRepositoryImpl(
            tvShowCache,
            cache,
            followedCache,
            favoriteCache,
            categoryCache,
            showCategoryCache,
            traktService,
            ioDispatcher,
            coroutineScope
        )

    @Singleton
    @Provides
    fun provideObserveTrailerInteractor(
        repository: TraktRepository
    ): ObserveTraktUserInteractor = ObserveTraktUserInteractor(repository)
}
