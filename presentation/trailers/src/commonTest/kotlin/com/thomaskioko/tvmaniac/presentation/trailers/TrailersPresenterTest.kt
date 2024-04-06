package com.thomaskioko.tvmaniac.presentation.trailers

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.ServerError
import com.thomaskioko.tvmaniac.presentation.trailers.model.Trailer
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

internal class TrailersPresenterTest {

  private val lifecycle = LifecycleRegistry()
  private val repository = FakeTrailerRepository()
  private val testDispatcher = StandardTestDispatcher()
  private lateinit var presenter: TrailersPresenter

  @BeforeTest
  fun setUp() {
    Dispatchers.setMain(testDispatcher)

    lifecycle.resume()

    presenter =
      TrailersPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        traktShowId = 84958,
        repository = repository,
      )
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `given result is success correct state is emitted`() = runTest {
    repository.setTrailerList(trailers)

    presenter.state.test {
      awaitItem() shouldBe LoadingTrailers
      awaitItem() shouldBe
        TrailersContent(
          selectedVideoKey = "Fd43V",
          trailersList =
            persistentListOf(
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
    repository.setTrailerList(trailers)

    repository.setTrailerResult(Either.Left(ServerError("Something went wrong.")))

    presenter.state.test {
      awaitItem() shouldBe LoadingTrailers
      awaitItem() shouldBe
        TrailersContent(
          selectedVideoKey = "Fd43V",
          trailersList =
            persistentListOf(
              Trailer(
                showId = 84958,
                key = "Fd43V",
                name = "Some title",
                youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
              ),
            ),
        )

      awaitItem() shouldBe TrailerError("Something went wrong.")

      presenter.dispatch(ReloadTrailers)

      repository.setTrailerResult(Either.Right(trailers))

      awaitItem() shouldBe LoadingTrailers
      awaitItem() shouldBe
        TrailersContent(
          selectedVideoKey = "Fd43V",
          trailersList =
            persistentListOf(
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
