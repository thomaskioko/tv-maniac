package com.thomaskioko.tvmaniac.presenter.home

import com.thomaskioko.tvmaniac.testing.di.TestIosComponent

internal class HomePresenterIosTest : HomePresenterTest() {
    private val testComponent: TestIosComponent = TestIosComponent.create()

    override val homePresenterFactory: HomePresenter.Factory
        get() = testComponent.homePresenterFactory
}
