package com.thomaskioko.tvmaniac.domain.showdetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.WatchProviderId
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.domain.showdetails.model.Providers
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObserveWatchProvidersInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )
    private val watchProviderRepository = FakeWatchProviderRepository()
    private lateinit var interactor: ObserveWatchProvidersInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveWatchProvidersInteractor(
            watchProviderRepository = watchProviderRepository,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit mapped providers list given watch provider data`() = runTest {
        val watchProvider = WatchProviders(
            provider_id = Id<WatchProviderId>(1L),
            name = "Netflix",
            logo_path = "/netflix.jpg",
            tmdb_id = Id<TmdbId>(84958L),
        )
        watchProviderRepository.setWatchProvidersResult(listOf(watchProvider))
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe listOf(
                Providers(
                    id = 1L,
                    name = "Netflix",
                    logoUrl = "/netflix.jpg",
                ),
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit empty list given no watch provider data`() = runTest {
        watchProviderRepository.setWatchProvidersResult(emptyList())
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe emptyList()
            cancelAndConsumeRemainingEvents()
        }
    }
}
