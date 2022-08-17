package com.thomaskioko.tvmaniac.shared.di

import com.thomaskioko.tvmaniac.core.db.di.dbPlatformModule
import com.thomaskioko.tvmaniac.details.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.details.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.details.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.details.implementation.di.detailDomainModule
import com.thomaskioko.tvmaniac.discover.implementation.di.discoverDomainModule
import com.thomaskioko.tvmaniac.episodes.implementation.di.episodeDomainModule
import com.thomaskioko.tvmaniac.genre.implementation.di.genreModule
import com.thomaskioko.tvmaniac.lastairepisodes.implementation.di.lastAirEpisodeDomainModule
import com.thomaskioko.tvmaniac.network.di.networkPlatformModule
import com.thomaskioko.tvmaniac.seasonepisodes.implementation.seasonEpisodesDomainModule
import com.thomaskioko.tvmaniac.seasons.implementation.di.seasonsDomainModule
import com.thomaskioko.tvmaniac.shared.core.ui.di.coreUiPlatformModule
import com.thomaskioko.tvmaniac.shared.domain.trailers.implementation.di.trailersModule
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.similar.implementation.di.similarDomainModule
import com.thomaskioko.tvmaniac.tmdb.implementation.tmdbServiceModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        tmdbServiceModule,
        cacheModule,
        dispatcherModule,
        detailDomainModule,
        seasonsDomainModule,
        episodeDomainModule,
        genreModule,
        lastAirEpisodeDomainModule,
        similarDomainModule,
        seasonEpisodesDomainModule,
        discoverDomainModule,
        trailersModule,
        coreUiPlatformModule(),
        dbPlatformModule(),
        networkPlatformModule()
    )
}

val cacheModule: Module = module {
    single<TvShowCache> { TvShowCacheImpl(get()) }
    single<ShowCategoryCache> { ShowCategoryCacheImpl(get()) }
}

val dispatcherModule = module {
    factory { Dispatchers.Default }
    factory { MainScope() }
}
