package com.thomaskioko.tvmaniac.featureflags.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.domain.featureflags.FeatureFlagRow
import com.thomaskioko.tvmaniac.domain.featureflags.ObserveFeatureFlagRowsInteractor
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagProvider
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlags
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class FeatureFlagsPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val featureFlags = FakeFeatureFlags()
    private val localStore = FakeFeatureFlagLocalStore()
    private val provider = FakeFeatureFlagProvider(localStore = localStore)
    private val navigator = FakeNavigator()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val observeRows = ObserveFeatureFlagRowsInteractor(
        featureFlags = featureFlags,
        provider = provider,
    )

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
        val presenter = buildPresenter(this)

        presenter.state.test {
            awaitItem() shouldBe FeatureFlagsState.DEFAULT_STATE
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should populate rows for every flag with Firebase source by default`() = runTest {
        val presenter = buildPresenter(this)

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.rows shouldBe FeatureFlag.entries.map { flag ->
                FeatureFlagRow(
                    featureFlag = flag,
                    value = flag.defaultValue,
                    featureFlagSource = FeatureFlagSource.Firebase,
                )
            }.toList()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should write to local store given ToggleFlag`() = runTest {
        val presenter = buildPresenter(this)

        presenter.dispatch(ToggleFlag(FeatureFlag.SIMKL_LOGIN_ENABLED, value = true))
        testDispatcher.scheduler.advanceUntilIdle()

        localStore.observeAll().test {
            awaitItem() shouldBe mapOf(FeatureFlag.SIMKL_LOGIN_ENABLED to true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should clear single flag given ClearLocal`() = runTest {
        val presenter = buildPresenter(this)
        localStore.set(FeatureFlag.SIMKL_LOGIN_ENABLED, true)

        presenter.dispatch(ClearLocal(FeatureFlag.SIMKL_LOGIN_ENABLED))
        testDispatcher.scheduler.advanceUntilIdle()

        localStore.observeAll().test {
            awaitItem() shouldBe emptyMap()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should empty store given ClearAllLocals`() = runTest {
        val presenter = buildPresenter(this)
        localStore.set(FeatureFlag.SIMKL_LOGIN_ENABLED, true)

        presenter.dispatch(ClearAllLocals)
        testDispatcher.scheduler.advanceUntilIdle()

        localStore.observeAll().test {
            awaitItem() shouldBe emptyMap()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should refresh feature flags and update lastFetchedAt given ForceRefresh`() = runTest {
        val presenter = buildPresenter(this)

        presenter.state.test {
            skipItems(2)
            presenter.dispatch(ForceRefresh)
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem().lastFetchedAt shouldBe dateTimeProvider.now()
            cancelAndIgnoreRemainingEvents()
        }
        featureFlags.refreshCount shouldBe 1
    }

    @Test
    fun `should filter rows to empty given SearchQueryChanged with no matches`() = runTest {
        val presenter = buildPresenter(this)

        presenter.state.test {
            skipItems(2)
            presenter.dispatch(SearchQueryChanged("no_match_for_this_query"))
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            state.searchQuery shouldBe "no_match_for_this_query"
            state.rows.isEmpty() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should flip ascending given DirectionToggled`() = runTest {
        val presenter = buildPresenter(this)

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
        val presenter = buildPresenter(this)

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
        val presenter = buildPresenter(this)

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
        val presenter = buildPresenter(this)

        presenter.dispatch(BackClicked)

        navigator.navigateBackCount shouldBe 1
    }

    private fun buildPresenter(scope: TestScope): FeatureFlagsPresenter {
        val lifecycle = LifecycleRegistry().apply { resume() }
        return FeatureFlagsPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            featureFlags = featureFlags,
            localStore = localStore,
            navigator = navigator,
            dateTimeProvider = dateTimeProvider,
            observeRows = observeRows,
        )
    }
}
