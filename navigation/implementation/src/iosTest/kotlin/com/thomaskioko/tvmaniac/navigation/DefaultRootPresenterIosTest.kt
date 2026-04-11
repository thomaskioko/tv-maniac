package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.testing.di.TestIosGraph
import dev.zacsweers.metro.createGraphFactory

internal class DefaultRootPresenterIosTest : DefaultRootPresenterTest() {
    private val testGraph: TestIosGraph by lazy {
        createGraphFactory<TestIosGraph.Factory>().create()
    }

    override val rootPresenterFactory: RootPresenter.Factory
        get() = testGraph.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testGraph.datastoreRepository
}
