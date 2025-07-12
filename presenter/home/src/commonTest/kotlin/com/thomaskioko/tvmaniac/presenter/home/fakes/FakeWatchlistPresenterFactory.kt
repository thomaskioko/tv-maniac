package com.thomaskioko.tvmaniac.presenter.home.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistAction
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistState
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
    }
}
