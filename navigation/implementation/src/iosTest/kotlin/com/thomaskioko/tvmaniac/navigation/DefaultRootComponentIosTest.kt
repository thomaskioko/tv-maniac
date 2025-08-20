package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.testing.di.TestIosComponent

internal class DefaultRootComponentIosTest : DefaultRootComponentTest() {
    private val testComponent: TestIosComponent = TestIosComponent.create()

    override val rootPresenterFactory: DefaultRootPresenter.Factory
        get() = testComponent.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testComponent.datastoreRepository
}
