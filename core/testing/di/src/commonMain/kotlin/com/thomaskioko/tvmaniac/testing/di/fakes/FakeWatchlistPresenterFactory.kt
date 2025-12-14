package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.testing.di.TestScope
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistAction
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, WatchlistPresenter.Factory::class)
class FakeWatchlistPresenterFactory : WatchlistPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
        navigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ): WatchlistPresenter = FakeWatchlistPresenter()
}

internal class FakeWatchlistPresenter : WatchlistPresenter {
    override val state: StateFlow<WatchlistState> = MutableStateFlow(WatchlistState())

    override fun dispatch(action: WatchlistAction) {
        // No-op for testing
    }
}
