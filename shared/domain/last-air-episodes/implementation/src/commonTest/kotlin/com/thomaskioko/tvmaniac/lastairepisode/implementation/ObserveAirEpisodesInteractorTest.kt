package com.thomaskioko.tvmaniac.lastairepisode.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.ObserveAirEpisodesInteractor
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ObserveAirEpisodesInteractorTest {

    private val repository: LastAirEpisodeRepository = mockk()
    private val interactor = ObserveAirEpisodesInteractor(repository)

    @Test
    fun wheneverObserveShowsByCategoryInteractorIsInvoked_ExpectedDataIsReturned() = runTest {
        coEvery { repository.observeAirEpisodes(any()) } returns flowOf(makeLastEpisodeList())

        interactor.invoke(84958).test {
            awaitItem() shouldBe makeLastAirEpisode()
            awaitComplete()
        }
    }
}
