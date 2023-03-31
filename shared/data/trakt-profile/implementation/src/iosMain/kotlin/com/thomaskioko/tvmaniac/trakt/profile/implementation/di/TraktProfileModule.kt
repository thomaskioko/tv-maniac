package com.thomaskioko.tvmaniac.trakt.profile.implementation.di

import com.thomaskioko.tvmaniac.trakt.profile.api.cache.TraktListCache
import com.thomaskioko.tvmaniac.trakt.profile.api.TraktProfileRepository
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.TraktStatsCache
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.profile.implementation.cache.TraktListCacheImpl
import com.thomaskioko.tvmaniac.trakt.profile.implementation.TraktProfileRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.profile.implementation.cache.TraktStatsCacheImpl
import com.thomaskioko.tvmaniac.trakt.profile.implementation.cache.TraktUserCacheImpl
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun traktProfileModule(): Module = module {

    single<TraktUserCache> {
        TraktUserCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }

    single<TraktStatsCache> {
        TraktStatsCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }

    single<TraktListCache> {
        TraktListCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }

    single<TraktProfileRepository> {
        TraktProfileRepositoryImpl(
            traktService = get(),
            traktListCache = get(),
            statsCache = get(),
            traktUserCache = get(),
            followedCache = get(),
            dateUtilHelper = get(),
            dispatcher = Dispatchers.Default
        )
    }

}