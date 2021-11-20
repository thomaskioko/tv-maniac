package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.dayResponse
import com.thomaskioko.tvmaniac.MockData.getTrendingDataMap
import com.thomaskioko.tvmaniac.MockData.weekResponse
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.THIS_WEEK
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TODAY
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow.DAY
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow.WEEK
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test

internal class GetTrendingShowsInteractorTest {

    private val repository: TvShowsRepository = mockk()
    private val interactor = GetTrendingShowsInteractor(repository)

    @Test
    fun wheneverGetTrendingShowsInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {
        coEvery { repository.getTrendingShowsByTime(DAY) } returns dayResponse

        coEvery { repository.getTrendingShowsByTime(WEEK) } returns weekResponse

        val trendingTypes = listOf(TODAY, THIS_WEEK)

        interactor(trendingTypes).test {
            awaitItem() shouldBe DomainResultState.Loading()
            awaitItem() shouldBe DomainResultState.Success(getTrendingDataMap())
            awaitComplete()
        }
    }

    @Test
    fun wheneverGetTrendingShowsInteractorIsInvoked_ErrorIsReturned() = runBlocking {
        val trendingTypes = listOf(
            TODAY,
            THIS_WEEK
        )
        interactor(trendingTypes).test {
            awaitItem() shouldBe DomainResultState.Loading()
            awaitItem() shouldBe DomainResultState.Error("Something went wrong")
            awaitComplete()
        }
    }
}