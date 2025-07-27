package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.domain.watchlist.WatchlistInteractor
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import kotlinx.coroutines.test.StandardTestDispatcher

class FakeWatchlistPresenterFactory : WatchlistPresenter.Factory {
    val repository = FakeWatchlistRepository()

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val fakeRefreshWatchlistInteractor = WatchlistInteractor(
        repository,
        dispatchers,
    )

    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): WatchlistPresenter = WatchlistPresenter(
        componentContext = componentContext,
        navigateToShowDetails = navigateToShowDetails,
        repository = repository,
        refreshWatchlistInteractor = fakeRefreshWatchlistInteractor,
        logger = FakeLogger(),
    )
}
