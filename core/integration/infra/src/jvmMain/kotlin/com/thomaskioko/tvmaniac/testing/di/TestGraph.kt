package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.oauth.api.OAuthLauncher
import com.thomaskioko.tvmaniac.presenter.home.di.HomeScreenGraph
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
public interface TestGraph {
    public val datastoreRepository: DatastoreRepository
    public val oAuthLauncher: OAuthLauncher
    public val navigator: Navigator
    public val rootPresenterFactory: RootPresenter.Factory
    public val navDestinations: Set<NavDestination<*>>
    public val homeScreenGraphFactory: HomeScreenGraph.Factory
    public val syncObserver: SyncObserver
    public val workerFactory: WorkerFactory
    public val featureFlags: Set<FeatureFlag<Boolean>>

    @DependencyGraph.Factory
    public fun interface Factory {
        public fun create(): TestGraph
    }
}
