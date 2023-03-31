package com.thomaskioko.tvmaniac.trakt.implementation.di

import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.trakt.implementation.TraktShowRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktFollowedCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktShowCacheImpl
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun traktModule(): Module = module {

    single<TraktFollowedCache> {
        TraktFollowedCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }

    single<TraktShowRepository> {
        TraktShowRepositoryImpl(
            tvShowCache = get(),
            followedCache = get(),
            categoryCache = get(),
            traktService = get(),
            dateUtilHelper = get(),
            dispatcher = Dispatchers.Default
        )
    }


    single<TvShowCache> {
        TraktShowCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }
}