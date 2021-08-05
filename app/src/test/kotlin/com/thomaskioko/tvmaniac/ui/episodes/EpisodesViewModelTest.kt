package com.thomaskioko.tvmaniac.ui.episodes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.thomaskioko.tvmaniac.interactor.EpisodeQuery
import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
import com.thomaskioko.tvmaniac.mockdata.MockData.getEpisodeEntityList
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

internal class EpisodesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val interactor: EpisodesInteractor = mockk()
    private val viewModel = EpisodesViewModel(interactor, testCoroutineDispatcher)

    @Test
    fun `givenDisplayStateIsInvoked verifyResultRepoListIsReturned`() = runBlocking {
        val query = EpisodeQuery(
            tvShowId = 84958,
            seasonId = 114355,
            seasonNumber = 1
        )
        every { interactor.invoke(query) } returns flowOf(Success(getEpisodeEntityList()))

        viewModel.stateFlow.test {

            viewModel.dispatchAction(EpisodesAction.LoadEpisodes(query))

            awaitItem() shouldBe EpisodesViewState.Loading
            awaitItem() shouldBe EpisodesViewState.Success(getEpisodeEntityList())
        }
    }

    @Test
    fun `givenFailureResponse verify errorStateIsReturned`() = runBlocking {
        val errorMessage = "Something went wrong"

        val query = EpisodeQuery(
            tvShowId = 84958,
            seasonId = 114355,
            seasonNumber = 1
        )
        every { interactor.invoke(query) } returns flowOf(Error(errorMessage))

        viewModel.stateFlow.test {

            viewModel.dispatchAction(EpisodesAction.LoadEpisodes(query))

            awaitItem() shouldBe EpisodesViewState.Loading
            awaitItem() shouldBe EpisodesViewState.Error(errorMessage)
        }
    }

}