package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getSeasonsList
import com.thomaskioko.tvmaniac.MockData.getSelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.util.runBlockingTest
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test

internal class SeasonsInteractorTest {

    private val repository: SeasonsRepository = mockk()
    private val interactor = SeasonsInteractor(repository)

    @Test
    fun wheneverInteractorIsInvoked_ExpectedDataIsReturned() = runBlockingTest {
        coEvery { repository.observeShowSeasons(84958) } returns getSelectSeasonsByShowId()

        interactor.invoke(84958).test {
            awaitItem() shouldBe getSeasonsList()
            awaitComplete()
        }
    }
}
