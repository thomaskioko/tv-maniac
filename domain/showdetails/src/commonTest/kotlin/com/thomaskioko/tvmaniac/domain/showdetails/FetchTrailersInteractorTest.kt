package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class FetchTrailersInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )
    private val trailerRepository = FakeTrailerRepository()
    private lateinit var interactor: FetchTrailersInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = FetchTrailersInteractor(
            trailerRepository = trailerRepository,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should complete given valid show id`() = runTest {
        interactor.executeSync(FetchTrailersInteractor.Param(id = 84958L))
    }

    @Test
    fun `should complete given force refresh`() = runTest {
        interactor.executeSync(FetchTrailersInteractor.Param(id = 84958L, forceRefresh = true))
    }
}
