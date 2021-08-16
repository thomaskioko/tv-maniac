package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getTvResponse
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShow
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.invoke
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test

internal class GetWatchlistInteractorTest {

    private val repository: TvShowsRepository = mockk()
    private val interactor = GetWatchListInteractor(repository)

    @Test
    fun wheneverPopularShowsInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {
        val result = getTvResponse().results
            .map { it.toTvShow() }

        every { runBlocking { repository.getWatchlist() } } returns flowOf(result)

        interactor.invoke().test {
            awaitItem() shouldBe DomainResultState.Success(result)
            awaitComplete()
        }
    }
}