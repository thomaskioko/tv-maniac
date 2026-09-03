package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ResolveEpisodeIdInteractorTest {

    private val episodeRepository = FakeEpisodeRepository()
    private val interactor = ResolveEpisodeIdInteractor(episodeRepository)

    @Test
    fun `should return the episode id given the episode is cached`() = runTest {
        episodeRepository.setEpisodeId(
            showId = 1399,
            seasonNumber = 1,
            episodeNumber = 2,
            episodeId = 63056,
        )

        interactor.executeSync(
            ResolveEpisodeIdParams(showId = 1399, seasonNumber = 1, episodeNumber = 2),
        ) shouldBe 63056
    }

    @Test
    fun `should return null given the episode is not cached`() = runTest {
        interactor.executeSync(
            ResolveEpisodeIdParams(showId = 1399, seasonNumber = 1, episodeNumber = 2),
        ) shouldBe null
    }

    @Test
    fun `should return null given only another episode of the same show is cached`() = runTest {
        episodeRepository.setEpisodeId(
            showId = 1399,
            seasonNumber = 1,
            episodeNumber = 1,
            episodeId = 63055,
        )

        interactor.executeSync(
            ResolveEpisodeIdParams(showId = 1399, seasonNumber = 1, episodeNumber = 2),
        ) shouldBe null
    }
}
