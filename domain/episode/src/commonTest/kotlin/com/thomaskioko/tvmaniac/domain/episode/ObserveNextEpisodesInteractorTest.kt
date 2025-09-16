package com.thomaskioko.tvmaniac.domain.episode

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObserveNextEpisodesInteractorTest {
    private val episodeRepository = FakeEpisodeRepository()
    private val watchlistRepository = FakeWatchlistRepository()

    private val interactor = ObserveNextEpisodesInteractor(
        episodeRepository = episodeRepository,
        watchlistRepository = watchlistRepository,
    )

    @Test
    fun `should return empty list when watchlist is empty`() = runTest {
        watchlistRepository.setObserveResult(emptyList())

        interactor(Unit)

        interactor.flow.test {
            awaitItem() shouldBe emptyList()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return next episodes when watchlist has shows`() = runTest {
        val watchlist = listOf(
            createWatchlistShow(84958, "Loki"),
            createWatchlistShow(1232, "The Lazarus Project"),
        )

        val nextEpisodes = listOf(
            createNextEpisode(84958, "Loki", 1, 1),
            createNextEpisode(1232, "The Lazarus Project", 1, 1),
        )

        watchlistRepository.setObserveResult(watchlist)
        episodeRepository.setNextEpisodesForWatchlist(nextEpisodes)

        interactor(Unit)

        interactor.flow.test {
            awaitItem() shouldBe nextEpisodes
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should update when watchlist changes`() = runTest {
        val initialWatchlist = listOf(createWatchlistShow(84958, "Loki"))
        val initialEpisodes = listOf(createNextEpisode(84958, "Loki", 1, 1))

        val updatedWatchlist = initialWatchlist + createWatchlistShow(1232, "The Lazarus Project")
        val updatedEpisodes = initialEpisodes + createNextEpisode(1232, "The Lazarus Project", 1, 1)

        watchlistRepository.setObserveResult(initialWatchlist)
        episodeRepository.setNextEpisodesForWatchlist(initialEpisodes)

        interactor(Unit)

        interactor.flow.test {
            awaitItem() shouldBe initialEpisodes

            watchlistRepository.setObserveResult(updatedWatchlist)
            episodeRepository.setNextEpisodesForWatchlist(updatedEpisodes)

            awaitItem() shouldBe updatedEpisodes
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should update when next episodes change`() = runTest {
        val watchlist = listOf(createWatchlistShow(84958, "Loki"))
        val episode1 = createNextEpisode(84958, "Loki", 1, 1)
        val episode2 = createNextEpisode(84958, "Loki", 1, 2)

        watchlistRepository.setObserveResult(watchlist)
        episodeRepository.setNextEpisodesForWatchlist(listOf(episode1))

        interactor(Unit)

        interactor.flow.test {
            awaitItem() shouldBe listOf(episode1)

            episodeRepository.setNextEpisodesForWatchlist(listOf(episode2))

            awaitItem() shouldBe listOf(episode2)
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun createWatchlistShow(id: Long, name: String) = Watchlists(
        id = Id(id),
        name = name,
        poster_path = "/poster.jpg",
        status = "Ongoing",
        first_air_date = "2024",
        created_at = 0,
        season_count = 2,
        episode_count = 12,
    )

    private fun createNextEpisode(
        showId: Long,
        showName: String,
        seasonNumber: Int,
        episodeNumber: Int,
    ) = NextEpisodeWithShow(
        showId = showId,
        showName = showName,
        showPoster = "/poster.jpg",
        episodeId = "${showId}0${seasonNumber}0$episodeNumber".toLong(),
        episodeName = "Episode $episodeNumber",
        seasonNumber = seasonNumber.toLong(),
        episodeNumber = episodeNumber.toLong(),
        runtime = 45,
        stillPath = "/still.jpg",
        overview = "Episode overview",
        followedAt = 0,
    )
}
