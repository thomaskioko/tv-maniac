package com.thomaskioko.tvmaniac.presenter.trailers

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
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
    private lateinit var presenter: TrailersPresenter

    private val title = localizer.getString(StringResourceKey.TitleTrailer)
    private val moreTrailersTitle = localizer.getString(StringResourceKey.StrMoreTrailers)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        lifecycle.resume()

        presenter = TrailersPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            showId = 84958,
            repository = repository,
            localizer = localizer,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given result is success correct state is emitted`() = runTest {
        repository.setTrailerResult(trailers)

        presenter.state.test {
            awaitItem() shouldBe LoadingTrailers(title)
            awaitItem() shouldBe TrailersContent(
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
        }
    }

    @Test
    fun `given trailer is selected then selection changes and list is kept`() = runTest {
        repository.setTrailerResult(trailers)

        presenter.state.test {
            awaitItem() shouldBe LoadingTrailers(title)
            awaitItem() shouldBe TrailersContent(
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

            presenter.dispatch(TrailerSelected(trailerKey = "aB9dE"))

            awaitItem() shouldBe TrailersContent(
                title = title,
                moreTrailersTitle = moreTrailersTitle,
                selectedVideoKey = "aB9dE",
                trailersList = persistentListOf(
                    Trailer(
                        showId = 84958,
                        key = "Fd43V",
                        name = "Some title",
                        youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
                    ),
                ),
            )
        }
    }

    @Test
    fun `given reload is clicked then correct state is emitted`() = runTest {
        repository.setTrailerResult(emptyList())

        presenter.state.test {
            awaitItem() shouldBe LoadingTrailers(title)
            awaitItem() shouldBe TrailersContent(
                title = title,
                moreTrailersTitle = moreTrailersTitle,
            )

            presenter.dispatch(ReloadTrailers)

            repository.setTrailerResult(trailers)

            awaitItem() shouldBe
                TrailersContent(
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
        }
    }
}
