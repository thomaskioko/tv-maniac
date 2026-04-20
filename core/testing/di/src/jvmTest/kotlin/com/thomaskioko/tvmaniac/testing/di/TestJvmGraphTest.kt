package com.thomaskioko.tvmaniac.testing.di

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

internal class TestJvmGraphTest {

    @Test
    fun `should provide fake DatastoreRepository`() = runTestWithGraph { graph ->
        graph.datastoreRepository.shouldBeInstanceOf<FakeDatastoreRepository>()
    }

    @Test
    fun `should provide fake TraktAuthManager`() = runTestWithGraph { graph ->
        graph.traktAuthManager.shouldBeInstanceOf<FakeTraktAuthManager>()
    }

    @Test
    fun `should resolve RootPresenter factory`() = runTestWithGraph { graph ->
        val lifecycle = LifecycleRegistry().apply { resume() }
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        val presenter = graph.rootPresenterFactory(componentContext)

        presenter.shouldBeInstanceOf<RootPresenter>()

        // Tear down the lifecycle before the test exits so presenter-internal
        // coroutines that were launched on `Dispatchers.Main` complete before
        // `runTestWithGraph`'s `Dispatchers.resetMain()` fires.
        lifecycle.destroy()
        advanceUntilIdle()
    }

    @Test
    fun `should resolve NavDestinations set`() = runTestWithGraph { graph ->
        graph.navDestinations.shouldBeInstanceOf<Set<*>>()
    }
}
