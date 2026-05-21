package com.thomaskioko.tvmaniac.featureflags.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.domain.featureflags.ObserveFeatureFlagRowsInteractor
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.flags.ContinueWatchingNitroFlag
import com.thomaskioko.tvmaniac.featureflags.flags.SimklLoginFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class FeatureFlagsPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val remoteConfig = FakeFeatureFlagsRemoteConfig()
    private val localStore = FakeFeatureFlagLocalStore()
    private val navigator = FakeNavigator()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val localizer = FakeLocalizer()

    private val nitroFlag = ContinueWatchingNitroFlag(remote = remoteConfig)
    private val simklFlag = SimklLoginFlag(remote = remoteConfig)
    private val flags: Set<FeatureFlag> = setOf(nitroFlag, simklFlag)

    private val observeRows = ObserveFeatureFlagRowsInteractor(flags = flags)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit default state on init`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem() shouldBe FeatureFlagsState.DEFAULT_STATE
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should populate items for every flag with Firebase source by default`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.items.map { it.key } shouldContainExactlyInAnyOrder flags.map { it.key }
            state.items.all { !it.isLocal } shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should write to local store given ToggleFlag`() = runTest {
        val presenter = buildPresenter()

        presenter.dispatch(ToggleFlag(key = simklFlag.key, value = true))
        testDispatcher.scheduler.advanceUntilIdle()

        localStore.observeAll().test {
            awaitItem() shouldBe mapOf(simklFlag.key to true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should clear single flag given ClearLocal`() = runTest {
        val presenter = buildPresenter()
        localStore.set(simklFlag.key, true)

        presenter.dispatch(ClearLocal(key = simklFlag.key))
        testDispatcher.scheduler.advanceUntilIdle()

        localStore.observeAll().test {
            awaitItem() shouldBe emptyMap()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should empty store given ClearAllLocals`() = runTest {
        val presenter = buildPresenter()
        localStore.set(simklFlag.key, true)

        presenter.dispatch(ClearAllLocals)
        testDispatcher.scheduler.advanceUntilIdle()

        localStore.observeAll().test {
            awaitItem() shouldBe emptyMap()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should refresh remote config and update forceRefreshSubtitle given ForceRefresh`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            skipItems(2)
            presenter.dispatch(ForceRefresh)
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem().forceRefreshSubtitle shouldBe "Last fetched at 2024-01-01 12:00"
            cancelAndIgnoreRemainingEvents()
        }
        remoteConfig.refreshCount shouldBe 1
    }

    @Test
    fun `should filter items to empty given SearchQueryChanged with no matches`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            skipItems(2)
            presenter.dispatch(SearchQueryChanged("no_match_for_this_query"))
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            state.searchQuery shouldBe "no_match_for_this_query"
            state.items.isEmpty() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should flip ascending given DirectionToggled`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            skipItems(2)
            presenter.dispatch(DirectionToggled)
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem().ascending shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update sort given SortChanged`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            skipItems(2)
            presenter.dispatch(SortChanged(FeatureFlagSortDescriptor.Title))
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem().sort shouldBe FeatureFlagSortDescriptor.Title
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should flip groupByType given GroupByTypeToggled`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            skipItems(2)
            presenter.dispatch(GroupByTypeToggled)
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem().groupByType shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate back given BackClicked`() = runTest {
        val presenter = buildPresenter()

        presenter.dispatch(BackClicked)

        navigator.navigateBackCount shouldBe 1
    }

    private fun buildPresenter(): FeatureFlagsPresenter {
        val lifecycle = LifecycleRegistry().apply { resume() }
        return FeatureFlagsPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            remoteConfig = remoteConfig,
            localStore = localStore,
            navigator = navigator,
            dateTimeProvider = dateTimeProvider,
            localizer = localizer,
            observeRows = observeRows,
        )
    }
}
