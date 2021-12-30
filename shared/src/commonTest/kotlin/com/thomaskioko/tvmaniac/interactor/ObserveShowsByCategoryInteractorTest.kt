package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getShowsCache
import com.thomaskioko.tvmaniac.MockData.getTrendingDataMap
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test

internal class ObserveShowsByCategoryInteractorTest {

    private val repository: TvShowsRepository = mockk()
    private val interactor = ObserveShowsByCategoryInteractor(repository)

    @Test
    fun wheneverGetTrendingShowsInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {

        coEvery {
            repository.observeShowsByCategoryID(ShowCategory.TRENDING.type)
        } returns getShowsCache()

        interactor(ShowCategory.TRENDING).test {
            awaitItem() shouldBe getTrendingDataMap()
            awaitComplete()
        }
    }
}
