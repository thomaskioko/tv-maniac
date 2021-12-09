package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getSeasonsList
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test

internal class SeasonsInteractorTest {

    private val repository: SeasonsRepository = mockk()
    private val interactor = SeasonsInteractor(repository)

    @Test
    fun wheneverInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {
        coEvery { repository.getSeasonListByTvShowId(84958) } returns getSeasonsList()

        interactor.invoke(84958).test {
            awaitItem() shouldBe DomainResultState.Loading()
            awaitItem() shouldBe DomainResultState.Success(getSeasonsList())
            awaitComplete()
        }
    }

    @Test
    fun wheneverInteractorIsInvoked_ErrorIsReturned() = runBlocking {

        coEvery {
            repository.getSeasonListByTvShowId(84958)
        }.throws(Exception("Something went wrong"))

        interactor.invoke(84958).test {
            awaitItem() shouldBe DomainResultState.Loading()
            awaitItem() shouldBe DomainResultState.Error("Something went wrong")
            awaitComplete()
        }
    }
}
