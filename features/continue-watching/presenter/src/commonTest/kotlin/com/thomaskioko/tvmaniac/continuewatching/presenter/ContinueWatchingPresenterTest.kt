package com.thomaskioko.tvmaniac.continuewatching.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.EpisodeBadge
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

class ContinueWatchingPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val factory = FakeContinueWatchingPresenterBuilder()

    private lateinit var presenter: ContinueWatchingPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)

        lifecycle.resume()
        presenter = factory.create(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit initial state on init`() = runTest(factory.testDispatcher) {
        presenter.state.test {
            awaitItem() shouldBe ContinueWatchingState()
        }
    }

    @Test
    fun `should emit ContinueWatchingState with content on success`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe ContinueWatchingState()

            factory.upNextRepository.setNextEpisodesForWatchlist(cachedNextEpisodes)

            val state = awaitItem()
            state.query shouldBe ""
            state.isGridMode shouldBe true
            state.watchNextItems shouldBe expectedUiResult(cachedNextEpisodes)

            factory.upNextRepository.setNextEpisodesForWatchlist(updatedNextEpisodes)

            val secondUpdate = awaitItem()
            secondUpdate.watchNextItems shouldBe expectedUiResult()
        }
    }

    @Test
    fun `should update query when onQueryChanged is called`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe ContinueWatchingState()

            factory.upNextRepository.setNextEpisodesForWatchlist(cachedNextEpisodes)

            val initialState = awaitItem()
            initialState.query shouldBe ""

            presenter.onQueryChanged("test query")

            val updatedState = awaitItem()
            updatedState.query shouldBe "test query"
        }
    }

    @Test
    fun `should emit watchNextEpisodes when episodes are available`() = runTest {
        val nextEpisodes = listOf(
            createNextEpisodeWithShow(showId = 1L, showName = "Loki", episodeId = 101L),
            createNextEpisodeWithShow(showId = 2L, showName = "Wednesday", episodeId = 201L),
        )

        presenter.state.test {
            awaitItem() shouldBe ContinueWatchingState()

            factory.upNextRepository.setNextEpisodesForWatchlist(nextEpisodes)

            val state = awaitItem()
            state.watchNextEpisodes.size shouldBe 2
            state.watchNextEpisodes[0].showName shouldBe "Loki"
            state.watchNextEpisodes[1].showName shouldBe "Wednesday"
        }
    }

    @Test
    fun `should filter watchNextEpisodes by show name when query is active`() = runTest {
        val nextEpisodes = listOf(
            createNextEpisodeWithShow(showId = 1L, showName = "Loki", episodeId = 101L),
            createNextEpisodeWithShow(showId = 2L, showName = "Wednesday", episodeId = 201L),
        )

        presenter.state.test {
            awaitItem() shouldBe ContinueWatchingState()

            factory.upNextRepository.setNextEpisodesForWatchlist(nextEpisodes)
            awaitItem()

            presenter.onQueryChanged("Loki")

            skipItems(1)

            val filteredState = awaitItem()
            filteredState.query shouldBe "Loki"
            filteredState.watchNextEpisodes.size shouldBe 1
            filteredState.watchNextEpisodes[0].showName shouldBe "Loki"
        }
    }

    @Test
    fun `should show PREMIERE badge for episode 1`() = runTest {
        val currentTime = LocalDate(2024, 11, 14).toEpochMillis()
        factory.dateTimeProvider.setCurrentTimeMillis(currentTime)

        val premiereEpisode = createNextEpisodeWithShow(
            showId = 1L,
            showName = "Loki",
            episodeId = 101L,
            episodeNumber = 1L,
            seasonNumber = 2L,
            firstAired = currentTime,
        )

        presenter.state.test {
            awaitItem() shouldBe ContinueWatchingState()

            factory.upNextRepository.setNextEpisodesForWatchlist(listOf(premiereEpisode))

            val state = awaitItem()
            state.watchNextEpisodes.size shouldBe 1
            state.watchNextEpisodes[0].badge shouldBe EpisodeBadge.PREMIERE
        }
    }

    @Test
    fun `should group episodes into stale section when last watched over 16 days ago`() = runTest {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val seventeenDaysAgo = LocalDate(2023, 10, 28).toEpochMillis()
        val oneDayAgo = LocalDate(2023, 11, 13).toEpochMillis()

        factory.dateTimeProvider.setCurrentTimeMillis(currentTime)

        val staleEpisode = createNextEpisodeWithShow(
            showId = 1L,
            showName = "Stale Show",
            episodeId = 101L,
            lastWatchedAt = seventeenDaysAgo,
        )
        val activeEpisode = createNextEpisodeWithShow(
            showId = 2L,
            showName = "Active Show",
            episodeId = 201L,
            lastWatchedAt = oneDayAgo,
        )

        presenter.state.test {
            awaitItem() shouldBe ContinueWatchingState()

            factory.upNextRepository.setNextEpisodesForWatchlist(
                listOf(
                    staleEpisode,
                    activeEpisode,
                ),
            )

            val state = awaitItem()
            state.watchNextEpisodes.size shouldBe 1
            state.watchNextEpisodes[0].showName shouldBe "Active Show"
            state.staleEpisodes.size shouldBe 1
            state.staleEpisodes[0].showName shouldBe "Stale Show"
        }
    }

    private fun createNextEpisodeWithShow(
        showId: Long,
        showName: String,
        episodeId: Long,
        episodeNumber: Long = 2L,
        seasonNumber: Long = 1L,
        lastWatchedAt: Long? = null,
        firstAired: Long? = LocalDate(2021, 6, 9).toEpochMillis(),
    ) = NextEpisodeWithShow(
        showId = showId,
        showName = showName,
        showPoster = "/poster.jpg",
        showStatus = "Ended",
        showYear = "2024",
        episodeId = episodeId,
        episodeName = "Episode Title",
        seasonId = seasonNumber,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Episode overview",
        firstAired = firstAired,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 2,
        episodeCount = 12,
        watchedCount = 0,
        totalCount = 10,
    )
}
