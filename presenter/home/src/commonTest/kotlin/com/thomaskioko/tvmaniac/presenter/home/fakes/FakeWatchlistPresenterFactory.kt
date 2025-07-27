package com.thomaskioko.tvmaniac.presenter.home.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.domain.watchlist.WatchlistInteractor
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import kotlinx.coroutines.test.StandardTestDispatcher

class FakeWatchlistPresenterFactory : WatchlistPresenter.Factory {
    val repository = FakeWatchlistRepository()
    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): WatchlistPresenter = WatchlistPresenter(
        componentContext = componentContext,
        navigateToShowDetails = navigateToShowDetails,
        repository = repository,
        logger = FakeLogger(),
        refreshWatchlistInteractor = WatchlistInteractor(
            watchlistRepository = repository,
            dispatchers = coroutineDispatcher,
        ),
    )
}
