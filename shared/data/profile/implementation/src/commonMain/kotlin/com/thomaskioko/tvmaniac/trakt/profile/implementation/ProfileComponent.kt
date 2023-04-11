package com.thomaskioko.tvmaniac.trakt.profile.implementation

import com.thomaskioko.tvmaniac.trakt.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.FavoriteListCache
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.StatsCache
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.UserCache
import com.thomaskioko.tvmaniac.trakt.profile.implementation.cache.FavoriteListCacheImpl
import com.thomaskioko.tvmaniac.trakt.profile.implementation.cache.StatsCacheImpl
import com.thomaskioko.tvmaniac.trakt.profile.implementation.cache.UserCacheImpl
import me.tatarka.inject.annotations.Provides

interface ProfileComponent {

    @Provides
    fun provideUserCache(bind: UserCacheImpl): UserCache = bind

    @Provides
    fun provideFavoriteListCache(bind: FavoriteListCacheImpl): FavoriteListCache = bind

    @Provides
    fun provideStatsCache(bind: StatsCacheImpl): StatsCache = bind

    @Provides
    fun provideProfileRepository(bind: ProfileRepositoryImpl): ProfileRepository = bind
}