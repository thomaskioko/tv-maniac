package com.thomaskioko.tvmaniac.ui.popularshows

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.thomaskioko.tvmaniac.interactor.PopularShowsInteractor
import com.thomaskioko.tvmaniac.mockdata.MockData.makeTvShowEntityList
import com.thomaskioko.tvmaniac.util.DomainResultState.Error
import com.thomaskioko.tvmaniac.util.DomainResultState.Success
import com.thomaskioko.tvmaniac.util.invoke
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.rules.TestRule

internal class PopularTvShowViewModelTest {

    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val interactor: PopularShowsInteractor = mockk()
    private val viewModel = PopularTvShowsViewModel(interactor, testCoroutineDispatcher)

    @Test
    fun `givenDisplayStateIsInvoked verifyResultRepoListIsReturned`() = runBlocking {
        val dataList = makeTvShowEntityList()
        every { interactor.invoke() } returns flowOf(Success(dataList))

        viewModel.stateFlow.test {

            viewModel.dispatchAction(PopularShowsAction.LoadPopularTvShows)

            awaitItem() shouldBe PopularShowsState.Loading
            awaitItem() shouldBe PopularShowsState.Success(dataList)
        }
    }

    @Test
    fun `givenFailureResponse verify errorStateIsReturned`() = runBlocking {
        val errorMessage = "Something went wrong"

        every { interactor.invoke() } returns flowOf(Error(errorMessage))

        viewModel.stateFlow.test {

            viewModel.dispatchAction(PopularShowsAction.LoadPopularTvShows)

            awaitItem() shouldBe PopularShowsState.Loading
            awaitItem() shouldBe PopularShowsState.Error(errorMessage)
        }
    }

}