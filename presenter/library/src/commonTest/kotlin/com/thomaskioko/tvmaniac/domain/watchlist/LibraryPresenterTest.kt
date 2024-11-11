package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryContent
import com.thomaskioko.tvmaniac.presentation.watchlist.LoadingShows
import com.thomaskioko.tvmaniac.watchlist.testing.FakeLibraryRepository
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class LibraryPresenterTest {

  private val lifecycle = LifecycleRegistry()
  private val repository = FakeLibraryRepository()
  private val testDispatcher = StandardTestDispatcher()

  private lateinit var presenter: LibraryPresenter

  @BeforeTest
  fun before() {
    Dispatchers.setMain(testDispatcher)

    lifecycle.resume()
    presenter =
      LibraryPresenter(
        navigateToShowDetails = {},
        repository = repository,
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
      )
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `should emit LoadingShows on init`() = runTest { presenter.state.value shouldBe LoadingShows }

  @Test
  fun `should emit LibraryContent on success`() = runTest {
    repository.setObserveResult(Either.Right(cachedResult))

    presenter.state.test {
      awaitItem() shouldBe LoadingShows
      awaitItem() shouldBe LibraryContent(list = uiResult)

      repository.setObserveResult(Either.Right(updatedData))

      awaitItem() shouldBe LibraryContent(list = expectedUiResult())
    }
  }
}
