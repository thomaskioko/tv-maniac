package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository

class FakeWatchlistPresenterFactory : WatchlistPresenter.Factory {
    val repository = FakeWatchlistRepository()

    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): WatchlistPresenter = DefaultWatchlistPresenter(
        componentContext = componentContext,
        navigateToShowDetails = navigateToShowDetails,
        repository = repository,
    )
}
