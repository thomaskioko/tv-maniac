package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getTvResponse
import com.thomaskioko.tvmaniac.datasource.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowCategory
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowEntityList
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.invoke
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

internal class PopularShowsInteractorTest {

    private val repository: TvShowsRepository = mockk()
    private val interactor = PopularShowsInteractor(repository)

    @Test
    fun wheneverPopularShowsInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {
        val result = getTvResponse().results
            .map { it.toTvShowEntityList(TvShowCategory.POPULAR_TV_SHOWS) }

        every { runBlocking { repository.getPopularTvShows(1) } } returns result

        interactor.invoke().test {
            expectItem() shouldBe DomainResultState.Loading()
            expectItem() shouldBe DomainResultState.Success(result)
            expectComplete()
        }
    }

    @Test
    fun wheneverPopularShowsInteractorIsInvoked_ErrorIsReturned() = runBlocking {

        interactor.invoke().test {
            expectItem() shouldBe DomainResultState.Loading()
            expectItem() shouldBe DomainResultState.Error("Something went wrong")
            expectComplete()
        }
    }
}