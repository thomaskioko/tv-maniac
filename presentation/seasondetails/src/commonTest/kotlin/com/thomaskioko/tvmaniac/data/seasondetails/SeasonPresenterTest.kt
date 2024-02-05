package com.thomaskioko.tvmaniac.data.seasondetails

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsContent
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.SeasonWithEpisodeList
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SeasonPresenterTest {

  private val lifecycle = LifecycleRegistry()
  private val testDispatcher = StandardTestDispatcher()
  private val seasonDetailsRepository = FakeSeasonDetailsRepository()
  private val castRepository = FakeCastRepository()

  private lateinit var presenter: SeasonDetailsPresenter

  @BeforeTest
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    presenter =
      SeasonDetailsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        param =
          SeasonDetailsUiParam(
            showId = 1,
            seasonId = 1,
            seasonNumber = 1,
          ),
        onBack = {},
        onEpisodeClick = {},
        seasonDetailsRepository = seasonDetailsRepository,
        castRepository = castRepository,
      )
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun onLoadSeasonDetails_correct_state_is_emitted() = runTest {
    seasonDetailsRepository.setCachedResults(SeasonWithEpisodeList)
    castRepository.setSeasonCast(emptyList())

    presenter.state.test {
      awaitItem() shouldBe SeasonDetailsContent.DEFAULT_SEASON_STATE
      awaitItem() shouldBe seasonDetailsContent
    }
  }
}
