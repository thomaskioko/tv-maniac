package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent.CreateComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.reflect.KClass

@SingleIn(TestScope::class)
@MergeComponent(TestScope::class)
abstract class TestIosComponent {
    abstract val datastoreRepository: DatastoreRepository
    abstract val traktAuthManager: TraktAuthManager

    abstract val rootPresenterFactory: RootPresenter.Factory
    abstract val homePresenterFactory: HomePresenter.Factory

    companion object {
        fun create() = TestIosComponent::class.createComponent()
    }
}

/**
 * The `actual fun` will be generated for each iOS specific target. TestIosComponentMerged is not being generated
 */
@CreateComponent
expect fun KClass<TestIosComponent>.createComponent(): TestIosComponent
