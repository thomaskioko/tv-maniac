package com.thomaskioko.tvmaniac.presenter.home

import com.thomaskioko.tvmaniac.testing.di.TestJvmGraph
import dev.zacsweers.metro.createGraphFactory

internal class HomePresenterJvmTest : HomePresenterTest() {
    private val testComponent: TestJvmGraph =
        createGraphFactory<TestJvmGraph.Factory>().create()

    override val homePresenterFactory: HomePresenter.Factory
        get() = testComponent.homePresenterFactory
}
