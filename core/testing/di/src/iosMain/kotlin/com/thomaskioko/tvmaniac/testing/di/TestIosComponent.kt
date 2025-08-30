package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.testing.TestScope
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(TestScope::class)
interface TestIosComponent {
    val datastoreRepository: DatastoreRepository
    val traktAuthManager: TraktAuthManager

    val rootPresenterFactory: DefaultRootPresenter.Factory
    val homePresenterFactory: DefaultHomePresenter.Factory

    @DependencyGraph.Factory
    interface Factory {
        fun create(): TestIosComponent
    }
}
