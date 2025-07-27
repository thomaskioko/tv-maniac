package com.thomaskioko.tvmaniac.seasondetails.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.domain.seasondetails.ObservableSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsInteractor
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.presenter.data.buildSeasonDetailsLoaded
import com.thomaskioko.tvmaniac.seasondetails.presenter.data.buildSeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SeasonPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val castRepository = FakeCastRepository()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var presenter: SeasonDetailsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        presenter = buildSeasonDetailsPresenter()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit InitialSeasonsState when no data is fetched`() = runTest {
        presenter.state.test { awaitItem() shouldBe SeasonDetailsModel.Empty }
    }

    @Test
    fun `should emit SeasonDetailsContent when data is fetched`() = runTest {
        val seasonDetails = buildSeasonDetailsWithEpisodes(
            episodeCount = 1,
            episodes = listOf(
                EpisodeDetails(
                    runtime = 45,
                    overview = "The journey to reunite the Ingham family continues as they travel to the USA.",
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

        seasonDetailsRepository.setSeasonsResult(seasonDetails)
        castRepository.setSeasonCast(emptyList())

        val expectedResult = buildSeasonDetailsLoaded(
            episodeDetailsList = persistentListOf(
                EpisodeDetailsModel(
                    id = 12345,
                    seasonId = 12343,
                    episodeTitle = "Some title",
                    episodeNumberTitle = "E1 • Some title",
                    overview = "The journey to reunite the Ingham family continues as they travel to the USA.",
                    imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    runtime = 45,
                    voteCount = 4958,
                    episodeNumber = "1",
                    seasonEpisodeNumber = "S00 | E1",
                ),
            ),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem() shouldBe expectedResult
        }
    }

    @Test
    fun `should update state when ReloadSeasonDetails action is dispatched and data is fetched`() = runTest {
        val updatedDetails = buildSeasonDetailsWithEpisodes(
            episodeCount = 1,
            episodes = listOf(
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
        val updatedCast = listOf(
            SeasonCast(
                id = Id(1L),
                name = "Updated Actor",
                character_name = "Updated Character",
                profile_path = "updated_profile_path",
            ),
        )

        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            // Update the repositories with new data
            seasonDetailsRepository.setSeasonsResult(updatedDetails)
            castRepository.setSeasonCast(updatedCast)

            awaitItem() shouldBe SeasonDetailsModel(
                isUpdating = false,
                showGalleryBottomSheet = false,
                seasonImages = persistentListOf(),
                showSeasonWatchStateDialog = false,
                expandEpisodeItems = false,
                watchProgress = 0F,
                isSeasonWatched = false,
                episodeCount = 1,
                seasonId = 12343,
                seasonName = "Season 01",
                seasonOverview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
                imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                episodeDetailsList = persistentListOf(
                    EpisodeDetailsModel(
                        id = 1,
                        seasonId = 2,
                        episodeTitle = "Episode 1",
                        overview = "Episode 1 Overview",
                        episodeNumberTitle = "E1 • Episode 1",
                        runtime = 123L,
                        voteCount = 100,
                        imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                        episodeNumber = "1",
                        seasonEpisodeNumber = "S01 | E1",
                    ),
                ),
                seasonCast = persistentListOf(),
            )

            val state = awaitItem()
            state.seasonName shouldBe "Season 01"
            state.episodeCount shouldBe 1
            state.episodeDetailsList.size shouldBe 1
            state.seasonCast.size shouldBe 1

            // Dispatch the ReloadSeasonDetails action
            presenter.dispatch(ReloadSeasonDetails)

            val updatedState = awaitItem()
            updatedState.seasonName shouldBe "Season 01"
            updatedState.episodeCount shouldBe 1
            updatedState.episodeDetailsList.size shouldBe 1
            updatedState.seasonCast[0].name shouldBe "Updated Actor"
        }
    }

    @Test
    fun `should invoke onBack when SeasonDetailsBackClicked action is dispatched`() = runTest {
        var backCalled = false
        presenter = buildSeasonDetailsPresenter(onBack = { backCalled = true })

        presenter.dispatch(SeasonDetailsBackClicked)

        advanceUntilIdle() // Allow time for the coroutine to execute

        backCalled shouldBe true
    }

    @Test
    fun `should invoke onEpisodeClick when EpisodeClicked action is dispatched`() = runTest {
        var clickedEpisodeId: Long? = null
        val onEpisodeClick: (Long) -> Unit = { clickedEpisodeId = it }

        presenter = buildSeasonDetailsPresenter(onEpisodeClick = onEpisodeClick)

        presenter.dispatch(EpisodeClicked(42L))

        advanceUntilIdle() // Allow time for the coroutine to execute

        clickedEpisodeId shouldBe 42L
    }

    @Test
    fun `should toggle showGalleryBottomSheet when SeasonGalleryClicked action is dispatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            presenter.dispatch(SeasonGalleryClicked)

            // Check updated state
            val updatedState = awaitItem()
            updatedState.showGalleryBottomSheet shouldBe true

            // Dispatch action again
            presenter.dispatch(SeasonGalleryClicked)

            // Check state toggled back
            val toggledState = awaitItem()
            toggledState.showGalleryBottomSheet shouldBe false
        }
    }

    private fun buildSeasonDetailsPresenter(
        onBack: () -> Unit = {},
        onEpisodeClick: (id: Long) -> Unit = {},
    ): SeasonDetailsPresenter {
        return SeasonDetailsPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            param =
            SeasonDetailsUiParam(
                showId = 1,
                seasonId = 1,
                seasonNumber = 1,
            ),
            onBack = onBack,
            onEpisodeClick = onEpisodeClick,
            observableSeasonDetailsInteractor = ObservableSeasonDetailsInteractor(
                seasonDetailsRepository = seasonDetailsRepository,
                castRepository = castRepository,
            ),
            seasonDetailsInteractor = SeasonDetailsInteractor(
                seasonDetailsRepository = seasonDetailsRepository,
                dispatchers = coroutineDispatcher,
            ),
            logger = FakeLogger(),
        )
    }
}
