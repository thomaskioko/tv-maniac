package com.thomaskioko.tvmaniac.domain.notifications.interactor

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.testing.FakeApiRateLimiter
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.FollowedShowSeason
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshUpcomingSeasonDetailsInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val seasonsRepository = FakeSeasonsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val episodeRepository = FakeEpisodeRepository()

    private val interactor = RefreshUpcomingSeasonDetailsInteractor(
        seasonsRepository = seasonsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        showMetadataSyncHelper = ShowMetadataSyncHelper(episodeRepository),
        apiRateLimiter = FakeApiRateLimiter(),
        dispatchers = dispatchers,
    )

    @Test
    fun `should refresh latest seasons for followed shows`() = runTest(testDispatcher) {
        seasonsRepository.setLatestSeasonsForFollowedShows(
            listOf(
                FollowedShowSeason(showId = 1L, seasonId = 10L, seasonNumber = 2L),
                FollowedShowSeason(showId = 2L, seasonId = 20L, seasonNumber = 5L),
            ),
        )

        interactor.executeSync(RefreshUpcomingSeasonDetailsInteractor.Params())

        seasonDetailsRepository.getFetchedSeasons().map { it.showId } shouldBe listOf(1L, 2L)
    }

    @Test
    fun `should skip ended show with complete episode data given latest season refresh`() = runTest(testDispatcher) {
        seasonsRepository.setLatestSeasonsForFollowedShows(
            listOf(
                FollowedShowSeason(showId = 1L, seasonId = 10L, seasonNumber = 2L),
                FollowedShowSeason(showId = 2L, seasonId = 20L, seasonNumber = 5L),
            ),
        )
        episodeRepository.setShowMetadataSyncInfo(
            showId = 2L,
            info = ShowMetadataSyncInfo(status = "Ended", metadataEpisodeCount = 62, localEpisodeCount = 62),
        )

        interactor.executeSync(RefreshUpcomingSeasonDetailsInteractor.Params())

        seasonDetailsRepository.getFetchedSeasons().map { it.showId } shouldBe listOf(1L)
    }

    @Test
    fun `should keep refreshing ended show given local episode data is incomplete`() = runTest(testDispatcher) {
        seasonsRepository.setLatestSeasonsForFollowedShows(
            listOf(FollowedShowSeason(showId = 2L, seasonId = 20L, seasonNumber = 5L)),
        )
        episodeRepository.setShowMetadataSyncInfo(
            showId = 2L,
            info = ShowMetadataSyncInfo(status = "Ended", metadataEpisodeCount = 62, localEpisodeCount = 12),
        )

        interactor.executeSync(RefreshUpcomingSeasonDetailsInteractor.Params())

        seasonDetailsRepository.getFetchedSeasons().map { it.showId } shouldBe listOf(2L)
    }

    @Test
    fun `should no-op given no followed shows have seasons`() = runTest(testDispatcher) {
        interactor.executeSync(RefreshUpcomingSeasonDetailsInteractor.Params())

        seasonDetailsRepository.getFetchedSeasons().shouldBeEmpty()
    }
}
