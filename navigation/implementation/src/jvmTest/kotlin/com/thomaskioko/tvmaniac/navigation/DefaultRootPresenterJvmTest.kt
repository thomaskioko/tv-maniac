package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.testing.di.TestJvmGraph
import dev.zacsweers.metro.createGraphFactory

internal class DefaultRootPresenterJvmTest : DefaultRootPresenterTest() {
    private val testComponent: TestJvmGraph by lazy {
        createGraphFactory<TestJvmGraph.Factory>().create()
    }

    override val rootPresenterFactory: RootPresenter.Factory
        get() = testComponent.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testComponent.datastoreRepository
}
