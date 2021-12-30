package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getTvResponse
import com.thomaskioko.tvmaniac.core.usecase.invoke
import com.thomaskioko.tvmaniac.datasource.mapper.toShow
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowList
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test

internal class GetWatchlistInteractorTest {

    private val repository: TvShowsRepository = mockk()
    private val interactor = GetWatchListInteractor(repository)

    @Test
    fun wheneverGetWatchlistInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {
        val result = getTvResponse().results
            .map { it.toShow() }

        coEvery { repository.observeWatchlist() } returns flowOf(result)

        interactor.invoke().test {
            awaitItem() shouldBe result.toTvShowList()
            awaitComplete()
        }
    }
}
