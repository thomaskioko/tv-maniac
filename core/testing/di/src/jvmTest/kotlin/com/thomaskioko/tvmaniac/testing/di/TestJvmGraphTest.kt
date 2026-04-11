package com.thomaskioko.tvmaniac.testing.di

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import dev.zacsweers.metro.createGraphFactory
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class TestJvmGraphTest {
    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()

    private val component: TestJvmGraph by lazy {
        createGraphFactory<TestJvmGraph.Factory>().create()
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should provide fake DatastoreRepository`() {
        component.datastoreRepository.shouldBeInstanceOf<FakeDatastoreRepository>()
    }

    @Test
    fun `should provide fake TraktAuthManager`() {
        component.traktAuthManager.shouldBeInstanceOf<FakeTraktAuthManager>()
    }

    @Test
    fun `should resolve RootPresenter factory`() {
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        val navigator = com.thomaskioko.tvmaniac.navigation.DefaultRootNavigator()
        val presenter = component.rootPresenterFactory(componentContext, navigator)

        presenter.shouldBeInstanceOf<RootPresenter>()
    }

    @Test
    fun `should resolve HomePresenter factory`() {
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        val presenter = component.homePresenterFactory.create(
            componentContext = componentContext,
            onShowClicked = {},
            onMoreShowClicked = {},
            onShowGenreClicked = {},
            onNavigateToSearch = {},
            onSettingsClicked = {},
            onSeasonClicked = { _, _, _ -> },
            onDiscoverEpisodeLongPressed = {},
            onUpNextEpisodeLongPressed = {},
            onCalendarEpisodeLongPressed = {},
        )

        presenter.shouldBeInstanceOf<HomePresenter>()
    }
}
