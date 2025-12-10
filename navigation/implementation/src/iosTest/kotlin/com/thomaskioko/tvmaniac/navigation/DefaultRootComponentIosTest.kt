package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.testing.di.TestIosComponent
import kotlin.test.Ignore

@Ignore
internal class DefaultRootComponentIosTest : DefaultRootComponentTest() {
    private val testComponent: TestIosComponent = TestIosComponent.create()

    override val rootPresenterFactory: RootPresenter.Factory
        get() = testComponent.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testComponent.datastoreRepository
}
