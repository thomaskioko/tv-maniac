package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.testing.di.TestIosGraph
import dev.zacsweers.metro.createGraphFactory

internal class HomePresenterIosTest : HomePresenterTest() {
    private val testGraph: TestIosGraph by lazy {
        createGraphFactory<TestIosGraph.Factory>().create()
    }

    override fun createHomePresenter(componentContext: ComponentContext): HomePresenter =
        testGraph.homeScreenGraphFactory.createHomeGraph(componentContext).homePresenter
}
