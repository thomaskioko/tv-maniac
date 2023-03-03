@file:Suppress("unused")

package com.thomaskioko.tvmaniac.shared.di

import com.thomaskioko.tvmaniac.core.db.di.dbPlatformModule
import com.thomaskioko.tvmaniac.core.util.di.coreUtilModule
import com.thomaskioko.tvmaniac.data.category.implementation.categoryDataModule
import com.thomaskioko.tvmaniac.data.seasondetails.seasonDetailsDomainModule
import com.thomaskioko.tvmaniac.data.showdetails.ShowDetailsStateMachineWrapper
import com.thomaskioko.tvmaniac.data.showdetails.showDetailsDomainModule
import com.thomaskioko.tvmaniac.data.trailers.TrailersStateMachineWrapper
import com.thomaskioko.tvmaniac.data.trailers.implementation.trailersModule
import com.thomaskioko.tvmaniac.data.trailers.trailerDomainModule
import com.thomaskioko.tvmaniac.datastore.implementation.di.datastoreModule
import com.thomaskioko.tvmaniac.domain.following.FollowingStateMachineWrapper
import com.thomaskioko.tvmaniac.domain.following.followingDomainModule
import com.thomaskioko.tvmaniac.episodes.implementation.di.episodeDataModule
import com.thomaskioko.tvmaniac.network.di.networkModule
import com.thomaskioko.tvmaniac.seasondetails.implementation.seasonDetailsDataModule
import com.thomaskioko.tvmaniac.settings.SettingsStateMachineWrapper
import com.thomaskioko.tvmaniac.settings.settingsDomainModule
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.domain.discover.discoverDomainModule
import com.thomaskioko.tvmaniac.similar.implementation.similarDataModule
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
        categoryDataModule(),
        coreUtilModule(),
        datastoreModule(),
        dbPlatformModule(),
        discoverDomainModule(),
        dispatcherModule(),
        episodeDataModule(),
        followingDomainModule(),
        seasonDetailsDataModule(),
        networkModule(),
        seasonDetailsDomainModule(),
        settingsDomainModule(),
        showDetailsDomainModule(),
        similarDataModule(),
        tmdbModule(),
        trailerDomainModule(),
        trailersModule(),
        traktModule()
    )
}

fun dispatcherModule() = module {
    single { MainScope() }
    single { Dispatchers.Default }
    single { Dispatchers.Main }
}
