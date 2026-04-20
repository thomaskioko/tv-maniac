package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.testing.di.TestGraph
import dev.zacsweers.metro.createGraphFactory

internal class HomePresenterJvmTest : HomePresenterTest() {
    private val testComponent: TestGraph =
        createGraphFactory<TestGraph.Factory>().create()

    override fun createHomePresenter(componentContext: ComponentContext): HomePresenter =
        testComponent.homeScreenGraphFactory.createHomeGraph(componentContext).homePresenter
}
