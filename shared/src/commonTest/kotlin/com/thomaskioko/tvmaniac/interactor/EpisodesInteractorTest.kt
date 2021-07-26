package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getEpisodeList
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

internal class EpisodesInteractorTest {

    private val repository: EpisodeRepository = mockk()
    private val interactor = EpisodesInteractor(repository)
    private val query: EpisodeQuery = EpisodeQuery(
        tvShowId = 84958,
        seasonId = 114355,
        seasonNumber = 1
    )

    @Test
    fun wheneverInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {
        every {
            runBlocking {
                repository.getEpisodesBySeasonId(
                    tvShowId = query.tvShowId,
                    seasonNumber = query.seasonNumber,
                    seasonId = query.seasonId
                )
            }
        } returns getEpisodeList()

        interactor.invoke(query).test {
            expectItem() shouldBe DomainResultState.Loading()
            expectItem() shouldBe DomainResultState.Success(getEpisodeList())
            expectComplete()
        }
    }

    @Test
    fun wheneverInteractorIsInvoked_ErrorIsReturned() = runBlocking {
        interactor.invoke(query).test {
            expectItem() shouldBe DomainResultState.Loading()
            expectItem() shouldBe DomainResultState.Error("Something went wrong")
            expectComplete()
        }
    }
}