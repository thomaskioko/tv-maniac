package com.thomaskioko.tvmaniac.ui.popularshows

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.mockdata.MockData.tvSeasonList
import com.thomaskioko.tvmaniac.ui.seasons.SeasonsAction
import com.thomaskioko.tvmaniac.ui.seasons.SeasonsViewModel
import com.thomaskioko.tvmaniac.ui.seasons.SeasonsViewState
import com.thomaskioko.tvmaniac.util.DomainResultState.Error
import com.thomaskioko.tvmaniac.util.DomainResultState.Success
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.rules.TestRule

internal class SeasonsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val interactor: SeasonsInteractor = mockk()
    private val viewModel = SeasonsViewModel(interactor, testCoroutineDispatcher)

    @Test
    fun `givenDisplayStateIsInvoked verifyResultRepoListIsReturned`() = runBlocking {
        val dataList = tvSeasonList()
        every { interactor.invoke(84958) } returns flowOf(Success(dataList))

        viewModel.stateFlow.test {

            viewModel.dispatchAction(SeasonsAction.LoadSeasons(84958))

            expectItem() shouldBe SeasonsViewState.Loading
            expectItem() shouldBe SeasonsViewState.Success(dataList)
        }
    }

    @Test
    fun `givenFailureResponse verify errorStateIsReturned`() = runBlocking {
        val errorMessage = "Something went wrong"

        every { interactor.invoke(84958) } returns flowOf(Error(errorMessage))

        viewModel.stateFlow.test {

            viewModel.dispatchAction(SeasonsAction.LoadSeasons(84958))

            expectItem() shouldBe SeasonsViewState.Loading
            expectItem() shouldBe SeasonsViewState.Error(errorMessage)
        }
    }

}