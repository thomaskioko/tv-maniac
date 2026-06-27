package com.thomaskioko.tvmaniac.presenter.showdetails.cast

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.domain.showdetails.FetchCastInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveCastInteractor
import com.thomaskioko.tvmaniac.presenter.showdetails.model.CastModel
import com.thomaskioko.tvmaniac.presenter.showdetails.showCast
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class ShowDetailsCastPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val castRepository = FakeCastRepository()
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
    fun `should map cast to state given cast is available`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            castRepository.setShowCast(showCast)

            var state = awaitItem()
            while (state.castsList.isEmpty()) {
                state = awaitItem()
            }

            state.castsList shouldBe listOf(
                CastModel(
                    id = 1L,
                    name = "Tom Hiddleston",
                    profileUrl = "/profile.jpg",
                    characterName = "Loki",
                ),
            )
            state.isRefreshing shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should retain cast given auth state changes to logged in`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            castRepository.setShowCast(showCast)

            var state = awaitItem()
            while (state.castsList.isEmpty()) {
                state = awaitItem()
            }

            accountManager.setActiveProvider(AccountProvider.TRAKT)
            testDispatcher.scheduler.advanceUntilIdle()

            val settled = expectMostRecentItem()
            settled.castsList.size shouldBe 1
            settled.isRefreshing shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun buildPresenter(forceRefresh: Boolean = false): ShowDetailsCastPresenter =
        ShowDetailsCastPresenter(
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            showId = SHOW_ID,
            forceRefresh = forceRefresh,
            observeCastInteractor = ObserveCastInteractor(
                castRepository = castRepository,
                dispatchers = dispatchers,
            ),
            fetchCastInteractor = FetchCastInteractor(
                castRepository = castRepository,
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
