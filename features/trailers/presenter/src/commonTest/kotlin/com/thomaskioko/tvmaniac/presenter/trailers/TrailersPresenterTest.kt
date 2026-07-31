package com.thomaskioko.tvmaniac.presenter.trailers

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.domain.showdetails.FetchTrailersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveTrailersInteractor
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.presenter.trailers.model.Trailer
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class TrailersPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val repository = FakeTrailerRepository()
    private val localizer = FakeLocalizer()
    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private lateinit var presenter: TrailersPresenter

    private val title = localizer.getString(StringResourceKey.TitleTrailer)
    private val moreTrailersTitle = localizer.getString(StringResourceKey.StrMoreTrailers)
    private val retryLabel = localizer.getString(StringResourceKey.GenericRetry)

    private val trailersContent = TrailersContent(
        title = title,
        moreTrailersTitle = moreTrailersTitle,
        selectedVideoKey = "Fd43V",
        trailersList = persistentListOf(
            Trailer(
                showId = 84958,
                key = "Fd43V",
                name = "Some title",
                youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
            ),
        ),
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        lifecycle.resume()

        presenter = TrailersPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            showId = 84958,
            observeTrailersInteractor = ObserveTrailersInteractor(
                trailerRepository = repository,
                dispatchers = dispatchers,
            ),
            fetchTrailersInteractor = FetchTrailersInteractor(
                trailerRepository = repository,
                dispatchers = dispatchers,
            ),
            localizer = localizer,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given result is success correct state is emitted`() = runTest {
        repository.setYoutubePlayerInstalled(false)
        repository.setTrailerResult(trailers)

        presenter.state.test {
            awaitItem() shouldBe LoadingTrailers(title)
            awaitItem() shouldBe trailersContent
        }
    }

    @Test
    fun `given trailer is selected then selection changes and list is kept`() = runTest {
        repository.setYoutubePlayerInstalled(false)
        repository.setTrailerResult(trailers)

        presenter.state.test {
            awaitItem() shouldBe LoadingTrailers(title)
            awaitItem() shouldBe trailersContent

            presenter.dispatch(TrailerSelected(trailerKey = "aB9dE"))

            awaitItem() shouldBe trailersContent.copy(selectedVideoKey = "aB9dE")
        }
    }

    @Test
    fun `given reload is clicked then correct state is emitted`() = runTest {
        repository.setYoutubePlayerInstalled(false)
        repository.setTrailerResult(emptyList())

        presenter.state.test {
            awaitItem() shouldBe LoadingTrailers(title)
            awaitItem() shouldBe TrailersContent(
                title = title,
                moreTrailersTitle = moreTrailersTitle,
            )

            presenter.dispatch(ReloadTrailers)

            awaitItem() shouldBe LoadingTrailers(title)
            awaitItem() shouldBe TrailersContent(
                title = title,
                moreTrailersTitle = moreTrailersTitle,
            )

            repository.setTrailerResult(trailers)

            awaitItem() shouldBe trailersContent
        }
    }

    @Test
    fun `given fetching trailers fails then error state is emitted`() = runTest {
        repository.setFetchTrailersFailure(RuntimeException("Something went wrong"))
        repository.setYoutubePlayerInstalled(false)

        presenter.state.test {
            awaitItem() shouldBe LoadingTrailers(title)
            awaitItem() shouldBe TrailerError(
                title = title,
                errorMessage = "Something went wrong",
                retryLabel = retryLabel,
            )
        }
    }

    @Test
    fun `given video playback fails then error state is emitted`() = runTest {
        repository.setYoutubePlayerInstalled(false)
        repository.setTrailerResult(trailers)

        presenter.state.test {
            awaitItem() shouldBe LoadingTrailers(title)
            awaitItem() shouldBe trailersContent

            presenter.dispatch(VideoPlayerError(errorMessage = "Playback failed"))

            awaitItem() shouldBe TrailerError(
                title = title,
                errorMessage = "Playback failed",
                retryLabel = retryLabel,
            )
        }
    }
}
