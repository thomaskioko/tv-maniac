package com.thomaskioko.tvmaniac.shows.implementation.di

import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.shows.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.implementation.ShowsRepositoryImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.TraktFollowedCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.TraktShowCacheImpl
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

    single<ShowsRepository> {
        ShowsRepositoryImpl(
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