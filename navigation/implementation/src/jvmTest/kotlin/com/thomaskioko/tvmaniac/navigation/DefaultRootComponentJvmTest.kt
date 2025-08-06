package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.testing.di.TestJvmComponent
import com.thomaskioko.tvmaniac.testing.di.create

internal class DefaultRootComponentJvmTest : DefaultRootComponentTest() {
    private val testComponent: TestJvmComponent = TestJvmComponent::class.create()

    override val rootPresenterFactory: DefaultRootPresenter.Factory
        get() = testComponent.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testComponent.datastoreRepository
}
