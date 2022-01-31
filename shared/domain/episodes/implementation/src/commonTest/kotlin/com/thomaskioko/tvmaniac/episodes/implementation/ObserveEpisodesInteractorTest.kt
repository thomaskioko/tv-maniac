package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.getEpisodeList
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.getEpisodesBySeasonId
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.episodes.api.EpisodeQuery
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.ObserveEpisodesInteractor
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test

internal class ObserveEpisodesInteractorTest {

    private val repository: EpisodeRepository = mockk()
    private val interactor = ObserveEpisodesInteractor(repository)
    private val query: EpisodeQuery = EpisodeQuery(
        tvShowId = 84958,
        seasonId = 114355,
        seasonNumber = 1
    )

    @Test
    fun wheneverInteractorIsInvoked_ExpectedDataIsReturned() = runBlockingTest {
        coEvery {
            repository.observeSeasonEpisodes(
                tvShowId = query.tvShowId,
                seasonNumber = query.seasonNumber,
                seasonId = query.seasonId
            )
        } returns getEpisodesBySeasonId()

        interactor.invoke(query).test {
            awaitItem() shouldBe getEpisodeList()
            awaitComplete()
        }
    }
}
