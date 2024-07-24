package com.thomaskioko.tvmaniac.data.seasondetails

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.db.SeasonCast
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.ServerError
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.presentation.seasondetails.EpisodeClicked
import com.thomaskioko.tvmaniac.presentation.seasondetails.InitialSeasonsState
import com.thomaskioko.tvmaniac.presentation.seasondetails.ReloadSeasonDetails
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsBackClicked
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsComponent
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsErrorState
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsLoaded
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonGalleryClicked
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class SeasonComponentTest {

  private val lifecycle = LifecycleRegistry()
  private val testDispatcher = StandardTestDispatcher()
  private val seasonDetailsRepository = FakeSeasonDetailsRepository()
  private val castRepository = FakeCastRepository()

  private lateinit var component: SeasonDetailsComponent

  @BeforeTest
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    component = buildSeasonDetailsPresenter()
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `should emitt InitialSeasonsState when no data is fetched`() = runTest {
    component.state.test { awaitItem() shouldBe InitialSeasonsState }
  }

  @Test
  fun `should emit SeasonDetailsContent when data is fetched`() = runTest {
    val seasonDetails =
      buildSeasonDetailsWithEpisodes(
        episodeCount = 1,
        episodes =
          listOf(
            EpisodeDetails(
              runtime = 45,
              overview =
                "The journey to reunite the Ingham family continues as they travel to the USA.",
              episodeNumber = 1,
              stillPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
              name = "Some title",
              seasonId = 12343,
              id = 12345,
              seasonNumber = 0,
              voteAverage = 8.0,
              voteCount = 4958,
              isWatched = false,
            ),
          ),
      )

    seasonDetailsRepository.setSeasonsResult(Either.Right(seasonDetails))
    castRepository.setSeasonCast(emptyList())

    val expectedResult =
      buildSeasonDetailsLoaded(
        episodeDetailsList =
          persistentListOf(
            EpisodeDetailsModel(
              id = 12345,
              seasonId = 12343,
              episodeTitle = "Some title",
              episodeNumberTitle = "E1 • Some title",
              overview =
                "The journey to reunite the Ingham family continues as they travel to the USA.",
              imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
              runtime = 45,
              voteCount = 4958,
              episodeNumber = "1",
              seasonEpisodeNumber = "S00 | E1",
            ),
          ),
      )

    component.state.test {
      awaitItem() shouldBe InitialSeasonsState
      awaitItem() shouldBe expectedResult
    }
  }

  @Test
  fun `should update state when ReloadSeasonDetails action is dispatched and data is fetched`() =
    runTest {
      val updatedDetails =
        buildSeasonDetailsWithEpisodes(
          episodeCount = 1,
          episodes =
            listOf(
              EpisodeDetails(
                id = 1,
                seasonId = 2L,
                name = "Episode 1",
                overview = "Episode 1 Overview",
                episodeNumber = 1,
                runtime = 123L,
                voteAverage = 4.5,
                voteCount = 100,
                isWatched = false,
                seasonNumber = 1,
                stillPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
              ),
            ),
        )
      val updatedCast =
        listOf(
          SeasonCast(
            id = Id(1L),
            name = "Updated Actor",
            character_name = "Updated Character",
            profile_path = "updated_profile_path",
          ),
        )

      val errorMessage = "Error fetching details"

      seasonDetailsRepository.setSeasonsResult(Either.Left(ServerError(errorMessage)))
      castRepository.setSeasonCast(emptyList())

      component.state.test {
        awaitItem() shouldBe InitialSeasonsState

        awaitItem() shouldBe SeasonDetailsErrorState(errorMessage = errorMessage)

        // Update the repositories with new data
        seasonDetailsRepository.setSeasonsResult(Either.Right(updatedDetails))
        castRepository.setSeasonCast(updatedCast)

        // Dispatch the ReloadSeasonDetails action
        component.dispatch(ReloadSeasonDetails)

        val updatedState = awaitItem()
        updatedState.shouldBeInstanceOf<SeasonDetailsLoaded>()
        updatedState.seasonName shouldBe "Season 01"
        updatedState.episodeCount shouldBe 1
        updatedState.episodeDetailsList.size shouldBe 1
        updatedState.seasonCast[0].name shouldBe "Updated Actor"
      }
    }

  @Test
  fun `should emitt ErrorState when fetching data fails`() = runTest {
    val errorMessage = "Error fetching details"

    seasonDetailsRepository.setSeasonsResult(Either.Left(ServerError(errorMessage)))
    castRepository.setSeasonCast(emptyList())

    component.state.test {
      awaitItem() shouldBe InitialSeasonsState
      awaitItem() shouldBe SeasonDetailsErrorState(errorMessage = errorMessage)
    }
  }

  @Test
  fun `should invoke onBack when SeasonDetailsBackClicked action is dispatched`() = runTest {
    var backCalled = false
    component = buildSeasonDetailsPresenter(onBack = { backCalled = true })

    component.dispatch(SeasonDetailsBackClicked)

    advanceUntilIdle() // Allow time for the coroutine to execute

    backCalled shouldBe true
  }

  @Test
  fun `should invoke onEpisodeClick when EpisodeClicked action is dispatched`() = runTest {
    var clickedEpisodeId: Long? = null
    val onEpisodeClick: (Long) -> Unit = { clickedEpisodeId = it }

    component = buildSeasonDetailsPresenter(onEpisodeClick = onEpisodeClick)

    component.dispatch(EpisodeClicked(42L))

    advanceUntilIdle() // Allow time for the coroutine to execute

    clickedEpisodeId shouldBe 42L
  }

  @Test
  fun `should toggle showGalleryBottomSheet when SeasonGalleryClicked action is dispatched`() =
    runTest {
      val initialDetails = buildSeasonDetailsWithEpisodes()
      seasonDetailsRepository.setSeasonsResult(Either.Right(initialDetails))
      castRepository.setSeasonCast(emptyList())

      component.state.test {
        awaitItem() shouldBe InitialSeasonsState

        val loadedState = awaitItem()
        loadedState.shouldBeInstanceOf<SeasonDetailsLoaded>()
        loadedState.showGalleryBottomSheet shouldBe false

        component.dispatch(SeasonGalleryClicked)

        // Check updated state
        val updatedState = awaitItem()
        updatedState.shouldBeInstanceOf<SeasonDetailsLoaded>()
        updatedState.showGalleryBottomSheet shouldBe true

        // Dispatch action again
        component.dispatch(SeasonGalleryClicked)

        // Check state toggled back
        val toggledState = awaitItem()
        toggledState.shouldBeInstanceOf<SeasonDetailsLoaded>()
        toggledState.showGalleryBottomSheet shouldBe false
      }
    }

  private fun buildSeasonDetailsPresenter(
    onBack: () -> Unit = {},
    onEpisodeClick: (id: Long) -> Unit = {},
  ): SeasonDetailsComponent {
    return SeasonDetailsComponent(
      componentContext = DefaultComponentContext(lifecycle = lifecycle),
      param =
        SeasonDetailsUiParam(
          showId = 1,
          seasonId = 1,
          seasonNumber = 1,
        ),
      onBack = onBack,
      onEpisodeClick = onEpisodeClick,
      seasonDetailsRepository = seasonDetailsRepository,
      castRepository = castRepository,
    )
  }
}
