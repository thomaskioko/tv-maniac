package com.thomaskioko.tvmaniac.shared.base

import com.thomaskioko.trakt.service.implementation.inject.TraktComponent
import com.thomaskioko.tvmaniac.common.voyagerutil.ScreenModelComponent
import com.thomaskioko.tvmaniac.data.category.implementation.CategoryComponent
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerComponent
import com.thomaskioko.tvmaniac.datastore.implementation.DataStoreComponent
import com.thomaskioko.tvmaniac.db.DatabaseComponent
import com.thomaskioko.tvmaniac.episodeimages.implementation.EpisodeImageComponent
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeComponent
import com.thomaskioko.tvmaniac.profile.implementation.ProfileComponent
import com.thomaskioko.tvmaniac.profilestats.implementation.StatsComponent
import com.thomaskioko.tvmaniac.resourcemanager.implementation.RequestManagerComponent
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonDetailsComponent
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsComponent
import com.thomaskioko.tvmaniac.shared.base.wrappers.DiscoverStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.ProfileStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.SeasonDetailsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.SettingsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.ShowDetailsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.TrailersStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.WatchlistStateMachineWrapper
import com.thomaskioko.tvmaniac.showimages.implementation.ShowImagesComponent
import com.thomaskioko.tvmaniac.shows.implementation.DiscoverComponent
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsComponent
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbComponent
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthenticationComponent
import com.thomaskioko.tvmaniac.util.inject.UtilPlatformComponent
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import com.thomaskioko.tvmaniac.watchlist.implementation.LibraryComponent
import me.tatarka.inject.annotations.Component

@ApplicationScope
@Component
abstract class ApplicationComponent :
    CategoryComponent,
    DatabaseComponent,
    DataStoreComponent,
    EpisodeComponent,
    EpisodeImageComponent,
    ProfileComponent,
    RequestManagerComponent,
    SeasonsComponent,
    SeasonDetailsComponent,
    DiscoverComponent,
    ShowImagesComponent,
    SimilarShowsComponent,
    StatsComponent,
    TmdbComponent,
    TraktComponent,
    TraktAuthenticationComponent,
    TrailerComponent,
    UtilPlatformComponent,
    LibraryComponent,
    ScreenModelComponent {

    abstract val discoverStateMachine: DiscoverStateMachineWrapper
    abstract val seasonDetailsStateMachineWrapper: SeasonDetailsStateMachineWrapper
    abstract val settingsStateMachineWrapper: SettingsStateMachineWrapper
    abstract val showDetailsStateMachineWrapper: ShowDetailsStateMachineWrapper
    abstract val trailerStateMachineWrapper: TrailersStateMachineWrapper
    abstract val watchlistStateMachineWrapper: WatchlistStateMachineWrapper
    abstract val profileStateMachineWrapper: ProfileStateMachineWrapper
}

fun discoverStateMachine(): DiscoverStateMachineWrapper =
    ApplicationComponent::class.create().discoverStateMachine

fun watchlistStateMachineWrapper(): WatchlistStateMachineWrapper =
    ApplicationComponent::class.create().watchlistStateMachineWrapper

fun seasonDetailsStateMachine(): SeasonDetailsStateMachineWrapper =
    ApplicationComponent::class.create().seasonDetailsStateMachineWrapper

fun settingsStateMachine(): SettingsStateMachineWrapper =
    ApplicationComponent::class.create().settingsStateMachineWrapper

fun showDetailsStateMachine(): ShowDetailsStateMachineWrapper =
    ApplicationComponent::class.create().showDetailsStateMachineWrapper

fun trailerStateMachine(): TrailersStateMachineWrapper =
    ApplicationComponent::class.create().trailerStateMachineWrapper

fun profileStateMachineWrapper(): ProfileStateMachineWrapper =
    ApplicationComponent::class.create().profileStateMachineWrapper
