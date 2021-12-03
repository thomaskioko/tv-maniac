package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getTvResponse
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.mapper.toShow
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowList
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
            .map { it.toShow() }

        coEvery { repository.getShowsByCategoryId(1, ShowCategory.POPULAR.type) } returns result

        interactor.invoke().test {
            awaitItem() shouldBe DomainResultState.Loading()
            awaitItem() shouldBe DomainResultState.Success(result.toTvShowList())
            awaitComplete()
        }
    }

    @Test
    fun wheneverPopularShowsInteractorIsInvoked_ErrorIsReturned() = runBlocking {

        coEvery { repository.getShowsByCategoryId(1, ShowCategory.POPULAR.type) }
            .throws(Exception("Something went wrong"))

        interactor.invoke().test {
            awaitItem() shouldBe DomainResultState.Loading()
            awaitItem() shouldBe DomainResultState.Error("Something went wrong")
            awaitComplete()
        }
    }
}
