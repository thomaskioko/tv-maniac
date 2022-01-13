package com.thomaskioko.tvmaniac.seasons.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.seasons.implementation.MockData.getSeasonsList
import com.thomaskioko.tvmaniac.seasons.implementation.MockData.getSelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.api.interactor.SeasonsInteractor
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
