package com.thomaskioko.tvmaniac.presenter.showdetails.similar

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveSimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presenter.showdetails.similarShowList
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class ShowDetailsSimilarPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val similarShowsRepository = FakeSimilarShowsRepository()
    private val accountManager = FakeAccountManager()
    private val navigator = FakeNavigator()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should map similar shows to state given shows are available`() = runTest {
        similarShowsRepository.setSimilarShowsResult(similarShowList)

        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.similarShows shouldBe listOf(
                ShowModel(
                    showId = 18495L,
                    title = "Loki",
                    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    isInLibrary = false,
                ),
            )
            state.isRefreshing shouldBe false
        }
    }

    @Test
    fun `should navigate to show details given similar show clicked`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ShowDetailsSimilarShowClicked(showId = 18495L))

        val route = navigator.lastNavigatedRoute
        route.shouldBeInstanceOf<ShowDetailsRoute>()
        route.param.showId shouldBe 18495L
    }

    @Test
    fun `should retain similar shows given auth state changes to logged in`() = runTest {
        similarShowsRepository.setSimilarShowsResult(similarShowList)

        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().similarShows.size shouldBe 1

            accountManager.setActiveProvider(SyncProviderSource.TRAKT)
            testDispatcher.scheduler.advanceUntilIdle()

            val settled = expectMostRecentItem()
            settled.similarShows.size shouldBe 1
            settled.isRefreshing shouldBe false
        }
    }

    private fun buildPresenter(forceRefresh: Boolean = false): ShowDetailsSimilarPresenter =
        ShowDetailsSimilarPresenter(
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            showId = SHOW_ID,
            forceRefresh = forceRefresh,
            observeSimilarShowsInteractor = ObserveSimilarShowsInteractor(
                similarShowsRepository = similarShowsRepository,
                dispatchers = dispatchers,
            ),
            similarShowsInteractor = SimilarShowsInteractor(
                similarShowsRepository = similarShowsRepository,
                dispatchers = dispatchers,
            ),
            navigator = navigator,
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

    private companion object {
        private const val SHOW_ID = 84958L
    }
}
