package com.thomaskioko.tvmaniac.seasondetails.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.domain.episode.FetchPreviousSeasonsInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkSeasonUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkSeasonWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveSeasonWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveUnwatchedInPreviousSeasonsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.ObservableSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsInteractor
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.MarkEpisodeUnwatchedCall
import com.thomaskioko.tvmaniac.episodes.testing.MarkEpisodeWatchedCall
import com.thomaskioko.tvmaniac.episodes.testing.MarkSeasonWatchedCall
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.presenter.data.buildSeasonDetailsLoaded
import com.thomaskioko.tvmaniac.seasondetails.presenter.data.buildSeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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
    private val episodeRepository = FakeEpisodeRepository()
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
                    airDate = null,
                    daysUntilAir = null,
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
                    episodeNumber = 1,
                    seasonNumber = 0,
                    seasonEpisodeNumber = "S00 | E1",
                    isWatched = false,
                    daysUntilAir = null,
                    hasPreviousUnwatched = false,
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
                    airDate = null,
                    daysUntilAir = null,
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

            seasonDetailsRepository.setSeasonsResult(updatedDetails)
            castRepository.setSeasonCast(updatedCast)

            awaitItem() shouldBe SeasonDetailsModel(
                isUpdating = false,
                dialogState = SeasonDialogState.Hidden,
                seasonImages = persistentListOf(),
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
                        episodeNumber = 1,
                        seasonNumber = 1,
                        seasonEpisodeNumber = "S01 | E1",
                        isWatched = false,
                        daysUntilAir = null,
                        hasPreviousUnwatched = false,
                    ),
                ),
                seasonCast = persistentListOf(),
            )

            val state = awaitItem()
            state.seasonName shouldBe "Season 01"
            state.episodeCount shouldBe 1
            state.episodeDetailsList.size shouldBe 1
            state.seasonCast.size shouldBe 1

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

        advanceUntilIdle()

        backCalled shouldBe true
    }

    @Test
    fun `should show gallery when ShowGallery action is dispatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            presenter.dispatch(ShowGallery)

            val updatedState = awaitItem()
            updatedState.dialogState.shouldBeInstanceOf<SeasonDialogState.Gallery>()

            presenter.dispatch(DismissDialog)

            val dismissedState = awaitItem()
            dismissedState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()
        }
    }

    @Test
    fun `should display correct watch progress percentage`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(
                showId = 1L,
                seasonNumber = 1L,
                watchedCount = 5,
                totalCount = 10,
            ),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            val state = awaitItem()
            state.watchProgress shouldBe 0.5f
            state.watchedEpisodeCount shouldBe 5
        }
    }

    @Test
    fun `should show season as fully watched when all episodes watched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(
                showId = 1L,
                seasonNumber = 1L,
                watchedCount = 10,
                totalCount = 10,
            ),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            val state = awaitItem()
            state.watchProgress shouldBe 1f
            state.isSeasonWatched shouldBe true
        }
    }

    @Test
    fun `should show season watch state dialog when toggling watched season`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(
                showId = 1L,
                seasonNumber = 1L,
                watchedCount = 10,
                totalCount = 10,
            ),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(MarkSeasonAsUnwatched)

            val state = awaitItem()
            state.dialogState.shouldBeInstanceOf<SeasonDialogState.UnwatchSeasonConfirmation>()
        }
    }

    @Test
    fun `should dismiss season watch state dialog when DismissDialog action is dispatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(
                showId = 1L,
                seasonNumber = 1L,
                watchedCount = 10,
                totalCount = 10,
            ),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(MarkSeasonAsUnwatched)
            awaitItem().dialogState.shouldBeInstanceOf<SeasonDialogState.UnwatchSeasonConfirmation>()

            presenter.dispatch(DismissDialog)
            awaitItem().dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()
        }
    }

    @Test
    fun `should toggle episode header expansion when OnEpisodeHeaderClicked is dispatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            presenter.dispatch(OnEpisodeHeaderClicked)

            val expandedState = awaitItem()
            expandedState.expandEpisodeItems shouldBe true

            presenter.dispatch(OnEpisodeHeaderClicked)

            val collapsedState = awaitItem()
            collapsedState.expandEpisodeItems shouldBe false
        }
    }

    @Test
    fun `should mark episode as watched when MarkEpisodeWatched is dispatched with no prior unwatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            presenter.dispatch(
                MarkEpisodeWatched(
                    episodeId = 12345,
                    seasonNumber = 1,
                    episodeNumber = 1,
                    hasPreviousUnwatched = false,
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()

            episodeRepository.lastMarkEpisodeWatchedCall shouldBe MarkEpisodeWatchedCall(
                showId = 1,
                episodeId = 12345,
                seasonNumber = 1,
                episodeNumber = 1,
                markPreviousEpisodes = false,
            )
        }
    }

    @Test
    fun `should show unwatch confirmation dialog when MarkEpisodeUnwatched is dispatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            presenter.dispatch(MarkEpisodeUnwatched(episodeId = 12345))

            val state = awaitItem()
            val dialog = state.dialogState
            dialog.shouldBeInstanceOf<SeasonDialogState.UnwatchEpisodeConfirmation>()
            (dialog as SeasonDialogState.UnwatchEpisodeConfirmation).primaryOperation.episodeId shouldBe 12345
        }
    }

    @Test
    fun `should mark season as unwatched when ConfirmDialogAction is dispatched from season watch state dialog`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(
                showId = 1L,
                seasonNumber = 1L,
                watchedCount = 10,
                totalCount = 10,
            ),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(MarkSeasonAsUnwatched)
            awaitItem()

            presenter.dispatch(ConfirmDialogAction)

            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            state.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()
        }
    }

    @Test
    fun `should show mark previous episodes dialog when marking episode with unwatched prior episodes`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            presenter.dispatch(
                MarkEpisodeWatched(
                    episodeId = 12345,
                    seasonNumber = 1,
                    episodeNumber = 5,
                    hasPreviousUnwatched = true,
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            state.dialogState.shouldBeInstanceOf<SeasonDialogState.MarkPreviousEpisodesConfirmation>()
        }
    }

    @Test
    fun `should mark episode with previous when ConfirmDialogAction is dispatched from mark previous episodes dialog`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            presenter.dispatch(
                MarkEpisodeWatched(
                    episodeId = 12345,
                    seasonNumber = 1,
                    episodeNumber = 5,
                    hasPreviousUnwatched = true,
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            val dialogState = awaitItem()
            dialogState.dialogState.shouldBeInstanceOf<SeasonDialogState.MarkPreviousEpisodesConfirmation>()

            presenter.dispatch(ConfirmDialogAction)

            testDispatcher.scheduler.advanceUntilIdle()

            val finalState = awaitItem()
            finalState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()

            episodeRepository.lastMarkEpisodeWatchedCall shouldBe MarkEpisodeWatchedCall(
                showId = 1,
                episodeId = 12345,
                seasonNumber = 1,
                episodeNumber = 5,
                markPreviousEpisodes = true,
            )
        }
    }

    @Test
    fun `should dismiss mark previous dialog when DismissDialog is dispatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            presenter.dispatch(
                MarkEpisodeWatched(
                    episodeId = 12345,
                    seasonNumber = 1,
                    episodeNumber = 5,
                    hasPreviousUnwatched = true,
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            val dialogState = awaitItem()
            dialogState.dialogState.shouldBeInstanceOf<SeasonDialogState.MarkPreviousEpisodesConfirmation>()

            presenter.dispatch(DismissDialog)

            testDispatcher.scheduler.advanceUntilIdle()

            val dismissedState = awaitItem()
            dismissedState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()
        }
    }

    @Test
    fun `should mark only current episode when SecondaryDialogAction is dispatched from mark previous episodes dialog`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(
                MarkEpisodeWatched(
                    episodeId = 12345,
                    seasonNumber = 1,
                    episodeNumber = 5,
                    hasPreviousUnwatched = true,
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            val dialogState = awaitItem()
            dialogState.dialogState.shouldBeInstanceOf<SeasonDialogState.MarkPreviousEpisodesConfirmation>()

            presenter.dispatch(SecondaryDialogAction)

            testDispatcher.scheduler.advanceUntilIdle()

            val finalState = awaitItem()
            finalState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()

            episodeRepository.lastMarkEpisodeWatchedCall shouldBe MarkEpisodeWatchedCall(
                showId = 1,
                episodeId = 12345,
                seasonNumber = 1,
                episodeNumber = 5,
                markPreviousEpisodes = false,
            )
        }
    }

    @Test
    fun `should unwatch episode when ConfirmDialogAction is dispatched from mark episode unwatched dialog`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty

            presenter.dispatch(MarkEpisodeUnwatched(episodeId = 12345))

            val dialogState = awaitItem()
            val dialog = dialogState.dialogState
            dialog.shouldBeInstanceOf<SeasonDialogState.UnwatchEpisodeConfirmation>()
            (dialog as SeasonDialogState.UnwatchEpisodeConfirmation).primaryOperation.episodeId shouldBe 12345

            presenter.dispatch(ConfirmDialogAction)

            testDispatcher.scheduler.advanceUntilIdle()

            val finalState = awaitItem()
            finalState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()

            episodeRepository.lastMarkEpisodeUnwatchedCall shouldBe MarkEpisodeUnwatchedCall(
                showId = 1,
                episodeId = 12345,
            )
        }
    }

    @Test
    fun `should show mark previous seasons dialog when marking season with unwatched previous seasons`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setUnwatchedCountInPreviousSeasons(5)

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.hasUnwatchedInPreviousSeasons shouldBe true

            presenter.dispatch(MarkSeasonAsWatched(hasUnwatchedInPreviousSeasons = true))

            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            state.dialogState.shouldBeInstanceOf<SeasonDialogState.MarkPreviousSeasonsConfirmation>()
        }
    }

    @Test
    fun `should mark season with previous seasons when ConfirmDialogAction is dispatched from mark previous seasons dialog`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setUnwatchedCountInPreviousSeasons(3)

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(MarkSeasonAsWatched(hasUnwatchedInPreviousSeasons = true))

            testDispatcher.scheduler.advanceUntilIdle()

            val dialogState = awaitItem()
            dialogState.dialogState.shouldBeInstanceOf<SeasonDialogState.MarkPreviousSeasonsConfirmation>()

            presenter.dispatch(ConfirmDialogAction)

            testDispatcher.scheduler.advanceUntilIdle()

            val finalState = awaitItem()
            finalState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()

            episodeRepository.lastMarkSeasonWatchedCall shouldBe MarkSeasonWatchedCall(
                showId = 1,
                seasonNumber = 1,
                markPreviousSeasons = true,
            )
        }
    }

    @Test
    fun `should mark only current season when SecondaryDialogAction is dispatched from mark previous seasons dialog`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setUnwatchedCountInPreviousSeasons(3)

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(MarkSeasonAsWatched(hasUnwatchedInPreviousSeasons = true))

            testDispatcher.scheduler.advanceUntilIdle()

            val dialogState = awaitItem()
            dialogState.dialogState.shouldBeInstanceOf<SeasonDialogState.MarkPreviousSeasonsConfirmation>()

            presenter.dispatch(SecondaryDialogAction)

            testDispatcher.scheduler.advanceUntilIdle()

            val finalState = awaitItem()
            finalState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()

            episodeRepository.lastMarkSeasonWatchedCall shouldBe MarkSeasonWatchedCall(
                showId = 1,
                seasonNumber = 1,
                markPreviousSeasons = false,
            )
        }
    }

    @Test
    fun `should dismiss mark previous seasons dialog when DismissDialog is dispatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setUnwatchedCountInPreviousSeasons(3)

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(MarkSeasonAsWatched(hasUnwatchedInPreviousSeasons = true))

            testDispatcher.scheduler.advanceUntilIdle()

            val dialogState = awaitItem()
            dialogState.dialogState.shouldBeInstanceOf<SeasonDialogState.MarkPreviousSeasonsConfirmation>()

            presenter.dispatch(DismissDialog)

            val dismissedState = awaitItem()
            dismissedState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()
        }
    }

    @Test
    fun `should close gallery when DismissDialog is dispatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(ShowGallery)
            awaitItem().dialogState.shouldBeInstanceOf<SeasonDialogState.Gallery>()

            presenter.dispatch(DismissDialog)

            awaitItem().dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()
        }
    }

    @Test
    fun `should dismiss unwatch dialog when DismissDialog is dispatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(MarkEpisodeUnwatched(episodeId = 12345))

            val dialogState = awaitItem()
            dialogState.dialogState.shouldBeInstanceOf<SeasonDialogState.UnwatchEpisodeConfirmation>()

            presenter.dispatch(DismissDialog)

            val dismissedState = awaitItem()
            dismissedState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()

            episodeRepository.lastMarkEpisodeUnwatchedCall shouldBe null
        }
    }

    @Test
    fun `should mark season directly when no unwatched episodes in previous seasons`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setUnwatchedCountInPreviousSeasons(0L)

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(MarkSeasonAsWatched(hasUnwatchedInPreviousSeasons = false))

            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()

            episodeRepository.lastMarkSeasonWatchedCall shouldBe MarkSeasonWatchedCall(
                showId = 1,
                seasonNumber = 1,
                markPreviousSeasons = false,
            )
        }
    }

    @Test
    fun `should update watch progress when episode is marked as watched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 0, totalCount = 10),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.watchedEpisodeCount shouldBe 0
            loadedState.watchProgress shouldBe 0f

            episodeRepository.setSeasonWatchProgress(
                SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 1, totalCount = 10),
            )

            presenter.dispatch(
                MarkEpisodeWatched(
                    episodeId = 12345,
                    seasonNumber = 1,
                    episodeNumber = 1,
                    hasPreviousUnwatched = false,
                ),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = awaitItem()
            updatedState.watchedEpisodeCount shouldBe 1
            updatedState.watchProgress shouldBe 0.1f

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update watch progress when episode is marked as unwatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 5, totalCount = 10),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.watchedEpisodeCount shouldBe 5
            loadedState.watchProgress shouldBe 0.5f

            presenter.dispatch(MarkEpisodeUnwatched(episodeId = 12345))
            awaitItem()

            presenter.dispatch(ConfirmDialogAction)
            testDispatcher.scheduler.advanceUntilIdle()

            val dialogDismissedState = awaitItem()
            dialogDismissedState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()

            episodeRepository.setSeasonWatchProgress(
                SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 4, totalCount = 10),
            )

            val updatedState = awaitItem()
            updatedState.watchedEpisodeCount shouldBe 4
            updatedState.watchProgress shouldBe 0.4f
        }
    }

    @Test
    fun `should show season as watched when all episodes are watched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 9, totalCount = 10),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.isSeasonWatched shouldBe false

            episodeRepository.setSeasonWatchProgress(
                SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 10, totalCount = 10),
            )

            presenter.dispatch(
                MarkEpisodeWatched(
                    episodeId = 12345,
                    seasonNumber = 1,
                    episodeNumber = 10,
                    hasPreviousUnwatched = false,
                ),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = awaitItem()
            updatedState.isSeasonWatched shouldBe true
            updatedState.watchProgress shouldBe 1.0f

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update watch progress when season is marked as watched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 3, totalCount = 10),
        )
        episodeRepository.setUnwatchedCountInPreviousSeasons(0L)

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.watchedEpisodeCount shouldBe 3
            loadedState.isSeasonWatched shouldBe false

            episodeRepository.setSeasonWatchProgress(
                SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 10, totalCount = 10),
            )

            presenter.dispatch(MarkSeasonAsWatched(hasUnwatchedInPreviousSeasons = false))
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = awaitItem()
            updatedState.watchedEpisodeCount shouldBe 10
            updatedState.isSeasonWatched shouldBe true
            updatedState.watchProgress shouldBe 1.0f

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should show season as unwatched when episode is unmarked from fully watched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 10, totalCount = 10),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.isSeasonWatched shouldBe true
            loadedState.watchProgress shouldBe 1.0f

            presenter.dispatch(MarkEpisodeUnwatched(episodeId = 12345))
            awaitItem()

            presenter.dispatch(ConfirmDialogAction)
            testDispatcher.scheduler.advanceUntilIdle()

            val dialogDismissedState = awaitItem()
            dialogDismissedState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()

            episodeRepository.setSeasonWatchProgress(
                SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 9, totalCount = 10),
            )

            val updatedState = awaitItem()
            updatedState.isSeasonWatched shouldBe false
            updatedState.watchProgress shouldBe 0.9f
            updatedState.watchedEpisodeCount shouldBe 9
        }
    }

    @Test
    fun `should update watch progress when season is marked as unwatched`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 10, totalCount = 10),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.isSeasonWatched shouldBe true
            loadedState.watchProgress shouldBe 1.0f

            presenter.dispatch(MarkSeasonAsUnwatched)
            val dialogState = awaitItem()
            dialogState.dialogState.shouldBeInstanceOf<SeasonDialogState.UnwatchSeasonConfirmation>()

            presenter.dispatch(ConfirmDialogAction)
            testDispatcher.scheduler.advanceUntilIdle()

            val dialogDismissedState = awaitItem()
            dialogDismissedState.dialogState.shouldBeInstanceOf<SeasonDialogState.Hidden>()

            episodeRepository.setSeasonWatchProgress(
                SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 0, totalCount = 10),
            )

            val updatedState = awaitItem()
            updatedState.isSeasonWatched shouldBe false
            updatedState.watchProgress shouldBe 0f
            updatedState.watchedEpisodeCount shouldBe 0
        }
    }

    @Test
    fun `should show unwatch dialog when ToggleEpisodeWatched is dispatched for watched episode`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes(
            episodes = listOf(
                EpisodeDetails(
                    id = 12345,
                    seasonId = 12343,
                    name = "Episode 1",
                    overview = "Episode overview",
                    episodeNumber = 1,
                    runtime = 45,
                    voteAverage = 8.0,
                    voteCount = 100,
                    isWatched = true,
                    seasonNumber = 1,
                    stillPath = "/image.jpg",
                    airDate = null,
                    daysUntilAir = null,
                ),
            ),
        )
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(ToggleEpisodeWatched(episodeId = 12345))

            val state = awaitItem()
            state.dialogState.shouldBeInstanceOf<SeasonDialogState.UnwatchEpisodeConfirmation>()
        }
    }

    @Test
    fun `should mark episode watched when ToggleEpisodeWatched is dispatched for unwatched episode`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes(
            episodes = listOf(
                EpisodeDetails(
                    id = 12345,
                    seasonId = 12343,
                    name = "Episode 1",
                    overview = "Episode overview",
                    episodeNumber = 1,
                    runtime = 45,
                    voteAverage = 8.0,
                    voteCount = 100,
                    isWatched = false,
                    seasonNumber = 1,
                    stillPath = "/image.jpg",
                    airDate = null,
                    daysUntilAir = null,
                ),
            ),
        )
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            awaitItem()

            presenter.dispatch(ToggleEpisodeWatched(episodeId = 12345))

            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()

            episodeRepository.lastMarkEpisodeWatchedCall shouldBe MarkEpisodeWatchedCall(
                showId = 1,
                episodeId = 12345,
                seasonNumber = 1,
                episodeNumber = 1,
                markPreviousEpisodes = false,
            )
        }
    }

    @Test
    fun `should show unwatch dialog when ToggleSeasonWatched is dispatched for watched season`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 10, totalCount = 10),
        )

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.isSeasonWatched shouldBe true

            presenter.dispatch(ToggleSeasonWatched)

            val state = awaitItem()
            state.dialogState.shouldBeInstanceOf<SeasonDialogState.UnwatchSeasonConfirmation>()
        }
    }

    @Test
    fun `should mark season watched when ToggleSeasonWatched is dispatched for unwatched season`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 0, totalCount = 10),
        )
        episodeRepository.setUnwatchedCountInPreviousSeasons(0L)

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.isSeasonWatched shouldBe false

            presenter.dispatch(ToggleSeasonWatched)

            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()

            episodeRepository.lastMarkSeasonWatchedCall shouldBe MarkSeasonWatchedCall(
                showId = 1,
                seasonNumber = 1,
                markPreviousSeasons = false,
            )
        }
    }

    @Test
    fun `should show mark previous seasons dialog when ToggleSeasonWatched is dispatched with unwatched previous seasons`() = runTest {
        val initialDetails = buildSeasonDetailsWithEpisodes()
        seasonDetailsRepository.setSeasonsResult(initialDetails)
        castRepository.setSeasonCast(emptyList())
        episodeRepository.setSeasonWatchProgress(
            SeasonWatchProgress(showId = 1, seasonNumber = 1, watchedCount = 0, totalCount = 10),
        )
        episodeRepository.setUnwatchedCountInPreviousSeasons(5)

        presenter.state.test {
            awaitItem() shouldBe SeasonDetailsModel.Empty
            val loadedState = awaitItem()
            loadedState.isSeasonWatched shouldBe false
            loadedState.hasUnwatchedInPreviousSeasons shouldBe true

            presenter.dispatch(ToggleSeasonWatched)

            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            state.dialogState.shouldBeInstanceOf<SeasonDialogState.MarkPreviousSeasonsConfirmation>()
        }
    }

    private fun buildSeasonDetailsPresenter(
        onBack: () -> Unit = {},
        onEpisodeClick: (id: Long) -> Unit = {},
    ): SeasonDetailsPresenter {
        return DefaultSeasonDetailsPresenter(
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
            markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            markEpisodeUnwatchedInteractor = MarkEpisodeUnwatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            markSeasonWatchedInteractor = MarkSeasonWatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            markSeasonUnwatchedInteractor = MarkSeasonUnwatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            fetchPreviousSeasonsInteractor = FetchPreviousSeasonsInteractor(
                episodeRepository = episodeRepository,
            ),
            observeSeasonWatchProgressInteractor = ObserveSeasonWatchProgressInteractor(
                episodeRepository = episodeRepository,
            ),
            observeUnwatchedInPreviousSeasonsInteractor = ObserveUnwatchedInPreviousSeasonsInteractor(
                episodeRepository = episodeRepository,
            ),
            logger = FakeLogger(),
        )
    }
}
