package com.thomaskioko.tvmaniac.di

import com.thomaskioko.tvmaniac.core.db.di.dbPlatformModule
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.genre.GenreCache
import com.thomaskioko.tvmaniac.datasource.cache.genre.GenreCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.trailers.TrailerCache
import com.thomaskioko.tvmaniac.datasource.cache.trailers.TrailerCacheImpl
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepository
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.genre.GenreRepository
import com.thomaskioko.tvmaniac.datasource.repository.genre.GenreRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepository
import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepositoryImpl
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.discover.implementation.cache.CategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.di.discoverDomainModule
import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
import com.thomaskioko.tvmaniac.interactor.GetGenresInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.interactor.GetTrailersInteractor
import com.thomaskioko.tvmaniac.interactor.GetWatchListInteractor
import com.thomaskioko.tvmaniac.interactor.UpdateWatchlistInteractor
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
        interactorModule,
        cacheModule,
        repositoryModule,
        dispatcherModule,
        discoverDomainModule,
        seasonsDomainModule,
        corePlatformModule(),
        dbPlatformModule(),
        remotePlatformModule()
    )
}

// IOS
fun initKoin() = initKoin {}

val repositoryModule: Module = module {
    single<EpisodeRepository> { EpisodeRepositoryImpl(get(), get(), get(), get()) }
    single<GenreRepository> { GenreRepositoryImpl(get(), get(), get()) }
    single<TrailerRepository> { TrailerRepositoryImpl(get(), get()) }
}

val interactorModule: Module = module {
    factory { EpisodesInteractor(get()) }
    factory { GetGenresInteractor(get()) }
    factory { GetShowInteractor(get()) }
    factory { GetShowsByCategoryInteractor(get()) }
    factory { GetTrailersInteractor(get()) }
    factory { GetWatchListInteractor(get()) }
    factory { UpdateWatchlistInteractor(get()) }
}

val cacheModule: Module = module {
    single<EpisodesCache> { EpisodesCacheImpl(get()) }
    single<GenreCache> { GenreCacheImpl(get()) }
    single<TrailerCache> { TrailerCacheImpl(get()) }
    single<TvShowCache> { TvShowCacheImpl(get()) }
    single<ShowCategoryCache> { ShowCategoryCacheImpl(get()) }
    single<CategoryCache> { CategoryCacheImpl(get()) }
}

val dispatcherModule = module {
    factory { Dispatchers.Default }
    factory { MainScope() }
}
