package com.thomaskioko.tvmaniac.presenter.root

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.testing.di.TestGraph
import dev.zacsweers.metro.createGraphFactory

internal class DefaultRootPresenterJvmTest : DefaultRootPresenterTest() {
    private val testComponent: TestGraph by lazy {
        createGraphFactory<TestGraph.Factory>().create()
    }

    override val rootPresenterFactory: RootPresenter.Factory
        get() = testComponent.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testComponent.datastoreRepository

    override val navigator: Navigator
        get() = testComponent.navigator
}
