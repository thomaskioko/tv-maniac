package com.thomaskioko.tvmaniac.domain.discover

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DiscoverShowsInteractorTest {
    private val testDispatcher = StandardTestDispatcher()

    private val featuredShowsRepository = FakeFeaturedShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()
    private val popularShowsRepository = FakePopularShowsRepository()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val genreRepository = FakeGenreRepository()
    private val episodeRepository = FakeEpisodeRepository()

    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var interactor: DiscoverShowsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        interactor = DiscoverShowsInteractor(
            featuredShowsRepository = featuredShowsRepository,
            topRatedShowsRepository = topRatedShowsRepository,
            popularShowsRepository = popularShowsRepository,
            trendingShowsRepository = trendingShowsRepository,
            upcomingShowsRepository = upcomingShowsRepository,
            genreRepository = genreRepository,
            episodeRepository = episodeRepository,
            dispatchers = coroutineDispatcher,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return empty DiscoverShowsData when all repositories return empty lists`() = runTest {
        // Given
        setTestData()

        // When
        interactor(Unit)

        // Then
        interactor.flow.test {
            awaitItem() shouldBe DiscoverShowsData(
                featuredShows = emptyList(),
                topRatedShows = emptyList(),
                popularShows = emptyList(),
                trendingShows = emptyList(),
                upcomingShows = emptyList(),
                nextEpisodes = emptyList(),
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return populated DiscoverShowsData when repositories return result`() = runTest {
        // Given
        val shows = createTestShows()
        val episodes = createNextEpisodesList()
        setTestData(shows)
        setNextEpisodes(episodes)

        // When
        interactor(Unit)

        // Then
        interactor.flow.test {
            awaitItem() shouldBe DiscoverShowsData(
                featuredShows = shows,
                topRatedShows = shows,
                popularShows = shows,
                trendingShows = shows,
                upcomingShows = shows,
                nextEpisodes = episodes,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    private suspend fun setTestData(shows: List<ShowEntity> = emptyList()) {
        featuredShowsRepository.setFeaturedShows(shows)
        topRatedShowsRepository.setTopRatedShows(shows)
        popularShowsRepository.setPopularShows(shows)
        trendingShowsRepository.setTrendingShows(shows)
        upcomingShowsRepository.setUpcomingShows(shows)
        genreRepository.setGenreResult(emptyList())
    }

    private fun setNextEpisodes(episodes: List<NextEpisodeWithShow>) {
        episodeRepository.setNextEpisodesForWatchlist(episodes)
    }

    private fun createNextEpisodesList(size: Int = 5) = List(size) { index ->
        NextEpisodeWithShow(
            showTraktId = 84958L + index,
            showName = "Test Show $index",
            showPoster = "/test-poster-$index.jpg",
            episodeId = 1000L + index,
            episodeName = "Test Episode $index",
            seasonNumber = 1L,
            episodeNumber = index.toLong() + 1,
            runtime = 45L,
            stillPath = "/test-still-$index.jpg",
            overview = "Test episode overview $index",
            seasonId = 1234,
        )
    }

    private fun createTestShows() = List(3) {
        ShowEntity(
            traktId = it.toLong(),
            tmdbId = it.toLong(),
            title = "Show $it",
            posterPath = "poster_$it.jpg",
            inLibrary = false,
            overview = "Overview $it",
        )
    }
}
