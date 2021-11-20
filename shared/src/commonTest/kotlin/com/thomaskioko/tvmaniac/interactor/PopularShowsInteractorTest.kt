package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getTvResponse
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShow
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.invoke
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test

internal class PopularShowsInteractorTest {

    private val repository: TvShowsRepository = mockk()
    private val interactor = PopularShowsInteractor(repository)

    @Test
    fun wheneverPopularShowsInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {
        val result = getTvResponse().results
            .map { it.toTvShow() }
            .map {
                it.copy(
                    showCategory = ShowCategory.POPULAR
                )
            }

        coEvery { repository.getPopularTvShows(1) } returns result

        interactor.invoke().test {
            awaitItem() shouldBe DomainResultState.Loading()
            awaitItem() shouldBe DomainResultState.Success(result)
            awaitComplete()
        }
    }

    @Test
    fun wheneverPopularShowsInteractorIsInvoked_ErrorIsReturned() = runBlocking {

        coEvery { repository.getPopularTvShows(1) }.throws(Exception("Something went wrong"))

        interactor.invoke().test {
            awaitItem() shouldBe DomainResultState.Loading()
            awaitItem() shouldBe DomainResultState.Error("Something went wrong")
            awaitComplete()
        }
    }
}