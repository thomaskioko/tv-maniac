package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
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

    abstract val rootPresenterFactory: RootPresenter.Factory
    abstract val homePresenterFactory: HomePresenter.Factory
}
