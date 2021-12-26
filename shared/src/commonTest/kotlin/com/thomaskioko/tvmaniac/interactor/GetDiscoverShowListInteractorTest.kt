package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getTrendingDataMap
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Ignore
import kotlin.test.Test

internal class GetDiscoverShowListInteractorTest {

    private val repository: TvShowsRepository = mockk()
    private val interactor = GetDiscoverShowListInteractor(repository)

    @Test
    fun wheneverGetTrendingShowsInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {
        val trendingTypes = listOf(ShowCategory.TRENDING, ShowCategory.FEATURED)

        coEvery { repository.getDiscoverShowList(trendingTypes) } returns getTrendingDataMap()

        interactor(trendingTypes).test {
            awaitItem() shouldBe getTrendingDataMap()
            awaitComplete()
        }
    }

    @Ignore
    @Test
    fun wheneverGetTrendingShowsInteractorIsInvoked_ErrorIsReturned() = runBlocking {
        val trendingTypes = listOf(
            ShowCategory.TRENDING,
            ShowCategory.FEATURED
        )
        coEvery { repository.getDiscoverShowList(trendingTypes) }.throws(Exception("Something went wrong"))

        interactor(trendingTypes).test {
            awaitItem() shouldBe Exception("Something went wrong")
            awaitComplete()
        }
    }
}
