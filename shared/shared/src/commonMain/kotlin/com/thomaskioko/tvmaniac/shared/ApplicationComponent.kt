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
import com.thomaskioko.tvmaniac.shows.implementation.ShowsComponent
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsComponent
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbPlatformComponent
import com.thomaskioko.tvmaniac.trakt.profile.implementation.ProfileComponent
import me.tatarka.inject.annotations.Component

@Component
@ApplicationScope
abstract class ApplicationComponent : AppComponent,
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
}

//fun discoverStateMachine() : DiscoverStateMachineWrapper = StateMachineWrapperComponent::class.create().discoverStateMachine