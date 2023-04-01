package com.thomaskioko.tvmaniac.shows.implementation.di

import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.shows.api.cache.FollowedCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsCache
import com.thomaskioko.tvmaniac.shows.implementation.ShowsRepositoryImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.FollowedCacheImpl
import com.thomaskioko.tvmaniac.shows.implementation.cache.ShowCacheImpl
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun showsModule(): Module = module {

    single<FollowedCache> {
        FollowedCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }

    single<ShowsRepository> {
        ShowsRepositoryImpl(
            showsCache = get(),
            followedCache = get(),
            categoryCache = get(),
            traktService = get(),
            dateUtilHelper = get(),
            dispatcher = Dispatchers.Default
        )
    }


    single<ShowsCache> {
        ShowCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }
}