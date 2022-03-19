package com.thomaskioko.tvmaniac.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.MockData.getDiscoverShowResult
import com.thomaskioko.tvmaniac.MockData.getShowsCache
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.util.invoke
import com.thomaskioko.tvmaniac.discover.api.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test

internal class ObserveDiscoverShowsInteractorTest {

    private val repository: DiscoverRepository = mockk()
    private val interactor = ObserveDiscoverShowsInteractor(repository)

    @Test
    fun wheneverObserveShowsByCategoryInteractorIsInvoked_ExpectedDataIsReturned() =
        runBlockingTest {

            coEvery {
                repository.observeShowsByCategoryID(any())
            } returns getShowsCache()

            interactor.invoke().test {
                awaitItem() shouldBe getDiscoverShowResult()
                awaitComplete()
            }
        }
}
