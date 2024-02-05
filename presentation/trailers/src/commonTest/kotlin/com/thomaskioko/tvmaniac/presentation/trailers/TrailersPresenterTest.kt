package com.thomaskioko.tvmaniac.presentation.trailers

import com.thomaskioko.tvmaniac.presentation.trailers.model.Trailer
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.ServerError
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@Ignore
@OptIn(ExperimentalCoroutinesApi::class)
internal class TrailersPresenterTest {

  private val repository = FakeTrailerRepository()
  private val testDispatcher = StandardTestDispatcher()
  private lateinit var presenter: TrailersPresenter

  @BeforeTest
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    /*  presenter = TrailersPresenter(
        traktShowId = 84958,
        repository = repository,
    )*/
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `given result is success correct state is emitted`() = runTest {
    repository.setTrailerList(trailers)

    presenter.state shouldBe LoadingTrailers
    presenter.state shouldBe
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

  @Test
  fun `given reload is clicked then correct state is emitted`() = runTest {
    repository.setTrailerList(trailers)

    repository.setTrailerResult(Either.Left(ServerError("Something went wrong.")))

    presenter.state shouldBe LoadingTrailers
    presenter.state shouldBe
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

    presenter.state shouldBe TrailerError("Something went wrong.")

    presenter.dispatch(ReloadTrailers)

    repository.setTrailerResult(Either.Right(trailers))

    presenter.state shouldBe LoadingTrailers
    presenter.state shouldBe
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
