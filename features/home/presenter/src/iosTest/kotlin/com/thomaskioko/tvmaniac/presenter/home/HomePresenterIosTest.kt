package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.testing.di.TestGraph
import dev.zacsweers.metro.createGraphFactory

internal class HomePresenterIosTest : HomePresenterTest() {
    private val testGraph: TestGraph by lazy {
        createGraphFactory<TestGraph.Factory>().create()
    }

    override fun createHomePresenter(componentContext: ComponentContext): HomePresenter =
        testGraph.homeScreenGraphFactory.createHomeGraph(componentContext).homePresenter
}
