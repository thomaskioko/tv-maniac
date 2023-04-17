package com.thomaskioko.tvmaniac.inject

import android.app.Application
import android.content.Context
import com.thomaskioko.trakt.service.implementation.TraktPlatformComponent
import com.thomaskioko.tvmaniac.TvManicApplication
import com.thomaskioko.tvmaniac.util.inject.UtilPlatformComponent
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import com.thomaskioko.tvmaniac.core.networkutil.NetworkUtilComponent
import com.thomaskioko.tvmaniac.data.category.implementation.CategoryComponent
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerComponent
import com.thomaskioko.tvmaniac.datastore.implementation.DataStorePlatformComponent
import com.thomaskioko.tvmaniac.db.DatabaseComponent
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeComponent
import com.thomaskioko.tvmaniac.initializers.AppInitializers
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonDetailsComponent
import com.thomaskioko.tvmaniac.shows.implementation.ShowsComponent
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsComponent
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbPlatformComponent
import com.thomaskioko.tvmaniac.trakt.profile.implementation.ProfileComponent
import com.thomaskioko.tvmaniac.traktauth.inject.TraktAuthComponent
import com.thomaskioko.tvmaniac.workmanager.factory.DiscoverWorkerFactory
import com.thomaskioko.tvmaniac.workmanager.inject.TasksComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ApplicationScope
abstract class ApplicationComponent(
    @get:Provides val application: Application,
) : UtilPlatformComponent,
    CategoryComponent,
    DatabaseComponent,
    DataStorePlatformComponent,
    EpisodeComponent,
    NavigationComponent,
    NetworkUtilComponent,
    ProfileComponent,
    SeasonDetailsComponent,
    ShowsComponent,
    SimilarShowsComponent,
    TasksComponent,
    TmdbPlatformComponent,
    TraktPlatformComponent,
    TrailerComponent,
    TraktAuthComponent {

    abstract val initializers: AppInitializers
    abstract val workerFactory: DiscoverWorkerFactory


    companion object {
        fun from(context: Context): ApplicationComponent {
            return (context.applicationContext as TvManicApplication).component
        }
    }
}

