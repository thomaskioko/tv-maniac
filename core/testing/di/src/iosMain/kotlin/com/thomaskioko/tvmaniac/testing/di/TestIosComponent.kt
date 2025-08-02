package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
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

    abstract val rootPresenterFactory: DefaultRootPresenter.Factory
    abstract val homePresenterFactory: DefaultHomePresenter.Factory

    companion object {
        fun create() = TestIosComponent::class.createComponent()
    }
}

/**
 * The `actual fun` will be generated for each iOS specific target. TestIosComponentMerged is not being generated
 */
@CreateComponent
expect fun KClass<TestIosComponent>.createComponent(): TestIosComponent
