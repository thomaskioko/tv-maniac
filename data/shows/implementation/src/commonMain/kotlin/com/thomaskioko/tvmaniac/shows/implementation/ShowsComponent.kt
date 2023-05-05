package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.shows.api.cache.FollowedCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsCache
import com.thomaskioko.tvmaniac.shows.implementation.cache.FollowedCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowCacheImpl
import me.tatarka.inject.annotations.Provides

interface ShowsComponent {

    @Provides
    fun provideFollowedCache(bind: FollowedCacheImpl): FollowedCache = bind

    @Provides
    fun provideShowsCache(bind: ShowCacheImpl): ShowsCache = bind

    @Provides
    fun provideShowsRepository(bind: ShowsRepositoryImpl): ShowsRepository = bind
}
