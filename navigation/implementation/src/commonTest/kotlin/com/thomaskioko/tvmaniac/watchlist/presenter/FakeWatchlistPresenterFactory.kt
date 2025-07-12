package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

class FakeWatchlistPresenterFactory : WatchlistPresenter.Factory {

    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): WatchlistPresenter = FakeWatchlistPresenter()
}

internal class FakeWatchlistPresenter : WatchlistPresenter {
    override val state: StateFlow<WatchlistState>
        get() = TODO("Not yet implemented")

    override fun dispatch(action: WatchlistAction) {
        TODO("Not yet implemented")
    }
}
