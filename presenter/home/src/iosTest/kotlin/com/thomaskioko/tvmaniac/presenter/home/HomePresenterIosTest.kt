package com.thomaskioko.tvmaniac.presenter.home

import com.thomaskioko.tvmaniac.testing.di.TestIosGraph
import dev.zacsweers.metro.createGraphFactory

internal class HomePresenterIosTest : HomePresenterTest() {
    private val testGraph: TestIosGraph by lazy {
        createGraphFactory<TestIosGraph.Factory>().create()
    }

    override val homePresenterFactory: HomePresenter.Factory
        get() = testGraph.homePresenterFactory
}
