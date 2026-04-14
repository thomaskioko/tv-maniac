package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
public interface TestIosGraph {
    public val datastoreRepository: DatastoreRepository
    public val traktAuthManager: TraktAuthManager
    public val rootPresenterFactory: RootPresenter.Factory
    public val navDestinations: Set<NavDestination>

    @DependencyGraph.Factory
    public fun interface Factory {
        public fun create(): TestIosGraph
    }
}
