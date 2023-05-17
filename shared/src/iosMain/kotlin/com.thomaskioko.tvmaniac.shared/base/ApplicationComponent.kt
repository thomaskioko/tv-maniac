package com.thomaskioko.tvmaniac.shared.base

import com.thomaskioko.trakt.service.implementation.TraktComponent
import com.thomaskioko.trakt.service.implementation.TraktPlatformComponent
import com.thomaskioko.tvmaniac.core.networkutil.NetworkUtilComponent
import com.thomaskioko.tvmaniac.data.category.implementation.CategoryComponent
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerComponent
import com.thomaskioko.tvmaniac.datastore.implementation.DataStorePlatformComponent
import com.thomaskioko.tvmaniac.db.DatabaseComponent
import com.thomaskioko.tvmaniac.episodeimages.implementation.EpisodeImageComponent
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeComponent
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonDetailsComponent
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsComponent
import com.thomaskioko.tvmaniac.shared.base.wrappers.DiscoverStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.FollowingStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.SeasonDetailsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.SettingsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.ShowDetailsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.base.wrappers.TrailersStateMachineWrapper
import com.thomaskioko.tvmaniac.showimages.implementation.ShowImagesComponent
import com.thomaskioko.tvmaniac.shows.implementation.ShowsComponent
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsComponent
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbPlatformComponent
import com.thomaskioko.tvmaniac.profile.implementation.ProfileComponent
import com.thomaskioko.tvmaniac.util.inject.UtilPlatformComponent
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Component

@ApplicationScope
@Component
abstract class ApplicationComponent :
    CategoryComponent,
    DatabaseComponent,
    DataStorePlatformComponent,
    EpisodeComponent,
    EpisodeImageComponent,
    NetworkUtilComponent,
    ProfileComponent,
    SeasonsComponent,
    SeasonDetailsComponent,
    ShowsComponent,
    ShowImagesComponent,
    SimilarShowsComponent,
    TmdbPlatformComponent,
    TraktComponent,
    TraktPlatformComponent,
    TrailerComponent,
    UtilPlatformComponent {

    abstract val discoverStateMachine: DiscoverStateMachineWrapper
    abstract val followingStateMachineWrapper: FollowingStateMachineWrapper
    abstract val seasonDetailsStateMachineWrapper: SeasonDetailsStateMachineWrapper
    abstract val settingsStateMachineWrapper: SettingsStateMachineWrapper
    abstract val showDetailsStateMachineWrapper: ShowDetailsStateMachineWrapper
    abstract val trailerStateMachineWrapper: TrailersStateMachineWrapper
}

fun discoverStateMachine(): DiscoverStateMachineWrapper =
    ApplicationComponent::class.create().discoverStateMachine

fun followingStateMachine(): FollowingStateMachineWrapper =
    ApplicationComponent::class.create().followingStateMachineWrapper

fun seasonDetailsStateMachine(): SeasonDetailsStateMachineWrapper =
    ApplicationComponent::class.create().seasonDetailsStateMachineWrapper

fun settingsStateMachine(): SettingsStateMachineWrapper =
    ApplicationComponent::class.create().settingsStateMachineWrapper

fun showDetailsStateMachine(): ShowDetailsStateMachineWrapper =
    ApplicationComponent::class.create().showDetailsStateMachineWrapper

fun trailerStateMachine(): TrailersStateMachineWrapper =
    ApplicationComponent::class.create().trailerStateMachineWrapper
