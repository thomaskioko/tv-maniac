package com.thomaskioko.tvmaniac.shared

import com.thomaskioko.trakt.service.implementation.TraktPlatformComponent
import com.thomaskioko.tvmaniac.base.BasePlatformComponent
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.core.networkutil.NetworkUtilComponent
import com.thomaskioko.tvmaniac.data.category.implementation.CategoryComponent
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerComponent
import com.thomaskioko.tvmaniac.datastore.implementation.DataStorePlatformComponent
import com.thomaskioko.tvmaniac.db.DatabaseComponent
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeComponent
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonDetailsComponent
import com.thomaskioko.tvmaniac.shared.wrappers.DiscoverStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.wrappers.FollowingStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.wrappers.SeasonDetailsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.wrappers.SettingsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.wrappers.ShowDetailsStateMachineWrapper
import com.thomaskioko.tvmaniac.shared.wrappers.TrailersStateMachineWrapper
import com.thomaskioko.tvmaniac.shows.implementation.ShowsComponent
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsComponent
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbPlatformComponent
import com.thomaskioko.tvmaniac.trakt.profile.implementation.ProfileComponent
import me.tatarka.inject.annotations.Component

@ApplicationScope
@Component
abstract class ApplicationComponent :
    BasePlatformComponent,
    CategoryComponent,
    DatabaseComponent,
    DataStorePlatformComponent,
    EpisodeComponent,
    NetworkUtilComponent,
    ProfileComponent,
    SeasonDetailsComponent,
    ShowsComponent,
    SimilarShowsComponent,
    TmdbPlatformComponent,
    TraktPlatformComponent,
    TrailerComponent {

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