package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.testing.di.TestIosComponent
import com.thomaskioko.tvmaniac.testing.di.create

internal class DefaultRootComponentIosTest : DefaultRootComponentTest() {
    private val testComponent: TestIosComponent = TestIosComponent::class.create()

    override val rootPresenterFactory: DefaultRootPresenter.Factory
        get() = testComponent.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testComponent.datastoreRepository
}
