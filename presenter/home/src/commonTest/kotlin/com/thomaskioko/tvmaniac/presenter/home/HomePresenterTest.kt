package com.thomaskioko.tvmaniac.presenter.home

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

abstract class HomePresenterTest {
    abstract fun createHomePresenter(componentContext: ComponentContext): HomePresenter

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var presenter: HomePresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()

        presenter = createHomePresenter(DefaultComponentContext(lifecycle = lifecycle))
    }

    @Test
    fun `initial state should be Discover`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
        }
    }

    @Test
    fun `should return Profile as active instance when onProfileClicked`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
            presenter.onProfileClicked()

            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Profile>()
        }
    }

    @Test
    fun `should return Library as active instance when onSettingsClicked`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
            presenter.onLibraryClicked()

            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Library>()
        }
    }
}
