package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
public interface TestJvmGraph {
    public val datastoreRepository: DatastoreRepository
    public val traktAuthManager: TraktAuthManager
    public val rootPresenterFactory: RootPresenter.Factory
    public val homePresenterFactory: HomePresenter.Factory

    @DependencyGraph.Factory
    public fun interface Factory {
        public fun create(): TestJvmGraph
    }
}
