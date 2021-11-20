package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getTrendingDataMap
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.THIS_WEEK
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TODAY
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
        val trendingTypes = listOf(TODAY, THIS_WEEK)

        coEvery { repository.getTrendingShows(trendingTypes) } returns getTrendingDataMap()

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
        coEvery { repository.getTrendingShows(trendingTypes) }.throws(Exception("Something went wrong"))

        interactor(trendingTypes).test {
            awaitItem() shouldBe DomainResultState.Loading()
            awaitItem() shouldBe DomainResultState.Error("Something went wrong")
            awaitComplete()
        }
    }
}