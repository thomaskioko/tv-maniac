package com.thomaskioko.tvmaniac.shared.di

import com.thomaskioko.tvmaniac.core.db.di.dbPlatformModule
import com.thomaskioko.tvmaniac.core.util.di.coreUtilModule
import com.thomaskioko.tvmaniac.details.api.ShowDetailsStateMachineWrapper
import com.thomaskioko.tvmaniac.details.implementation.di.detailDomainModule
import com.thomaskioko.tvmaniac.episodes.implementation.di.episodeDomainModule
import com.thomaskioko.tvmaniac.network.di.networkPlatformModule
import com.thomaskioko.tvmaniac.seasonepisodes.implementation.seasonEpisodesDomainModule
import com.thomaskioko.tvmaniac.seasons.implementation.di.seasonsDomainModule
import com.thomaskioko.tvmaniac.settings.implementation.di.settingsModule
import com.thomaskioko.tvmaniac.shared.core.ui.di.coreUiPlatformModule
import com.thomaskioko.tvmaniac.shared.domain.trailers.implementation.di.trailersModule
import com.thomaskioko.tvmaniac.shows.api.ShowsStateMachineWrapper
import com.thomaskioko.tvmaniac.shows.implementation.di.showDomainModule
import com.thomaskioko.tvmaniac.similar.implementation.di.similarDomainModule
import com.thomaskioko.tvmaniac.tmdb.implementation.tmdbModule
import com.thomaskioko.tvmaniac.trakt.implementation.di.traktModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun KoinApplication.Companion.start(): KoinApplication = initKoin {}

val Koin.showStateMachine: ShowsStateMachineWrapper
    get() = ShowsStateMachineWrapper(get(), get(named("main-dispatcher")))

val Koin.showDetailsStateMachine: ShowDetailsStateMachineWrapper
    get() = ShowDetailsStateMachineWrapper(get(), get(named("main-dispatcher")))

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        dispatcherModule,
        detailDomainModule,
        seasonsDomainModule,
        episodeDomainModule,
        similarDomainModule,
        seasonEpisodesDomainModule,
        trailersModule,
        showDomainModule,
        traktModule(),
        coreUiPlatformModule(),
        dbPlatformModule(),
        networkPlatformModule(),
        tmdbModule(),
        coreUtilModule(),
        settingsModule()
    )
}

val dispatcherModule = module {
    factory { Dispatchers.Default }
    single(named("main-dispatcher")) { Dispatchers.Main }
    factory { MainScope() }
}
