@file:Suppress("unused")

package com.thomaskioko.tvmaniac.shared.di

import com.thomaskioko.tvmaniac.core.db.di.dbPlatformModule
import com.thomaskioko.tvmaniac.core.util.di.coreUtilModule
import com.thomaskioko.tvmaniac.details.api.ShowDetailsStateMachineWrapper
import com.thomaskioko.tvmaniac.details.implementation.di.detailDomainModule
import com.thomaskioko.tvmaniac.domain.following.api.FollowingStateMachineWrapper
import com.thomaskioko.tvmaniac.domain.following.api.di.followingModule
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailersStateMachineWrapper
import com.thomaskioko.tvmaniac.domain.trailers.implementation.di.trailersModule
import com.thomaskioko.tvmaniac.episodes.implementation.di.episodeDomainModule
import com.thomaskioko.tvmaniac.network.di.networkPlatformModule
import com.thomaskioko.tvmaniac.seasondetails.implementation.di.seasonDetailsDomainModule
import com.thomaskioko.tvmaniac.settings.api.SettingsStateMachineWrapper
import com.thomaskioko.tvmaniac.settings.implementation.di.settingsModule
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
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun KoinApplication.Companion.start(): KoinApplication = initKoin {}

val Koin.showStateMachine: ShowsStateMachineWrapper
    get() = ShowsStateMachineWrapper(get(), get())

val Koin.showDetailsStateMachine: ShowDetailsStateMachineWrapper
    get() = ShowDetailsStateMachineWrapper(get(), get())

val Koin.settingsStateMachine: SettingsStateMachineWrapper
    get() = SettingsStateMachineWrapper(get(), get())

val Koin.trailersStateMachine: TrailersStateMachineWrapper
    get() = TrailersStateMachineWrapper(get(), get())

val Koin.followingStateMachineWrapper: FollowingStateMachineWrapper
    get() = FollowingStateMachineWrapper(get(), get())

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        dispatcherModule,
        detailDomainModule,
        seasonDetailsDomainModule,
        episodeDomainModule,
        similarDomainModule,
        trailersModule,
        showDomainModule,
        followingModule,
        traktModule(),
        dbPlatformModule(),
        networkPlatformModule(),
        tmdbModule(),
        coreUtilModule(),
        settingsModule()
    )
}

val dispatcherModule = module {
    single { MainScope() }
    single { Dispatchers.Default }
    single { Dispatchers.Main }
}
