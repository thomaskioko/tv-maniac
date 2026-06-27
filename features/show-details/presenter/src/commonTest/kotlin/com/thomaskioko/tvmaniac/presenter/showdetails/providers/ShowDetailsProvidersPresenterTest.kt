package com.thomaskioko.tvmaniac.presenter.showdetails.providers

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.FetchWatchProvidersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveWatchProvidersInteractor
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.watchProviderList
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class ShowDetailsProvidersPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val watchProvidersRepository = FakeWatchProviderRepository()
    private val accountManager = FakeAccountManager()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should map watch providers to state given providers are available`() = runTest {
        watchProvidersRepository.setWatchProvidersResult(watchProviderList)

        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.providers shouldBe listOf(
                ProviderModel(
                    id = 184958L,
                    logoUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    name = "Netflix",
                ),
            )
            state.isRefreshing shouldBe false
        }
    }

    @Test
    fun `should re-fetch providers given refresh is called`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()
        watchProvidersRepository.clearFetchInvocations()

        presenter.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        val invocation = watchProvidersRepository.fetchInvocations().last()
        invocation.showId shouldBe SHOW_ID
        invocation.forceRefresh shouldBe true
    }

    @Test
    fun `should re-fetch providers given auth state changes to logged in`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()
        watchProvidersRepository.clearFetchInvocations()

        accountManager.setActiveProvider(AccountProvider.TRAKT)
        testDispatcher.scheduler.advanceUntilIdle()

        val invocation = watchProvidersRepository.fetchInvocations().last()
        invocation.showId shouldBe SHOW_ID
        invocation.forceRefresh shouldBe true
    }

    private fun buildPresenter(forceRefresh: Boolean = false): ShowDetailsProvidersPresenter =
        ShowDetailsProvidersPresenter(
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            showId = SHOW_ID,
            forceRefresh = forceRefresh,
            observeWatchProvidersInteractor = ObserveWatchProvidersInteractor(
                watchProviderRepository = watchProvidersRepository,
                dispatchers = dispatchers,
            ),
            fetchWatchProvidersInteractor = FetchWatchProvidersInteractor(
                watchProviderRepository = watchProvidersRepository,
                dispatchers = dispatchers,
            ),
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

    private companion object {
        private const val SHOW_ID = 84958L
    }
}
