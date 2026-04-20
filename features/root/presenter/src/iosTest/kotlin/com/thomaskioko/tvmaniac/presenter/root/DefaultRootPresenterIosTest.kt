package com.thomaskioko.tvmaniac.presenter.root

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.testing.di.TestGraph
import dev.zacsweers.metro.createGraphFactory

internal class DefaultRootPresenterIosTest : DefaultRootPresenterTest() {
    private val testGraph: TestGraph by lazy {
        createGraphFactory<TestGraph.Factory>().create()
    }

    override val rootPresenterFactory: RootPresenter.Factory
        get() = testGraph.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testGraph.datastoreRepository

    override val navigator: Navigator
        get() = testGraph.navigator
}
