package com.thomaskioko.tvmaniac.presenter.home

import com.thomaskioko.tvmaniac.testing.di.TestJvmComponent
import com.thomaskioko.tvmaniac.testing.di.create

internal class HomePresenterJvmTest : HomePresenterTest() {
    private val testComponent: TestJvmComponent = TestJvmComponent::class.create()

    override val homePresenterFactory: DefaultHomePresenter.Factory
        get() = testComponent.homePresenterFactory
}
