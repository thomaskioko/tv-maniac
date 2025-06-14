package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.watchlist.presenter.di.WatchlistPresenterFactory
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository

class FakeWatchlistPresenterFactory : WatchlistPresenterFactory {
    val repository = FakeWatchlistRepository()

    override fun create(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): WatchlistPresenter = WatchlistPresenter(
        componentContext = componentContext,
        navigateToShowDetails = navigateToShowDetails,
        repository = repository,
    )
}
