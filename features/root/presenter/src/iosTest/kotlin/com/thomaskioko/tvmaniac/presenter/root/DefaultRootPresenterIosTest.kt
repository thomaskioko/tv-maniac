package com.thomaskioko.tvmaniac.presenter.root

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.featureflags.testing.FakeRemoteConfigBridge
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.testing.di.TestGraph
import dev.zacsweers.metro.createGraphFactory

internal class DefaultRootPresenterIosTest : DefaultRootPresenterTest() {
    private val testGraph: TestGraph by lazy {
        createGraphFactory<TestGraph.Factory>().create(remoteConfigBridge = FakeRemoteConfigBridge())
    }

    override val rootPresenterFactory: RootPresenter.Factory
        get() = testGraph.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testGraph.datastoreRepository

    override val navigator: Navigator
        get() = testGraph.navigator

    override val syncObserver: SyncObserver
        get() = testGraph.syncObserver
}
