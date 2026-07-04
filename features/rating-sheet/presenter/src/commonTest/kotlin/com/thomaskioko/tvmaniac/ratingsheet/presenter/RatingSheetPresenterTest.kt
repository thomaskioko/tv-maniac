package com.thomaskioko.tvmaniac.ratingsheet.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.domain.ratings.ObserveRatingInteractor
import com.thomaskioko.tvmaniac.domain.ratings.RateInteractor
import com.thomaskioko.tvmaniac.domain.ratings.RemoveRatingInteractor
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetParam
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class RatingSheetPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val appCoroutineScope = CoroutineScope(testDispatcher + SupervisorJob())
    private val ratingsRepository = FakeRatingsRepository()
    private val navigator = FakeNavigator()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        navigator.reset()
    }

    @Test
    fun `should emit user rating given rating is observed`() = runTest {
        ratingsRepository.setShowRating(
            ShowRating(userRating = 7, communityRating = null, communityVotes = null, pendingAction = PendingAction.NOTHING),
        )

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().userRating shouldBe 7
        }
    }

    @Test
    fun `should update rating without dismissing given star selected`() = runTest {
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.state.test {
            presenter.dispatch(RatingSheetAction.RatingSelected(9))
            testDispatcher.scheduler.advanceUntilIdle()

            expectMostRecentItem().userRating shouldBe 9
            navigator.overlayDismissCount shouldBe 0
        }
    }

    @Test
    fun `should clear rating without dismissing given rating cleared`() = runTest {
        ratingsRepository.setShowRating(
            ShowRating(userRating = 7, communityRating = null, communityVotes = null, pendingAction = PendingAction.NOTHING),
        )
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.state.test {
            presenter.dispatch(RatingSheetAction.RatingCleared)
            testDispatcher.scheduler.advanceUntilIdle()

            expectMostRecentItem().userRating.shouldBeNull()
            navigator.overlayDismissCount shouldBe 0
        }
    }

    @Test
    fun `should dismiss overlay given dismissed`() = runTest {
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(RatingSheetAction.Dismissed)
        testDispatcher.scheduler.advanceUntilIdle()

        navigator.overlayDismissCount shouldBe 1
    }

    private fun createPresenter(): RatingSheetPresenter =
        RatingSheetPresenter(
            param = RatingSheetParam(ratingType = RatingEntityType.SHOW, id = 1L),
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            observeRatingInteractor = ObserveRatingInteractor(ratingsRepository),
            rateInteractor = RateInteractor(ratingsRepository),
            removeRatingInteractor = RemoveRatingInteractor(ratingsRepository),
            navigator = navigator,
            localizer = FakeLocalizer(),
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
            appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
        )
}
