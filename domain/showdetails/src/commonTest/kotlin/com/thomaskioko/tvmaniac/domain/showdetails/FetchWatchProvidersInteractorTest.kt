package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class FetchWatchProvidersInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )
    private val watchProviderRepository = FakeWatchProviderRepository()
    private lateinit var interactor: FetchWatchProvidersInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = FetchWatchProvidersInteractor(
            watchProviderRepository = watchProviderRepository,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should fetch watch providers given valid show id`() = runTest {
        interactor.executeSync(FetchWatchProvidersInteractor.Param(id = 84958L))
        watchProviderRepository.fetchInvocations().single().showId shouldBe 84958L
    }

    @Test
    fun `should propagate force refresh to repository`() = runTest {
        interactor.executeSync(FetchWatchProvidersInteractor.Param(id = 84958L, forceRefresh = true))
        watchProviderRepository.fetchInvocations().single().forceRefresh shouldBe true
    }
}
