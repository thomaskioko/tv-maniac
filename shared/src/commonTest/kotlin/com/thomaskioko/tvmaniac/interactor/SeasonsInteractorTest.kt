package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.tvSeasonsList
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

internal class SeasonsInteractorTest {

    private val repository: SeasonsRepository = mockk()
    private val interactor = SeasonsInteractor(repository)

    @Test
    fun wheneverInteractorIsInvoked_ExpectedDataIsReturned() = runBlocking {
        every { runBlocking { repository.getSeasonListByTvShowId(84958) } } returns tvSeasonsList

        interactor.invoke(84958).test {
            expectItem() shouldBe DomainResultState.Loading()
            expectItem() shouldBe DomainResultState.Success(tvSeasonsList)
            expectComplete()
        }
    }

    @Test
    fun wheneverInteractorIsInvoked_ErrorIsReturned() = runBlocking {

        interactor.invoke(84958).test {
            expectItem() shouldBe DomainResultState.Loading()
            expectItem() shouldBe DomainResultState.Error("Something went wrong")
            expectComplete()
        }
    }
}