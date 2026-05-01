package com.thomaskioko.tvmaniac.presenter.home

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.library.nav.LibraryRoot
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import io.kotest.matchers.shouldBe
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
    fun `initial active root should be Discover`() = runTest {
        presenter.activeRoot.test {
            awaitItem().shouldBeInstanceOf<DiscoverRoot>()
        }
    }

    @Test
    fun `should switch active root to Profile when onProfileClicked`() = runTest {
        presenter.activeRoot.test {
            awaitItem() shouldBe DiscoverRoot
            presenter.onProfileClicked()

            awaitItem() shouldBe ProfileRoot
        }
    }

    @Test
    fun `should switch active root to Library when onLibraryClicked`() = runTest {
        presenter.activeRoot.test {
            awaitItem() shouldBe DiscoverRoot
            presenter.onLibraryClicked()

            awaitItem() shouldBe LibraryRoot
        }
    }
}
