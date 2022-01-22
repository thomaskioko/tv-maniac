package com.thomaskioko.tvmaniac.di

import com.thomaskioko.tvmaniac.core.db.di.dbPlatformModule
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.discover.implementation.cache.CategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.di.discoverDomainModule
import com.thomaskioko.tvmaniac.episodes.implementation.di.episodeDomainModule
import com.thomaskioko.tvmaniac.genre.implementation.di.genreModule
import com.thomaskioko.tvmaniac.lastairepisodes.implementation.di.lastAirEpisodeDomainModule
import com.thomaskioko.tvmaniac.remote.di.remotePlatformModule
import com.thomaskioko.tvmaniac.remote.di.serviceModule
import com.thomaskioko.tvmaniac.seasons.implementation.di.seasonsDomainModule
import com.thomaskioko.tvmaniac.shared.core.di.corePlatformModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        serviceModule,
        cacheModule,
        dispatcherModule,
        discoverDomainModule,
        seasonsDomainModule,
        episodeDomainModule,
        genreModule,
        lastAirEpisodeDomainModule,
        corePlatformModule(),
        dbPlatformModule(),
        remotePlatformModule()
    )
}

// IOS
fun initKoin() = initKoin {}

val cacheModule: Module = module {
    single<TvShowCache> { TvShowCacheImpl(get()) }
    single<ShowCategoryCache> { ShowCategoryCacheImpl(get()) }
    single<CategoryCache> { CategoryCacheImpl(get()) }
}

val dispatcherModule = module {
    factory { Dispatchers.Default }
    factory { MainScope() }
}
