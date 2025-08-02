package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import me.tatarka.inject.annotations.Component
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Component
@SingleIn(TestScope::class)
@MergeComponent(TestScope::class)
abstract class TestJvmComponent : TestJvmComponentMerged {
    abstract val datastoreRepository: DatastoreRepository
    abstract val traktAuthManager: TraktAuthManager

    abstract val rootPresenterFactory: DefaultRootPresenter.Factory
    abstract val homePresenterFactory: DefaultHomePresenter.Factory
}
