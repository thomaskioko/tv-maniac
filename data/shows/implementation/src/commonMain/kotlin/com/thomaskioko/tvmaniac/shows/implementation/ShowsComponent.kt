package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.shows.api.cache.FollowedCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsDao
import com.thomaskioko.tvmaniac.shows.implementation.cache.FollowedCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowDaoImpl
import me.tatarka.inject.annotations.Provides

interface ShowsComponent {

    @Provides
    fun provideFollowedCache(bind: FollowedCacheImpl): FollowedCache = bind

    @Provides
    fun provideShowsCache(bind: ShowDaoImpl): ShowsDao = bind

    @Provides
    fun provideShowsRepository(bind: ShowsRepositoryImpl): ShowsRepository = bind
}
