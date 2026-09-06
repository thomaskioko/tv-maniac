package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class FetchMissingListShowDetailsInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val listRepository = FakeListRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private lateinit var interactor: FetchMissingListShowDetailsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = FetchMissingListShowDetailsInteractor(
            listRepository = listRepository,
            showDetailsRepository = showDetailsRepository,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should fetch details for every show missing a poster given the list has some`() = runTest {
        listRepository.setTmdbIdsMissingPoster(listOf(1L, 2L, 3L))

        interactor.executeSync(FetchMissingListShowDetailsInteractor.Params(listId = 10L))

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(1L, 2L, 3L)
    }

    @Test
    fun `should fetch nothing given the list has no shows missing a poster`() = runTest {
        listRepository.setTmdbIdsMissingPoster(emptyList())

        interactor.executeSync(FetchMissingListShowDetailsInteractor.Params(listId = 10L))

        showDetailsRepository.fetchInvocations().shouldBeEmpty()
    }

    @Test
    fun `should stop at the first failure given a fetch fails`() = runTest {
        listRepository.setTmdbIdsMissingPoster(listOf(1L, 2L, 3L))
        showDetailsRepository.setFetchError(IllegalStateException("boom"))

        shouldThrow<IllegalStateException> {
            interactor.executeSync(FetchMissingListShowDetailsInteractor.Params(listId = 10L))
        }

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(1L)
    }
}
